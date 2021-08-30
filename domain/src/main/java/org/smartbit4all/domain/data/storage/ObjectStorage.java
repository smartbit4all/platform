package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.smartbit4all.api.storage.bean.ObjectReference;

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
  public URI save(T object, URI uri) throws Exception;

  /**
   * Save the object with the given URI.
   */
  public URI save(T object) throws Exception;

  /**
   * Save the references for the object in the request.
   * 
   * @param referenceRequest The request.
   */
  public void saveReferences(ObjectReferenceRequest referenceRequest);

  /**
   * Load the references for the object in the request.
   * 
   * @param uri The uri of the object.
   */
  public Set<ObjectReference> loadReferences(URI uri);

  /**
   * Load the object with the given URI.
   */
  public Optional<T> load(URI uri) throws Exception;

  /**
   * Load the objects with the given URI.
   */
  public List<T> load(List<URI> uris) throws Exception;

  /**
   * List all data objects. Warning: can be high amount of data in the result, use carefully!
   * 
   * @return All objects stored with the type T
   */
  public List<T> loadAll() throws Exception;

  /**
   * Delete the data with the given URI.
   */
  public boolean delete(URI uri) throws Exception;

  public ObjectUriProvider<T> getUriProvider();

}
