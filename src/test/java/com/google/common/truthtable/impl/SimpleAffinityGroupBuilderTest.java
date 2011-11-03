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
import com.google.common.truthtable.testing.BogusTruthTableTestingEnums.Wine;

import junit.framework.TestCase;

import java.util.Set;

/**
 * Tests the SimpleCombinationBuilder
 *
 * @author zorzella@google.com
 */
public class SimpleAffinityGroupBuilderTest extends TestCase {

  public void testSimpleScenario() throws Exception {
    Set<AffinityGroup> affinityGroups = 
      new SimpleAffinityGroupBuilder()
        .touching(Bread.WHITE, Bread.OAT)
        .touching(Wine.PORT)
        .create();
    assertEquals(1, affinityGroups.size());
    AffinityGroup loneAffinityGroup = affinityGroups.iterator().next();
    assertTrue(loneAffinityGroup.touches(Bread.WHITE));
    assertTrue(loneAffinityGroup.touches(Bread.OAT));
    assertFalse(loneAffinityGroup.touches(Bread.WHEAT));
    assertTrue(loneAffinityGroup.touches(Wine.PORT));
  }
  
  public void testBuildingACombinationBetweenTwoDataPointsOnSameEnumFails() 
      throws Exception {
    try {
      new SimpleAffinityGroupBuilder()
      .touching(Bread.WHITE)
      .touching(Bread.PITA)
      .create().iterator().next();
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }
}
