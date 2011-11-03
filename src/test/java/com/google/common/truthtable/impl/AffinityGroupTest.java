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
import com.google.common.truthtable.testing.BogusTruthTableTestingEnums.Wine;

import junit.framework.TestCase;

/**
 * Tests the {@link AffinityGroupsBuilder}s, and basic properties of 
 * {@link AffinityGroup}s.
 *
 * @author zorzella@google.com
 */
public class AffinityGroupTest extends TestCase {

  public void testMatchingCombinationsCompareToEquals() throws Exception {

    AffinityGroup affinityGroup1 = new SimpleAffinityGroupBuilder()
    .touching(Entree.CHICKEN, Entree.STEAK)
    .touching(Bread.WHITE)
    .create().iterator().next();

    AffinityGroup affinityGroup2 = new SimpleAffinityGroupBuilder()
    .touching(Bread.WHITE)
    .touching(Entree.STEAK, Entree.CHICKEN)
    .create().iterator().next();

    assertEquals(0, 
        TruthTableComparators.FOR_COORDINATES_BUNDLE
          .compare(affinityGroup1, affinityGroup2));
  }

  public void testThatACombinationWithMoreDimensionsComparesAsLess() throws Exception {

    AffinityGroup affinityGroup1 = new SimpleAffinityGroupBuilder()
    .touching(Entree.CHICKEN, Entree.STEAK)
    .touching(Bread.WHITE)
    .touching(Wine.PORT)
    .create().iterator().next();

    AffinityGroup affinityGroup2 = new SimpleAffinityGroupBuilder()
    .touching(Bread.WHITE)
    .touching(Entree.STEAK, Entree.CHICKEN)
    .create().iterator().next();

    assertTrue(
        TruthTableComparators.FOR_COORDINATES_BUNDLE
          .compare(affinityGroup1, affinityGroup2) < 0);
  }

  public void testThatACombinationWithMoreElementsInADimensionsComparesAsLess() throws Exception {

    AffinityGroup affinityGroup1 = new SimpleAffinityGroupBuilder()
    .touching(Entree.CHICKEN, Entree.STEAK, Entree.PASTA)
    .touching(Bread.WHITE)
    .create().iterator().next();

    AffinityGroup affinityGroup2 = new SimpleAffinityGroupBuilder()
    .touching(Bread.WHITE)
    .touching(Entree.STEAK, Entree.CHICKEN)
    .create().iterator().next();

    assertTrue(TruthTableComparators.FOR_COORDINATES_BUNDLE
        .compare(affinityGroup1, affinityGroup2) < 0);
  }

  public void testCombinationInvolves() throws Exception {
    AffinityGroup c = new SimpleAffinityGroupBuilder()
    .touching(Bread.WHITE)
    .touching(Entree.CHICKEN, Entree.STEAK)
    .create().iterator().next();
    assertTrue(c.touches(Entree.STEAK));
    assertFalse(c.touches(Bread.WONDER));
    assertFalse(c.touches(Wine.PORT));
  }

  public void testAffinityGroupMustTouchTwoDimensions() throws Exception {
    try {
      new SimpleAffinityGroupBuilder().create();
      fail();
    } catch (IllegalStateException expected) {
    }
  }
}
