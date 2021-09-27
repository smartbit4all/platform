package org.smartbit4all.core.object;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Describes a bean with the detail referenced api objects.
 * 
 * @author Peter Boros
 */
public class ApiBeanDescriptor {

  Class<?> clazz;

  private Set<Class<?>> allApiBeanClass;

  public ApiBeanDescriptor(Class<?> clazz, Set<Class<?>> allApiBeanClass) {
    super();
    this.clazz = clazz;
    this.allApiBeanClass = allApiBeanClass;
  }

  public final Set<Class<?>> getAllApiBeanClass() {
    return allApiBeanClass;
  }

  public static final Map<Class<?>, ApiBeanDescriptor> of(Set<Class<?>> apiBeans) {
    Map<Class<?>, ApiBeanDescriptor> result = new HashMap<>();
    for (Class<?> class1 : apiBeans) {
      result.put(class1, new ApiBeanDescriptor(class1, apiBeans));
    }
    return result;
  }

}
