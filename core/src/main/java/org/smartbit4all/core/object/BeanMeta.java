package org.smartbit4all.core.object;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.smartbit4all.core.utility.StringConstant;

/**
 * The meta is the result of discovering the clazz over the reflection API.
 * 
 * @author Peter Boros
 */
class BeanMeta {

  private final Class<?> clazz;

  private final Map<String, PropertyMeta> properties = new HashMap<>();

  BeanMeta(Class<?> clazz) {
    super();
    this.clazz = clazz;
  }

  public final Class<?> getClazz() {
    return clazz;
  }

  public final Map<String, PropertyMeta> getProperties() {
    return properties;
  }

  /**
   * Construct the description of the meta for logging and debugging purposes.
   * 
   * @return
   */
  public String getDescription() {
    StringBuilder sb = new StringBuilder();
    sb.append(clazz.getName());
    for (PropertyMeta propertyMeta : properties.values().stream()
        .sorted((c1, c2) -> c1.getName().compareTo(c2.getName())).collect(Collectors.toList())) {
      sb.append(StringConstant.NEW_LINE);
      sb.append(StringConstant.TAB);
      propertyMeta.appendTo(sb);
    }
    return sb.toString();
  }

}
