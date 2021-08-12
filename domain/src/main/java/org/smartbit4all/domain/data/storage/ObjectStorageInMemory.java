package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Simple Map based implementation of object storage.
 * It can be used for testing with storage. 
 * 
 * @author Zoltan Szegedi
 *
 * @param <T> Type of the object
 */
public class ObjectStorageInMemory<T> implements ObjectStorage<T> {

  private Map<URI, T> objects;
  
  private Function<T, URI> uriProvider;
  
  public ObjectStorageInMemory(Function<T, URI> uriProvider) {
    this.objects = new ConcurrentHashMap<>();
    this.uriProvider = uriProvider;
  }
  
  @Override
  public void save(T object, URI uri) throws Exception {
    objects.put(uri, object);
  }

  @Override
  public void save(T object) throws Exception {
    save(object, uriProvider.apply(object));
  }

  @Override
  public Optional<T> load(URI uri) throws Exception {
    return Optional.ofNullable(objects.get(uri));
  }

  @Override
  public List<T> load(List<URI> uris) throws Exception {
    List<T> result = new ArrayList<>();
    for(URI uri : uris) {
      Optional<T> loaded = load(uri);
      if(loaded.isPresent()) {
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

}
