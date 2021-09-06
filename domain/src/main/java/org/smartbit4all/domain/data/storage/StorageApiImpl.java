package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
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
      return new HashSet<>(
          get(typeClass)
              .load(get(clazz).loadReferences(uri, typeClass.getName()).getReferences().stream()
                  .map(r -> URI.create(r.getReferenceId())).collect(Collectors.toList())));
    } catch (Exception e) {
      throw new RuntimeException(
          "Unable to load referenced objects for " + uri + " typeClass = " + typeClass.getName(),
          e);
    }
  }

  @Override
  public <T, R> void onChange(Class<T> storageClass, Class<R> referencedClass,
      BiConsumer<T, Set<R>> onChange) {
    Storage<T> storage = get(storageClass);
    storage.getObjectChangePublisher()
        .subscribe(
            c -> onChange.accept(c.object, loadReferences(c.uri, storageClass, referencedClass)));
  }

  // @Override
  // public <R> void onChange(Class<R> referencedClass,
  // BiConsumer<org.smartbit4all.domain.data.storage.T, Set<R>> onChange) {
  // // TODO Auto-generated method stub
  // super.onChange(referencedClass, onChange);
  // }
  //
  // /**
  // * Adds a new change listener. Typical use case is that ...Api subscribes and decide what to do
  // * when the object has been changed.
  // *
  // * @param onChange
  // */
  // public <R> void onChange(Class<R> referencedClass, BiConsumer<T, Set<R>> onChange) {
  // objectChangePublisher.subscribe(c -> onChange.accept(c.object, loadReferences(c.uri)));
  // }
  //
  // {
  // objectChangePublisher.subscribe(c -> onChange.accept(c.object, loadReferences(c.uri)));
  // }}

}
