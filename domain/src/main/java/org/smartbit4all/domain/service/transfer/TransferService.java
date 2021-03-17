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
package org.smartbit4all.domain.service.transfer;

import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.service.transfer.convert.Converter;

/**
 * The transfer service collects all the bindings between transfer objects and domains. They can be
 * bean to bean mappings but also can be domain to domain mappings. The domains are defined at the
 * domain level in the platform. So the basic implementation at the core level contains only the
 * bean to bean bindings.
 * 
 * This service is designed using the
 * <a href= "https://www.tutorialspoint.com/design_pattern/transfer_object_pattern.htm">Design
 * Pattern - Transfer Object Pattern</a>.
 * 
 * As part of the transfer object - business object bindings it manages the converters that could
 * convert between the different data types.
 * 
 * @author Peter Boros
 */
public interface TransferService {

  /**
   * The binding is the meta class that describes the binding between the POJO bean class and the
   * {@link EntityDefinition} of a domain.
   * 
   * @param beanClazz The bean class we have.
   * @param entityDef The domain entity.
   * @return
   * @throws Exception
   */
  BeanEntityBinding binding(Class<?> beanClazz, EntityDefinition entityDef) throws Exception;

  BeanBeanBinding binding(Class<?> beanClazz1, Class<?> beanClazz2) throws Exception;

  EntityEntityBinding binding(EntityDefinition entityDef1, EntityDefinition entityDef2)
      throws Exception;

  /**
   * The operation finds the right converter for the type conversion between the fromType and toType
   * by its name.
   * 
   * @param <F> The type of the from value.
   * @param <T> The type of the to value.
   * @param name If we have a conversion with a given name then we try to find this conversion by
   *        this name and return.
   * @param fromType The class of the from value.
   * @param toType The class of the to value.
   * @return If we have name for the conversion then we try to find it by this name. If this
   *         converter is missing then we get null.
   */
  <F, T> Converter<F, T> converterByName(String name, Class<F> fromType, Class<T> toType);

  /**
   * The operation finds the right converter for the type conversion between the fromType and
   * toType.
   * 
   * @param <F> The type of the from value.
   * @param <T> The type of the to value.
   * @param fromType The class of the from value.
   * @param toType The class of the to value.
   * @return
   */
  <F, T> Converter<F, T> converterByType(Class<F> fromType, Class<T> toType);

  /**
   * The operation finds the right converter for the type conversion between the fromType and
   * toType. It tries to find the converter by name. If it's not exist then try to find by the
   * types.
   * 
   * @param <F> The type of the from value.
   * @param <T> The type of the to value.
   * @param name The name of the conversion that can be optional. If we have a conversion with a
   *        given name then we try to find this conversion by this name and return.
   * @param fromType The class of the from value.
   * @param toType The class of the to value.
   * @return If we have name for the conversion then we try to find it by this name. If this
   *         converter is missing then we get null. Else if the name is null then we try to find the
   *         conversion by the types. In this case we assume only the unnamed conversions.
   */
  <F, T> Converter<F, T> converterDefault(String name, Class<F> fromType, Class<T> toType);

  /**
   * Tries to execute an inline conversion by the type of the source and the type of the target.
   * 
   * @param <F>
   * @param <T>
   * @param value The value to convert. Must not be null!
   * @param toType The class of the target to define the result.
   * @return
   */
  <F, T> T convert(F value, Class<T> toType);

}
