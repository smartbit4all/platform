package org.smartbit4all.core.object.store;

import java.net.URI;

/**
 * Stores and retrieves objects by URIs 
 */
public interface ObjectStoreApi {

  void store(URI objectUri, Object object);
  
  Object get(URI objectUri);
  
  <T> T get(URI objectUri, Class<T> clazz);
  
}
