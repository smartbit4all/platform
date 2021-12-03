package org.smartbit4all.storage.fs;

import org.smartbit4all.domain.data.storage.Storage;

/**
 * The test api for the storage test.
 * 
 * @author Peter Boros
 */
public interface StorageFSTestApi {

  FSTestBean saveAndLoad(
      Storage storage,
      String testText);

  void doSomething();

}
