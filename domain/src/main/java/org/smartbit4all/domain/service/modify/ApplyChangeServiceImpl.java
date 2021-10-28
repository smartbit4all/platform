package org.smartbit4all.domain.service.modify;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ChangeItem;
import org.smartbit4all.core.object.ChangeState;
import org.smartbit4all.core.object.CollectionChange;
import org.smartbit4all.core.object.CollectionObjectChange;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectChange;
import org.smartbit4all.core.object.ObjectChangeSimple;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.PropertyChange;
import org.smartbit4all.core.object.ReferenceChange;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.TableDatas;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertyOwned;
import org.smartbit4all.domain.meta.PropertyRef;
import org.smartbit4all.domain.meta.Reference;
import org.smartbit4all.domain.meta.Reference.Join;
import org.smartbit4all.domain.service.modify.ApplyChangeObjectConfig.CollectionMappingItem;
import org.smartbit4all.domain.service.modify.ApplyChangeObjectConfig.PropertyMappingItem;
import org.smartbit4all.domain.service.modify.ApplyChangeObjectConfig.PropertyProcessorMappingItem;
import org.smartbit4all.domain.service.modify.ApplyChangeObjectConfig.ReferenceDescriptor;
import org.smartbit4all.domain.service.modify.ApplyChangeObjectConfig.ReferenceMappingItem;
import org.smartbit4all.domain.service.modify.ApplyChangeOperation.ChangeOperation;
import org.smartbit4all.domain.service.transfer.TransferService;
import org.springframework.util.ObjectUtils;

public class ApplyChangeServiceImpl implements ApplyChangeService {

  private ObjectApi objectApi;

  private TransferService transferService;

  private List<Supplier<ApplyChangeObjectConfig>> configFactories;

  private Map<String, ApplyChangeObjectConfig> configsByName;

  /**
   * 
   * @param objectApi used for getting bean meta information
   * @param transferService used to convert values
   * @param configFactories The {@link ApplyChangeObjectConfig} objects need
   *        {@link EntityDefinition} parameters which wont be available at configuration time.
   *        Because of it, we only hold the factories, and the objects are created only on first
   *        access.
   */
  public ApplyChangeServiceImpl(ObjectApi objectApi, TransferService transferService,
      List<Supplier<ApplyChangeObjectConfig>> configFactories) {
    this.objectApi = objectApi;
    this.transferService = transferService;
    this.configFactories = configFactories;
  }

  @Override
  public void applyChange(ObjectChange objectChange, Object object,
      ApplyChangeObjectConfig configuration) throws Exception {
    Objects.requireNonNull(objectChange, "objectChange can not be null!");
    Objects.requireNonNull(object, "object can not be null!");
    Objects.requireNonNull(configuration, "configuration can not be null!");

    ApplyChangeOperation aco =
        createApplyChangeOperation(objectChange, configuration, object, null);
    if (aco != null) {
      aco.execute();
    }
  }

  @Override
  public void applyChange(ObjectChange objectChange, Object object) throws Exception {
    ApplyChangeObjectConfig config = getConfig(object);
    applyChange(objectChange, object, config);
  }

  private ApplyChangeObjectConfig getConfig(Object object) throws Exception {
    if (configsByName == null) {
      initConfigs();
    }
    ApplyChangeObjectConfig config = configsByName.get(object.getClass().getName());
    if (config == null) {
      throw new IllegalStateException(
          "There is no ApplyChangeObjectConfiguger for the class: " + object.getClass().getName());
    }
    return config;
  }

  private ApplyChangeOperation createApplyChangeOperation(ObjectChange objectChange,
      ApplyChangeObjectConfig config, Object rootObject, String potentialContainmentId) {
    EntityDefinition rootEntity = config.getRootEntity();
    ChangeOperation changeOperation = getChangeOperation(objectChange.getOperation());
    if (changeOperation == null || !objectChange.hasChange()) {
      return null;
    }
    TableData<EntityDefinition> rootTable = TableDatas.of(rootEntity);
    String rootId = getObjectId(rootObject, potentialContainmentId);
    Property<String> entityIdProperty = config.getEntityIdProperty();
    rootTable.addColumnOwn(entityIdProperty);
    DataRow rootRow = rootTable.addRow();
    rootRow.setObject(entityIdProperty, rootId);

    Function<Object, Map<Property<?>, Object>> idProvider = config.getEntityPrimaryKeyIdProvider();
    if (idProvider != null) {
      Map<Property<?>, Object> idValuesByProperty = idProvider.apply(rootId);
      idValuesByProperty.forEach((prop, value) -> {
        rootTable.addColumnOwn(prop);
        rootRow.setObject(prop, value);
      });
    }

    ApplyChangeOperation aco = new ApplyChangeOperation(rootTable, changeOperation);
    aco.setUniqueId(rootId);

    Map<String, ApplyChangeOperation> referredEntityAcosByName = new HashMap<>();
    referredEntityAcosByName.put("root", aco);

    processObjectChange(objectChange, config, rootId, referredEntityAcosByName, "");
    return aco;
  }

