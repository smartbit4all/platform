package org.smartbit4all.core.object;

import java.util.Collection;
import java.util.Map;
import org.smartbit4all.api.object.bean.ObjectDefinitionData;
import org.smartbit4all.api.object.bean.PropertyDefinitionData;
import static java.util.stream.Collectors.toMap;

public class ObjectDefinitionBuilder {

  private final ObjectDefinition<?> definition;

  private final Map<String, PropertyDefinitionData> propertiesByName;

  private boolean changed = false;

  public ObjectDefinitionBuilder(ObjectDefinition<?> definition) {
    super();
    this.definition = definition;
    propertiesByName = definition.getDefinitionData().getProperties().stream()
        .collect(toMap(PropertyDefinitionData::getName, p -> p));
  }

  public ObjectDefinitionBuilder addProperty(String propertyName, Class<?> typeClass) {
    return addProperty(propertyName, typeClass.getName());
  }

  public ObjectDefinitionBuilder addAll(Collection<PropertyDefinitionData> properties) {
    if (properties != null) {
      properties.stream().forEach(pd -> addProperty(pd.getName(), pd.getTypeClass()));
    }
    return this;
  }

  public ObjectDefinitionBuilder addAllOutgoingReference(
      Collection<ReferenceDefinition> references) {
    if (references != null) {
      for (ReferenceDefinition refDef : references) {
        definition.getOutgoingReferences().putIfAbsent(refDef.getSourcePropertyPath(), refDef);
      }
    }
    return this;
  }

  public ObjectDefinitionBuilder addProperty(String propertyName, String typeClassName) {
    propertiesByName.computeIfAbsent(propertyName, name -> {
      PropertyDefinitionData newProperty =
          new PropertyDefinitionData().name(propertyName).typeClass(typeClassName);
      definition.getDefinitionData().addPropertiesItem(newProperty);
      changed = true;
      return newProperty;
    });
    return this;
  }

  public ObjectDefinitionBuilder alias(String alias) {
    definition.setAlias(alias);
    return this;
  }

  /**
   * Save the current {@link ObjectDefinitionData} for the given {@link ObjectDefinition}.
   */
  public void commit() {
    if (changed) {
      definition.saveDefinitionData();
      definition.setPropertiesByNameDirty(true);
    }
  }

}
