package org.smartbit4all.storage.fs;

import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class StorageFSTestApiImpl implements StorageFSTestApi {

  @Autowired
  StorageApi storageApi;

  @Autowired
  StorageFSTestApi self;

  @Override
  @Transactional("storageTx")
  public FSTestBean saveAndLoad(Storage storage, String testText) {
    self.doSomething();
    return null;
  }

  @Override
  @Transactional(transactionManager = "storageTx", propagation = Propagation.REQUIRES_NEW)
  public void doSomething() {}

}
