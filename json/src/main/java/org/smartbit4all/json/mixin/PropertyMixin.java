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
