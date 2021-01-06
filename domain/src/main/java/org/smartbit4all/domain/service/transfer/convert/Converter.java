/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.domain.service.transfer.convert;

import java.util.function.Function;

/**
 * The converter is binding two {@link Function} that makes the conversion to and from between the
 * types. The conversion could be named and in this case it's a symbolic name that can be used if we
 * would like to refer to the conversion. Otherwise we can find the default conversion between the
 * types. The default is the unnamed version.
 * 
 * @author Peter Boros
 *
 */
public interface Converter<F extends Object, T extends Object> {

  /**
   * The name of the converter. Could be a manually set name but also a name generated from the
   * types converted by the instance.
   * 
   * @return
   */
  String name();

  /**
   * The converter function to produce the target value from the source value.
   * 
   * @return
   */
  Function<Object, Object> toward();

  /**
   * The converter function to produce the source value from the target value.
   * 
   * @return
   */
  Function<Object, Object> backward();

  Class<F> fromType();

  Class<T> toType();

  /**
   * The convert service that convert the from value to the target.
   * 
   * @param fromValue
   * @return The target object instance
   */
  T convertTo(F fromValue);

  /**
   * The backward conversion from the toValue.
   * 
   * @param toValue The to value
   * @return The converted from value instance.
   */
  F convertFrom(T toValue);

}
