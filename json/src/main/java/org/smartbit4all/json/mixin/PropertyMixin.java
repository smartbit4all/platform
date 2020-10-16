package org.smartbit4all.json.mixin;

import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.PropertyComputed;
import org.smartbit4all.domain.meta.PropertyOwned;
import org.smartbit4all.domain.meta.PropertyRef;
import org.smartbit4all.json.deserializer.PropertyDeserializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = PropertyOwned.class, name = "PropertyOwned"),
  @JsonSubTypes.Type(value = PropertyRef.class, name = "PropertyRef"),
  @JsonSubTypes.Type(value = PropertyComputed.class, name = "PropertyComputed")})
@JsonDeserialize(using = PropertyDeserializer.class)
@JsonAutoDetect(
    getterVisibility = Visibility.NONE,
    isGetterVisibility = Visibility.NONE
)
public abstract class PropertyMixin {
  
  @JsonProperty(PropertyDeserializer.PROPERTY_NAME)
  String name;
  
  @JsonProperty(PropertyDeserializer.PROPERTY_ENTITY_DEF)
  EntityDefinition entityDef;
}
