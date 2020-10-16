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
   * The converter will find the right converter for the type conversion between the fromType and
   * toType.
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
  <F, T> Converter<F, T> converter(String name, Class<F> fromType, Class<T> toType);

}
