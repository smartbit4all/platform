package org.smartbit4all.api.value;

import java.net.URI;
import java.util.List;
import org.smartbit4all.api.value.bean.ValueSetData;
import org.smartbit4all.api.value.bean.ValueSetDefinitionData;
import org.smartbit4all.api.value.bean.ValueSetExpression;
import org.smartbit4all.api.view.bean.ValueSet;

/**
 * The value set api is responsible for the central value set registration of application tenant.
 * The {@link ValueSetDefinitionData} is a generic descriptor for the available set of values in the
 * application. {@link ValueSetExpression} can be constructed to define a value set based on others.
 * These features can be used to support the master data management of an application even if some
 * of the values are complex objects.
 *
 * @author Peter Boros
 */
public interface ValueSetApi {

  /**
   * The definition data can be retrieved by the namespace and qualified name.
   *
   * @param namespace
   * @param qualifiedName
   * @return The data or null if not found.
   */
  ValueSetDefinitionData getDefinitionData(String namespace, String qualifiedName);

  /**
   * The definition data can be retrieved by the qualified name in the "globalValueSets".
   *
   * @param qualifiedName
   * @return The data or null if not found.
   */
  ValueSetDefinitionData getDefinitionData(String qualifiedName);

  /**
   * Retrieve the values of a value set where the set is identified by an {@link Enum}.
   *
   * @param clazz The type of the {@link Enum}.
   * @return The values in the set.
   */
  @SuppressWarnings("rawtypes")
  <T extends Enum> ValueSetData valuesOf(Class<T> clazz);

  /**
   * Retrieve the values of a value set where the set is identified by a unique name in a global
   * value set reference map.
   *
   * @param name The unique name of the value set.
   * @return The result {@link ValueSetData}.
   */
  ValueSetData valuesOf(String name);

  /**
   * Retrieve the values of a value set where the set is identified by a unique name in a global
   * value set reference map.
   *
   * @param namespace The namespace of the value set.
   * @param name The unique name of the value set.
   * @param branchUri The branch to use to read values from, if applicable
   * @param useObjectNode If true, ValueSetData.values will be List<ObjectNode>
   * @return The result {@link ValueSetData}.
   */
  ValueSetData valuesOf(String namespace, String name, URI branchUri, boolean useObjectNode);

  default ValueSetData valuesOf(String namespace, String name) {
    return valuesOf(namespace, name, null, false);
  };

  /**
   * Retrieve the values of a value set where the set is identified by a unique name in a global
   * value set reference map.
   *
   * @param definitionData That defines the value set retrieval parameters.
   * @param branchUri THe branch to use to read values from, if applicable
   * @return The result {@link ValueSetData}.
   */
  ValueSetData valuesOf(ValueSetDefinitionData definitionData, URI branchUri,
      boolean useObjectNode);

  default ValueSetData valuesOf(ValueSetDefinitionData definitionData) {
    return valuesOf(definitionData, null, false);
  };

  /**
   * Saves the given value set.
   *
   * @param namespace
   * @param valueSetDef
   */
  void save(String namespace, ValueSetDefinitionData valueSetDef);

  /**
   * Saves the given value set into the global name space.
   *
   * @param valueSetDef
   */
  void save(ValueSetDefinitionData valueSetDef);

  /**
   * This function can extract a value from the objects in the {@link ValueSetData}.
   *
   * @param <T>
   * @param typeClass
   * @param valueSet
   * @param path
   * @return
   */
  <T> List<T> getValues(Class<T> typeClass, ValueSetData valueSet, String... path);

  /**
   * Creates a ValueSet where values will be List<Value>,
   *
   * @param namespace
   * @param name
   * @param branchUri
   * @param path This path will be used to fill displayValue
   * @return
   */
  ValueSet getValueSetWithValues(String namespace, String name, URI branchUri, String... path);

}
