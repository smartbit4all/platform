package org.smartbit4all.domain.service.entity;

import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;

public interface EntitySource {

  String getSourceId();
  
  EntityDefinition getEntity(String entityPath);
  
  Property<?> getProperty(String entityPath, String property);
}
