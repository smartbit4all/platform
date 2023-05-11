package org.smartbit4all.api.view.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
public @interface ActionHandler {

  /**
   * Name of action codes, which annotated method handles. Empty string means all codes.
   *
   * @return
   */
  String[] value() default "";

}
