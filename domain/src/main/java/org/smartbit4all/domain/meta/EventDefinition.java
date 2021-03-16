/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.domain.meta;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URI;

/**
 * This is the super interface of the event definition singletons represent the domain events of a
 * system. The meta data is available via the operations of the event definitions implementing this
 * interface. The meta data can be used to setup a subscription for an event. The EventProvider
 * publishes the {@link EventDefinition}s for the listeners. The listeners can subscribe for the
 * given event. This interface doesn't have any implementation but can have several {@link Proxy} -
 * {@link InvocationHandler}.
 * 
 * This theory is published by Martin Fowler in 2002.
 * 
 * @see <a href="https://martinfowler.com/eaaDev/DomainEvent.html">Martin Fowler - Domain Event
 *      concept</a>
 * 
 * @author Peter Boros
 *
 */
public interface EventDefinition<E> {

  /**
   * The unique identifier of the given event. It's structured like this:
   * 
   * <code>event:/domain/EventIdentifier</code>
   * 
   * This URI must be unique in a system context so we can use this to get the event definition from
   * the MetaApi.
   * 
   * @return
   */
  URI getUri();

  /**
   * The definition provides a builder API for the subscription. We can add listeners to the given
   * subscription.
   * 
   * @return Return a specific {@link EventSubscription} typed with the parameter of the Listeners.
   *         The definitions will always define the specific subscription extension as a builder api
   *         for subscribing.
   */
  EventSubscription<E> subscribe();

}
