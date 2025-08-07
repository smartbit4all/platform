package org.smartbit4all.core.utility;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ObjectDefinitionUtils {

  /**
   * The set contains the types (classes) of the properties that must be assumed as value and not as
   * embedded object. This is the default set that is used if it is not set manually.
   */
  public static final Set<Class<?>> defaultAsValueClasses =
      Set.of(BigDecimal.class, Byte.class, Boolean.class, Character.class, Date.class,
          java.sql.Date.class, Double.class, Float.class, Integer.class, LocalDate.class,
          LocalDateTime.class,
          LocalTime.class, OffsetDateTime.class, Long.class, Short.class, String.class, URI.class,
          UUID.class);

  public static final boolean isAssignableFromDefaultValueClass(Object o) {
    return List.class.isAssignableFrom(o.getClass());
  }

  /**
   * Check if the given object is a value object that shouldn't be converted to a map or used as
   * Bean.
   * 
   * @param o
   * @return
   */
  public static final boolean isValue(Object o) {
    return o == null ? true
        : (defaultAsValueClasses.contains(o.getClass()) || isAssignableFromDefaultValueClass(o));
  }

  private ObjectDefinitionUtils() {}
}