  private void processObjectChange(ObjectChange objectChange, ApplyChangeObjectConfig config,
      String rootId, Map<String, ApplyChangeOperation> referredEntityAcosByName,
      String parentName) {

    ChangeOperation changeOperation = getChangeOperation(objectChange.getOperation());
    if (changeOperation == null || !objectChange.hasChange()) {
      return;
    }

    ApplyChangeOperation rootAco = referredEntityAcosByName.get("root");


    for (PropertyChange pChange : objectChange.getProperties()) {
      Object newValue = pChange.getNewValue();
      String propertyName = getPropertyNameFromChange(parentName, pChange);

      handlePropertyMappings(config, referredEntityAcosByName, changeOperation, rootAco, newValue,
          propertyName);

      handlePropertyProcessors(config, referredEntityAcosByName, changeOperation, rootAco, newValue,
          propertyName);

    }


    List<ReferenceChange> references = objectChange.getReferences();
    for (int i = 0; i < references.size(); i++) {
      ReferenceChange rChange = references.get(i);
      String referenceName = getPropertyNameFromChange(parentName, rChange);
      ReferenceMappingItem referenceMappingItem =
          config.getReferenceMappings().get(referenceName);
      if (referenceMappingItem == null) {
        continue;
      }

      // create ApplyChangeOperation with recursive method
      ObjectChange changedReference = rChange.getChangedReference();

      processObjectChange(changedReference, config, rootId, referredEntityAcosByName,
          referenceName);

    }

    List<CollectionChange> collections = objectChange.getCollections();
    for (int i = 0; i < collections.size(); i++) {
      CollectionChange cChange = collections.get(i);
      CollectionObjectChange collectionObjectChange = objectChange.getCollectionObjects().get(i);
      String collectionName = getPropertyNameFromChange(parentName, cChange);
      CollectionMappingItem collectionMappingItem =
          config.getCollectionMappings().get(collectionName);
      if (collectionMappingItem == null) {
        continue;
      }

      Property<?> refferringDetailProperty = collectionMappingItem.getRefferringDetailProperty();
      Property<?> referredProperty = collectionMappingItem.getReferredProperty();
      ApplyChangeOperation parentAco = null;
      if (referredProperty instanceof PropertyOwned) {
        parentAco = referredEntityAcosByName.get("root");
      } else if (referredProperty instanceof PropertyRef) {
        parentAco = getReferredAco((PropertyRef<?>) referredProperty, referredEntityAcosByName,
            changeOperation, config);
      } else {
        throw new RuntimeException("Unhandled property subtype");
      }



      List<ObjectChange> changes = cChange.getChanges();
      for (int j = 0; j < changes.size(); j++) {
        ObjectChange collectionChangeObject = changes.get(j);
        ObjectChangeSimple objectChangeSimple = collectionObjectChange.getChanges().get(j);
        Object collectionObject = objectChangeSimple.getObject();

        // TODO later we may not get a change for every list node. When it happens the iter index
        // wont fit for this parameter. The path should be used instead..
        String containmentId =
            createPotentialContainmentId(rootId, collectionName, j);
        ApplyChangeOperation collectionAco = createApplyChangeOperation(collectionChangeObject,
            collectionMappingItem.getOc2AcoMapping(),
            collectionObject,
            containmentId);

        if (collectionAco != null) {
          Property<?> parentIdProp = referredProperty instanceof PropertyRef
              ? ((PropertyRef<?>) referredProperty).getReferredOwnedProperty()
              : referredProperty;
          Object collectionParentId = parentAco.getRow().get(parentIdProp);
          setProperty(collectionAco, refferringDetailProperty, collectionParentId);
          parentAco.postCalls().call(collectionAco);
        }
      }

    }
  }

  private void handlePropertyMappings(ApplyChangeObjectConfig config,
      Map<String, ApplyChangeOperation> referredEntityAcosByName, ChangeOperation changeOperation,
      ApplyChangeOperation rootAco, Object newValue, String propertyName) {

    List<PropertyMappingItem> propertyMappingItems =
        config.getPropertyMappings().get(propertyName);
    if (propertyMappingItems != null) {
      for (PropertyMappingItem propertyMappingItem : propertyMappingItems) {
        // convert if needed
        Property<?> property = propertyMappingItem.getProperty();
        setValueForEntityProperty(newValue, property, config, referredEntityAcosByName,
            changeOperation, rootAco);
      }
    }
  }

