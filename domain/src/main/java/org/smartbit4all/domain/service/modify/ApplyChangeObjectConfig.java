package org.smartbit4all.domain.service.modify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertySet;

public class ApplyChangeObjectConfig {

  private String name;
  private EntityDefinition rootEntity;
  private Property<String> entityIdProperty;
  private Function<Object, Map<Property<?>, Object>> entityPrimaryKeyIdProvider;
  private Map<String, PropertyMappingItem> propertyMappings = new HashMap<>();
  private Map<String, ReferenceMappingItem> referenceMappings = new HashMap<>();
  private Map<String, CollectionMappingItem> collectionMappings = new HashMap<>();

  private ApplyChangeObjectConfig(Builder builder) {
    this.name = builder.name;
    this.rootEntity = builder.entityDefinition;
    this.entityIdProperty = builder.entityIdProperty;
    this.entityPrimaryKeyIdProvider = builder.entityPrimaryKeyIdProvider;
    builder.propertyMappingBuilders.forEach(pmb -> {
      PropertyMappingItem pmi = new PropertyMappingItem();
      pmi.property = pmb.property;
      pmi.name = pmb.propertyName;
      propertyMappings.put(pmb.propertyName, pmi);
    });

    builder.referenceMappingBuilders.forEach(rmb -> {
      ReferenceMappingItem rmi = new ReferenceMappingItem();
      rmi.refferringRootProperty = rmb.referringProperty;
      rmi.name = rmb.propertyName;
      rmi.oc2AcoMapping = rmb.config;
      referenceMappings.put(rmi.name, rmi);
    });

    builder.collectionMappingBuilders.forEach(cmb -> {
      CollectionMappingItem cmi = new CollectionMappingItem();
      cmi.refferringDetailProperty = cmb.referringProperty;
      cmi.name = cmb.propertyName;
      cmi.oc2AcoMapping = cmb.config;
      collectionMappings.put(cmi.name, cmi);
    });
  }

  public static Builder builder(String configName, EntityDefinition entityDefinition) {
    return new Builder(configName, entityDefinition);
  }

  public static Builder builder(Class<?> rootClassType, EntityDefinition entityDefinition) {
    return builder(rootClassType.getName(), entityDefinition);
  }

  public String getName() {
    return name;
  }

  public EntityDefinition getRootEntity() {
    return rootEntity;
  }

  public Map<String, PropertyMappingItem> getPropertyMappings() {
    return propertyMappings;
  }

  public Map<String, ReferenceMappingItem> getReferenceMappings() {
    return referenceMappings;
  }

  public Map<String, CollectionMappingItem> getCollectionMappings() {
    return collectionMappings;
  }

  public Property<String> getEntityIdProperty() {
    return entityIdProperty;
  }

  public Function<Object, Map<Property<?>, Object>> getEntityPrimaryKeyIdProvider() {
    return entityPrimaryKeyIdProvider;
  }

  public abstract static class MappingItem {

    protected String name;

    public String getName() {
      return name;
    }

  }

  public static final class PropertyMappingItem extends MappingItem {

    private Property<?> property;

    public Property<?> getProperty() {
      return property;
    }

  }

  public static final class ReferenceMappingItem extends MappingItem {

    private Property<?> refferringRootProperty;
    private ApplyChangeObjectConfig oc2AcoMapping;

    public Property<?> getRefferringRootProperty() {
      return refferringRootProperty;
    }

    public ApplyChangeObjectConfig getOc2AcoMapping() {
      return oc2AcoMapping;
    }

  }

  public static final class CollectionMappingItem extends MappingItem {

    private Property<?> refferringDetailProperty;
    private ApplyChangeObjectConfig oc2AcoMapping;

    public Property<?> getRefferringDetailProperty() {
      return refferringDetailProperty;
    }

    public ApplyChangeObjectConfig getOc2AcoMapping() {
      return oc2AcoMapping;
    }

  }

  public static class Builder {
    String name;
    EntityDefinition entityDefinition;
    Property<String> entityIdProperty;
    Function<Object, Map<Property<?>, Object>> entityPrimaryKeyIdProvider;
    List<PropertyMappingBuilder> propertyMappingBuilders = new ArrayList<>();
    List<ReferenceOrCollectionMappingBuilder> referenceMappingBuilders = new ArrayList<>();
    List<ReferenceOrCollectionMappingBuilder> collectionMappingBuilders = new ArrayList<>();

    protected Builder(String name, EntityDefinition entityDefinition) {
      Objects.requireNonNull(name, "Config name can not be null!");
      Objects.requireNonNull(entityDefinition, "EntityDefinition can not be null!");
      this.name = name;
      this.entityDefinition = entityDefinition;
    }

    public Builder entityIdProperty(Property<String> entityIdProperty) {
      this.entityIdProperty = entityIdProperty;
      return this;
    }

    public Builder entityPrimaryKeyIdProvider(
        Function<Object, Map<Property<?>, Object>> entityPrimaryKeyIdProvider) {
      this.entityPrimaryKeyIdProvider = entityPrimaryKeyIdProvider;
      return this;
    }

    public Builder addPropertyMapping(String propertyName, Property<?> property) {
      // now the properyMappint is simple and contains only mandatory fields, thus we create an own
      // builder for extendibility!
      return new PropertyMappingBuilder(propertyName, property, this).and();
    }

