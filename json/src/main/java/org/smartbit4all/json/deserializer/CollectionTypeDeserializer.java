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
package org.smartbit4all.json.deserializer;

import java.util.Set;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.json.deserializer.collection.DataRowSetDeserializer;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * The counterpart of {@link CollectionTypeDeserializer}, that handles the process of
 * deserialization of the custom serialized collection types.
 * 
 * @see CollectionTypeDeserializer
 */
public class CollectionTypeDeserializer extends SimpleDeserializers {

  @Override
  public JsonDeserializer<?> findCollectionDeserializer(CollectionType type,
      DeserializationConfig config, BeanDescription beanDesc,
      TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer)
      throws JsonMappingException {

    if (isDataRowSetType(type)) {
      return new DataRowSetDeserializer();
    }

    return super.findCollectionDeserializer(type, config, beanDesc, elementTypeDeserializer,
        elementDeserializer);
  }

  private boolean isDataRowSetType(CollectionType type) {
    CollectionType dataRowSetType = TypeFactory.defaultInstance()
        .constructCollectionType(Set.class, DataRow.class);
    return type.equals(dataRowSetType);
  }

}
