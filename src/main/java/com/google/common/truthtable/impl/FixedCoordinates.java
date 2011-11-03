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

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * The {@link FixedCoordinates} class is a {@link CoordinatesBundle} to be 
 * passed to {@link TruthTable#getAll(Class, FixedCoordinates)}.
 * 
 * @author zorzella@google.com
 */
/*
 * Ordinarily we would not suppress warnings, and never for an entire class.
 * But the nature of this problem makes it impossible to properly generify
 * (we would need to generify a variable number of parameters).
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class FixedCoordinates extends CoordinatesBundle {

  public FixedCoordinates(Collection<SingleAxisCoordinateSet> coordinates) {
    super(TruthTableComparators.FOR_SINGLE_AXIS_COORDINATES_SET, coordinates);
  }

  public FixedCoordinates(Comparator<SingleAxisCoordinateSet> sacsComparator, 
      Collection<SingleAxisCoordinateSet> coordinates) {
    super(sacsComparator, coordinates);
  }
  
  FixedCoordinates(Comparator<SingleAxisCoordinateSet> sacsComparator, 
      Enum... dPoints) {
    super(sacsComparator, getSingleAxisCoordinatesSetForEnumValues(dPoints));
  }

  /**
   * Convenience constructor for the simple case. Each dPoint must be in a 
   * different dimension.
   * 
   * @throws IllegalArgumentException if any two dPoints are in the same 
   *   dimension
   */
  public FixedCoordinates(Enum... dPoints) {
    super(TruthTableComparators.FOR_SINGLE_AXIS_COORDINATES_SET, 
        getSingleAxisCoordinatesSetForEnumValues(dPoints));
  }

  @SuppressWarnings("unchecked")
  private static Set<SingleAxisCoordinateSet> 
    getSingleAxisCoordinatesSetForEnumValues(
      Enum... dPoints) {
    Set<SingleAxisCoordinateSet> temp = 
      new TreeSet<SingleAxisCoordinateSet>(
          TruthTableComparators.FOR_SINGLE_AXIS_COORDINATES_SET);
    for (Enum dPoint: dPoints) {
      temp.add(new SingleAxisCoordinateSet(dPoint));
    }
    return temp;
  }
}
