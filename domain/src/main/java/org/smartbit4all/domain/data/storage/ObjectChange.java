package org.smartbit4all.domain.data.storage;

import java.net.URI;

/**
 * The object for the object change.
 * 
 * @author Peter Boros
 */
class ObjectChange<T> {

  T object;

  URI uri;

  enum Operation {
    CREATED, MODIFIED, DELETED
  }

}
