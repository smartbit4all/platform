package org.smartbit4all.domain.annotation.property;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(ElementType.METHOD)
public @interface ReferenceEntity {

  /**
   * Name of the entity reference. If not specified, defaults to method name.
   * 
   * @return
   */
  String value() default "";

}
