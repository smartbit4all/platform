package org.smartbit4all.core.object;

import java.net.URI;
import org.smartbit4all.api.object.bean.ObjectDefinitionData;

/**
 * Collects and handles object definitions for API objects.
 * 
 * @author matea
 *
 */
public interface ObjectDefinitionApi {

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
   * The fully qualified name of the object class tries to identify the {@link ObjectDefinition}.
   * 
   * @param className The object class name.
   * @return The {@link ObjectDefinition} if it was identified by the existing source. If it is not
   *         found then we try to read the {@link ObjectDefinitionData} from the storage or creates
   *         a new definition it is not found. The newly created definition can be extended by the
   *         ObjectDefinitionBuilder that will save the definition into the storage.
   */
  ObjectDefinition<?> definition(String className);

  /**
   * @return The default serializer for the objects.
   */
  ObjectSerializer getDefaultSerializer();

  void reloadDefinitionData(ObjectDefinition<?> definition);

  void saveDefinitionData(ObjectDefinition<?> definition);

}
