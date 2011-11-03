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

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

/**
 * Convenience builders to build {@link AffinityGroup}s.
 * 
 * @see MultimapAffinityGroupBuilder
 * @see SimpleAffinityGroupBuilder
 */
public abstract class AffinityGroupsBuilder<S extends AffinityGroupsBuilder<S>> {

  protected final SortedSet<SingleAxisCoordinateSet> sacses;
  
  private final Set<Class<? extends Enum<?>>> allAxesTouched = 
    new HashSet<Class<? extends Enum<?>>>();

  protected final Comparator<SingleAxisCoordinateSet> sacsComparator;

  public AffinityGroupsBuilder(
      Comparator<SingleAxisCoordinateSet> sacsComparator) {
    this.sacsComparator = sacsComparator;
    sacses = Sets.newTreeSet(sacsComparator);
  }
  
  /**
   * Adds coordinates to a certain axis of the {@link AffinityGroup}s to be
   * created.
   */
  public <T extends Enum<T>> S touching(T first, T... rest) {
    List<T> dPoints = Lists.asList(first, rest);
    return touching(dPoints);
  }

  /**
   * Adds coordinates to a certain axis of the {@link AffinityGroup}s to be 
   * created.
   */
  @SuppressWarnings("unchecked")
  public <E extends Enum<E>> S touching(Collection<E> coordinates) {
    Preconditions.checkNotNull(coordinates);
    Preconditions.checkArgument(coordinates.size() > 0);
    SingleAxisCoordinateSet sacs = new SingleAxisCoordinateSet(coordinates);
    //TODO(zorzella): call the other TODO's refactored method
    Set<SingleAxisCoordinateSet> temp = Sets.newTreeSet(sacsComparator);
    temp.add(sacs);
    return touchingAll(temp);
  }

  /**
   * Adds coordinates in multiple axes with a single call. Equivalent to calling 
   * {@link #touching(Collection)} multiple times, one for each axis.
   */
  public S touchingAll(Collection<SingleAxisCoordinateSet> coordinates) {
    Preconditions.checkNotNull(coordinates);
    for (SingleAxisCoordinateSet coordinateSet: coordinates) {
      Preconditions.checkNotNull(coordinateSet);
      //TODO(zorzella): refactor this to a different method
      this.sacses.add(coordinateSet);
      Class<? extends Enum<?>> axis = coordinateSet.getAxis();
      if (!allAxesTouched.add(axis)) {
        throw new IllegalArgumentException(
        String.format("Axis '%s' was touched twice.", axis));
      }
    }
    return self();
  }

  @SuppressWarnings("unchecked")
  protected S self() {
    return (S) this;
  }
  
  /**
   * Creates a set of {@link AffinityGroup}.
   */
  public abstract Set<AffinityGroup> create();

  /**
   * @return true if the given {@code axis} has been touched already.
   */
  @SuppressWarnings("unchecked")
  public boolean hasTouched(Class<? extends Enum<?>> axis) {
    Preconditions.checkNotNull(axis);
    return allAxesTouched.contains(axis);
  }
}
