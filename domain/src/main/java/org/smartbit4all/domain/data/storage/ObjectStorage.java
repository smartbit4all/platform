package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import org.smartbit4all.api.storage.bean.ObjectReferenceList;

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
  public URI save(T object, URI uri);

  /**
   * Save the object with the given URI.
   */
  public URI save(T object);

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
  public Optional<ObjectReferenceList> loadReferences(URI uri, String typeClassName);

  /**
   * Load the object with the given URI.
   */
  public Optional<T> load(URI uri);

  /**
   * Load the objects with the given URI.
   */
  public List<T> load(List<URI> uris);

  /**
   * List all data objects. Warning: can be high amount of data in the result, use carefully!
   * 
   * @return All objects stored with the type T
   */
  public List<T> loadAll();

  /**
   * Delete the data with the given URI.
   */
  public boolean delete(URI uri);

  public ObjectUriProvider<T> getUriProvider();

  public URI getObjectUri(T Object);

  ObjectStorage<T> setUriMutator(BiConsumer<T, URI> mutator);

}
