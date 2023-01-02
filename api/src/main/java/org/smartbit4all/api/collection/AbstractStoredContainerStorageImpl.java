package org.smartbit4all.api.collection;

import java.lang.ref.WeakReference;
import java.net.URI;
import org.smartbit4all.domain.data.storage.Storage;

abstract class AbstractStoredContainerStorageImpl {

  protected WeakReference<Storage> storageRef;

  protected URI uri;

  protected String name;

  protected AbstractStoredContainerStorageImpl(Storage storage, URI uri, String name) {
    super();
    storageRef = new WeakReference<>(storage);
    this.uri = uri;
    this.name = name;
  }

}
