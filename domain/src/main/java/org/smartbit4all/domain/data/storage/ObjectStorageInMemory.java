package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.smartbit4all.api.storage.bean.ObjectReference;

/**
 * Simple Map based implementation of object storage. It can be used for testing with storage.
 * 
 * @author Zoltan Szegedi
 *
 * @param <T> Type of the object
 */
public class ObjectStorageInMemory<T> extends ObjectStorageImpl<T> {

  /**
   * The object in the storage by their URI as key.
   */
  private Map<URI, T> objects;

  /**
   * The references in the storage by their URI as key.
   */
  private Map<URI, Set<ObjectReference>> references;

  public ObjectStorageInMemory(Function<T, URI> uriAccessor, ObjectUriProvider<T> uriProvider) {
    super(uriAccessor, uriProvider);
    this.objects = new ConcurrentHashMap<>();
    this.references = new ConcurrentHashMap<>();
  }

  @Override
  public URI save(T object, URI uri) throws Exception {
    URI result = constructUri(object, uri);
    objects.put(result, object);
    return result;
  }

  @Override
  public URI save(T object) throws Exception {
    URI result = constructUri(object, uriAccessor.apply(object));
    return save(object, result);
  }

  @Override
  public Optional<T> load(URI uri) throws Exception {
    return Optional.ofNullable(objects.get(uri));
  }

  @Override
  public List<T> load(List<URI> uris) throws Exception {
    List<T> result = new ArrayList<>();
    for (URI uri : uris) {
      Optional<T> loaded = load(uri);
      if (loaded.isPresent()) {
        result.add(loaded.get());
      }
    }
    return result;
  }

  @Override
  public List<T> loadAll() throws Exception {
    return new ArrayList<>(objects.values());
  }

  @Override
  public boolean delete(URI uri) throws Exception {
    return objects.remove(uri) != null ? true : false;
  }

  @Override
  public void saveReferences(ObjectReferenceRequest referenceRequest) {
    if (referenceRequest == null) {
      return;
    }
    Set<ObjectReference> refSet =
        references.computeIfAbsent(referenceRequest.getObjectUri(), u -> new HashSet<>());
    referenceRequest.updateReferences(refSet);
    if (refSet.isEmpty()) {
      references.remove(referenceRequest.getObjectUri());
    }
  }

  @Override
  public Set<ObjectReference> loadReferences(URI uri) {
    return references.getOrDefault(uri, Collections.emptySet());
  }

}
