package org.smartbit4all.api.object;

import java.net.URI;
import java.util.Map;
import org.smartbit4all.api.contribution.ContributionApiImpl;
import org.smartbit4all.core.object.ObjectDefinition;
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
      Map<String, Object> objectAsMap) {
    Storage storage = storageApi.get(storageScheme);
    StorageObject<?> storageObject = storage.instanceOf(objectDefinition.getClazz());
    storageObject.asMap().setObjectAsMap(objectAsMap);
    storage.save(storageObject);
    return storageObject.getVersionUri();
  }

  @Override
  public URI update(URI versionUri, Map<String, Object> objectAsMap) {
    // We always update the latest version.
    StorageObject<?> storageObject = storageApi.load(versionUri);
    storageObject.setObjectAsMap(objectAsMap);
    return storageObject.getStorage().saveVersion(storageObject);
  }

}