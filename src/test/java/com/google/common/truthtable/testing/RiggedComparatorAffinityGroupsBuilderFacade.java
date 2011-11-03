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
package com.google.common.truthtable.testing;

import com.google.common.collect.Sets;
import com.google.common.truthtable.TruthTable;
import com.google.common.truthtable.impl.AffinityGroup;
import com.google.common.truthtable.impl.AffinityGroupsBuilder;
import com.google.common.truthtable.impl.CoordinatesBundle;
import com.google.common.truthtable.impl.RealTruthTable;
import com.google.common.truthtable.impl.SimpleAffinityGroupBuilder;
import com.google.common.truthtable.impl.SingleAxisCoordinateSet;
import com.google.common.truthtable.impl.TruthTableComparators;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;

/**
 * Do not use this for production code.
 * 
 * <p>This class provides a way to create a truth table using a special kind of 
 * comparator that is useful to test some scenarios. The 
 * {@link SingleAxisCoordinateSet} and {@link CoordinatesBundle}
 * ({@link AffinityGroup}) will be ordered by the {@link TruthTable} in the same
 * order in which they were created. This makes it simpler to test algorithm
 * scenarios that would require a certain ordering of {@link AffinityGroup} or 
 * {@link SingleAxisCoordinateSet}s therein, etc.<p>
 * 
 * <p>To use this feature, use this class -- instead of a regular 
 * {@link AffinityGroupsBuilder} -- to create your {@link AffinityGroup}s, 
 * and, then, create the {@link RealTruthTable} by passing the comparators 
 * {@link #sacsComparator} and {@link #coordinatesBundleComparator}.<p>
 * 
 *  <p>Note that, contrary to a regular builder, you should not instantiate this
 *  class more than once, since the Comparators to pass to the engine are kept 
 *  here. For this purpose, after you call {@link #createAndResetBuilder()}, the
 *  {@link #backingBuilder} is reset, and you can create a new 
 *  {@link AffinityGroup}.
 * 
 * @author zorzella@google.com
 */
public class RiggedComparatorAffinityGroupsBuilderFacade {
  
  /**
   * Just to allow this file to access the protected "sacses".
   */
  private final class MySimpleAffinityGroupBuilder extends
      SimpleAffinityGroupBuilder {
    private MySimpleAffinityGroupBuilder(
        Comparator<SingleAxisCoordinateSet> groupComparator) {
      super(groupComparator);
    }

    public SortedSet<SingleAxisCoordinateSet> getSacses() {
      return sacses;
    }
  }

  final Set<SingleAxisCoordinateSet> sacsesInOrder = Sets.newLinkedHashSet();
  final Set<CoordinatesBundle> coordinatesBundlesInOrder = Sets.newLinkedHashSet();

  public final Comparator<SingleAxisCoordinateSet> sacsComparator = 
    new Comparator<SingleAxisCoordinateSet>() {
      @Override
      public int compare(SingleAxisCoordinateSet o1, SingleAxisCoordinateSet o2) {
        int regularCompareTo = 
          TruthTableComparators.FOR_SINGLE_AXIS_COORDINATES_SET.compare(o1, o2);
        if (regularCompareTo == 0) {
          return 0;
        }
        for (SingleAxisCoordinateSet toTest: sacsesInOrder) {
          if (toTest.equals(o1)) {
            return -1;
          }
          if (toTest.equals(o2)) {
            return 1;
          }
        }
        return regularCompareTo;
      }
  };

  public final Comparator<CoordinatesBundle> coordinatesBundleComparator = 
    new Comparator<CoordinatesBundle>() {
  
    @Override
    public int compare(CoordinatesBundle o1, CoordinatesBundle o2) {
      int regularCompareTo = 
        TruthTableComparators.FOR_COORDINATES_BUNDLE.compare(o1, o2);
      if (regularCompareTo == 0) {
        return 0;
      }
      for (CoordinatesBundle toTest: coordinatesBundlesInOrder) {
        if (toTest.equals(o1)) {
          return -1;
        }
        if (toTest.equals(o2)) {
          return 1;
        }
      }
      return regularCompareTo;
    }
  };
  
  /**
   * The builder we use behind the scenes. Every time 
   * {@link #createAndResetBuilder()} is called, a new instance is created here.
   */
  MySimpleAffinityGroupBuilder backingBuilder = getNewBackingBuilder();

  public AffinityGroup createAndResetBuilder() {
    AffinityGroup result = backingBuilder.create().iterator().next();
    backingBuilder = getNewBackingBuilder();
    coordinatesBundlesInOrder.add(result);
    return result;
  }

  private MySimpleAffinityGroupBuilder getNewBackingBuilder() {
    return new MySimpleAffinityGroupBuilder(sacsComparator);
  }

  public <T extends Enum<T>> RiggedComparatorAffinityGroupsBuilderFacade touching(
      T first, T... rest) {
    backingBuilder.touching(first, rest);
    // each call to backingBuilder.touching will add to its internal 
    //backingBuilder.sacses in the last position. Here we add this 
    // most-recently-added SingleAxisCoordinateSet to our list of known ones
    sacsesInOrder.add(backingBuilder.getSacses().last());
    return this;
  }
  
}