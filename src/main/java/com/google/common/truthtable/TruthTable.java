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
package com.google.common.truthtable;

import com.google.common.truthtable.impl.FixedCoordinates;

import java.util.Set;

/**
 * A TruthTable represents a n-dimensional array of booleans.
 * 
 * <p>See the {@link com.google.common.truthtable package documentation} for 
 * details
 * 
 * @author zorzella@google.com
 */
public interface TruthTable {

  /**
   * TODO: fix this doc!
   * 
   * Find all coordinates in a given {@code axis} that are still valid, given a
   * set of fixed coordinates. 
   */
  <E extends Enum<E>> Set<E> getAll(
      Class<E> axis, FixedCoordinates fixedCoordinates);

  /**
   * Find all the coordinates in a given {@code axis} that are part of a
   * valid path. This is equivalent to calling
   * {@link #getAll(Class, FixedCoordinates)}, for an empty {@link FixedCoordinates}.
   */
  <E extends Enum<E>> Set<E> getAll(Class<E> axis);
}
