/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.domain.meta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the collection of fields and properties that are dependents of the given computation.
 * It's meta level because we don't know what is the source of the {@link InputValue},
 * {@link OutputValue} and {@link InOutValue} parameters.
 * 
 * @author Peter Boros
 */
class ComputationParameterMeta {

  private static final Logger log = LoggerFactory.getLogger(ComputationParameterMeta.class);

  /**
   * The {@link InputValue}s including the {@link InOutValue}s. This field are annotated by
   * {@link PropertyWired} and {@link PropertyDynamic} annotations.
   */
  List<Field> inputs = new ArrayList<>();

  /**
   * The {@link OutputValue}s including the {@link InOutValue}s. This field are annotated by
   * {@link PropertyWired} and {@link PropertyDynamic} annotations.
   */
  List<Field> outputs = new ArrayList<>();

  /**
   * All the parameters that we have for the computation.
   */
  final List<Field> parameters;

  /**
   * The fields that points to computations that provide input data. They must run earlier then
   * this.
   */
  List<Field> inputProviders = new ArrayList<>();

  /**
   * The fields that points to computations that consume output data. They can run later the this.
   */
  List<Field> outputConsumers = new ArrayList<>();


  /**
   * Constructs the parameter meta for the computation logic.
   * 
   * @param parameters
   */
  ComputationParameterMeta(Set<Field> parameters) {
    super();
    this.parameters = new ArrayList<>(parameters);
  }



  /**
   * Analyzing the given logic by the field that defines input, output or in - out values. Collects
   * the input and the outputs in a separated list. The in - outs will be added to both. The
   * {@link InputProvider} and {@link OutputConsumer} annotated fields will be added to the
   * {@link #inputProviders} and {@link #outputConsumers} list.
   * 
   */
  final void fromFields() {
    for (Field field : parameters) {
      if (field.getType().isAssignableFrom(InputValue.class)) {
        inputs.add(field);
      }
      if (field.getType().isAssignableFrom(OutputValue.class)) {
        inputs.add(field);
      }
      if (field.isAnnotationPresent(InputProvider.class)
          && field.getType().isAssignableFrom(ComputationLogic.class)) {
        inputProviders.add(field);
      }
      if (field.isAnnotationPresent(OutputConsumer.class)
          && field.getType().isAssignableFrom(ComputationLogic.class)) {
        outputConsumers.add(field);
      }
    }
  }

  /**
   * The inputs defined by the fields of the logic.
   * 
   * @param logic The logic instance.
   * @return The {@link Iterable} for the values.
   */
  Iterable<InputValue<?>> inputs(ComputationLogic logic) {
    return values(logic, inputs);
  }

  /**
   * The outputs defined by the fields of the logic.
   * 
   * @param logic The logic instance.
   * @return The {@link Iterable} for the values.
   */
  Iterable<OutputValue<?>> outputs(ComputationLogic logic) {
    return values(logic, outputs);
  }

  /**
   * The input providers defined by the fields of the logic.
   * 
   * @param logic The logic instance.
   * @return The {@link Iterable} for the values.
   */
  Iterable<ComputationLogic> inputProviders(ComputationLogic logic) {
    return values(logic, inputProviders);
  }

  /**
   * The output consumers defined by the fields of the logic.
   * 
   * @param logic The logic instance.
   * @return The {@link Iterable} for the values.
   */
  Iterable<ComputationLogic> outputConsumers(ComputationLogic logic) {
    return values(logic, outputConsumers);
  }

  /**
   * The values stored in the list of {@link Field}s.
   * 
   * @param <T> The type of the fields must be the same.
   * @param obj The object to evaluate.
   * @param fields The field list.
   * @return
   */
  private final <T> Iterable<T> values(Object obj, List<Field> fields) {
    Iterator<Field> iterField = fields.iterator();
    return new Iterable<T>() {

      @Override
      public Iterator<T> iterator() {
        return new Iterator<T>() {

          @Override
          public boolean hasNext() {
            return iterField.hasNext();
          }

          @Override
          public T next() {
            Field field = iterField.next();
            try {
              field.setAccessible(true);
              @SuppressWarnings("unchecked")
              T value = (T) field.get(obj);
              return value;
            } catch (Exception e) {
              log.debug("The {}.{} field can't be read", obj.getClass().getName(), field.getName(),
                  e);
            } finally {
              field.setAccessible(false);
            }
            return null;
          }
        };
      }

    };
  }

}
