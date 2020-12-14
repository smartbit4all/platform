/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.core.utility;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

/**
 * This is a simple extension of {@link EnumSet} with a default value. If there is no registered
 * value for the given enum this {@link #defaultValue} will be returned by the {@link #get(Enum)}.
 * 
 * @author Peter Boros
 *
 * @param <K> The enum that defines the possible keys.
 * @param <V> The value belongs to this keys.
 */
public class EnumSpecificValue<K extends Enum<K>, V> {

  /**
   * Holds the specific values for the enum.
   */
  private EnumMap<K, V> specificValues;

  /**
   * This is the default value for the enum specific value set to avoid null when get.
   */
  private V defaultValue;

  public EnumSpecificValue(V defaultValue, Class<K> keyType) {
    super();
    this.defaultValue = defaultValue;
    this.specificValues = new EnumMap<K, V>(keyType);
  }
  
  public EnumSpecificValue(V defaultValue, Map<K, V> map) {
    super();
    this.defaultValue = defaultValue;
    this.specificValues = new EnumMap<K, V>(map);
  }
  
  /**
   * The value registered for the given enum value.
   * 
   * @param enumValue
   * @return If the enum value
   */
  public V get(K enumValue) {
    if (enumValue == null) {
      return defaultValue;
    }
    V v = specificValues.get(enumValue);
    return v == null ? defaultValue : v;
  }
  
  /**
   * Associates the specified value with the specified key in this map.
   * @param key
   * @param value
   */
  public void put(K key, V value) {
    specificValues.put(key, value);
  }

}
