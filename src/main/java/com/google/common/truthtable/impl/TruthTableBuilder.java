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

import com.google.common.truthtable.TruthTable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Use this class to build a {@link TruthTable}.
 * 
 * @author zorzella@google.com
 */
/*
 * Ordinarily we would not suppress warnings, and never for an entire class.
 * But the nature of this problem makes it impossible to properly generify
 * (we would need to generify a variable number of parameters).
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class TruthTableBuilder {

  private final Set<Class<? extends Enum>> registeredAxes = 
    new HashSet<Class<? extends Enum>>();

  private final Set<AffinityGroup> affinityGroups;

  public TruthTableBuilder() {
    this(TruthTableComparators.FOR_COORDINATES_BUNDLE);
  }
  
  public TruthTableBuilder(
      Comparator<CoordinatesBundle> coordinatesBundleComparator) {
    affinityGroups = new TreeSet<AffinityGroup>(coordinatesBundleComparator);
  }
  
  /**
   * Registers all of the given classes as axes to the {@link TruthTable}
   * to be created.
   */
  public TruthTableBuilder forAxes(Class<? extends Enum> toRegister) {
    registeredAxes.add(toRegister);
    return this;
  }

  /**
   * Adds all given {@link AffinityGroup}s to the {@link TruthTable} to be
   * created.
   */
  public TruthTableBuilder addAffinityGroups(AffinityGroup... affinityGroups) {
    for (AffinityGroup affinityGroup: affinityGroups) {
      addAffinityGroup(affinityGroup);
    }
    return this;
  }
  
  /**
   * Adds all given {@link AffinityGroup}s to the {@link TruthTable} to be
   * created.
   */
  public TruthTableBuilder addAffinityGroups(Set<AffinityGroup> affinityGroups) {
    for (AffinityGroup affinityGroup: affinityGroups) {
      addAffinityGroup(affinityGroup);
    }
    return this;
  }

  /**
   * Adds a given {@link AffinityGroup} to the {@link TruthTable} to be created
   */
  @SuppressWarnings("unchecked")
  TruthTableBuilder addAffinityGroup(AffinityGroup affinityGroup) {
    if (!affinityGroups.add(affinityGroup)) {
      throw new IllegalArgumentException(String.format(
        "Trying to add the same affinity group '%s' twice", affinityGroup));
    }
    for (SingleAxisCoordinateSet enumValues: affinityGroup) {
      assertAxisWasRegistered(enumValues.getAxis());
    }
    return this;
  }

  /**
   * Creates the {@link TruthTable}
   */
  public TruthTable create() {
    return create(TruthTableComparators.FOR_SINGLE_AXIS_COORDINATES_SET, 
        TruthTableComparators.FOR_COORDINATES_BUNDLE);
  }
  
  public TruthTable create(
      Comparator<SingleAxisCoordinateSet> sacsComparator, 
      Comparator<CoordinatesBundle> coordinatesbundleComparator) {
    Set<Enum<?>> foundDPoints = new HashSet<Enum<?>>();
    Set<Class<? extends Enum>> foundAxes = 
      new HashSet<Class<? extends Enum>>();

    for (AffinityGroup vc: affinityGroups) {
      foundAxes.addAll(vc.allAxesTouched);
      foundDPoints.addAll(vc.getAllCoordinatesTouched());
    }
    
    if (!foundAxes.equals(registeredAxes)) {
      Set<Class<? extends Enum>> neverReachedAxes = 
        new HashSet<Class<? extends Enum>>();
      neverReachedAxes.addAll(registeredAxes);
      neverReachedAxes.removeAll(foundAxes);
      throw new IllegalArgumentException(String.format(
        "The axes '%s' are never touched by the given AffinityGroups.", 
        neverReachedAxes));
    }

    // TODO: somewhere, make these immutable/unmodifiable
    return new RealTruthTable(
      registeredAxes, affinityGroups, foundDPoints,
      sacsComparator, coordinatesbundleComparator);
  }
  
  private void assertAxisWasRegistered(Class<? extends Enum> axis) {
    if (!registeredAxes.contains(axis)) {
      throw new IllegalArgumentException(String.format(
        "Dimension '%s' was not registered.", axis));
    }
  }

  public TruthTableBuilder forAxes(Class<? extends Enum> first, 
      Class<? extends Enum> second) {
    registeredAxes.add(first);
    registeredAxes.add(second);
    return this;
  }

  public TruthTableBuilder forAxes(Class<? extends Enum> first,
      Class<? extends Enum> second, Class<? extends Enum> third) {
    registeredAxes.add(first);
    registeredAxes.add(second);
    registeredAxes.add(third);
    return this;
  }

  public TruthTableBuilder forAxes(Class<? extends Enum> first,
      Class<? extends Enum> second, Class<? extends Enum> third,
      Class<? extends Enum> fourth) {
    registeredAxes.add(first);
    registeredAxes.add(second);
    registeredAxes.add(third);
    registeredAxes.add(fourth);
    return this;
  }

  public TruthTableBuilder forAxes(Class<? extends Enum> first,
      Class<? extends Enum> second, Class<? extends Enum> third,
      Class<? extends Enum> fourth, Class<? extends Enum> fifth) {
    registeredAxes.add(first);
    registeredAxes.add(second);
    registeredAxes.add(third);
    registeredAxes.add(fourth);
    registeredAxes.add(fifth);
    return this;
  }

  public TruthTableBuilder forAxes(Class<? extends Enum> first,
      Class<? extends Enum> second, Class<? extends Enum> third,
      Class<? extends Enum> fourth, Class<? extends Enum> fifth, 
      Class<? extends Enum> sixth) {
    registeredAxes.add(first);
    registeredAxes.add(second);
    registeredAxes.add(third);
    registeredAxes.add(fourth);
    registeredAxes.add(fifth);
    registeredAxes.add(sixth);
    return this;
  }

  public TruthTableBuilder forAxes(Class<? extends Enum> first,
      Class<? extends Enum> second, Class<? extends Enum> third,
      Class<? extends Enum> fourth, Class<? extends Enum> fifth, 
      Class<? extends Enum> sixth, Class<? extends Enum>... rest) {
    registeredAxes.add(first);
    registeredAxes.add(second);
    registeredAxes.add(third);
    registeredAxes.add(fourth);
    registeredAxes.add(fifth);
    registeredAxes.add(sixth);
    registeredAxes.addAll(Arrays.asList(rest));
    return this;
  }
}
