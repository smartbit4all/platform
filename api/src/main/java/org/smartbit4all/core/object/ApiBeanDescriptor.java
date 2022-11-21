package org.smartbit4all.core.object;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Describes a bean with the detail referenced api objects.
 * 
 * @author Peter Boros
 */
public class ApiBeanDescriptor {

  Class<?> clazz;

  private Set<Class<?>> allApiBeanClass;

  /**
   * The set contains the types (classes) of the properties that must be assumed as value and not as
   * embedded object. This is the default set that is used if it is not set manually.
   */
  protected static final Set<Class<?>> defaultAsValueContainedClasses = new HashSet<>();

  static {
    defaultAsValueContainedClasses.add(BigDecimal.class);
    defaultAsValueContainedClasses.add(Boolean.class);
    defaultAsValueContainedClasses.add(Date.class);
    defaultAsValueContainedClasses.add(Double.class);
    defaultAsValueContainedClasses.add(Integer.class);
    defaultAsValueContainedClasses.add(LocalDate.class);
    defaultAsValueContainedClasses.add(LocalDateTime.class);
    defaultAsValueContainedClasses.add(LocalTime.class);
    defaultAsValueContainedClasses.add(Long.class);
    defaultAsValueContainedClasses.add(String.class);
    defaultAsValueContainedClasses.add(URI.class);
    defaultAsValueContainedClasses.add(UUID.class);
  }

  public ApiBeanDescriptor(Class<?> clazz, Set<Class<?>> allApiBeanClass) {
    super();
    this.clazz = clazz;
    this.allApiBeanClass = allApiBeanClass;
  }

  public final Set<Class<?>> getAllApiBeanClass() {
    return allApiBeanClass;
  }

  public static final Map<Class<?>, ApiBeanDescriptor> of(Set<Class<?>> apiBeans) {
    return of(apiBeans, null);
  }

  public static final Map<Class<?>, ApiBeanDescriptor> of(Set<Class<?>> apiBeans,
      Set<Class<?>> asValueContainedClasses) {
    Map<Class<?>, ApiBeanDescriptor> result = new HashMap<>();
    for (Class<?> class1 : apiBeans) {
      ApiBeanDescriptor descriptor = new ApiBeanDescriptor(class1, apiBeans);
      result.put(class1, descriptor);
    }
    return result;
  }

  /**
   * Check whether a given class is a reference for this domain object.
   * 
   * @param propertyType The property type.
   * @return
   */
  public final boolean isReferenceType(Class<?> propertyType) {
    return getAllApiBeanClass().contains(propertyType);
  }

  public static final boolean isReferenceByDefault(Class<?> propertyType) {
    return !propertyType.isPrimitive() && !defaultAsValueContainedClasses.contains(propertyType);
  }

}
