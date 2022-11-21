package org.smartbit4all.core.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This descriptor defines properties for a Class of an {@link ObjectNode}. This can be used to
 * define printout property list.
 * 
 * @author Peter Boros
 *
 */
public class ObjectProperties {

  private Map<Class<?>, List<String>> propertyMap = new HashMap<>();

  public ObjectProperties() {
    super();
  }

  public ObjectProperties property(Class<?> clazz, String propertyName) {
    propertyMap.computeIfAbsent(clazz, c -> new ArrayList<>()).add(propertyName);
    return this;
  }

  public final Map<Class<?>, List<String>> getPropertyMap() {
    return propertyMap;
  }

}
