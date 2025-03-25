package org.smartbit4all.api.object;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import org.smartbit4all.api.contribution.ContributionApiImpl;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.domain.data.storage.ObjectStorageImpl;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.springframework.beans.factory.annotation.Autowired;

public class ModifyContributionApiStorageImpl extends ContributionApiImpl
    implements ModifyContributionApi {

  public ModifyContributionApiStorageImpl() {
    super(ModifyContributionApiStorageImpl.class.getName());
  }

  @Autowired
  private StorageApi storageApi;

  @Override
  public URI saveAsNew(ObjectDefinition<?> objectDefinition, String storageScheme,
      ObjectNode objectNode) {
    Storage storage = storageApi.get(storageScheme);
    StorageObject<?> storageObject = storage.fromDefinition(objectDefinition);
    // If we have an explicit uri and the id path is set then we construct th uri for the storage.
    if (objectDefinition.getTimePath() != null) {
      // Read the string value from the map
      LocalDateTime timeValue =
          ObjectStorageImpl.getTimeOf(objectDefinition, objectNode, LocalDateTime.class);
      storageObject.setCreatedAt(
          ObjectStorageImpl.getTimeOf(objectDefinition, objectNode, OffsetDateTime.class));
      objectNode.setValue(
          storage.constructUri(objectDefinition, storageObject.getUuid(), null, timeValue),
          ObjectDefinition.URI_PROPERTY);
    } else if (objectDefinition.getIdPath() != null) {
      // Read the string value from the map
      String idValue = objectNode.getValueAsString(objectDefinition.getIdPath());
      objectNode.setValue(storage.constructUriForId(objectDefinition, idValue),
          ObjectDefinition.URI_PROPERTY);
    }
    storageObject.asMap().setObjectAsMap(objectNode.getObjectAsMap());
    storageObject.setAspects(objectNode.aspects().get());
    storage.save(storageObject);
    return storageObject.getVersionUri();
  }

  @Override
  public URI update(URI versionUri, ObjectNode objectNode) {
    // We always update the latest version.
    StorageObject<?> storageObject = storageApi.load(versionUri);
    storageObject.asMap().setObjectAsMap(objectNode.getObjectAsMap());
    storageObject.setAspects(objectNode.aspects().get());
    return storageObject.getStorage().saveVersion(storageObject);
  }

  // TODO delete?

}
