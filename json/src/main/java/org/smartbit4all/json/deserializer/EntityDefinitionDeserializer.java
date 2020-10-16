package org.smartbit4all.json.deserializer;

import java.io.IOException;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.service.CrudService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class EntityDefinitionDeserializer extends JsonDeserializer<EntityDefinition> {

  public static final String ENTITYDEFINITION_NAME = "entityDefName";
  
  @Autowired
  private ApplicationContext context;

  @Override
  public EntityDefinition deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    ObjectCodec oc = p.getCodec();
    JsonNode node = oc.readTree(p);

    String entityDefName = node.get(ENTITYDEFINITION_NAME).asText();
    EntityDefinition entityDef = null;
    try {
      entityDef = (EntityDefinition) context.getBean(entityDefName);
    } catch (BeansException e) {
      // TODO throw exception?
      e.printStackTrace();
    }
    return entityDef;
  }

}
