package org.smartbit4all.domain.annotation.property;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
public @interface Ref {

  /**
   * Name of the reference. If not specified, defaults to method name.
   * 
   * @return
   */
  String value() default "";

}
