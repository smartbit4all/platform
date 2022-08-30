package org.smartbit4all.api.object;

import java.net.URI;
import org.smartbit4all.api.contribution.ContributionApiImpl;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.springframework.beans.factory.annotation.Autowired;

public class BranchContributionApiStorageImpl extends ContributionApiImpl
    implements BranchContributionApi {

  public BranchContributionApiStorageImpl() {
    super(BranchContributionApiStorageImpl.class.getName());
  }

  @Autowired
  private StorageApi storageApi;

  @Override
  public URI saveAsNew(URI versionUri) {
    Storage storage = storageApi.get(versionUri.getScheme());
    if (storage == null) {
      throw new IllegalArgumentException(
          "Unable to find the proper storage for the uri " + versionUri);
    }
    return storage.saveAsNew(storage.load(versionUri).getObject());
  }

}
