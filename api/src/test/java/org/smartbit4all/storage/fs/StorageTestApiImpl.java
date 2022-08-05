package org.smartbit4all.storage.fs;

import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.TransactionalStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;

public class StorageTestApiImpl implements StorageTestApi {

  @Autowired
  StorageApi storageApi;

  @Autowired
  StorageTestApi self;

  @Override
  @TransactionalStorage
  public FSTestBean saveAndLoad(Storage storage, String testText) {
    try {
      Thread.sleep(50);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    self.doSomething();
    return null;
  }

  @Override
  @TransactionalStorage(propagation = Propagation.REQUIRES_NEW)
  public void doSomething() {
    try {
      Thread.sleep(50);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
