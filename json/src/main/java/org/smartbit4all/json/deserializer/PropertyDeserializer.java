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
package org.smartbit4all.json.deserializer;

import java.io.IOException;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PropertyDeserializer extends JsonDeserializer<Property<?>> {

  public static final String PROPERTY_NAME = "name";
  
  public static final String PROPERTY_ENTITY_DEF = "entityDef";
  
  @Override
  public Property<?> deserialize(JsonParser p, DeserializationContext context)
      throws IOException, JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper().setConfig(context.getConfig());
    ObjectCodec oc = p.getCodec();
    JsonNode node = oc.readTree(p);
    String propertyName = node.get(PROPERTY_NAME).asText();
    
    JsonNode entityDefNode = node.get(PROPERTY_ENTITY_DEF);
    if (entityDefNode != null) {
      EntityDefinition entityDef = objectMapper.convertValue(entityDefNode, EntityDefinition.class);
      return getEntityDefProperty(entityDef, propertyName);
    } else {
      // TODO throw exception?
      return null;
    }
  }
  
  protected Property<?> getEntityDefProperty(EntityDefinition entityDef, String propertyName) {
    return entityDef.getProperty(propertyName); 
  }
  

}
