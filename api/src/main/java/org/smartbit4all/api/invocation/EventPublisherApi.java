package org.smartbit4all.api.invocation;

/**
 * This interface denotes the apis that are publishing events. The {@link InvocationRegisterApi}
 * collects them all and save the subscription into the api registration. The event has a logical
 * name generated from the api class name and the name of the PublishedEvent. When we would like to
 * consume event we must have {@link ProviderApiInvocationHandler}. The function that are denoted
 * with ConsumeEvent annotation will be saved as subscription for the events.
 * 
 * This theory is published by Martin Fowler in 2002.
 * 
 * @see <a href="https://martinfowler.com/eaaDev/DomainEvent.html">Martin Fowler - Domain Event
 *      concept</a>
 * 
 * @author Peter Boros
 */
public interface EventPublisherApi {

}
