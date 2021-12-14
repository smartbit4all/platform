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
package org.smartbit4all.core.event;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import org.smartbit4all.core.utility.StringConstant;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * The abstract superclass of all publisher implementation. It uses some reflection to discover the
 * {@link EventDefinition}s on the API of the instance. Using this the implementation provide meta
 * API for accessing the event for example by URI.
 * 
 * The helper is used by the API implementations to support the <code>EventAware</code> behavior.
 * Typically the events() is supported by a helper that provides {@link Proxy} with
 * {@link InvocationHandler}.
 * 
 * This helper will be initiated for every provided publisher instance. In case of stateful API
 * every instance will have it's own. Therefore it stores the subscriptions for the given instance.
 * 
 * @author Peter Boros
 *
 */
public class EventPublisherHelper<P extends EventPublisher> implements InvocationHandler {

  /**
   * The meta information about an EventPublisher interface.
   * 
   * @author Peter Boros
   */
  private static final class PublisherMeta {

    /**
     * This identify the methods returning {@link EventDefinition}.
     */
    Map<Method, Class<EventDefinition<?>>> definitionsByMethods = new HashMap<>();

  }

  /**
   * When we have an API with an {@link EventPublisher} instance then we need the meta information
   * about it to initiate the registry of the given publisher instance. We use this cache for the
   * discovered meta data of the publisher classes.
   */
  public static final Cache<Class<EventPublisher>, PublisherMeta> publisherCache =
      CacheBuilder.newBuilder().build();

  /**
   * The publisher proxy created with this helper as {@link InvocationHandler}.
   */
  private final P publisherProxy;

  /**
   * The class of the interface.
   */
  private final Class<P> publisherIF;

  /**
   * The meta information belongs to the publisher interface supported by this helper instance.
   */
  private PublisherMeta publisherMeta;

  /**
   * The URI of the API. <code>api:/MyApi</code> The URI of the given event is constructed in this
   * way: <code>api:/MyApi/events/eventName</code>
   */
  private String apiUri;

