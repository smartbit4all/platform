package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.smartbit4all.api.storage.bean.ObjectReferenceList;

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
  private Map<URI, Map<String, ObjectReferenceList>> references;

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
    return save(object, getObjectUri(object));
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
    ObjectReferenceList refList =
        references.computeIfAbsent(referenceRequest.getObjectUri(), u -> new HashMap<>())
            .computeIfAbsent(referenceRequest.getTypeClassName(),
                s -> new ObjectReferenceList().referenceTypeClass(s));
    referenceRequest.updateReferences(refList);
  }

  @Override
  public ObjectReferenceList loadReferences(URI uri, String typeClassName) {
    Map<String, ObjectReferenceList> map = references.get(uri);
    if (map != null) {
      return map.get(typeClassName);
    }
    return null;
  }

}
