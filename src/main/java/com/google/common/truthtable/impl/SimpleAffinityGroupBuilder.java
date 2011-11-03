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

import com.google.common.collect.ImmutableSet;

import java.util.Comparator;
import java.util.Set;

/**
 * Simple, canonical way to build an {@link AffinityGroup}. Despite the general
 * signature of {@link #create()} returning a collection, it is expected that
 * this builder return a single element in it. E.g. the following code creates
 * a single AffinityGroup [[WHITE,OAT],[PORT]]:
 * 
 * <pre>
 *     Set<AffinityGroup> affinityGroups = 
 *      new SimpleCombinationBuilder()
 *        .touching(Bread.WHITE,Bread.OAT)
 *        .touching(Wine.PORT)
 *        .create();
 * </pre>
 *
 * See this example at {@link SimpleAffinityGroupBuilderTest#testSimpleScenario()} 
 * 
 * @author zorzella@google.com
 */
public class SimpleAffinityGroupBuilder
    extends AffinityGroupsBuilder<SimpleAffinityGroupBuilder> {

  public SimpleAffinityGroupBuilder() {
    this(TruthTableComparators.FOR_SINGLE_AXIS_COORDINATES_SET);
  }
  
  public SimpleAffinityGroupBuilder(
      Comparator<SingleAxisCoordinateSet> sacsComparator) {
    super(sacsComparator);
  }

  @Override
  public Set<AffinityGroup> create() {
    return ImmutableSet.of(
        AffinityGroup.forCoordinates(sacsComparator, this.sacses));
  }
}
