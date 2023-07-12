package org.smartbit4all.api.view.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation to indicate that this method should called when a specific data is changed. Expected
 * signature: method(UUID viewUuid, DataChangeEvent event).
 *
 * @author amate
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface DataChangeListener {

  /**
   * Data paths, which annotated method handles. Empty string means all changes.
   *
   * @return
   */
  String[] value() default "";

}
