package org.smartbit4all.core.object;

/**
 * Collects the object definitions for the api objects.
 * 
 * @author Peter Boros
 */
public interface ObjectApi {

  /**
   * Get the definition for the given Class.
   * 
   * @param <T> The type of the class
   * @param clazz The class of the domain object (Java bean)
   * @return The definition of the given class.
   */
  <T> ObjectDefinition<T> definition(Class<T> clazz);

  /**
   * The meta of the given bean.
   * 
   * @param apiClass
   * @return
   */
  BeanMeta meta(Class<?> apiClass);

}