  private void handlePropertyProcessors(ApplyChangeObjectConfig config,
      Map<String, ApplyChangeOperation> referredEntityAcosByName, ChangeOperation changeOperation,
      ApplyChangeOperation rootAco, Object newValue, String propertyName) {

    PropertyProcessorMappingItem propertyProcessorMappingItem =
        config.getPropertyProcessorMappings().get(propertyName);

    if (propertyProcessorMappingItem != null) {
      Map<Property<?>, Object> processedValuesForProperties =
          propertyProcessorMappingItem.getPropertyProcessor().apply(newValue);
      processedValuesForProperties.forEach((property, value) -> {
        setValueForEntityProperty(value, property, config, referredEntityAcosByName,
            changeOperation, rootAco);
      });
    }
  }

  private void setValueForEntityProperty(final Object newValue, Property<?> property,
      ApplyChangeObjectConfig config,
      Map<String, ApplyChangeOperation> referredEntityAcosByName, ChangeOperation changeOperation,
      ApplyChangeOperation rootAco) {
    Object valueToSet = newValue;
    if (!property.type().isAssignableFrom(valueToSet.getClass())) {
      valueToSet = transferService.convert(valueToSet, property.type());
    }

    if (property instanceof PropertyOwned) {
      setProperty(rootAco, property, valueToSet);
    } else if (property instanceof PropertyRef) {

      PropertyRef<?> propertyRef = (PropertyRef<?>) property;
      ApplyChangeOperation referredAco =
          getReferredAco(propertyRef, referredEntityAcosByName, changeOperation, config);
      PropertyOwned<?> targetOwnedProperty = propertyRef.getReferredOwnedProperty();
      setProperty(referredAco, targetOwnedProperty, valueToSet);
    } else {
      throw new RuntimeException("Unhandled property subtype");
    }
  }

  private String getPropertyNameFromChange(String parentName, ChangeItem pChange) {
    String propertyName = ObjectUtils.isEmpty(parentName) ? pChange.getName()
        : parentName + "." + pChange.getName();
    return propertyName;
  }

  private ApplyChangeOperation getReferredAco(
      PropertyRef<?> propertyRef,
      Map<String, ApplyChangeOperation> referredEntityAcosByName,
      ChangeOperation changeOperation,
      ApplyChangeObjectConfig config) {



    List<Reference<?, ?>> joinReferences = propertyRef.getJoinReferences();
    ApplyChangeOperation referredAco = null;
    ApplyChangeOperation parentAco = referredEntityAcosByName.get("root");
    String referenceName = "root";
    String rootUuid = parentAco.getUniqueId();

    for (Reference<?, ?> reference : joinReferences) {
      referenceName = referenceName + "/" + reference.getName();

      referredAco = referredEntityAcosByName.get(referenceName);
      if (referredAco == null) {
        EntityDefinition targetEntityDef = reference.getTarget();
        String referenceUuId = referenceName.replaceFirst("root", rootUuid);
        referredAco = new ApplyChangeOperation(TableDatas.of(targetEntityDef), changeOperation);
        referredAco.setUniqueId(referenceUuId);
        referredEntityAcosByName.put(referenceName, referredAco);
        parentAco.preCalls().call(referredAco);

        Function<Object, Map<Property<?>, Object>> idProvider = null;
        Property<?> targetUuidProp = null;
        ReferenceDescriptor referenceDescriptor =
            config.getReferenceDescriptor(reference.getTarget().entityDefName());
        if (referenceDescriptor != null) {
          idProvider = referenceDescriptor.getEntityPrimaryKeyIdProvider();
          targetUuidProp = referenceDescriptor.getEntityUuidProperty();
        } else {
          targetUuidProp = reference.joins().get(0).getTargetProperty();
        }


        if (idProvider != null) {
          Map<Property<?>, Object> idValuesByProperty = idProvider.apply(referenceUuId);

          // FIXME can not get the value from the map when the key is a property proxy
          // for (Join<?> join : reference.joins()) {
          // Object idValue = idValuesByProperty.get(join.getTargetProperty());
          // if (idValue == null) {
          // throw new IllegalStateException("null value can not be provided as join property!");
          // }
          // setProperty(parentAco, join.getSourceProperty(), idValue);
          // setProperty(referredAco, join.getTargetProperty(), idValue);
          // }

          for (Entry<Property<?>, Object> entry : idValuesByProperty.entrySet()) {
            Object idValue = entry.getValue();
            if (idValue == null) {
              throw new IllegalStateException("null value can not be provided as join property!");
            }
            Property<?> targetProp = entry.getKey();

            Join<?> join = reference.joins().stream()
                .filter(j -> j.getTargetProperty().getName().equals(targetProp.getName()))
                .findAny()
                .orElseThrow(
                    () -> new IllegalStateException(
                        "There is no join property for the configured id "
                            + "property: " + targetProp.getName()));

            setProperty(parentAco, join.getSourceProperty(), idValue);
            setProperty(referredAco, targetProp, idValue);
          }

          if (ChangeOperation.CREATE == changeOperation) {
            setProperty(referredAco, targetUuidProp, referenceUuId);
          }
        } else {
          Property<?> sourceUuidProp = reference.joins().get(0).getSourceProperty();
          setProperty(parentAco, sourceUuidProp, referenceUuId);
          setProperty(referredAco, targetUuidProp, referenceUuId);
        }

      }

      parentAco = referredAco;
    }
    return referredAco;
  }

