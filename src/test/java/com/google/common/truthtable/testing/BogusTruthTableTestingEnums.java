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
package com.google.common.truthtable.testing;

import com.google.common.truthtable.TruthTable;

public class BogusTruthTableTestingEnums {

  /**
   * Bogus enum to allow for unit testing the {@link TruthTable}
   */
  public enum Bread {
    OAT,
    PITA,
    WHEAT,
    WHITE,
    WONDER,
  }
  
  /**
   * Bogus enum to allow for unit testing the {@link TruthTable}
   */
  public enum Entree {
    BURGER,
    CHICKEN,
    EGGPLANT,
    LASAGNA,
    MEATPIE,
    OKRA,
    PASTA,
    PIZZA,
    REUBEN,
    STEAK,
    SUSHI,
    UNICORN,
  }
  
  /**
   * Bogus enum to allow for unit testing the {@link TruthTable}
   */
  public enum Wine {
    CHIANTI,
    MERLOT,
    PINOT_NOIR,
    PORT,
    ZINFANDEL,
  }
  
  /**
   * Bogus enum to allow for unit testing the {@link TruthTable}. This is, on
   * purpose, a large enum so as to allow benchmarking of large truthtables. 
   * 
   * <p>We are sure to have more than 64 items in this enum, so as to trigger
   * the usage of the (slower) JumboEnumSet -- see
   * {@link java.util.EnumSet#noneOf(Class)}.
   */
  public enum Dessert {
    APPLE_PIE,
    APPLE_CRISP,
    APPLE_TART,
    BAKLAVA,
    BANANA_SPLIT,
    BANANA_SORBET,
    BLACK_FOREST_CAKE,
    BLUEBERRY,
    BLUEBERRY_ICE_CREAM,
    BLUEBERRY_SORBET,
    CAKE,
    CARROT_CAKE,
    CHEESECAKE,
    CHERRY_PIE,
    CHERRY_CREAM,
    CHOCOLATE,
    CHOCOLATE_BROWNIE,
    CHOCOLATE_ECLAIR,
    CHOCOLATE_FONDUE,
    CHOCOLATE_MOUSSE,
    CHOCOLATE_PIE,
    CHOCOLATE_TART,
    CINNAMON_ROLL,
    COCONUT_FUDGE,
    COCONUT_ICE_CREAM,
    COFFEE_DESSERT,
    CREAM_TANGERINE,
    CROISSANT,
    DOUGHNUT,
    FORTUNE_COOKIE,
    FRUIT_SALAD,
    FUDGE,
    GINGER_BREAD,
    GINGER_SLING_WITH_A_PINEAPPLE_HEART,
    GRAPE,
    GUAVA_CANDY,
    GULAB_JAMUN,
    GUMMY_BEAR,
    HONEY_BREAD,
    HONEY_WAFFLE,
    ICE_CREAM,
    LEMON_PIE,
    LEMON_SORBET,
    MACAROON,
    MANGO,
    MANGO_ICE_CREAM,
    MANGO_SORBET,
    MERINGUE,
    MONTELIMAR,
    MUDPIE,
    ORANGE,
    ORANGE_ICE_CREAM,
    ORANGE_JELLO,
    PEACH_COBBLER,
    PEANUT_BUTTER_COOKIE,
    PECAN_PIE,
    PUMPKIT_NUT_BREAD,
    RASPBERRY_COBBLER,
    RHUBARB_CRUMBLE,
    SAVOY_TRUFFLE,
    SOUFFLE,
    STRAWBERRY,
    STRAWBERRY_JELLO,
    STRAWBERRY_ICE_CREAM,
    STRAWBERRY_SHORTCAKE,
    STRAWBERRY_SORBET,
    SUGAR_COOKIE,
    SUNDAE,
    TAPIOCA_PUDDING,
    TIRAMISSU,
    TRUFFLE,
    VANILLA_ICE_CREAM,
    WHIPPED_CREAM_WAFLLE,
  }

  public enum MealTime {
    BREAKFAST, LUNCH, DINNER,
  }

  public enum Cuttlery {
    PLASTIC, SILVER, GOLD, PLATINUM,
  }
}
