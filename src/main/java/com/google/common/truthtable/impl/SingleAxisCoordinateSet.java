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

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A set of coordinates in a single axis, and is sometimes abbreviated to SACS. 
 * 
 * <p>A {@link SingleAxisCoordinateSet} is said to touch certain coordinates,
 * and also to touch the axis of those coordinates.
 * 
 * <p>This is nothing more than a wrapper to an {@link EnumSet} with:<ul>
 * 
 * <li> a custom comparator</li>
 * <li> a guarantee of non-emptiness</li>
 * <li> a {@link #getAxis()}</li>
 * <li> some other convenience methods, such as {@link #touches(EnumSet)}</li>

 * </ul>
 * 
 * @author zorzella@google.com
 */
/*
 * Ordinarily we would not suppress warnings, and never for an entire class.
 * But the nature of this problem makes it impossible to properly generify
 * (we would need to generify a variable number of parameters).
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class SingleAxisCoordinateSet implements Iterable<Enum<?>> {
  
  // TODO(zorzella): make this immutable
  // Intentional raw type because of EnumSet.copyOf in GuruEngineImpl
  final EnumSet backingEnumSet;
  final Class<? extends Enum<?>> axis;
  
  @SuppressWarnings("unchecked")
  public SingleAxisCoordinateSet(Enum<?>... coordinates) {
    this(asList(coordinates));
  }
  
  /**
   * Helper method to transform varargs to Collection
   */
  private static List<Enum<?>> asList(Enum<?>... coordinates) {
    List<Enum<?>> temp = new ArrayList<Enum<?>>();
    for (Enum<?> value : coordinates) {
      temp.add(value);
    }
    return temp;
  }
  
  /**
   * Creates an instance of a {@link SingleAxisCoordinateSet} with all the given 
   * {@code coordinates}. 
   * 
   * @throws IllegalArgumentException if no {@code coordinates} are given, or if 
   *   the same coordinate was given more than once. While this method could 
   *   simply silently de-dupe these, there is no legitimate reason for that to
   *   be allowed, and it's quite likely a typo, so it errs on the side of 
   *   safety.
   *   
   * @throws ClassCastException if coordinates are not all of the same axis
   */
  @SuppressWarnings("unchecked")
  // Intentional raw type for EnumSet.copyOf
  public SingleAxisCoordinateSet(Collection<? extends Enum> coordinates) {
    if (coordinates.isEmpty()) {
      throw new IllegalArgumentException(
        "A SingleAxisCoordinateSet must have at least one coordinate.");
    }
    Enum<?> firstDPoint = coordinates.iterator().next();
    axis = firstDPoint.getDeclaringClass();
    // TODO(zorzella): profile this with immutable set
    this.backingEnumSet = EnumSet.copyOf(coordinates);
    if (backingEnumSet.size() != coordinates.size()) {
      throw new IllegalArgumentException(String.format(
      "A coordinate in '%s' was present more than once.", coordinates));
    }
  }

  // TODO(zorzella): decide if we want to nuke this
  /**
   * Returns the EnumSet with all the DPoints
   */
  public Set<? extends Enum<?>> getEnumSet() {
    return backingEnumSet;
  }

  // TODO(zorzella): decide if we want to nuke this (or make package protected)
  /**
   * Returns the axis touched.
   */
  public Class<? extends Enum<?>> getAxis() {
    return axis;
  }
  
  /**
   * Returns true if we touch any DPoint in a given EnumSet
   */
  public boolean touches(EnumSet<?> that) {
    //TODO: profile with Collections.disjoint
    for (Enum<?> dPoint: that) {
      if (backingEnumSet.contains(dPoint)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return backingEnumSet.toString();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Iterator<Enum<?>> iterator() {
    return backingEnumSet.iterator();
  }
  
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof SingleAxisCoordinateSet)) {
      return false;
    }
    return backingEnumSet.equals(((SingleAxisCoordinateSet)obj).backingEnumSet);
  }
  
  @Override
  public int hashCode() {
    return backingEnumSet.hashCode();
  }
}
