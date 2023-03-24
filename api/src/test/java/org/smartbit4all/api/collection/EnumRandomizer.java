package org.smartbit4all.api.collection;

import java.util.Random;

/**
 * A static utility that optimize the random value generation from an enum value list.
 * 
 * @author Peter Boros
 */
public class EnumRandomizer {

  private static final Random RND = new Random();

  private EnumRandomizer() {}

  @SuppressWarnings("rawtypes")
  public static final <T extends Enum> T randomEnum(Class<T> enumClass) {
    T[] enumConstants = enumClass.getEnumConstants();
    return enumConstants[RND.nextInt(enumConstants.length)];
  }


}
