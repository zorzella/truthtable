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

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.truthtable.testing.BogusTruthTableTestingEnums.Bread;
import com.google.common.truthtable.testing.BogusTruthTableTestingEnums.Entree;
import com.google.common.truthtable.testing.BogusTruthTableTestingEnums.Wine;

import junit.framework.TestCase;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author zorzella@google.com
 */
public class MultimapAffinityGroupBuilderTest extends TestCase {

  private static final Multimap<Wine, Entree> 
    WINES_TO_ENTREES_MULTIMAP = new ImmutableMultimap.Builder<Wine,Entree>()
      .put(Wine.CHIANTI, Entree.PASTA)
      .put(Wine.CHIANTI, Entree.STEAK)
      .put(Wine.PORT, Entree.STEAK)
      .put(Wine.MERLOT, Entree.STEAK)
      .build();

  /**
   * Tests a simple scenario: each key in WINES_TO_ENTREES_MULTIMAP will beget
   * an AffinityGroup with that key, its values and the two Breads below.
   */
  public void testSimpleScenario() throws Exception {
    MultimapAffinityGroupBuilder builder = new MultimapAffinityGroupBuilder();
    builder.forMultimap(WINES_TO_ENTREES_MULTIMAP);
    builder.touching(Bread.WHEAT, Bread.WHITE);
    Set<AffinityGroup> affinityGroups = builder.create();
    assertEquals(3, affinityGroups.size());
    
    Set<AffinityGroup> expected = 
      new TreeSet<AffinityGroup>(TruthTableComparators.FOR_COORDINATES_BUNDLE);

    expected.addAll(
      new MultimapAffinityGroupBuilder()
      .touching(Bread.WHEAT, Bread.WHITE)
      .touching(Wine.CHIANTI)
      .touching(Entree.PASTA, Entree.STEAK)
      .create());
    
    expected.addAll(
        new MultimapAffinityGroupBuilder()
        .touching(Bread.WHEAT, Bread.WHITE)
        .touching(Wine.PORT)
        .touching(Entree.STEAK)
        .create());
    
    expected.addAll(
        new MultimapAffinityGroupBuilder()
        .touching(Bread.WHEAT, Bread.WHITE)
        .touching(Wine.MERLOT)
        .touching(Entree.STEAK)
        .create());
        
    assertEquals(expected, affinityGroups);
    
  }

  public void testDoesNotRequireCallToSetMultimap() throws Exception {
    new MultimapAffinityGroupBuilder()
      .touching(Wine.MERLOT)
      .touching(Bread.PITA)
      .create();
  }
}
