package org.smartbit4all.core.object;

import java.net.URI;
import org.smartbit4all.api.object.bean.ObjectNodeData;

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
   * The object api tries to identify the {@link ObjectDefinition} based on the standard format of
   * the URI. The first part of the URI contains the alias of the {@link ObjectDefinition}.
   * 
   * @param objectUri The object URI that must match the standard.
   * @return The {@link ObjectDefinition} if it was identified or null if it was not found.
   */
  ObjectDefinition<?> definition(URI objectUri);

  /**
   * The object name tries to identify the {@link ObjectDefinition}.
   * 
   * @param className The object class name.
   * @return The {@link ObjectDefinition} if it was identified or null if it was not found.
   */
  ObjectDefinition<?> definition(String className);

  /**
   * The meta of the given bean.
   * 
   * @param apiClass
   * @return
   */
  BeanMeta meta(Class<?> apiClass);

  /**
   * @return The default serializer for the objects.
   */
  ObjectSerializer getDefaultSerializer();

  ObjectNode node(ObjectNodeData data);

  ObjectNode node(String storageScheme, Object object);

}