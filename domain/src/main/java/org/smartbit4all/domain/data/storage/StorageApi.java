package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.Set;
import java.util.function.BiConsumer;
import javax.validation.constraints.NotNull;

/**
 * The storage api is the access for the {@link Storage} instances defined in the configurations of
 * the system.
 * 
 * TODO We must define more storage for the same class. They can be managed by
 * 
 * @author Peter Boros
 */
public interface StorageApi {

  /**
   * Retrieves the {@link Storage} instance responsible for persisting given class.
   * 
   * @param <T>
   * @param clazz The class.
   * @return The storage if it exists.
   */
  <T> Storage<T> get(Class<T> clazz);

  /**
   * Generic function to load the referenced objects for a given URI.
   * 
   * @param <T> The type of the referenced object we are looking for.
   * @param uri The uri of the object that has the references.
   * @param typeClass The type of the referenced objects. Must be a Java type available in the given
   *        JVM!
   * @return The set of referenced objects.
   */
  <T, R> Set<R> loadReferences(URI uri, @NotNull Class<T> clazz, @NotNull Class<R> typeClass);

  /**
   * Adds a new change listener. Typical use case is that ...Api subscribes and decide what to do
   * when the object has been changed.
   * 
   * @param onChange
   */
  <T, R> void onChange(Class<T> storageClass, Class<R> referencedClass,
      BiConsumer<T, Set<R>> onChange);

}
