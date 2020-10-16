package org.smartbit4all.domain.annotation.property;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
public @interface Entity {

  /**
   * Name of the entity. Defaults to class name.
   * 
   * @return
   */
  String value() default "";

}
