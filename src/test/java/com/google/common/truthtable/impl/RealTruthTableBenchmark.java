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

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import com.google.common.collect.Sets;
import com.google.common.truthtable.TruthTable;
import com.google.common.truthtable.testing.BogusTruthTableTestingEnums.Bread;
import com.google.common.truthtable.testing.BogusTruthTableTestingEnums.Cuttlery;
import com.google.common.truthtable.testing.BogusTruthTableTestingEnums.Dessert;
import com.google.common.truthtable.testing.BogusTruthTableTestingEnums.Entree;
import com.google.common.truthtable.testing.BogusTruthTableTestingEnums.MealTime;
import com.google.common.truthtable.testing.BogusTruthTableTestingEnums.Wine;

import java.util.Set;

/**
 * Benchmarks for the {@link RealTruthTable}.
 *
 * <p>The tests present here will often parallel tests in
 * {@link RealTruthTableTest} -- eliminating the assertions (so the benchmark
 * does not time asserting) and focusing on a particular part of the algo (e.g.
 * querying, creating the {@link RealTruthTable} instance, etc).
 *
 * <p>To avoid the risk of having the benchmark code optimized out of existence,
 * the methods will return values derived from the computed results as a matter
 * of policy.
 *
 * <p>Results posted to
 * https://microbenchmarks.prom.corp.google.com/run/zorzella@google.com/com.google.common.truthtable.impl.RealTruthTableBenchmark
 *
 * @author zorzella@google.com
 */
public class RealTruthTableBenchmark extends SimpleBenchmark {

  /**
   * A bogus instance, used to avoid code being optimized out of existence.
   */
  private static final Object BOGUS = "bogus";
  
  // About 23 us (microseconds) per run as of 2011 Oct 28
  public boolean timeCreatingASmallTruthTable(int reps) {
    for (int i = 0; i < reps; i++) {
      TruthTable truthTable = buildSmallTruthTableToBenchmark();
      if (BOGUS == truthTable) {
        return true;
      }
    }
    return false;
  }

  // About 10 us (microseconds) per run as of 2011 Oct 28
  public int timeQueryingMixingCombinationsOnSmall(int reps) {
    int result = 0;

    // This benchmark will focus on querying, so we build the truthTable outside
    // the loop. I could also do it in a "setUp" method, but I dislike that
    // pattern.
    TruthTable truthTable = buildSmallTruthTableToBenchmark();

    for (int i = 0; i < reps; i++) {
      result += doTimeQueryingMixingCombinations(truthTable);
    }
    return result;
  }
  
  // About 190 us (microseconds) per run as of 2011 Oct 28
  public boolean timeCreatingALargeTruthTable(int reps) {
    for (int i = 0; i < reps; i++) {
      TruthTable truthTable = buildTruthTableToBenchmark();
      if (BOGUS == truthTable) {
        return true;
      }
    }
    return false;
  }
  
  // About 300 us (microseconds) per run as of 2011 Oct 28
  public int timeQueryingMixingCombinationsOnLarge(int reps) {
    int result = 0;

    // This benchmark will focus on querying, so we build the truthTable outside
    // the loop. I could also do it in a "setUp" method, but I dislike that
    // pattern.
    TruthTable truthTable = buildTruthTableToBenchmark();

    for (int i = 0; i < reps; i++) {
      result += doTimeQueryingMixingCombinations(truthTable);
    }
    return result;
  }

  public int doTimeQueryingMixingCombinations(TruthTable truthTable) {

    Set<Bread> breads0 = truthTable.getAll(Bread.class,
        new FixedCoordinates(Wine.PORT));

    Set<Entree> entrees = truthTable.getAll(Entree.class,
        new FixedCoordinates (Wine.PORT));

    Set<Bread> breads1 = truthTable.getAll(Bread.class,
        new FixedCoordinates(Wine.CHIANTI));

    return breads0.size() + entrees.size() + breads1.size();
  }

  private TruthTable buildTruthTableToBenchmark() {
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
        .touching(
            Wine.PORT,
            Wine.ZINFANDEL,
            Wine.CHIANTI)
        .touching(
            Entree.STEAK,
            Entree.EGGPLANT,
            Entree.PASTA,
            Entree.SUSHI,
            Entree.CHICKEN)
        .touching(Dessert.CAKE)
        .touching(MealTime.DINNER)
        .create());

    builder.addAffinityGroups(
        new SimpleAffinityGroupBuilder()
        .touching(Wine.PORT)
        .touching(Bread.PITA)
        .create());
        
    builder.addAffinityGroups(
        new SimpleAffinityGroupBuilder()
        .touching(Wine.ZINFANDEL)
        .touching(Bread.WHEAT)
        .create());
        
    builder.addAffinityGroups(
        new SimpleAffinityGroupBuilder()
        .touching(Wine.MERLOT)
        .touching(Dessert.BANANA_SPLIT)
        .create());

    builder.addAffinityGroups(
        new SimpleAffinityGroupBuilder()
        .touching(Wine.CHIANTI)
        .touching(Dessert.CHERRY_PIE)
        .create());
        
    builder.addAffinityGroups(
        new SimpleAffinityGroupBuilder()
        .touching(Wine.CHIANTI)
        .touching(Entree.CHICKEN)
        .touching(Bread.WHEAT)
        .create());

    builder.addAffinityGroups(
        new SimpleAffinityGroupBuilder()
        .touching(MealTime.DINNER)
        .touching(Cuttlery.SILVER)
        .create());

    builder.addAffinityGroups(
        new SimpleAffinityGroupBuilder()
        .touching(Wine.PORT)
        .touching(allDeserts())
        .create());
    
    return builder.create();
  }

  private static Set<Dessert> allDeserts() {
    Set<Dessert> result = Sets.newHashSet(Dessert.values());
    return result;
  }

  private TruthTable buildSmallTruthTableToBenchmark() {
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

    return builder.create();
  }

  public static void main(String[] args) {
    Runner.main(RealTruthTableBenchmark.class, args);
  }
}
