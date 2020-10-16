package org.smartbit4all.json.mixin;

import org.smartbit4all.json.deserializer.PropertyRefDeserializer;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonTypeName("PropertyRef")
@JsonDeserialize(using = PropertyRefDeserializer.class)
public abstract class PropertyRefMixin extends PropertyMixin {
}
