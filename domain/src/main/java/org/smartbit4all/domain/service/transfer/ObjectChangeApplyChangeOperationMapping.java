package org.smartbit4all.domain.service.transfer;

import java.util.HashMap;
import java.util.Map;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;

public class ObjectChangeApplyChangeOperationMapping {

  public EntityDefinition rootEntity;
  public Map<String, PropertyMappingItem> propertyMappings = new HashMap<>();
  public Map<String, ReferenceMappingItem> referenceMappings = new HashMap<>();
  public Map<String, CollectionMappingItem> collectionMappings = new HashMap<>();

  public abstract static class MappingItem {

    public String name;

  }

  public static final class PropertyMappingItem extends MappingItem {

    public Property<?> property;

  }

  public static final class ReferenceMappingItem extends MappingItem {

    public Property<?> refferringRootProperty;
    public ObjectChangeApplyChangeOperationMapping oc2AcoMapping;

  }

  public static final class CollectionMappingItem extends MappingItem {

    public Property<?> refferringDetailProperty;
    public ObjectChangeApplyChangeOperationMapping oc2AcoMapping;

  }



}
