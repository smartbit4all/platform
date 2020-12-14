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

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertyRef;
import org.smartbit4all.domain.meta.Reference;

public class PropertyRefDeserializer extends PropertyDeserializer {

  @Override
  protected Property<?> getEntityDefProperty(EntityDefinition entityDef, String propertyName) {
    Property<?> property = super.getEntityDefProperty(entityDef, propertyName);

    if (property == null) {
      String[] propertyRefs = propertyName.split("\\.");
      String referredPropertyName = propertyRefs[propertyRefs.length - 1];

      List<Reference<?, ?>> joinPath = new ArrayList<>();
      EntityDefinition currentEntity = entityDef;
      for (int i = 0; i < propertyRefs.length - 1; i++) {
        String referenceName = propertyRefs[i];
        Reference<?, ?> reference = currentEntity.getReference(referenceName);
        joinPath.add(reference);
        currentEntity = reference.getTarget();
      }
      Property<?> referredProperty = currentEntity.getProperty(referredPropertyName);
      property = entityDef.getReferredPropertyByPath(joinPath, referredProperty);
      if (property == null) {
        property = new PropertyRef<>(propertyName, joinPath, referredProperty);
        property.setEntityDef(entityDef);
      }
    }
    return property;
  }
}
