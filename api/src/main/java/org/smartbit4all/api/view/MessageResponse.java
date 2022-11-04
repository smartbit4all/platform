package org.smartbit4all.api.view;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
public @interface MessageResponse {

  /**
   * Name of the response code, which annotated method handles. Empty string means all code.
   * 
   * @return
   */
  String[] value() default "";

}
