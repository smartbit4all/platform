package org.smartbit4all.api.invocation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.smartbit4all.api.invocation.bean.EventSubscriptionType;

/**
 * In a provided api we can annotate a method with this annotation. It results that the given
 * function is going to be saved into the registration storage and if someone call a function by the
 * {@link InvocationApi} and add this event definition string as event definition then all of the
 * subscribed apis will be called. The consume event can have more parameter to specify the way of
 * call.
 * 
 * @author Peter Boros
 */
@Retention(RUNTIME)
@Target({METHOD})
public @interface EventSubscription {

  /**
   * @return The name of the api publishing the event.
   */
  String api();

  /**
   * @return The name of the event. Use the String constant of the publisher api if it is possible.
   */
  String event();

  /**
   * @return Defines the type of the consumer.
   */
  EventSubscriptionType type() default EventSubscriptionType.ONE_RUNTIME;

  /**
   * @return True if the event is accepted in asynchronous way. This is an instruction for the
   *         publisher.
   */
  boolean asynchronous() default true;

  /**
   * @return Defines the asynchronous channel to use for processing the invocation.
   */
  String channel();

}
