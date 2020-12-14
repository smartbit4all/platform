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
package org.smartbit4all.json.serializer;

import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.json.deserializer.CollectionTypeDeserializer;
import org.smartbit4all.json.serializer.collection.DataRowCollectionSerializer;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import com.fasterxml.jackson.databind.type.CollectionType;

/**
 * The types that need to be serialized different from the default must be handled in this class by
 * checking the parameter {@link CollectionType} and return the corresponding serializer.
 * In case of collection type hierarchies the subtype seriealizers must appear before the supertypes'.  
 * 
 * @see CollectionTypeDeserializer
 */
public class CollectionTypeSerializer extends SimpleSerializers {

  @Override
  public JsonSerializer<?> findCollectionSerializer(SerializationConfig config, CollectionType type,
      BeanDescription beanDesc, TypeSerializer elementTypeSerializer,
      JsonSerializer<Object> elementValueSerializer) {
    
    if(isDataRowCollectionType(type)) {
      return new DataRowCollectionSerializer();
    }
    
    
    return super.findCollectionSerializer(config, type, beanDesc, elementTypeSerializer,
        elementValueSerializer);
  }
  
  private boolean isDataRowCollectionType(CollectionType type) {
    return type.containedType(0) != null && type.containedType(0).isTypeOrSubTypeOf(DataRow.class);
  }
  
}
