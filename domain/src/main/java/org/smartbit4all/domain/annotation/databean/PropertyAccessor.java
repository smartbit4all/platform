package org.smartbit4all.domain.annotation.databean;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(ElementType.METHOD)
public @interface PropertyAccessor {

  /**
   * The name of the property in the entity definition.
   */
  String value() default "";

}
