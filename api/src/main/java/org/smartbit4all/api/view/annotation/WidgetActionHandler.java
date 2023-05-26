package org.smartbit4all.api.view.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation to indicate that this method should be used to handle a widget specific UiAction call.
 * Expected signature: method(UUID viewUuid, String widgetId, String nodeId, UiActionRequest
 * request).
 *
 * @author amate
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface WidgetActionHandler {

  /**
   * Action codes, which annotated method handles. Empty string means all codes.
   *
   * @return
   */
  String[] value() default "";

  /**
   * Widget ids, which annotated method handles. Empty string means all widgets.
   *
   * @return
   */
  String[] widget() default "";
}
