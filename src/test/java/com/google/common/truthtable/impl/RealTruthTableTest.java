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
import com.google.common.truthtable.testing.BogusTruthTableTestingEnums.Bread;
import com.google.common.truthtable.testing.BogusTruthTableTestingEnums.Cuttlery;
import com.google.common.truthtable.testing.BogusTruthTableTestingEnums.Dessert;
import com.google.common.truthtable.testing.BogusTruthTableTestingEnums.Entree;
import com.google.common.truthtable.testing.BogusTruthTableTestingEnums.MealTime;
import com.google.common.truthtable.testing.BogusTruthTableTestingEnums.Wine;
import com.google.common.truthtable.testing.RiggedComparatorAffinityGroupsBuilderFacade;

import junit.framework.TestCase;

import java.util.EnumSet;
import java.util.Set;

/**
 * @author zorzella@google.com
 */
public class RealTruthTableTest extends TestCase {
  
  public void testTruthTableRequiresRegisteringEnums() throws Exception {
    Set<AffinityGroup> affinityGroups = new SimpleAffinityGroupBuilder()
      .touching(Bread.WHITE)
      .touching(Entree.CHICKEN, Entree.STEAK)
      .create();
    TruthTableBuilder builder = new TruthTableBuilder();
    try {
      builder.addAffinityGroups(affinityGroups);
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testTruthTable() throws Exception {
    Set<AffinityGroup> affinityGroups = new SimpleAffinityGroupBuilder()
      .touching(Bread.WHITE)
      .touching(Entree.CHICKEN, Entree.STEAK)
      .create();
    TruthTableBuilder builder = new TruthTableBuilder();
    builder.forAxes(Bread.class, Entree.class);
    builder.addAffinityGroups(affinityGroups);

    RealTruthTable truthTable = (RealTruthTable) builder.create();

    assertEquals(1, truthTable.getAffinityGroupsInvolving(Bread.WHITE).size());
  }

  public void testTruthTablePopulatorCanAddAffinityGroupAfterCreate() throws Exception {
    Set<AffinityGroup> affinityGroups1 = new SimpleAffinityGroupBuilder()
      .touching(Bread.WHITE)
      .touching(Entree.CHICKEN, Entree.STEAK)
      .create();

    Set<AffinityGroup> affinityGroups2 = new SimpleAffinityGroupBuilder()
      .touching(Bread.PITA)
      .touching(Entree.CHICKEN, Entree.STEAK)
      .create();
    TruthTableBuilder builder = new TruthTableBuilder();
    builder.forAxes(Bread.class, Entree.class);
    builder.addAffinityGroups(affinityGroups1);

    builder.create();
    
    builder.addAffinityGroups(affinityGroups2);
  }
  
  public void testTruthTablePopulatorCanCallCreateTwice() throws Exception {
    Set<AffinityGroup> affinityGroups = new SimpleAffinityGroupBuilder()
      .touching(Bread.WHITE)
      .touching(Entree.CHICKEN, Entree.STEAK)
      .create();
    TruthTableBuilder builder = new TruthTableBuilder();
    builder.forAxes(Bread.class, Entree.class);
    builder.addAffinityGroups(affinityGroups);

    builder.create();
    builder.create();
  }

  public void testAddingSameCombinationTwiceFails() throws Exception {
    TruthTableBuilder builder = new TruthTableBuilder();
    builder.forAxes(Bread.class, Entree.class);

    builder.addAffinityGroups(
      new SimpleAffinityGroupBuilder()
      .touching(Bread.WHITE)
      .touching(Entree.CHICKEN, Entree.STEAK)
      .create());

    try {
      builder.addAffinityGroups(      
        new SimpleAffinityGroupBuilder()
        .touching(Bread.WHITE)
        .touching(Entree.CHICKEN, Entree.STEAK)
        .create());
      fail();
    } catch (IllegalArgumentException expected) {
    }

    RealTruthTable truthTable = (RealTruthTable) builder.create();

    assertEquals(1, truthTable.getAffinityGroupsInvolving(Bread.WHITE).size());
  }

  public void testMultipleCombinations() throws Exception {
    TruthTableBuilder builder = new TruthTableBuilder();
    builder.forAxes(Bread.class, Entree.class);

    builder.addAffinityGroups(
      new SimpleAffinityGroupBuilder()
      .touching(Bread.WHITE)
      .touching(Entree.CHICKEN, Entree.STEAK)
      .create());

    builder.addAffinityGroups(      
      new SimpleAffinityGroupBuilder()
      .touching(Bread.WHEAT)
      .touching(Entree.CHICKEN, Entree.STEAK)
      .create());

    builder.create();
  }

  public void testQueryingCantQueryClassTypesNotRegistered() throws Exception {
    TruthTableBuilder builder = new TruthTableBuilder();
    builder.forAxes(Bread.class, Entree.class);

    builder.addAffinityGroups(
      new SimpleAffinityGroupBuilder()
      .touching(Bread.WHITE)
      .touching(Entree.CHICKEN, Entree.STEAK)
      .create());

    builder.addAffinityGroups(      
      new SimpleAffinityGroupBuilder()
      .touching(Bread.WHEAT)
      .touching(Entree.CHICKEN, Entree.STEAK)
      .create());

    TruthTable truthTable = builder.create();

    try {
      truthTable.getAll(Wine.class);
      fail();
    } catch (IllegalArgumentException expected) {

    }
  }

  public void testQuerying() throws Exception {
    TruthTableBuilder builder = new TruthTableBuilder();
    builder.forAxes(Bread.class, Entree.class);

    builder.addAffinityGroups(
      new SimpleAffinityGroupBuilder()
      .touching(Bread.WHITE)
      .touching(Entree.CHICKEN, Entree.STEAK)
      .create());

    builder.addAffinityGroups(      
      new SimpleAffinityGroupBuilder()
      .touching(Bread.WHEAT)
      .touching(Entree.CHICKEN, Entree.SUSHI)
      .create());

    TruthTable truthTable = builder.create();

    Set<Bread> breads = truthTable.getAll(Bread.class);
    assertTrue(breads.contains(Bread.WHEAT));
    assertFalse(breads.contains(Bread.OAT));
  }

  public void testQueryingRestrictionsMustBeOnDifferentDimensions() throws Exception {
    TruthTableBuilder builder = new TruthTableBuilder();
    builder.forAxes(Bread.class, Entree.class);

    builder.addAffinityGroups(
      new SimpleAffinityGroupBuilder()
      .touching(Bread.WHITE)
      .touching(Entree.CHICKEN, Entree.STEAK)
      .create());

    builder.addAffinityGroups(      
      new SimpleAffinityGroupBuilder()
      .touching(Bread.WHEAT)
      .touching(Entree.CHICKEN, Entree.SUSHI)
      .create());

    TruthTable truthTable = builder.create();

    try {
      Set<Bread> breads = truthTable.getAll(Bread.class, 
        new FixedCoordinates (Entree.STEAK, Entree.CHICKEN));
      fail ();
    } catch (IllegalArgumentException expected) {
    }
  }
  
  public void testQueryingWithFixedDimension() throws Exception {
    TruthTableBuilder builder = new TruthTableBuilder();
    builder.forAxes(Bread.class, Entree.class);

    builder.addAffinityGroups(
      new SimpleAffinityGroupBuilder()
      .touching(Bread.WHITE)
      .touching(Entree.CHICKEN, Entree.STEAK)
      .create());

    builder.addAffinityGroups(      
      new SimpleAffinityGroupBuilder()
      .touching(Bread.WHEAT)
      .touching(Entree.CHICKEN, Entree.SUSHI)
      .create());

    TruthTable truthTable = builder.create();

    Set<Bread> breads = truthTable.getAll(Bread.class, 
      new FixedCoordinates (Entree.STEAK));
    assertTrue(breads.contains(Bread.WHITE));
    assertFalse(breads.contains(Bread.WHEAT));
  }

  /**
   * This test catches an flaw that existed in the algorithm, where it would
   * give up as soon as it found a complete path (see 
   * {@link RealTruthTable#getValidCoordinatesFor(Class, FixedCoordinates)}. To 
   * completely isolate this flaw, we use a custom comparator that makes 
   * vc1 < vc2 < vc3. This is what that triggers:
   * 
   * <ul>
   * <li>vc1 is chosen as a starting point
   * <li>vc2 is chosen as a NextHop, and a complete path was found. Because vc2
   *     touches BogusRegionCode, and does not touch BogusRegionCode.BR, that 
   *     point is not part of this path.
   * <li>algorithm must, at this point, accept that path as a solution, but try
   *     to find other paths that being with vc1. This is where it used to be
   *     flawed. Continuing from after the first step, vc3 is also a valid 
   *     NextHop, and, because it does not touch BogusRegionCode, it implicitly 
   *     matches both BogusRegionCode.AD and BogusRegionCode.BR.
   * </ul>
   */
  public void testExhaustiveSearchIsRequired() throws Exception {
    TruthTableBuilder builder = new TruthTableBuilder();
    builder.forAxes(Bread.class, Entree.class, Wine.class);
    
    RiggedComparatorAffinityGroupsBuilderFacade riggedAffinityGroupBuilder = 
      new RiggedComparatorAffinityGroupsBuilderFacade();
    
    AffinityGroup affinityGroup1 = riggedAffinityGroupBuilder
        .touching(
            Bread.WHITE, // This matches vc2 and
            Bread.WHEAT) // this implicitly matches vc3
        .touching(
            Entree.CHICKEN, // This matches vc2 and
            Entree.STEAK) // this matches vc3
        .createAndResetBuilder();
    
    AffinityGroup affinityGroup2 = riggedAffinityGroupBuilder
        .touching(Bread.WHITE)
        .touching(Entree.CHICKEN)
        .touching(Wine.CHIANTI)
        .createAndResetBuilder();
    
    AffinityGroup affinityGroup3 = riggedAffinityGroupBuilder
        .touching(Entree.STEAK)
        .touching(Wine.MERLOT)
        .createAndResetBuilder();
    
    TruthTable engine = builder
      .addAffinityGroups(affinityGroup1, affinityGroup2, affinityGroup3)
      .create(riggedAffinityGroupBuilder.sacsComparator,
          riggedAffinityGroupBuilder.coordinatesBundleComparator);
    
    Set<Bread> results = engine.getAll(Bread.class);
    // This is mere sanity check
    assertTrue(results.contains(Bread.WHITE));
    // This is where the rubber meets the road
    assertTrue(results.contains(Bread.WHEAT));
    
  }

  public void testSeedingNeedsToReachAllDimensions() throws Exception {
    TruthTableBuilder builder = new TruthTableBuilder();
    builder.forAxes(Wine.class, Bread.class, Entree.class);

    builder.addAffinityGroups(
      new SimpleAffinityGroupBuilder()
      .touching(Wine.PORT)
      .touching(Entree.CHICKEN)
      .create());

    try {
      builder.create();
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }
  
  public void testSimpleNextHop() throws Exception {
    TruthTableBuilder builder = new TruthTableBuilder();
    builder.forAxes(Wine.class, Bread.class, Entree.class);

    Set<AffinityGroup> affinityGroups1 = new SimpleAffinityGroupBuilder()
          .touching(Wine.PORT)
          .touching(Entree.CHICKEN, Entree.STEAK)
          .create();
    
    builder.addAffinityGroups(affinityGroups1);

    Set<AffinityGroup> affinityGroups2 = new SimpleAffinityGroupBuilder()
          .touching(Bread.PITA)
          .touching(Entree.STEAK)
          .create();
    
    builder.addAffinityGroups(affinityGroups2);

    RealTruthTable truthTable = (RealTruthTable) builder.create();
    
    // There will only be one combination in each set
    assertEquals(1, affinityGroups1.size());
    assertEquals(1, affinityGroups2.size());
    
    AffinityGroup affinityGroup1 = affinityGroups1.iterator().next();
    AffinityGroup affinityGroup2 = affinityGroups2.iterator().next();
    
    assertTrue (truthTable.targetsAreNextHopsFor(affinityGroup1, affinityGroup2));
    assertTrue (truthTable.targetsAreNextHopsFor(affinityGroup2, affinityGroup1));
  }

  
  public void testQueryingWithTransitiveValidity() throws Exception {
    TruthTableBuilder builder = new TruthTableBuilder();
    builder.forAxes(Wine.class, Bread.class, Entree.class);

    builder.addAffinityGroups(
      new SimpleAffinityGroupBuilder()
      .touching(Wine.PORT)
      .touching(Entree.CHICKEN, Entree.STEAK)
      .create());

    builder.addAffinityGroups(
      new SimpleAffinityGroupBuilder()
      .touching(Bread.PITA)
      .touching(Entree.STEAK)
      .create());

    TruthTable truthTable = builder.create();

    Set<Bread> breads = truthTable.getAll(Bread.class, 
      new FixedCoordinates(Wine.PORT));
    
    assertTrue(breads.contains(Bread.PITA));
    assertFalse(breads.contains(Bread.WHEAT));
  }

  public void testQueryingTripleCombinations() throws Exception {
    TruthTableBuilder builder = new TruthTableBuilder();
    builder.forAxes(Wine.class, Bread.class, Entree.class);

    builder.addAffinityGroups(
      new SimpleAffinityGroupBuilder()
      .touching(Wine.PORT)
      .touching(Bread.PITA)
      .touching(Entree.STEAK)
      .create());

    builder.addAffinityGroups(      
      new SimpleAffinityGroupBuilder()
      .touching(Wine.CHIANTI)
      .touching(Entree.CHICKEN)
      .touching(Bread.WHEAT)
      .create());
    
    TruthTable truthTable = builder.create();

    Set<Bread> breads = truthTable.getAll(Bread.class, 
      new FixedCoordinates(Wine.PORT));
    
    assertTrue(breads.contains(Bread.PITA));
    assertFalse(breads.contains(Bread.WHEAT));

    breads = truthTable.getAll(Bread.class, 
      new FixedCoordinates(Wine.CHIANTI));
    
    assertTrue(breads.contains(Bread.WHEAT));
    assertFalse(breads.contains(Bread.PITA));
  }
  
  public void testQueryingMixingCombinations() throws Exception {
    TruthTableBuilder builder = new TruthTableBuilder();
    builder.forAxes(Wine.class, Bread.class, Entree.class);

    builder.addAffinityGroups(
      new SimpleAffinityGroupBuilder()
      .touching(Wine.PORT)
      .touching(Bread.PITA)
      .touching(Entree.STEAK, Entree.CHICKEN)
      .create());

    builder.addAffinityGroups(      
      new SimpleAffinityGroupBuilder()
      .touching(Wine.CHIANTI)
      .touching(Entree.CHICKEN)
      .touching(Bread.WHEAT)
      .create());
    
    TruthTable truthTable = builder.create();

    Set<Bread> breads = truthTable.getAll(Bread.class, 
      new FixedCoordinates(Wine.PORT));
    
    assertTrue(breads.contains(Bread.PITA));
    assertFalse(breads.contains(Bread.WHEAT));

    Set<Entree> entrees = truthTable.getAll(Entree.class, 
      new FixedCoordinates (Wine.PORT));
    
    assertTrue(entrees.contains(Entree.STEAK));
    assertTrue(entrees.contains(Entree.CHICKEN));

    breads = truthTable.getAll(Bread.class, 
      new FixedCoordinates(Wine.CHIANTI));
    
    assertTrue(breads.contains(Bread.WHEAT));
    assertFalse(breads.contains(Bread.PITA));
  }

  /**
   * Tests that non-linear paths are found. To find "PITA" here one has to 
   * use all four affinity groups, and the path they form is non-linear.
   */
  public void testStarConfiguration() throws Exception {
    TruthTableBuilder builder = new TruthTableBuilder();
    builder.forAxes(
        Wine.class, 
        Bread.class, 
        Entree.class, 
        Dessert.class,
        MealTime.class, 
        Cuttlery.class);
    
    builder.addAffinityGroups(
        new SimpleAffinityGroupBuilder()
        .touching(Wine.PORT)
        .touching(Bread.PITA)
        .touching(MealTime.DINNER)
        .create());

    builder.addAffinityGroups(      
      new SimpleAffinityGroupBuilder()
      .touching(Wine.PORT)
      .touching(Entree.CHICKEN)
      .create());

    builder.addAffinityGroups(      
      new SimpleAffinityGroupBuilder()
      .touching(Bread.PITA)
      .touching(Dessert.CAKE)
      .create());
    
    builder.addAffinityGroups(      
      new SimpleAffinityGroupBuilder()
      .touching(MealTime.DINNER)
      .touching(Cuttlery.SILVER)
      .create());
      
    TruthTable truthTable = builder.create();

    Set<Bread> breads = truthTable.getAll(Bread.class, 
        new FixedCoordinates(Wine.PORT));

    assertEquals(EnumSet.of(Bread.PITA), breads);
  }
}
