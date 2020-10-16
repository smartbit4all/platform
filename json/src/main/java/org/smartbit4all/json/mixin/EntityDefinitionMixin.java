package org.smartbit4all.json.mixin;

import org.smartbit4all.json.deserializer.EntityDefinitionDeserializer;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = EntityDefinitionDeserializer.class)
public interface EntityDefinitionMixin {

  @JsonGetter(EntityDefinitionDeserializer.ENTITYDEFINITION_NAME)
  String entityDefName();
}
