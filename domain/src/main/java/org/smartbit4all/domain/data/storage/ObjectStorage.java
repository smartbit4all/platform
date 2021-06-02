package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * ObjectStorage can store serialized objects, identified by their URIs.
 * 
 * @author Zoltan Szegedi
 *
 * @param <T> Type of the object, which can be stored with the ObjectStorage.
 */
public interface ObjectStorage<T> {

  /**
   * Save the object with the given URI.
   */
  public void save(T object, URI uri) throws Exception;

  /**
   * Save the object with the given URI.
   */
  public void save(T object) throws Exception;

  /**
   * Load the object with the given URI.
   */
  public Optional<T> load(URI uri) throws Exception;

  /**
   * Load the objects with the given URI.
   */
  public List<T> load(List<URI> uris) throws Exception;

  /**
   * Delete the data with the given URI.
   */
  public boolean delete(URI uri) throws Exception;

}
