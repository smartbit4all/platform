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
 * The counterpart of {@link CollectionTypeDeserializer}, that handles the process
 * of deserialization of the custom serialized collection types.
 * 
 *  @see {@link CollectionTypeDeserializer}
 */
public class CollectionTypeDeserializer extends SimpleDeserializers {

  @Override
  public JsonDeserializer<?> findCollectionDeserializer(CollectionType type,
      DeserializationConfig config, BeanDescription beanDesc,
      TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer)
      throws JsonMappingException {
    
    if(isDataRowSetType(type)) {
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
