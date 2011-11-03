/*
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.common.truthtable.impl;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;
import com.google.common.truthtable.TruthTable;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

//TODO(zorzella): make this whole class immutable
/*
 * Ordinarily we would not suppress warnings, and never for an entire class.
 * But the nature of this problem makes it impossible to properly generify
 * (we would need to generify a variable number of parameters).
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class RealTruthTable implements TruthTable {

  /** 
   * All the affinity groups this truth table was populated with.
   */
  public final Set<AffinityGroup> allAffinityGroups;

  /**
   * All axes that were registered.
   */
  private final Set<Class<? extends Enum>> registeredAxes;
  
  // Start cheat sheets. The data in these is derived from the others, but
  // it's stored here in a way so as to be of quickest access.
  /**
   * Quick way to find all the coordinates for with a given axis.
   */
  private final Multimap<Class<? extends Enum>, Enum<?>> axesToCoordinatesMap = 
    HashMultimap.create();

  // TODO(zorzella): What I need is a TreeMultimap backed by a TreeSet
  /**
   * All the {@link AffinityGroup}s that touch a given axis.
   */
  private final Map<Class<? extends Enum>, Set<AffinityGroup>> axisToAffinityGroups =
    new LinkedHashMap<Class<? extends Enum>, Set<AffinityGroup>>();
  
  // TODO(zorzella): What I need is a TreeMultimap backed by a TreeSet
  /**
   * All {@link AffinityGroup}s that touch a given coordinate.
   */
  private final Map<Enum<?>, Set<AffinityGroup>> coordinateToAffinityGroupsMap =
    new LinkedHashMap<Enum<?>, Set<AffinityGroup>>();

  private final int numberOfAxes;

  private final Comparator<SingleAxisCoordinateSet> sacsComparator;
  private final Comparator<CoordinatesBundle> coordinatesBundleComparator;

  /**
   * For each affinity group, all the other affinity groups that are possible 
   * next hops for the algorithm to find a solution path.
   */
  final Multimap<AffinityGroup,NextHop> nextHopsForAffinityGroup;
  
  // End cheat sheets

  RealTruthTable(Set<Class<? extends Enum>> registeredAxes,
      Set<AffinityGroup> affinityGroups, 
      Set<Enum<?>> allCoordinates) {
    this(registeredAxes, affinityGroups, allCoordinates, 
        TruthTableComparators.FOR_SINGLE_AXIS_COORDINATES_SET, 
        TruthTableComparators.FOR_COORDINATES_BUNDLE);
  }
  
  public RealTruthTable(Set<Class<? extends Enum>> registeredAxes,
      Set<AffinityGroup> allPopulatedAffinityGroups, 
      Set<Enum<?>> allCoordinates, 
      Comparator<SingleAxisCoordinateSet> sacsComparator,
      Comparator<CoordinatesBundle> coordinatesBundleComparator) {
    this.registeredAxes = registeredAxes;
    this.allAffinityGroups = allPopulatedAffinityGroups;
    this.numberOfAxes = registeredAxes.size();
    this.sacsComparator = sacsComparator;
    Comparator<NextHop> nextHopComparator = 
      TruthTableComparators.forNextHop(coordinatesBundleComparator);
    this.coordinatesBundleComparator = coordinatesBundleComparator;
    this.nextHopsForAffinityGroup = TreeMultimap.create(
        coordinatesBundleComparator,
        nextHopComparator);

    populateCheatSheets(allCoordinates, allPopulatedAffinityGroups);
  }

  /**
   * Populates maps that makes querying data quick.
   */
  private void populateCheatSheets(Set<Enum<?>> allCoordinates,
      Set<AffinityGroup> allAffinityGroups) {
    for (Enum coordinate : allCoordinates) {
      axesToCoordinatesMap.put(coordinate.getClass(), coordinate);
    }
    for (AffinityGroup affinityGroup : allAffinityGroups) {
      for (Enum<?> coordinate : affinityGroup.getAllCoordinatesTouched()) {
        Set<AffinityGroup> set = coordinateToAffinityGroupsMap.get(coordinate);
        if (set == null) {
          set = new TreeSet<AffinityGroup>(coordinatesBundleComparator);
          coordinateToAffinityGroupsMap.put(coordinate, set);
        }
        set.add(affinityGroup);
      }
      
      for (Class<? extends Enum> axis: affinityGroup.allAxesTouched) {
        Set<AffinityGroup> set = axisToAffinityGroups.get(axis);
        if (set == null) {
          set = new TreeSet<AffinityGroup>(coordinatesBundleComparator);
          axisToAffinityGroups.put(axis, set);
        }
        set.add(affinityGroup);
      }
    }
    for (AffinityGroup affinityGroup: allAffinityGroups) {
      Set<NextHop> nextHopsFor = calculateNextHopsFor(affinityGroup);
      nextHopsForAffinityGroup.putAll(affinityGroup, nextHopsFor);
    }
  }
  
  private void assertAxisWasRegistered(Class<? extends Enum> axis) {
    if (!registeredAxes.contains(axis)) {
      throw new IllegalArgumentException(String.format(
        "Axis '%s' was not registered.", 
        axis));
    }
  }
  
  Set<AffinityGroup> getAffinityGroupsInvolving(Enum<?> coordinate) {
    Set<AffinityGroup> result = coordinateToAffinityGroupsMap.get(coordinate);
    if (result == null) {
      return Collections.emptySet();
    }
    return result;
  }

  Set<AffinityGroup> getAffinityGroupsTouching(Class<? extends Enum> axis) {
    Set<AffinityGroup> result = axisToAffinityGroups.get(axis);
    if (result == null) {
      return Collections.emptySet();
    }
    return result;
  }
  
  @Override
  public <T extends Enum<T>> Set<T> getAll(
      Class<T> axis, FixedCoordinates fixedCoordinates) {
    assertAxisWasRegistered(axis);
    Set<T> result = getValidCoordinatesFor(axis, fixedCoordinates);
    return result;
  }

  @Override
  public <T extends Enum<T>> Set<T> getAll(
      Class<T> coordinates) {
    return getAll(coordinates, new FixedCoordinates(sacsComparator));
  }

  /**
   * We keep track of the path we are currently examining for validity with this 
   * class.
   */
  static class PathTrack {

    /**
     * Axes already visited, and, thus, that should not be tried again
     */
    private Set<Class<? extends Enum>> axesVisited = 
      new LinkedHashSet<Class<? extends Enum>>();
    
    /**
     * For each axis, the coordinates that this path touches. I.e. for every
     * used affinity and every fixed coordinate that touch a given axis, the
     * intersection of the coordinates in that axis. No entry in this map should
     * be of zero size, for that would mean that no path is possible.
     */
    private Map<Class<? extends Enum>,EnumSet<?>> axesToCoordinates =
      new LinkedHashMap<Class<? extends Enum>, EnumSet<?>>();
    
    /**
     * All affinity groups visited.
     */
    private final ImmutableSet<AffinityGroup> visited;
    
    private PathTrack(AffinityGroup startingPoint) {
      visited = ImmutableSet.of(startingPoint);
    }

    // TODO(zorzella): I think that if I store an axesToCoordinates map in the
    // FixedCoordinates classes, I'd do away with this constructor
    public PathTrack(FixedCoordinates fixedCoordinates) {
      visited = ImmutableSet.of();
      for (SingleAxisCoordinateSet fixedCoordinate: fixedCoordinates) {
        axesToCoordinates.put(fixedCoordinate.getAxis(), 
          EnumSet.copyOf(fixedCoordinate.backingEnumSet));
      }
    }
    
    PathTrack(PathTrack old, NextHop nextHop) {
      visited =
          ImmutableSet.<AffinityGroup>builder()
              .addAll(old.visited)
              .add(nextHop.target)
              .build();
      axesVisited.addAll(old.axesVisited);
      axesToCoordinates = deepCopy(old.axesToCoordinates);
    }

    /**
     * Given a {@code source} Map of axis to coordinates, returns a copy of 
     * that Map. We need to copy these maps for 
     * {@link #narrowDownAxesToCoordinatesMap(CoordinatesBundle)} to safely 
     * changes them without disturbing the original path where they came from.
     */
    private Map<Class<? extends Enum>, EnumSet<?>> deepCopy(
        Map<Class<? extends Enum>, EnumSet<?>> source) {
      Map<Class<? extends Enum>, EnumSet<?>> result = 
        new LinkedHashMap<Class<? extends Enum>, EnumSet<?>>();
      for (Map.Entry<Class<? extends Enum>,EnumSet<?>> entry: source.entrySet()) {
        EnumSet value = entry.getValue();
        result.put(entry.getKey(), EnumSet.copyOf(value));
      }
      return result;
    }

    PathTrack startAt(AffinityGroup startingPoint) {
      PathTrack result = new PathTrack(startingPoint);
      
      result.axesToCoordinates = deepCopy(axesToCoordinates);
      
      if (!result.narrowDownAxesToCoordinatesMap(startingPoint)) {
        return null;
      }
      
      result.axesVisited.addAll(startingPoint.allAxesTouched);
      
      return result;
    }

    /**
     * Returns a new PathTrack, based on the current one plus a visit to a given
     * tentative {@code nextHop}. If the result would be an invalid path, or if 
     * this visit it fruitless, return {@code null}. An invalid path is one were 
     * a given axis would be empty of coordinates. A fruitless visit is one that 
     * does not add any axis to the path.
     */
    public PathTrack visit(NextHop nextHop) {
      if (visited.contains(nextHop.target)) {
        // If we have already visited the nextHop's target, this would result
        // in no new axis, which would be taken care of below. This simply
        // short-circuits that.
        return null;
      }
      PathTrack result = new PathTrack(this, nextHop);
      
      if (!result.axesVisited.addAll(nextHop.newAxes)) {
        // Adding this hop to this Path would not add any new dimension
        return null;
      }
      
      if (!result.narrowDownAxesToCoordinatesMap(nextHop.target)) {
        return null;
      }
      return result;
    }

    /**
     * Given a target {@link CoordinatesBundle}, we narrow down our list of 
     * still-valid coordinates.
     */
    private boolean narrowDownAxesToCoordinatesMap(CoordinatesBundle target) {
      // We iterate over all axes->coordinates of "target"
      for (Class<? extends Enum> oneAxis: target.axisToSacsMap.keySet()) {
        // For the current path, and the axis on this iteration of the loop, we 
        // find if that axis has already been narrowed down to certain 
        // coordinates
        EnumSet<?> coordinatesForAnAxisInCurrentPath = 
          axesToCoordinates.get(oneAxis);
        // If the current path has not yet narrowed down the valid coordinates
        // for this axis...
        if (coordinatesForAnAxisInCurrentPath == null) {
          EnumSet temp = target.axisToSacsMap.get(oneAxis).backingEnumSet;
          // ...we set the collection of valid coordinates for this axis to be
          // a copy of the target hop's coordinates for that axis,...
          axesToCoordinates.put(oneAxis, EnumSet.copyOf(temp));
        } else {
          //... otherwise, we just narrow down the current existing list
          coordinatesForAnAxisInCurrentPath.retainAll(
              target.axisToSacsMap.get(oneAxis).backingEnumSet);

          // Ending up with an empty set means that target is not compatible
          // with this path (for we know target touches this one shared axis at
          // a coordinate that this path does not touch)
          if (coordinatesForAnAxisInCurrentPath.size() == 0) {
            return false;
          }
        }
      }
      return true;
    }

    @Override
    public String toString() {
      return String.format(
          "axes: [%s], visited: [%s], coordinates: [%s]",
          simpleClassNames(axesVisited), visited, axesToCoordinates.values());
    }

    private static Set<String> simpleClassNames(
        Set<Class<? extends Enum>> axes) {
      Set<String> result = Sets.newLinkedHashSet();
      for (Class<? extends Enum> axis: axes) {
        result.add(axis.getSimpleName());
      }
      return result;
    }
  }
  
  public <T extends Enum<T>> EnumSet<T> getValidCoordinatesFor(Class<T> axis, 
      FixedCoordinates fixedCoordinates) {

    EnumSet<T> result = EnumSet.noneOf(axis);
    EnumSet<T> stillNotFound = EnumSet.complementOf(result);
    
    PathTrack pathTrack = new PathTrack(fixedCoordinates);
    
    Set<AffinityGroup> startingAffinityGroups = 
      getAffinityGroupsTouching(axis);
    
    for (AffinityGroup startingAffinityGroup: startingAffinityGroups) {
      // Skip affinity groups that would not add some value, i.e. that only touch 
      // dPoints already found to be part of the result
      if (!startingAffinityGroup.axisToSacsMap.get(axis).touches(stillNotFound)) {
        continue;
      }
      PathTrack newPathTrack = pathTrack.startAt(startingAffinityGroup);
      if (newPathTrack == null) {
        continue;
      }
      Collection<PathTrack> allCompleteValidPathsBeginningWithNewPathTrack = 
        getAllCompleteValidPathsBeginningWith(newPathTrack);
      for (PathTrack validPath: allCompleteValidPathsBeginningWithNewPathTrack) {
        EnumSet<T> newlyFoundCoordinates = 
          (EnumSet<T>) validPath.axesToCoordinates.get(axis);
        result.addAll(newlyFoundCoordinates);
        stillNotFound.removeAll(newlyFoundCoordinates);
        // Optimization only -- if we already found everything, we're done 
        if (stillNotFound.isEmpty()) {
          return result;
        }
      }
    }
    return result;
  }

  /**
   * Returns all paths that are complete (a complete path touches all axes),
   * valid (i.e. abide by the affinities declared) and are super-paths of
   * {@code pathTrack}.
   * 
   * <p>If no path is found, return an empty collection (and <em>not</em> a 
   * collection with {@code pathTrack}).
   * 
   * <p>The given {@code pathTrack} is assumed by this method to be valid itself. 
   * If it violates this contract, the results of this method are undefined.
   * 
   * <p>{@code pathTrack} may itself be already complete -- in that case, the 
   * result will be a collection with that single path {@code pathTrack} in it. 
   * Indeed, this is the exit condition for this recursive algorithm.
   */
  private Collection<PathTrack> getAllCompleteValidPathsBeginningWith(
      PathTrack pathTrack) {
    List<PathTrack> result = Lists.newArrayList();
    // If the path track touches all dimensions, our job is done
    if (pathTrack.axesVisited.size() == numberOfAxes) {
      result.add(pathTrack);
      return result;
    }
    Set<PathTrack> possiblePathsFromPathTrack = getPossiblePathsFrom(pathTrack);
    for (PathTrack possiblePath: possiblePathsFromPathTrack) {
      Collection<PathTrack> validPaths = 
        getAllCompleteValidPathsBeginningWith(possiblePath);
      result.addAll(validPaths);
    }
    return result;
  }
  
  /**
   * Given a source, returns true if all targets are next hops for source.
   */
  @VisibleForTesting
  boolean targetsAreNextHopsFor(AffinityGroup source, 
      AffinityGroup... target) {
    Collection<NextHop> nextHops = nextHopsForAffinityGroup.get(source);
    Set<AffinityGroup> targets = Sets.newTreeSet(coordinatesBundleComparator);
    Collections.addAll(targets, target);
    if (nextHops.size() == 0) {
      return target.length == 0;
    }
    for (AffinityGroup c: target) {
      for (NextHop nextHop: nextHops) {
        if (!targets.contains(nextHop.target)) {
          return false;
        }
      }
    }
    return true;
  }
  
  /**
   * This method returns a collection of PathTracks. Each is guaranteed to be a 
   * "continuation" of {@code pathTrack}, i.e.:
   * 
   * <ul>
   *  <li>touch at least one more affinity than {@code pathTrack}
   *  <li>touch at least one more axis than {@code pathTrack}
   *  <li>still touch all axis touched by {@code pathTrack}, and for each of
   *      those, touch at least one of the coordinates that {@code pathTrack}
   *      touches.
   * </ul>
   */
  private Set<PathTrack> getPossiblePathsFrom(PathTrack pathTrack) {
    Set<PathTrack> result = new LinkedHashSet<PathTrack>();

    for (AffinityGroup lastHop : pathTrack.visited) {
      for (NextHop nextHop: nextHopsForAffinityGroup.get(lastHop)) {
        PathTrack newPathTrack = pathTrack.visit(nextHop);
        if (newPathTrack == null) {
          continue;
        }
        result.add(newPathTrack);
      }
    }
    return result;
  }

  /**
   * Figures out the possible next hops for a given {@link AffinityGroup}
   */
  private Set<NextHop> calculateNextHopsFor(AffinityGroup source) {
    Set<NextHop> result = new LinkedHashSet<NextHop>();
    for (AffinityGroup tentativeNextHop : allAffinityGroups) {
      if (tentativeNextHop == source) {
        continue;
      }
      NextHop nextHop = getNextHopFor(source, tentativeNextHop);
      if (nextHop == null) {
        continue;
      }
      result.add(nextHop);
    }
    return result;
  }
  
  /**
   * Encapsulates a target {@link AffinityGroup} and some other useful 
   * information related to a source {@link AffinityGroup}. 
   * 
   * <p>NextHops are useful for the speed of the algorithm: it will ultimately
   * have to navigate from a given {@link AffinityGroup} to (some) overlapping
   * {@link AffinityGroup}s. Instead of trying to find out if two groups are 
   * overlapping during execution of the algorithm, and since that information 
   * is never changing, we first populate a Map that links each 
   * {@link AffinityGroup} to all its {@link NextHop}s. Furthermore, since the
   * algorithm is always interested in expanding the path, we only store an
   * overlapping {@link AffinityGroup} as a {@link NextHop} if it touches at
   * least one axis more.
   */
  public static class NextHop {

    public AffinityGroup target;
    
    public NextHop(AffinityGroup target) {
      this.target = target;
    }
    
    Set<Class<? extends Enum>> newAxes =
      new LinkedHashSet<Class<? extends Enum>>();
    
    @Override
    public String toString() {
      return target + ":" + newAxes;
    }
  }
  
  /**
   * This method returns target as a NextHop for source, or null if target
   * can't be a next hop for source (according to the rules below). The NextHop 
   * class is used here (instead of simply Combination) to preserve the list of 
   * dimensions that target adds to source, thus optimizing the calling code. 
   * 
   * In order for Combination target to be used as the immediate next hop of 
   * Combination source one in a path, all these conditions must be met:
   * 
   *  - source and target share at least one dimension
   *  - For every dimension shared between source and target, the intersection 
   *    of DPoints in that dimension must not be empty
   *  - target must have at least one dimension that source does not have (which 
   *    makes this relationship sometimes assymetric)
   * 
   */
  private static NextHop getNextHopFor(
      AffinityGroup source, AffinityGroup target) {

    NextHop result = new NextHop(target);
    
    Set<Class<? extends Enum<?>>> sharedAxes =
      new LinkedHashSet<Class<? extends Enum<?>>>();
    for (Class<? extends Enum<?>>axis: target.allAxesTouched) {
      if (source.touches(axis)) {
        sharedAxes.add(axis);
      } else {
        result.newAxes.add(axis);
      }
    }
    if ((sharedAxes.size() == 0) || (result.newAxes.size() == 0)) {
      // (A) No shared dimensions OR (C) no new dimensions
      return null;
    }
    
    for (Class<? extends Enum<?>>axis: sharedAxes) {
      Set<Enum<?>> sharedDPoints = getSharedCoordinates(source, target, axis);
      if (sharedDPoints.size() == 0) {
        // (B) this is a shared axis that does not share any coordinate
        return null;
      }
    }
    return result;
  }

  private static Set<Enum<?>> getSharedCoordinates(
      AffinityGroup source, AffinityGroup target,
      Class<? extends Enum<?>> axis) {
    Set<Enum<?>> result = new LinkedHashSet<Enum<?>>();
    SingleAxisCoordinateSet coordinateSet = source.axisToSacsMap.get(axis);
    for (Enum<?> coordinate: coordinateSet) {
      if (target.touches(coordinate)) {
        result.add(coordinate);
      }
    }
    return result;
  }

  @Override
  public String toString() {
    return this.allAffinityGroups.toString();
  }
}
