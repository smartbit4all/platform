package org.smartbit4all.api.invocation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation declares a method that is responsible for emitting an event. If we use this
 * annotation then the event will be registered by the {@link InvocationRegisterApi} and can be used
 * as event source for the subscribers.
 */
@Retention(RUNTIME)
@Target({METHOD})
public @interface PublishedEvent {

  /**
   * @return The name of the event. Use the String constant of the publisher api if it is possible.
   */
  String event();

}