    public ReferenceOrCollectionMappingBuilder addReferenceMapping(String propertyName,
        Property<?> referringProperty) {
      ReferenceOrCollectionMappingBuilder referenceOrCollectionMappingBuilder =
          new ReferenceOrCollectionMappingBuilder(propertyName, referringProperty, this);
      this.referenceMappingBuilders.add(referenceOrCollectionMappingBuilder);
      return referenceOrCollectionMappingBuilder;
    }

    public ReferenceOrCollectionMappingBuilder addCollectionMapping(String propertyName,
        Property<?> referringProperty) {
      ReferenceOrCollectionMappingBuilder referenceOrCollectionMappingBuilder =
          new ReferenceOrCollectionMappingBuilder(propertyName, referringProperty, this);
      this.collectionMappingBuilders.add(referenceOrCollectionMappingBuilder);
      return referenceOrCollectionMappingBuilder;
    }

    public ApplyChangeObjectConfig build() {
      checkContent();
      checkEntityIdProperty();
      checkEntityPrimaryKeyIdProvider();
      return new ApplyChangeObjectConfig(this);
    }

    private void checkContent() {
      if (propertyMappingBuilders.isEmpty() && referenceMappingBuilders.isEmpty()
          && collectionMappingBuilders.isEmpty()) {
        throw new IllegalStateException(
            "Can not build ApplyChangeObjectConfig without declaring at least one inner property "
                + "for a bean!");
      }
      boolean hasMissingBuilder = referenceMappingBuilders.stream()
          .anyMatch(rb -> rb.innerConfigBuilder == null);
      hasMissingBuilder &= collectionMappingBuilders.stream()
          .anyMatch(rb -> rb.innerConfigBuilder == null);
      if (hasMissingBuilder) {
        throw new IllegalStateException(
            "Can not build ApplyChangeObjectConfig with missing inner config declaration!");
      }
    }

    @SuppressWarnings("unchecked")
    private void checkEntityIdProperty() {
      if (entityIdProperty == null) {
        PropertySet primarykeydef = entityDefinition.PRIMARYKEYDEF();
        if (primarykeydef.size() > 1) {
          throw new IllegalStateException(
              "The entity definition [" + entityDefinition.entityDefName() + "] has multiple "
                  + "primary key fields! Use the builder's entityIdProperty() setter to define the "
                  + "String property that holds the UUIDs!");
        }
        Property<?> primaryKey = primarykeydef.iterator().next();
        if (!String.class.equals(primaryKey.type())) {
          throw new IllegalStateException(
              "The entity definition [" + entityDefinition.entityDefName() + "] has a primary key"
                  + " that is not String typed. Use the builder's entityIdProperty() setter to "
                  + "define the String property that holds the UUIDs!");

        }
        entityIdProperty = (Property<String>) primaryKey;
      }
    }

    private void checkEntityPrimaryKeyIdProvider() {
      if (entityPrimaryKeyIdProvider == null) {
        PropertySet primarykeydef = entityDefinition.PRIMARYKEYDEF();
        if (primarykeydef.size() > 1) {
          throw new IllegalStateException(
              "The entity definition [" + entityDefinition.entityDefName() + "] has multiple "
                  + "primary key fields! Use the builder's entityPrimaryKeyIdProvider() to set a"
                  + " provider that provides the mandatory id field values to the corresponting"
                  + " properties!");
        }
        Property<?> primaryKey = primarykeydef.iterator().next();
        if (!primaryKey.getName().equals(entityIdProperty.getName())) {
          throw new IllegalStateException(
              "The entity definition [" + entityDefinition.entityDefName() + "] has a primary key"
                  + " that different from the configured entityIdProperty! Use the builder's"
                  + " entityPrimaryKeyIdProvider() to set a provider that provides the mandatory"
                  + " id field values to the corresponting properties!");
        }
      }
    }

  }

  public static class PropertyMappingBuilder {

    String propertyName;
    Property<?> property;
    private Builder parent;

    private PropertyMappingBuilder(String propertyName, Property<?> property, Builder parent) {
      Objects.requireNonNull(propertyName, "propertyName can not be null!");
      Objects.requireNonNull(property, "property can not be null!");
      this.propertyName = propertyName;
      this.property = property;
      this.parent = parent;
    }

    public Builder and() {
      parent.propertyMappingBuilders.add(this);
      return parent;
    }

  }

  /*
   * Now the structure of the reference and the collection is the same, so we implement only one
   * builder for them. Later may be separate builders...
   */
  public static class ReferenceOrCollectionMappingBuilder {

    String propertyName;
    Property<?> referringProperty;
    Builder innerConfigBuilder;
    ApplyChangeObjectConfig config;
    private Builder parent;

    private ReferenceOrCollectionMappingBuilder(String propertyName, Property<?> referringProperty,
        Builder parent) {
      Objects.requireNonNull(propertyName, "propertyName can not be null!");
      Objects.requireNonNull(referringProperty, "referringProperty can not be null!");
      this.propertyName = propertyName;
      this.referringProperty = referringProperty;
      this.parent = parent;
    }

    public ReferenceOrCollectionMappingBuilder config(ApplyChangeObjectConfig config) {
      this.config = config;
      return this;
    }

    public Builder and() {
      return parent;
    }

  }

}
