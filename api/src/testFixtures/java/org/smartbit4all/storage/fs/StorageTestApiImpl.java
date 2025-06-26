package org.smartbit4all.storage.fs;

import java.net.URI;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.TransactionalStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Propagation;

public class StorageTestApiImpl implements StorageTestApi {

  @Autowired
  StorageApi storageApi;

  @Autowired
  @Lazy
  StorageTestApi self;

  @Autowired
  ObjectApi objectApi;

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
  public Boolean getRemovableItems(Object object, Object object2) {
    return Boolean.TRUE;
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

  @Override
  public void setFutureValue(String p1, Boolean error) {
    URI uri = objectApi.saveAsNew(StorageTestConfig.TESTSCHEME, new FSTestBean(p1));
    if (Boolean.TRUE == error) {
      StorageTestApi.futureValue.setValue(uri);
      throw new RuntimeException("Error in setFutureValue");
    }
    StorageTestApi.futureValue.setValue(uri);
  }

}
