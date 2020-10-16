package org.smartbit4all.domain.annotation.property;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
public @interface Table {

  /**
   * Name of the table.
   */
  String value();

  /**
   * The schema of the table.
   */
  String schema() default "";

}