  private void setProperty(ApplyChangeOperation aco, Property<?> property, Object value) {
    try {
      aco.getTableData().addColumnOwn(property);
      aco.getRow().setObject(property, value);
    } catch (Exception e) {
      String msg = String.format(
          "The given value can not be set! Value: [%1$s] of type [%2$s]. "
              + "Target property: [%3$s] of type [%4$s]",
          value.toString(), value.getClass().toString(), property.getUri(),
          property.type().toString());
      throw new IllegalStateException(msg, e);
    }
  }

  private String createPotentialContainmentId(String parentId, String propertyName, int idx) {
    StringBuilder sb = new StringBuilder()
        .append(parentId)
        .append("/")
        .append(propertyName);

    if (idx >= 0) {
      sb.append("/").append(idx);
    }

    return sb.toString();
  }

  @SuppressWarnings("unchecked")
  private <T> String getObjectId(T object, String containmentId) {
    String objectId = containmentId;
    if (objectId == null) {
      // should fall to this only in case of the root object
      ObjectDefinition<T> referredObjectDef =
          (ObjectDefinition<T>) objectApi.definition(object.getClass());
      objectId = referredObjectDef.getId(object);
    }
    if (objectId == null) {
      throw new IllegalStateException("The actual ID is null or there is no id getter for the "
          + "object! Object class: " + object.getClass().getName());
    }
    return objectId;
  }

  private ChangeOperation getChangeOperation(ChangeState operation) {
    switch (operation) {
      case DELETED:
        return ChangeOperation.DELETE;
      case MODIFIED:
        return ChangeOperation.MODIFY;
      case NEW:
        return ChangeOperation.CREATE;
      case NOP:
      default:
        return null;
    }
  }

  public void initConfigs() throws Exception {
    if (configFactories == null) {
      throw new IllegalStateException("There is no ApplyChangeObjectConfig factories configured for"
          + "the service instance! Call the applyChange() method with explicit ApplyChangeObjectConfig"
          + "or set the proper configuration factories for the service!");
    }
    configsByName = configFactories.stream()
        .map(factory -> factory.get())
        .collect(Collectors.toMap(ApplyChangeObjectConfig::getName, Function.identity()));
  }

  @Override
  public void createBean(Object newBean, Map<Class<?>, ApiBeanDescriptor> descriptor,
      ApplyChangeObjectConfig configuration) throws Exception {
    newBean = ApiObjectRef.unwrapObject(newBean);
    ApiObjectRef apiObjRef = new ApiObjectRef(null, newBean, descriptor);
    ObjectChange change = apiObjRef.renderAndCleanChanges().orElse(null);
    applyChange(change, newBean, configuration);
  }

  @Override
  public void createBean(Object newBean, Map<Class<?>, ApiBeanDescriptor> descriptor)
      throws Exception {
    ApplyChangeObjectConfig config = getConfig(newBean);
    createBean(newBean, descriptor, config);
  }

  @Override
  public void updateBean(Object oldBean, Object newBean,
      Map<Class<?>, ApiBeanDescriptor> descriptor, ApplyChangeObjectConfig configuration)
      throws Exception {
    oldBean = ApiObjectRef.unwrapObject(oldBean);
    newBean = ApiObjectRef.unwrapObject(newBean);
    ApiObjectRef apiObjRef = new ApiObjectRef(null, oldBean, descriptor);
    apiObjRef.renderAndCleanChanges();
    apiObjRef.mergeObject(newBean);
    ObjectChange change = apiObjRef.renderAndCleanChanges().orElse(null);
    if (change != null && change.hasChange()) {
      applyChange(change, newBean);
    }
  }

  @Override
  public void updateBean(Object oldBean, Object newBean,
      Map<Class<?>, ApiBeanDescriptor> descriptor) throws Exception {
    ApplyChangeObjectConfig config = getConfig(newBean);
    updateBean(oldBean, newBean, descriptor, config);
  }


}
