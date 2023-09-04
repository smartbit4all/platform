package org.smartbit4all.core.object;

import java.util.Map;
import java.util.function.UnaryOperator;
import org.smartbit4all.api.object.bean.ObjectNodeData;
import org.smartbit4all.api.storage.bean.ObjectAspect;

public interface ObjectNodeAspects {

  /**
   * This function set or modify the existing aspects object of the given object. This modification
   * will be saved into the {@link ObjectNodeData#ASPECTS} field.
   * 
   * @param <T>
   * @param aspectName The aspect name like ACL, VALIDATIONS etc.
   * @param clazz The class of the given aspects.
   * @param update The unary operation to update. If the given aspect is not exists then it will get
   *        null as parameter. If it returns null then the given aspect is going to be cleared. If
   *        the input is null and we return an object then it will be set.
   */
  public <T> void modify(String aspectName, Class<T> clazz, UnaryOperator<T> update);

  /**
   * Retrieve the aspect as the class in the parameter.
   * 
   * @param <T> The parameter class type.
   * @param aspectName The aspect name like ACL, VALIDATIONS etc.
   * @param clazz The class of the aspect.
   * @return The return value is not modifiable use only to read.
   */
  public <T> T get(String aspectName, Class<T> clazz);

  /**
   * Retrieve all the aspects related to the given object.
   * 
   * @return Null if the aspects are empty.
   */
  public Map<String, ObjectAspect> get();

}
