package org.smartbit4all.domain.service.modify;

import java.util.List;
import java.util.Objects;
import org.smartbit4all.core.object.ChangeState;
import org.smartbit4all.core.object.CollectionChange;
import org.smartbit4all.core.object.CollectionObjectChange;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectChange;
import org.smartbit4all.core.object.ObjectChangeSimple;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.PropertyChange;
import org.smartbit4all.core.object.ReferenceChange;
import org.smartbit4all.core.object.ReferencedObjectChange;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.TableDatas;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertyRef;
import org.smartbit4all.domain.meta.PropertySet;
import org.smartbit4all.domain.service.modify.ApplyChangeObjectConfig.CollectionMappingItem;
import org.smartbit4all.domain.service.modify.ApplyChangeObjectConfig.PropertyMappingItem;
import org.smartbit4all.domain.service.modify.ApplyChangeObjectConfig.ReferenceMappingItem;
import org.smartbit4all.domain.service.modify.ApplyChangeOperation.ChangeOperation;
import org.smartbit4all.domain.service.transfer.TransferService;

public class ApplyChangeServiceImpl implements ApplyChangeService {

  private ObjectApi objectApi;

  private TransferService transferService;

  public ApplyChangeServiceImpl(ObjectApi objectApi, TransferService transferService) {
    this.objectApi = objectApi;
    this.transferService = transferService;
  }

  @Override
  public void applyChange(ObjectChange objectChange, Object object,
      ApplyChangeObjectConfig configuration) throws Exception {
    ApplyChangeOperation aco =
        createApplyChangeOperation(objectChange, configuration, object, null);
    aco.execute();
  }

  @Override
  public void applyChange(ObjectChange objectChange, Object object) throws Exception {
    ApplyChangeObjectConfig config = getConfig(object);
    applyChange(objectChange, object, config);
  }

  private ApplyChangeObjectConfig getConfig(Object object) {
    // TODO Auto-generated method stub
    return null;
  }

  private ApplyChangeOperation createApplyChangeOperation(ObjectChange objectChange,
      ApplyChangeObjectConfig config, Object rootObject,
      String potetntialContainmentId) {
    EntityDefinition rootEntity = config.getRootEntity();
    ChangeOperation changeOperation = getChangeOperation(objectChange.getOperation());
    if (changeOperation == null || !objectChange.hasChange()) {
      return null;
    }
    TableData<EntityDefinition> rootTable = TableDatas.of(rootEntity);
    PropertySet primaryKeys = rootEntity.PRIMARYKEYDEF();
    if (primaryKeys.size() > 1) {
      throw new IllegalStateException("Not handled entity: it has multiple primary keys!");
    }
    String rootId = getObjectId(rootObject, potetntialContainmentId);
    Property<?> primaryKey = primaryKeys.iterator().next();
    rootTable.addColumnOwn(primaryKey);
    DataRow rootRow = rootTable.addRow();
    rootRow.setObject(primaryKey, rootId);

    ApplyChangeOperation aco = new ApplyChangeOperation(rootTable, changeOperation);

    for (PropertyChange pChange : objectChange.getProperties()) {
      Object newValue = pChange.getNewValue();
      PropertyMappingItem propertyMappingItem = config.getPropertyMappings().get(pChange.getName());
      if (propertyMappingItem == null) {
        continue;
      }
      Property<?> property = propertyMappingItem.getProperty();
      if (!property.type().isAssignableFrom(newValue.getClass())) {
        newValue = transferService.convert(newValue, property.type());
      }
      if (!(property instanceof PropertyRef)) {
        rootTable.addColumnOwn(property);
        rootRow.setObject(property, newValue);
      } else {
        // it is a containment

        // TODO handle containment
        // PropertyRef<?> propertyRef = (PropertyRef<?>) property
        // propertyRef.getJoinReferences().get(0).getSource()
      }
    }

    // TODO handle when referenced object's field points the the master entity property

    // manage references: add recursively created ApplyChangeOperation to preCalls
    List<ReferenceChange> references = objectChange.getReferences();
    for (int i = 0; i < references.size(); i++) {
      ReferenceChange rChange = references.get(i);
      ReferenceMappingItem referenceMappingItem =
          config.getReferenceMappings().get(rChange.getName());
      if (referenceMappingItem == null) {
        continue;
      }

      // create ApplyChangeOperation with recursive method
      ObjectChange changedReference = rChange.getChangedReference();
      ApplyChangeObjectConfig referenceMapping = referenceMappingItem.getOc2AcoMapping();

      ReferencedObjectChange referencedObjectChange = objectChange.getReferencedObjects().get(i);
      Object changedReferredObject = referencedObjectChange.getChange().getObject();

      String potentialContainmentId = createPotentialContainmentId(rootId, rChange.getName(), -1);
      ApplyChangeOperation referenceAco =
          createApplyChangeOperation(changedReference, referenceMapping, changedReferredObject,
              potentialContainmentId);
      if (referenceAco != null) {

        String referredId = getObjectId(changedReferredObject, potentialContainmentId);

        // set the referenced uid to the tabeleData
        Objects.requireNonNull(referredId, "Unique ID of referred object can not be null!"); // FIXME
        rootTable.addColumnOwn(referenceMappingItem.getRefferringRootProperty());
        rootRow.setObject(referenceMappingItem.getRefferringRootProperty(), referredId);

        // add preCall
        aco.preCalls().call(referenceAco);
      }
    }

    List<CollectionChange> collections = objectChange.getCollections();
    for (int i = 0; i < collections.size(); i++) {
      CollectionChange cChange = collections.get(i);
      CollectionObjectChange collectionObjectChange = objectChange.getCollectionObjects().get(i);
      CollectionMappingItem collectionMappingItem =
          config.getCollectionMappings().get(cChange.getName());
      if (collectionMappingItem == null) {
        continue;
      }

      List<ObjectChange> changes = cChange.getChanges();
      for (int j = 0; j < changes.size(); j++) {
        ObjectChange collectionChangeObject = changes.get(j);
        ObjectChangeSimple objectChangeSimple = collectionObjectChange.getChanges().get(j);
        Object collectionObject = objectChangeSimple.getObject();
        String potentialContainmentId = createPotentialContainmentId(rootId, cChange.getName(), j);
        ApplyChangeOperation collectionAco = createApplyChangeOperation(collectionChangeObject,
            collectionMappingItem.getOc2AcoMapping(),
            collectionObject,
            potentialContainmentId);

        if (collectionAco != null) {

          collectionAco.getTableData()
              .addColumnOwn(collectionMappingItem.getRefferringDetailProperty());
          collectionAco.getRow().setObject(collectionMappingItem.getRefferringDetailProperty(),
              rootId);

          aco.postCalls().call(collectionAco);
        }
      }

    }
    return aco;
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
      throw new IllegalStateException("There is no id getter for the object! "
          + "Object class: " + object.getClass().getName());
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

}
