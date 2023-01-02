package org.smartbit4all.api.view.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
@Repeatable(ViewApis.class)
public @interface ViewApi {

  /**
   * Name of the view.
   * 
   * @return
   */
  String value();

  /**
   * Name of the parent view.
   * 
   * @return
   */
  String parent() default "";

}
