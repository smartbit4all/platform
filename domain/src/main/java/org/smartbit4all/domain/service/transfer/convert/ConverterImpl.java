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
import org.smartbit4all.domain.service.transfer.TransferServiceImpl;

/**
 * The basic implementation. We extend this in the configurations.
 * 
 * @author Peter Boros
 */
public class ConverterImpl<F extends Object, T extends Object> implements Converter<F, T> {

  /**
   * The convert to function.
   */
  protected Function<F, T> toward;

  /**
   * The class of the to value.
   */
  protected Class<T> toClass;

  /**
   * The convert from function.
   */
  protected Function<T, F> backward;

  /**
   * The class of the from value.
   */
  protected Class<F> fromClass;

  /**
   * The name of the converter.
   */
  protected String name;

  public ConverterImpl(Class<T> toClass,
      Function<F, T> toward, Class<F> fromClass, Function<T, F> backward) {
    super();
    this.toward = toward;
    this.backward = backward;
    this.fromClass = fromClass;
    this.toClass = toClass;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Function<Object, Object> toward() {
    return (Function<Object, Object>) toward;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Function<Object, Object> backward() {
    return (Function<Object, Object>) backward;
  }

  @Override
  public Class<F> fromType() {
    return fromClass;
  }

  @Override
  public Class<T> toType() {
    return toClass;
  }

  @Override
  public String name() {
    return name == null ? TransferServiceImpl.generateName(fromClass, toClass) : name;
  }



  /**
   * Builder API for setting the name of the conversion. More readable in the configuration.
   * Something like this:
   * 
   * <p/>
   * 
   * <code>
   * return new ConverterImpl(...).name("MyFavoriteConversion");
   * </code>
   * 
   * @param name
   * @return
   */
  public ConverterImpl<F, T> name(String name) {
    this.name = name;
    return this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public T convertTo(F fromValue) {
    return (T) toward().apply(fromValue);
  }

  @SuppressWarnings("unchecked")
  @Override
  public F convertFrom(T toValue) {
    return (F) backward().apply(toValue);
  }

}
