package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.smartbit4all.api.storage.bean.ObjectReferenceList;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The storage api is the access for the {@link Storage} instances defined in the configurations of
 * the system.
 * 
 * @author Peter Boros
 */
public final class StorageApiImpl implements StorageApi, InitializingBean {

  /**
   * All the storages we have in the configuration. Autowired by spring.
   */
  @Autowired(required = false)
  private List<Storage<?>> storages;

  /**
   * The preprocessed storages identified by the class.
   */
  private Map<Class<?>, Storage<?>> storagesByClass = new HashMap<>();

  @Override
  public void afterPropertiesSet() throws Exception {
    if (storages != null) {
      for (Storage<?> storage : storages) {
        storagesByClass.put(storage.getClazz(), storage);
      }
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> Storage<T> get(Class<T> clazz) {
    return (Storage<T>) storagesByClass.get(clazz);
  }

  @Override
  public <T, R> Set<R> loadReferences(URI uri, Class<T> clazz, Class<R> typeClass) {
    try {

      Optional<ObjectReferenceList> optReferences =
          get(clazz).loadReferences(uri, typeClass.getName());

      List<URI> uriList = null;
      if (optReferences.isPresent()) {
        uriList = optReferences.get().getReferences().stream()
            .map(r -> URI.create(r.getReferenceId())).collect(Collectors.toList());
      } else {
        uriList = Collections.emptyList();
      }

      return new HashSet<>(
          get(typeClass)
              .load(uriList));
    } catch (Exception e) {
      throw new RuntimeException(
          "Unable to load referenced objects for " + uri + " typeClass = " + typeClass.getName(),
          e);
    }
  }

}
