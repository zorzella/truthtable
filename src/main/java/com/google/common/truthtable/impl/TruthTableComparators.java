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

import com.google.common.truthtable.impl.RealTruthTable.NextHop;

import java.util.Comparator;
import java.util.Iterator;

public class TruthTableComparators {

  public static final Comparator<SingleAxisCoordinateSet> FOR_SINGLE_AXIS_COORDINATES_SET = 
    new Comparator<SingleAxisCoordinateSet>() {

    /** 
     * Defines an arbitrary, but self-consistent ordering which will only return
     * 0 if the two {@link SingleAxisCoordinateSet}s' internal enum sets are 
     * equal.
     */
    @Override
    @SuppressWarnings({"unchecked", "cast"})
    public int compare(SingleAxisCoordinateSet x, SingleAxisCoordinateSet y) {
      int result = 0;
      String yClassName = y.axis.toString();
      String xClassName = x.axis.toString();

      result = xClassName.compareTo(yClassName);
      
      // Sacses are in different dimensions
      if (result != 0) {
        return result;
      }
      
      // Sacses don't have the same number of coordinates
      result = y.backingEnumSet.size() - x.backingEnumSet.size();
      
      if (result != 0) {
        return result;
      }
      
      Iterator<? extends Enum<?>> yIt = y.backingEnumSet.iterator();

      for (Enum<?> thisEnum : x) {
        // We know these are the same type, but the compiler doesn't, 
        // so we can't easily use compareTo().
        result = thisEnum.ordinal() - yIt.next().ordinal();
        // Some element of "one" is not in "two", or the other way around
        if (result != 0) {
          return result;
        }
      }
      return result;
    }
  };

  public static final Comparator<CoordinatesBundle> FOR_COORDINATES_BUNDLE = 
    forSingleAxisCoordinatesSet(FOR_SINGLE_AXIS_COORDINATES_SET);

  public static final Comparator<CoordinatesBundle> forSingleAxisCoordinatesSet(
        final Comparator<SingleAxisCoordinateSet> sacsComparator) {
    
      return new Comparator<CoordinatesBundle> () {
      
      @Override
      public int compare(CoordinatesBundle x, CoordinatesBundle y) {
        int result = 0;
        result = y.backingSet.size() - x.backingSet.size();
        if (result != 0) {
          return result;
        }
        
        Iterator<SingleAxisCoordinateSet> yIt = y.backingSet.iterator();
        Iterator<SingleAxisCoordinateSet> xIt = x.backingSet.iterator();
        
        while (yIt.hasNext()) {
          result = sacsComparator.compare(xIt.next(), (yIt.next()));
          if (result != 0) {
            return result;
          }
        }
        return result;
      }
    };
  }

  public static final Comparator<NextHop> forNextHop(
      final Comparator<CoordinatesBundle> sacsComparator) {
    return new Comparator<NextHop>() {
  
      @Override
      public int compare(NextHop x, NextHop y) {
        int result = sacsComparator.compare(x.target, y.target);
         if (result != 0) {
           return result;
         }
         String yClassName = y.newAxes.toString();
         String xClassName = x.newAxes.toString();
  
         return xClassName.compareTo(yClassName);
       }
    };
  }
}
