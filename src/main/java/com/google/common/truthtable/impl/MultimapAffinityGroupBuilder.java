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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * A {@link AffinityGroupsBuilder} that leverages a {@link Multimap}. E.g.:
 * 
 * <verbatim>
 *     WINES_TO_ENTREES_MULTIMAP = new ImmutableMultimap.Builder<Wine,Entree>()
 *      .put(Wine.CHIANTI, Entree.PASTA)
 *      .put(Wine.CHIANTI, Entree.STEAK)
 *      .put(Wine.PORT, Entree.STEAK)
 *      .put(Wine.MERLOT, Entree.STEAK)
 *      .build();
 * 
 *     MultimapCombinationBuilder builder = new MultimapCombinationBuilder();
 *     builder.forMultimap(WINES_TO_ENTREES_MULTIMAP);
 *     builder.touching(Bread.WHEAT, Bread.WHITE);
 *     Set<AffinityGroup> affinityGroups = builder.create();
 * </verbatim>
 * 
 * This will create three affinity groups: 
 * 
 * 1) [[CHIANTI], [PASTA,STEAK], [WHEAT,WHITE]]
 * 2) [[PORT], [STEAK], [WHEAT,WHITE]]
 * 3) [[MERLOT], [STEAK], [WHEAT,WHITE]]
 * 
 * This very example can be found at {@link MultimapAffinityGroupBuilderTest#testSimpleScenario()}
 * 
 * See the doc at the {@link #create()} method for more information.  
 * 
 * See {@link SimpleAffinityGroupBuilder} for a simpler builder. In fact, if
 * {@link #forMultimap(Multimap)} is never called, this class behaves just like
 * a {@link SimpleAffinityGroupBuilder}.
 * 
 * @author zorzella@google.com
 */
public class MultimapAffinityGroupBuilder 
    extends AffinityGroupsBuilder<MultimapAffinityGroupBuilder> {

  private final Comparator<CoordinatesBundle> coordinatesBundleComparator;

  private Multimap<? extends Enum<?>, ? extends Enum<?>> multimap;

  public MultimapAffinityGroupBuilder() {
    this(TruthTableComparators.FOR_SINGLE_AXIS_COORDINATES_SET, 
        TruthTableComparators.FOR_COORDINATES_BUNDLE);
  }
      
  public MultimapAffinityGroupBuilder(
      Comparator<SingleAxisCoordinateSet> sacsComparator,
      Comparator<CoordinatesBundle> coordinatesBundleComparator) {
    super(sacsComparator);
    this.coordinatesBundleComparator = coordinatesBundleComparator;
  }
  
  /**
   * Sets the Multimap to be expanded by the {@link #create()} method.
   */
  public MultimapAffinityGroupBuilder forMultimap(
      Multimap<? extends Enum<?>, ? extends Enum<?>> multimapToExpand) {
    Preconditions.checkState(this.multimap == null);
    Preconditions.checkNotNull(multimapToExpand);
    this.multimap = multimapToExpand;
    return this;
  }
  
  /**
   * Creates a set of {@link AffinityGroup}. It will return an
   * {@link AffinityGroup}, for each key in the multimap, that touches that key 
   * and all its values. If no multimap is given (i.e.
   * {@link #forMultimap(Multimap)} is not called), it behaves just like
   * {@link SimpleAffinityGroupBuilder#create()}.
   */
  @SuppressWarnings({"unchecked", "cast", "rawtypes"})
  @Override
  public Set<AffinityGroup> create() {
    Set<AffinityGroup> result = 
      new TreeSet<AffinityGroup>(coordinatesBundleComparator);

    if (multimap == null) {
      return ImmutableSet.of(AffinityGroup.forCoordinates(
          sacsComparator, this.sacses));
    }

    for (Enum<?> keys : multimap.keySet()) {
      Set<SingleAxisCoordinateSet> sacses = 
        new TreeSet<SingleAxisCoordinateSet>(sacsComparator);
      sacses.addAll(this.sacses);
      sacses.add(new SingleAxisCoordinateSet(keys));

      // TODO(kevinb): my god, there must be a better way
      sacses.add(new SingleAxisCoordinateSet((Collection<Enum<?>>)
                             ((Multimap) multimap).get(keys)));
      result.add(new AffinityGroup(sacsComparator, sacses));
    }
    return Collections.unmodifiableSet(result);
  }
}
