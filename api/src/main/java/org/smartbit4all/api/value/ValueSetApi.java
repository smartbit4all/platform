package org.smartbit4all.api.value;

import org.smartbit4all.api.value.bean.ValueSet;
import org.smartbit4all.api.value.bean.ValueSetDefinitionData;
import org.smartbit4all.api.value.bean.ValueSetExpression;

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
   * Evaluate the value set expression to construct the result definition data. The definition data
   * will be
   * 
   * @param expression
   * @return
   */
  ValueSet evaluate(ValueSetExpression expression);

  /**
   * Retrieve the values of a value set where the set is identified by an {@link Enum}.
   * 
   * @param clazz The type of the {@link Enum}.
   * @return The values in the set.
   */
  @SuppressWarnings("rawtypes")
  <T extends Enum> ValueSet valuesOf(Class<T> clazz);

  /**
   * Retrieve the values of a value set where the set is identified by a unique name in a global
   * value set reference map.
   * 
   * @param name The unique name of the value set.
   * @return The result {@link ValueSet}.
   */
  ValueSet valuesOf(String name);

}
