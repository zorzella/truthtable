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

import java.util.Collection;
import java.util.Comparator;

/** 
 * 
 * An AffinityGroup represents a collection of Affinities for all combinations
 * between axes coordinates. E.g. AffinityGroup
 * of [[Bread.WHITE,Bread.WHEAT],[Entree.PASTA,Entree.SUSHI],[Wine.CHIANTI]], is the 
 * conceptual equivalent of the following four Affinities:
 * 
 * [Bread.WHITE, Entree.PASTA, Wine.CHIANTI]
 * [Bread.WHITE, Entree.SUSHI, Wine.CHIANTI]
 * [Bread.WHEAT, Entree.PASTA, Wine.CHIANTI]
 * [Bread.WHEAT, Entree.SUSHI, Wine.CHIANTI]
 * 
 * @author zorzella@google.com
 */
public class AffinityGroup extends CoordinatesBundle {

  static AffinityGroup forCoordinates(
      Comparator<SingleAxisCoordinateSet> sacsComparator,
      Collection<SingleAxisCoordinateSet> coordinates) {
    return new AffinityGroup(sacsComparator, coordinates);
  }

  protected AffinityGroup(
      Comparator<SingleAxisCoordinateSet> sacsComparator,
      Collection<SingleAxisCoordinateSet> coordinates) {
    super(sacsComparator, coordinates);
    if (coordinates.size() < 2) {
      throw new IllegalStateException(
        "An AffinityGroup must touch at least two axes");
    }
  }
}
