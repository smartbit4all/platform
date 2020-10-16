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
