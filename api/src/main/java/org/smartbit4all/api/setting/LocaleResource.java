package org.smartbit4all.api.setting;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
public @interface LocaleResource {

  /**
   * The array of class path URLs for the locale resource to load.
   * 
   * @return
   */
  String[] value() default {};

}
