package org.smartbit4all.domain.service.modify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertyOwned;
import org.smartbit4all.domain.meta.PropertyRef;
import org.smartbit4all.domain.meta.PropertySet;

public class ApplyChangeObjectConfig {

  private String name;
  private EntityDefinition rootEntity;
  private Property<String> entityIdProperty;
  private Function<Object, Map<Property<?>, Object>> entityPrimaryKeyIdProvider;
  private Map<String, ReferenceDescriptor> referenceDescriptorsByEntityName = new HashMap<>();
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
      rmi.name = rmb.referenceName;
      referenceMappings.put(rmi.name, rmi);
    });

    builder.collectionMappingBuilders.forEach(cmb -> {
      CollectionMappingItem cmi = new CollectionMappingItem();
      cmi.refferringDetailProperty = cmb.referringProperty;
      cmi.referredProperty = cmb.referredProperty;
      cmi.name = cmb.propertyName;
      cmi.oc2AcoMapping = cmb.config;
      collectionMappings.put(cmi.name, cmi);
    });
    builder.referenceDescriptors.forEach(rd -> {
      referenceDescriptorsByEntityName.put(rd.entityName, rd);
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

  public ReferenceDescriptor getReferenceDescriptor(String entityName) {
    return referenceDescriptorsByEntityName.get(entityName);
  }

  public static class ReferenceDescriptor {

    private String entityName;
    private Property<String> entityUuidProperty;
    private Function<Object, Map<Property<?>, Object>> entityPrimaryKeyIdProvider;

    public Property<String> getEntityUuidProperty() {
      return entityUuidProperty;
    }

    public Function<Object, Map<Property<?>, Object>> getEntityPrimaryKeyIdProvider() {
      return entityPrimaryKeyIdProvider;
    }


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


  }

  public static final class CollectionMappingItem extends MappingItem {

    private Property<?> refferringDetailProperty;
    private Property<?> referredProperty;
    private ApplyChangeObjectConfig oc2AcoMapping;

    public Property<?> getRefferringDetailProperty() {
      return refferringDetailProperty;
    }

    public ApplyChangeObjectConfig getOc2AcoMapping() {
      return oc2AcoMapping;
    }

    public Property<?> getReferredProperty() {
      return referredProperty;
    }

  }

  public static class Builder {
    String name;
    EntityDefinition entityDefinition;
    Property<String> entityIdProperty;
    Function<Object, Map<Property<?>, Object>> entityPrimaryKeyIdProvider;
    List<ReferenceDescriptor> referenceDescriptors = new ArrayList<>();
    List<PropertyMappingBuilder> propertyMappingBuilders = new ArrayList<>();
    List<ReferenceMappingBuilder> referenceMappingBuilders = new ArrayList<>();
    List<CollectionMappingBuilderBase<?, ?>> collectionMappingBuilders = new ArrayList<>();

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

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Builder addEntityReferenceDescriptor(Property<String> uidProperty,
        Function<Object, Map<Property<?>, Object>> entityPrimaryKeyIdProvider) {
      Objects.requireNonNull(uidProperty, "uidProperty can not be null!");
      Objects.requireNonNull(entityPrimaryKeyIdProvider,
          "entityPrimaryKeyIdProvider can not be null!");
      Property<String> propToSet = null;
      if (uidProperty instanceof PropertyOwned) {
        propToSet = uidProperty;
      } else if (uidProperty instanceof PropertyRef) {
        propToSet = ((PropertyRef) uidProperty).getReferredOwnedProperty();
      } else {
        throw new IllegalStateException("Unhandled Property type");
      }

      ReferenceDescriptor rd = new ReferenceDescriptor();
      rd.entityUuidProperty = propToSet;
      rd.entityName = propToSet.getEntityDef().entityDefName();
      rd.entityPrimaryKeyIdProvider = entityPrimaryKeyIdProvider;
      referenceDescriptors.add(rd);

      return this;
    }

    public ReferenceMappingBuilder addReferenceMapping(String propertyName) {
      ReferenceMappingBuilder referenceMappingBuilder =
          new ReferenceMappingBuilder(propertyName, this);
      this.referenceMappingBuilders.add(referenceMappingBuilder);
      return referenceMappingBuilder;
    }

    public CollectionMappingBuilder addCollectionMapping(String propertyName,
        Property<?> referringProperty, ApplyChangeObjectConfig config) {
      CollectionMappingBuilder collectionMappingBuilder =
          new CollectionMappingBuilder(propertyName, referringProperty, config, this);
      this.collectionMappingBuilders.add(collectionMappingBuilder);
      return collectionMappingBuilder;
    }

    public ApplyChangeObjectConfig build() {
      checkContent();
      checkEntityIdProperty();
      checkEntityPrimaryKeyIdProvider();
      handleCollectionReferProperties();
      checkReferenceDescriptors();
      return new ApplyChangeObjectConfig(this);
    }

    private void checkReferenceDescriptors() {
      // TODO should check if all needed reference descriptors are set
      // need to check entity definition references with the types of the fks
    }

    private void handleCollectionReferProperties() {
      for (CollectionMappingBuilderBase<?, ?> cmb : collectionMappingBuilders) {
        if (cmb.referredProperty == null) {
          // when there is no referred property set, it is handled as the detail points to the
          // root's id
          cmb.referredProperty = entityIdProperty;
        }
        if (!cmb.referredProperty.type().equals(cmb.referringProperty.type())) {
          throw new IllegalStateException(
              "The referred and the property types are not identical on collection "
                  + cmb.propertyName);
        }
      }

    }

    private void checkContent() {
      if (propertyMappingBuilders.isEmpty() && referenceMappingBuilders.isEmpty()
          && collectionMappingBuilders.isEmpty()) {
        throw new IllegalStateException(
            "Can not build ApplyChangeObjectConfig without declaring at least one inner property "
                + "for a bean!");
      }
      boolean hasMissingConfig = collectionMappingBuilders.stream()
          .anyMatch(rb -> rb.config == null);
      if (hasMissingConfig) {
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

  public static class ReferenceMappingBuilder {
    String referenceName;
    Builder rootBuilder;
    ReferenceMappingBuilder parentRefBuilder;

    public ReferenceMappingBuilder(String referenceName, Builder builder) {
      Objects.requireNonNull(referenceName, "referenceName can not be null!");
      this.referenceName = referenceName;
      this.rootBuilder = builder;
    }

    public ReferenceMappingBuilder addPropertyMapping(String propertyName, Property<?> property) {
      new PropertyMappingBuilder(createReferencePropName(propertyName), property, rootBuilder)
          .and();
      return this;
    }

    public ReferenceMappingBuilder addReferenceMapping(String propertyName,
        Property<?> referringProperty) {
      ReferenceMappingBuilder refMapBuilder =
          new ReferenceMappingBuilder(createReferencePropName(propertyName), rootBuilder);
      refMapBuilder.setParentBuilder(this);
      rootBuilder.referenceMappingBuilders.add(refMapBuilder);
      return refMapBuilder;
    }

    public CollectionMappingBuilderRef addCollectionMapping(String propertyName,
        Property<?> referringProperty, ApplyChangeObjectConfig config) {
      CollectionMappingBuilderRef collectionMappingBuilder =
          new CollectionMappingBuilderRef(createReferencePropName(propertyName), referringProperty,
              config,
              this, rootBuilder);
      rootBuilder.collectionMappingBuilders.add(collectionMappingBuilder);
      return collectionMappingBuilder;
    }

    private String createReferencePropName(String propertyName) {
      return referenceName + "." + propertyName;
    }

    private void setParentBuilder(ReferenceMappingBuilder parentRefBuilder) {
      this.parentRefBuilder = parentRefBuilder;
    }

    public ReferenceMappingBuilder and() {
      if (parentRefBuilder == null) {
        throw new RuntimeException(
            "Can not call and() onn root ReferencaMappingBuilder! Call end() or build() instead!");
      }
      return parentRefBuilder;
    }

    public Builder end() {
      return rootBuilder;
    }

    public ApplyChangeObjectConfig build() {
      return rootBuilder.build();
    }

  }

  public abstract static class CollectionMappingBuilderBase<P, T extends CollectionMappingBuilderBase<P, T>> {

    String propertyName;
    Property<?> referringProperty;
    Property<?> referredProperty;
    ApplyChangeObjectConfig config;
    private P parent;

    private CollectionMappingBuilderBase(String propertyName, Property<?> referringProperty,
        ApplyChangeObjectConfig config,
        P parent) {
      Objects.requireNonNull(propertyName, "propertyName can not be null!");
      Objects.requireNonNull(referringProperty, "referringProperty can not be null!");
      Objects.requireNonNull(config, "config can not be null!");
      this.propertyName = propertyName;
      this.referringProperty = referringProperty;
      this.config = config;
      this.parent = parent;
    }

    abstract T self();

    public T referredProperty(Property<?> referredProperty) {
      Objects.requireNonNull(referredProperty, "referredProperty can not be null!");
      this.referredProperty = referredProperty;
      return self();
    }

    public P and() {
      return parent;
    }

  }

  public static class CollectionMappingBuilder
      extends CollectionMappingBuilderBase<Builder, CollectionMappingBuilder> {

    public CollectionMappingBuilder(String propertyName, Property<?> referringProperty,
        ApplyChangeObjectConfig config,
        Builder parent) {
      super(propertyName, referringProperty, config, parent);
    }

    @Override
    CollectionMappingBuilder self() {
      return this;
    }

  }

  public static class CollectionMappingBuilderRef
      extends CollectionMappingBuilderBase<ReferenceMappingBuilder, CollectionMappingBuilderRef> {

    Builder rootBuilder;

    public CollectionMappingBuilderRef(String propertyName, Property<?> referringProperty,
        ApplyChangeObjectConfig config,
        ReferenceMappingBuilder parent,
        Builder rootBuilder) {
      super(propertyName, referringProperty, config, parent);
      this.rootBuilder = rootBuilder;
    }

    @Override
    CollectionMappingBuilderRef self() {
      return this;
    }

    public ApplyChangeObjectConfig build() {
      return rootBuilder.build();
    }

    public Builder end() {
      return rootBuilder;
    }

  }

}
