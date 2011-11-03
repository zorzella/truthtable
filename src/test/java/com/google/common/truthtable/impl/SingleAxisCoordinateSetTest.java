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

import com.google.common.truthtable.testing.BogusTruthTableTestingEnums.Bread;
import com.google.common.truthtable.testing.BogusTruthTableTestingEnums.Entree;

import junit.framework.TestCase;

import java.util.Set;
import java.util.TreeSet;

/**
 * Tests basic properties of {@link SingleAxisCoordinateSet}
 *
 * @author zorzella@google.com
 */
@SuppressWarnings("unchecked")
public class SingleAxisCoordinateSetTest extends TestCase {

  public void testEnumValues() throws Exception {
    SingleAxisCoordinateSet coordinates = 
      new SingleAxisCoordinateSet(Entree.STEAK, Entree.CHICKEN);
  }

  public void testEnumValuesCantBeEmpty() throws Exception {
    try {
      SingleAxisCoordinateSet coordinates = new SingleAxisCoordinateSet();
      fail();
    } catch (IllegalArgumentException expected) {

    }
  }

  public void testEnumValuesCantMixEnums() throws Exception {
    try {
      SingleAxisCoordinateSet sacs = 
        new SingleAxisCoordinateSet(Entree.STEAK, Bread.PITA);
      fail();
    } catch (ClassCastException expected) {

    }
  }

  public void testEnumValuesCantHaveEntryMultipleTimes() throws Exception {
    try {
      SingleAxisCoordinateSet coordinates = 
        new SingleAxisCoordinateSet(Entree.STEAK, Entree.STEAK);
      fail();
    } catch (IllegalArgumentException expected) {

    }
  }

  public void testEnumValuesComparation() throws Exception {
    SingleAxisCoordinateSet coordinates1 = 
      new SingleAxisCoordinateSet(Entree.STEAK, Entree.CHICKEN);
    SingleAxisCoordinateSet coordinates2 = 
      new SingleAxisCoordinateSet(Entree.CHICKEN, Entree.STEAK);
    SingleAxisCoordinateSet coordinates3 = 
      new SingleAxisCoordinateSet(Entree.CHICKEN);
    SingleAxisCoordinateSet coordinates4 = 
      new SingleAxisCoordinateSet(Bread.WHEAT);
    assertEquals(0, 
        TruthTableComparators.FOR_SINGLE_AXIS_COORDINATES_SET
          .compare(coordinates1, coordinates2));
    Set<SingleAxisCoordinateSet> a = 
      new TreeSet<SingleAxisCoordinateSet>(
          TruthTableComparators.FOR_SINGLE_AXIS_COORDINATES_SET);
    assertTrue(a.add(coordinates1));
    assertFalse(a.add(coordinates2));
    assertTrue(a.add(coordinates3));
    assertTrue(a.add(coordinates4));
  }
}
