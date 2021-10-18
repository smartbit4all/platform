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
package org.smartbit4all.core.config;

import java.util.Optional;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataObject;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectApiImpl;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectSerializer;
import org.smartbit4all.core.object.ObjectSerializerByObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The basic services we have in the platform to support the function execution and transformations
 * between API data formats.
 * 
 * @author Peter Boros
 */
@Configuration
public class CoreServiceConfig {

  @Bean
  public ObjectApi objectApi() {
    return new ObjectApiImpl();
  }

  @Bean
  public ObjectSerializer objectMapperSerializer() {
    return new ObjectSerializerByObjectMapper();
  }

  @Bean
  public ObjectDefinition<BinaryDataObject> binaryDataDefinition() {
    ObjectDefinition<BinaryDataObject> result =
        ObjectApiImpl.constructDefinitionBase(BinaryDataObject.class);
    result.setPreferredSerializerName(BinaryDataObject.class.getName());
    return result;
  }

  @Bean
  public ObjectSerializer binaryDataSerializer() {
    return new ObjectSerializer() {

      @Override
      public BinaryData serialize(Object obj, Class<?> clazz) {
        return ((BinaryDataObject) obj).getBinaryData();
      }

      @Override
      public String getName() {
        return BinaryDataObject.class.getName();
      }

      @SuppressWarnings("unchecked")
      @Override
      public <T> Optional<T> deserialize(BinaryData data, Class<T> clazz) {
        return clazz.isAssignableFrom(BinaryDataObject.class)
            ? (Optional<T>) Optional.of(new BinaryDataObject(data))
            : Optional.empty();
      }
    };
  }
}