  /**
   * The helper can create a {@link Proxy} for the given publisher class. It will use reflection to
   * discover and identify the publisher {@link EventDefinition}s.
   * 
   * @param publisherIF
   */
  @SuppressWarnings("unchecked")
  public EventPublisherHelper(Class<P> publisherIF, String apiUri) {
    super();
    this.publisherIF = publisherIF;
    this.apiUri = apiUri;
    try {
      publisherMeta =
          publisherCache.get((Class<EventPublisher>) publisherIF, new Callable<PublisherMeta>() {

            @Override
            public PublisherMeta call() throws Exception {
              PublisherMeta result = new PublisherMeta();
              for (int i = 0; i < publisherIF.getMethods().length; i++) {
                Method method = publisherIF.getMethods()[i];
                // If it's an event definition method then we register this.
                if (EventDefinition.class.isAssignableFrom(method.getReturnType())) {
                  result.definitionsByMethods.put(method,
                      (Class<EventDefinition<?>>) method.getReturnType());
                }
              }
              return result;
            }

          });
    } catch (ExecutionException e) {
      throw new IllegalArgumentException(
          "Unable to process the " + publisherIF + " by reflection API.", e);
    }
    publisherProxy =
        (P) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] {this.publisherIF},
            this);
  }

  /**
   * The helper offer the Proxy implementation of the .
   * 
   * @return A Proxy for the interface. This interfaces doesn't have any other implementations
   *         because they are only definitions.
   */
  public P publisher() {
    return publisherProxy;
  }

  /**
   * The definition instances for the supported events. They are mapped by the name of the method
   * returning the {@link EventDefinition} on the publisher interface.
   */
  final Map<String, EventDefinition<?>> definitions = new HashMap<>();

  /**
   * The name constant for the method.
   */
  static final String M_GETNAME = "getName";

  /**
   * The name constant for the method.
   */
  static final String M_GETURI = "getUri";

  /**
   * The name constant for the method.
   */
  static final String M_SUBSCRIPTION = "subscribe";

  /**
   * The instance of the event definition. It's also the invocation handler and the original
   * publisher method will return this Proxy. All the {@link EventDefinition} function will be
   * implemented by this invocation handler.
   * 
   * @author Peter Boros
   */
  private static final class EventDefinitionInstance implements InvocationHandler {

    /**
     * The unique identifier of the given event. It can be used to retrieve the Object via MetaApi.
     * This API is generated from the URI of the API and the name of the event publisher function.
     */
    private URI uri;

    /**
     * The name of the method that returning this event definition instance.
     */
    private String name;

    public EventDefinitionInstance(URI uri, String name) {
      super();
      this.uri = uri;
      this.name = name;
    }

    /**
     * The subscriptions for the given instance.
     */
    final List<EventSubscription<?>> subscriptions = new ArrayList<EventSubscription<?>>();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if (M_GETURI.equals(method.getName())) {
        return uri;
      }
      if (M_GETNAME.equals(method.getName())) {
        return name;
      }
      if (M_SUBSCRIPTION.equals(method.getName())) {
        Class<?> returnType = method.getReturnType();
        Object result = returnType.newInstance();
        subscriptions.add((EventSubscription<?>) result);
        return result;
      }
      // The subscribe() will create a specialized subscription object and return this
      // TODO Auto-generated method stub
      return null;
    }

  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    Class<EventDefinition<?>> eventDefinitionIF = publisherMeta.definitionsByMethods.get(method);
    // We try to find
    if (eventDefinitionIF != null) {
      EventDefinition<?> definitionInstance = definitions.get(method.getName());
      if (definitionInstance == null) {
        // We have to create the Proxy for the given EventDefinition
        definitionInstance =
            (EventDefinition<?>) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[] {eventDefinitionIF},
                new EventDefinitionInstance(
                    URI.create(apiUri + StringConstant.SLASH + method.getName()),
                    method.getName()));
        definitions.put(method.getName(), definitionInstance);
      }
      return definitionInstance;
    }
    return null;
  }

  /**
   * We can fire an event using the function.
   * 
   * @param <E> The type of the event object
   * @param eventDefinition The event definition returned by the publisher interface. Like
   *        events().someEvent()
   * @param eventObjectSupplier The supplier lambda for the value object of the event. If there is
   *        no subscriber for the given event then we can skip constructing this.
   */
  public <E> void fire(EventDefinition<E> eventDefinition, Supplier<E> eventObjectSupplier) {
    EventDefinitionInstance definitionIH =
        (EventDefinitionInstance) Proxy.getInvocationHandler(eventDefinition);
    if (definitionIH != null && !definitionIH.subscriptions.isEmpty()) {
      E eventObject = eventObjectSupplier.get();
      callSubscribers(eventObject, definitionIH);
    }
  }

  /**
   * We can fire an event using the function.
   * 
   * @param <E> The type of the event object
   * @param eventDefinition The event definition returned by the publisher interface. Like
   *        events().someEvent()
   * @param eventObject The value object of the event. It must be constructed prior.
   */
  public <E> void fire(EventDefinition<E> eventDefinition, E eventObject) {
    EventDefinitionInstance definitionIH =
        (EventDefinitionInstance) Proxy.getInvocationHandler(eventDefinition);
    if (definitionIH != null) {
      callSubscribers(eventObject, definitionIH);
    }
  }

  @SuppressWarnings("unchecked")
  private final <E> void callSubscribers(E eventObject, EventDefinitionInstance definitionIH) {
    for (EventSubscription<?> subscription : definitionIH.subscriptions) {
      ((EventSubscription<E>) subscription).fire(eventObject);
    }
  }

  @SuppressWarnings("unchecked")
  public static <T extends EventPublisher> T createNewProxyInstance(String apiUri, Class<T> clazz) {
    return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
        new Class[] {clazz, EventPublisherImpl.class},
        new EventPublisherHelper<>(clazz, apiUri));
  }

  @SuppressWarnings("unchecked")
  public static <T extends EventPublisher> EventPublisherHelper<T> get(T eventPublisher) {
    return (EventPublisherHelper<T>) Proxy.getInvocationHandler(eventPublisher);
  }

}
