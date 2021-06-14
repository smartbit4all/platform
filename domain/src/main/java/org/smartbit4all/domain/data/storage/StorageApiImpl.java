package org.smartbit4all.domain.data.storage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

}
