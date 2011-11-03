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
/**
 * A compact representation of an n-dimensional truth table, which is populated
 * by declaring affinities, and supports arbitrary queries.
 * 
 * <p>For example, say we need to set up a fancy dinner. Our chef tell us that 
 * Port wine goes well with Oat Bread. And that an entree of pasta goes well 
 * with Zinfandel wine, and so forth.  These statements are called affinities. 
 * We can populate a truth table with our chef's list of affinities and then 
 * ask it such questions as:
 * 
 * <ul>
 *  <li>If I want to serve Zinfandel, which main courses can I serve? Or
 *  <li>If I want to serve either Zinfandel or Port but I also want to serve 
 *     pasta, which breads can I serve?
 * </ul>
 * 
 * <p>This truth table handles affinities between elements of <em>different</em>
 * enum types, called dimensions or axes. In this example, the axes are 
 * Bread, Entree, Wine and Dessert.  Affinities between elements of the same
 * type are not supported, so it cannot, for example, help you match the colors 
 * of garments you might wear, like "if brown goes 
 * well with black and green goes well with purple, which colors go well with 
 * pink?" 
 * 
 * Back to our example, imagine the following enums:
 * 
 * <ul>
 *  <li>{@code enum Bread {WHITE, WHEAT, OAT}}
 *  <li>{@code enum Entree {PASTA, CHICKEN, SUSHI}}
 *  <li>{@code enum Wine {ZINFANDEL, CHIANTI, MERLOT, PORT}}
 *  <li>{@code enum Dessert {GRAPE, CAKE}}
 * <ul>
 * 
 * <p>This defines a truth table with 4 axes, each with a few possible 
 * coordinates. 
 * 
 * <p>As a taste of what it looks like to use this library, here's a piece of
 * code taken from {@link RealTruthTableTest}.
 * 
 * <pre>
 *   public void testQuerying() throws Exception {
 *    TruthTableBuilder builder = new TruthTableBuilder();
 *    builder.forAxes(Bread.class, Entree.class);
 *
 *    builder.addAffinityGroups(
 *      new SimpleAffinityGroupBuilder()
 *      .touching(Bread.WHITE)
 *      .touching(Entree.CHICKEN, Entree.STEAK)
 *      .create());
 *
 *    builder.addAffinityGroups( 
 *      new SimpleAffinityGroupBuilder()
 *      .touching(Bread.WHEAT)
 *      .touching(Entree.CHICKEN, Entree.SUSHI)
 *      .create());
 *
 *    TruthTable truthTable = builder.create();
 *
 *    Set<Bread> breads = truthTable.getAll(Bread.class);
 *    assertTrue(breads.contains(Bread.WHEAT));
 *    assertFalse(breads.contains(Bread.OAT));
 *  }
 * </pre>
 *  
 * <p>A set of these coordinates that is singular (has only one coordinate per 
 * axis) and complete (had one coordinate for each axis)  
 * identifies a single cell in this truth table, which will be either "true" or 
 * "false". E.g. 
 * [[Bread.WHITE], [Entree.PASTA], [Wine.CHIANTI], [Dessert.GRAPE]]
 * is a single cell.
 *
 * <p>But instead of expecting the users of this library to provide each cell's
 * value, those valued are derived from affinities, as explained below.
 * 
 * <p>Note that even though an affinity the unit of thought for 
 * understanding this library, and thus, used throughout this documentation, 
 * it is itself too fine-grained. In reality, individual affinities are derived 
 * from Affinity Groups, and these are often created through builders.
 *  
 * <p>An affinity is basically a -- most often incomplete -- singular set of 
 * coordinates, and it spans at least two dimensions. For example, 
 * [[Bread.WHITE], [Entree.PASTA], [Wine.CHIANTI]] specifies an affinity. This
 * is said to touch the axes Bread, Entree and Wine. It is also said to touch
 * the coordinates Bread.WHITE, Entree.PASTA and Wine.CHIANTI.
 * 
 * <p>On the other hand 
 * [[Bread.WHITE, Bread.OAT], [Entree.PASTA], [Wine.CHIANTI]] 
 * is <em>not</em> an affinity, for it touches two coordinates in the Bread 
 * axis.
 * 
 * <p>Also, populating a truth table with the the affinity 
 * [[Bread.WHITE], [Entree.PASTA], [Wine.CHIANTI]] tells it that CHIANTI 
 * can be served when both WHITE <em>and</em> PASTA are served, but does not 
 * tell us whether CHIANTI can be served when OAT and PASTA are served (that 
 * may be a valid meal, but by a different affinity). 
 * 
 * <p>Lastly, an affinity is rarely self-sufficient. Unless an
 * affinity touches all dimensions (thus being a single cell in the truth 
 * table), the truth table needs other affinities before it can decide which 
 * cells are "true". In other words, if the only affinity given to the truth 
 * table was [[Bread.WHITE], [Entree.PASTA], Wine.CHIANTI]], 
 * it would not be able to help you plan the complete meal, for it knows nothing 
 * about which desserts can be served. This truth table is said to be in an 
 * incomplete state, and is unable to answer any queries. 
 * 
 * <p>So we've seen that an affinity that spans all axis defines a complete, 
 * singular set of coordinates -- thus defining a single cell, and being 
 * "self-sufficient" to define that cell as "true". E.g. a truth table populated
 * with the single affinity 
 * [[Bread.WHITE], [Entree.PASTA], [Wine.CHIANTI], [Dessert.GRAPE]] enables the
 * truth table to set a complete dinner, though, in this case, only one dinner.
 * 
 * <p>Another scenario that's easy to understand is binary affinities. The same
 * single possible dinner can be defined by populating the truth table with 
 * these three affinities:
 * 
 * <ul>
 *  <li>[[Bread.WHITE], [Entree.PASTA]] 
 *  <li>[[Entree.PASTA], [Wine.CHIANTI]]
 *  <li>[[Wine.CHIANTI], [Dessert.GRAPE]]
 * </ul>
 * 
 * <p>Binary affinities are easy to understand: WHITE Bread <em>always</em> goes
 * well with PASTA Entree, and PASTA <em>always</em> goes well with CHIANTI, etc.
 * 
 * <p>A little trickier to understand are n-way affinities. E.g. the same meal
 * can be defined by these affinities:
 * 
 * <ul>
 *  <li>[[Bread.WHITE], [Entree.PASTA], [Wine.CHIANTI]] 
 *  <li>[[Entree.PASTA], [Wine.CHIANTI], [Dessert.GRAPE]]
 * </ul>
 * 
 * <p>Since WHITE can be served so long as both PASTA 
 * and CHIANTI are served, and GRAPE can be served so long as both PASTA and
 * CHIANTI are served, these four things can be served together.
 * 
 * <h1>More formally</h1> 
 * 
 * <p>Now that we've documented the behavior of this truth table informally, and
 * by example, let's also be formal. 
 * 
 * <p>A truth table engine is a class that can be instantiated and populated, 
 * subsequently allowing for querying.
 * 
 * <p>An empty truth table instance is an instance of the truth table engine 
 * that knows its dimensions, but has not yet been populated with affinities. 
 * At this point, it the n-dimensionality of the truth table is known.
 * 
 * <p>A populated truth table instance is an instance of the truth table engine
 * that knows its dimensions and also has been populated with affinities.
 * 
 * <p>Each dimension in a truth table instance is an axis. This corresponds to 
 * a java enum.
 * 
 * <p>Each value of each enum registered in a truth table instance is a 
 * coordinate in that axis.
 * 
 * <p>A set of coordinates is said to touch each dimension and each coordinate
 * contained in that set.
 * 
 * <p>A singular set of coordinates touches no more than one 
 * coordinate per axis.
 * 
 * <p>A complete set of coordinates touches all dimensions.
 * 
 * <p>An affinity is an (often incomplete) singular set of coordinates that 
 * spans at least two axes.
 * 
 * <p>Two affinities A and B are said to be compatible if, for all the axes 
 * that both A and B touch, they touch the same coordinates.
 * 
 * <p>Two affinities C and D are said to be orthogonal if C and D touch no axes 
 * in common.  Note that orthogonal affinities are by definition compatible.
 *
 * <p>Two affinities E and F are said to be overlapping if they are compatible
 * and non-orthogonal. In other words, they share all coordinates for each 
 * dimension they both touch. These two dimensions in a set form a set of 
 * overlapping affinities.
 *  
 * <p>Given a set of overlapping affinities, adding 
 * an affinity to that set that overlaps with any other already in the set 
 * forms another set of affinities that is also overlapping. Note that any 
 * given pair of affinities in such set is compatible -- either for being 
 * overlapping or by being orthogonal.
 *
 * <p>A set of affinities is said to touch the union of all axes and coordinates
 * of its affinities.
 * 
 * <p>A complete set is one that touches all dimensions. Note that a complete, 
 * overlapping set affinities defines a single cell in
 * a truth table (i.e. a complete, singular set of coordinates).
 * 
 * <p>From these definitions, follows that <em>A cell is said to be true if and 
 * only if there exists a complete set 
 * of overlapping affinities that defines it.</em>
 * 
 * E.g.:
 * 
 * <ul>
 *  <li>[Bread(WHITE),Entree(PASTA)] is compatible with 
 *      [Entree(PASTA),Wine(CHIANTI),Dessert(GRAPE)], for, in the only shared 
 *      axis (Entree), they touch the same coordinate Entree.PASTA. They are 
 *      also overlapping there (i.e. non-orthogonal), and together, they are
 *      complete, for they touch all four dimensions. That implies that
 *      that [Bread(WHITE),Entree(PASTA),Wine(CHIANTI),Dessert(GRAPE)] is
 *      "true".
 *  <li>[Bread(WHITE),Entree(PASTA)] is <em>not</em> compatible with 
 *      [Entree(SUSHI),Wine(CHIANTI)] because they don't touch the same 
 *      coordinate in the shared "Entree" axis.
 *  <li>[Bread(WHITE),Entree(PASTA)] is compatible with 
 *      [Wine(CHIANTI),Dessert(GRAPE)]. But, even though they touch all 
 *      dimensions, they are orthogonal, and, thus, do not (by themselves) make
 *      any cell "true".
 * </ul>
 * 
 * Note that all the documentation talks about is how to make cells "true". By
 * design, there is no way to turn a cell "false" (or undo a "true"). In other
 * words, all cells are "false" until the conditions above exist for it to be
 * "true".
 * 
 * <p>TODO(zorzella): list some POEs/main classes/main methods
 * 
 * @author zorzella
 */
package com.google.common.truthtable;
