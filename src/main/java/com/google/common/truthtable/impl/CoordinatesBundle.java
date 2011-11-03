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

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

//TODO(zorzella): make this immutable
/**
 * A {@link CoordinatesBundle} class holds 1 or more coordinates in multiple
 * axis. 
 * 
 * <p>It is not to be thought of as a complete set of coordinates that defines 
 * a single cell. It can be, but more often than not, it is not complete
 * (i.e. does not touch all axis) and it touches multiple coordinates in each
 * axis.
 * 
 * <p>Internally, this is implemented as a {@link Set} of 
 * {@link SingleAxisCoordinateSet}s, one for each axis touched.
 */
/*
 * Ordinarily we would not suppress warnings, and never for an entire class.
 * But the nature of this problem makes it impossible to properly generify
 * (we would need to generify a variable number of parameters).
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class CoordinatesBundle implements Iterable<SingleAxisCoordinateSet>{

  public final SortedSet<SingleAxisCoordinateSet> backingSet;
  
  /**
   * All dimensions touched by this {@link CoordinatesBundle}.
   * TODO: profile deleting this in favor of axisToSacsMap.keySet()
   */
  public final Set<Class<? extends Enum<?>>> allAxesTouched = 
    new HashSet<Class<? extends Enum<?>>>();
  
  private final Set<Enum<?>> allCoordinatesTouched = new HashSet<Enum<?>>();
  
  /**
   * Maps each axis touched to the corresponding {@link CoordinatesBundle}
   */
  public final Map<Class<? extends Enum>,SingleAxisCoordinateSet> axisToSacsMap = 
    new HashMap<Class<? extends Enum>, SingleAxisCoordinateSet>();

  protected CoordinatesBundle(
      Comparator<SingleAxisCoordinateSet> sacsComparator, 
      Collection<SingleAxisCoordinateSet> coordinatesSets) {
    backingSet = new TreeSet<SingleAxisCoordinateSet>(sacsComparator);
    for (SingleAxisCoordinateSet sacs: coordinatesSets) {
      backingSet.add(sacs);
      axisToSacsMap.put(sacs.getAxis(), sacs);
      for (Enum<?> dPoint: sacs) {
        allCoordinatesTouched.add(dPoint);
      }
      allAxesTouched.add(sacs.getAxis());
    }    
    if (allAxesTouched.size() != coordinatesSets.size()) {
      throw new IllegalArgumentException(String.format(
        "The same axis is used more than once in '%s'.", 
        coordinatesSets));
    }
  }
  
  //TODO: delete allDPointsTouched and use some Collections collator -- profile
  public Collection<Enum<?>> getAllCoordinatesTouched () {
    return allCoordinatesTouched;
  }

  /**
   * Returns true if this touches a {@code coordinate}
   */
  public boolean touches(Enum<?> coordinate) {
    return allCoordinatesTouched.contains(coordinate);
  }

  /**
   * Returns true if this touches an {@code axis}
   */
  public boolean touches(Class<? extends Enum> axis) {
    return allAxesTouched.contains(axis);
  }
  
  @Override
  public Iterator<SingleAxisCoordinateSet> iterator() {
    return backingSet.iterator();
  }

  @Override
  public String toString() {
    return backingSet.toString();
  }
  
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof CoordinatesBundle)) {
      return false;
    }
    return backingSet.equals(((CoordinatesBundle)obj).backingSet);
  }
  
  @Override
  public int hashCode() {
    return backingSet.hashCode();
  }
}
