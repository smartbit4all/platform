package org.smartbit4all.api.invocation;

/**
 * The subscriptions can be declared in the source code via {@link EventSubscription} annotation on
 * the event handler methods. The events are emitted to these methods if and only if they are
 * provided by the {@link Invocations#asProvider(Class, Object)} constructed beans.
 */
public interface SubscriptionInternalApi {

}
