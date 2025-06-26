package org.smartbit4all.storage.fs;

import java.net.URI;
import org.smartbit4all.core.utility.concurrent.FutureValue;
import org.smartbit4all.domain.data.storage.Storage;

/**
 * The test api for the storage test.
 * 
 * @author Peter Boros
 */
public interface StorageTestApi {

  public static FutureValue<URI> futureValue = new FutureValue<>();

  FSTestBean saveAndLoad(
      Storage storage,
      String testText);

  void doSomething();

  Boolean getRemovableItems(Object object, Object object2);

  void setFutureValue(String p1, Boolean error);

}
