package org.smartbit4all.domain.service.modify.applychange;

import java.util.Map;
import java.util.function.Function;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;

public interface EntityChangeConfig {
  EntityDefinition getRootEntity();

  Property<String> getEntityIdProperty();

  Function<Object, Map<Property<?>, Object>> getEntityPrimaryKeyIdProvider();
}
