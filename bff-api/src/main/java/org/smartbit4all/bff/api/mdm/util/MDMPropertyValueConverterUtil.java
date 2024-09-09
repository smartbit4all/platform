package org.smartbit4all.bff.api.mdm.util;


import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.smartbit4all.core.object.ObjectApi;

public class MDMPropertyValueConverterUtil {

  private MDMPropertyValueConverterUtil() {}

  public static Object objStringValueConvertToListString(Map<String, Object> modelMap,
      String property, String separator) {
    Objects.requireNonNull(modelMap, "modelMap can not be null!");
    Objects.requireNonNull(property, "property can not be null!");
    Objects.requireNonNull(separator, "separator can not be null!");

    Object value = modelMap.get(property);
    if (value instanceof String) {
      modelMap.remove(property);
      String[] split = value.toString().split(separator);
      List<String> stringList = Arrays.asList(split);
      modelMap.put(property, stringList);
    }
    return modelMap;
  }

  public static Object objListStringValueConvertToString(Object modelMap,
      String property, ObjectApi objectApi, String separator) {
    Objects.requireNonNull(modelMap, "obj can not be null!");
    Objects.requireNonNull(property, "property can not be null!");
    Objects.requireNonNull(objectApi, "objectApi can not be null!");
    Objects.requireNonNull(separator, "separator can not be null!");

    Map<String, Object> objMap = objectApi.asType(Map.class, modelMap);
    Object value = objMap.get(property);
    if (value instanceof String) {
      return modelMap;
    }
    objMap.remove(property);
    objMap.put(property, String.join(separator, (Collection) value));
    return objectApi.asType(Object.class, objMap);
  }
}
