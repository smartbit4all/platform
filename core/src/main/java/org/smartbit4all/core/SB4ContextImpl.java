/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.core;

import java.lang.ref.WeakReference;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The basic implementation of the {@link SB4Context}. Every implementation extends this one... The
 * implementation of the services are based on the next publication:
 * 
 * <a href="https://en.wikipedia.org/wiki/Aspect-oriented_programming">RecordSet</a>
 * 
 * The registry contains the {@link RegistryEntry}s by the class of the {@link SB4Service}
 * interface. We cann't put in two identical service into a context to avoid the collision. If we
 * need two service from the same {@link SB4Service} then it might be a design issue. We could use
 * the nested context or we can create a different service API for them.
 * 
 * @author Peter Boros
 *
 */
public class SB4ContextImpl implements SB4Context {

  private static final Logger log = LoggerFactory.getLogger(SB4ContextImpl.class);

  /**
   * For the sake of fast toString() we store the qualified name of the context. TODO Exact
   * algorithm!!
   */
  protected String qualifiedName;

  /**
   * The service based API will follow the current position of execution. It means that if we call
   * an operation on a {@link SB4ContextScope} service then it will install its context as actual.
   * We can access the parent context but we use the current context by default. So the application
   * logic is not necessarily is aware of the current context. If we leave the scope then the parent
   * will be installed as default again.
   */
  private static final ThreadLocal<Deque<SB4Context>> contextStack =
      new ThreadLocal<Deque<SB4Context>>();

  private static final ThreadLocal<SB4Context> currentContext = new ThreadLocal<>();

  /**
   * This entry holds the reference of the service instance and the proxy itself. We always get the
   * proxy to be able to add aspect oriented behavior to the services. The proxy has only
   * {@link WeakReference} to the instance. So the instance exists until the destruction of the
   * context. After this all the proxies will throw an unavailable exception when calling the
   * services.
   * 
   * @author Peter Boros
   *
   */
  static final class RegistryEntry {

    /**
     * Serves the proxy and hold the original instance of the service.
     */
    final SB4ServiceInvocationHandler<?> invocationHandler;

    /**
     * The public reference of the given service. It's always a Proxy.
     */
    final SB4Service proxy;

    /**
     * The class of the implementation.
     */
    final Class<? extends SB4Service> serviceIF;

    /**
     * The class of the implementation.
     */
    final Class<? extends SB4Service> implClass;

    /**
     * Create a new registry entry.
     * 
     * @param instance The loaded instance as the implementation of the service.
     * @param proxy The proxy created as the facade for the given service interface.
     * @param invocationHandler The invocation handler that serves the invocations of the interface.
     */
    RegistryEntry(SB4Service proxy, SB4ServiceInvocationHandler<?> invocationHandler) {
      super();
      this.proxy = proxy;
      this.invocationHandler = invocationHandler;
      this.implClass = null;
      this.serviceIF = null;
    }

    /**
     * Create a new registry entry.
     * 
     * @param instance The loaded instance as the implementation of the service.
     * @param proxy The proxy created as the facade for the given service interface.
     * @param invocationHandler The invocation handler that serves the invocations of the interface.
     */
    <T extends SB4Service> RegistryEntry(Class<T> serviceIF, Class<? extends T> implClass) {
      super();
      this.proxy = null;
      this.invocationHandler = null;
      this.serviceIF = serviceIF;
      this.implClass = implClass;
    }

    public SB4Service getProxy() {
      return proxy;
    }

    public SB4Service getInstance() {
      return invocationHandler.getServiceInstance();
    }

  }

  /**
   * The registry contains the {@link RegistryEntry}s by the class of the {@link SB4Service}
   * interface. We cann't put in two identical service into a context to avoid the collision. If we
   * need two service from the same {@link SB4Service} then it might be a design issue.
   * 
   * TODO Is there any option where the {@link Collections#emptyList()} could be a good option at
   * first?
   */
  protected Map<Class<? extends SB4Service>, RegistryEntry> registry = new HashMap<>();

  /**
   * The parent context is the reference for super context of this. By entering a
   * {@link SB4ContextScope} the scope will add itself as current context.
   */
  protected SB4Context parentContext;

  @SuppressWarnings("unchecked")
  @Override
  public <T extends SB4Service> T get(Class<T> serviceIF) {
    RegistryEntry registryEntry = registry.get(serviceIF);
    if (registryEntry == null) {
      // TODO search (see find) in parent? parentContext never set, use contextStack? or both?
      return null;
    }
    if (registryEntry.implClass == null) {
      // We can return the proxy as the singleton instance.
      return (T) registryEntry.getProxy();
    } else {
      // We have to create a new instance from the implClass
      try {
        return (T) registryEntry.implClass.getDeclaredConstructor().newInstance();
      } catch (Exception e) {
        log.error("Unable to create new instance from the {} service.", serviceIF.getName(), e);
      }
      return null;
    }
  }

  @Override
  public <T extends SB4Service> T find(Class<T> serviceIF) {
    T result = get(serviceIF);
    if (result == null && parentContext != null) {
      result = parentContext.find(serviceIF);
    }
    return null;
  }

  @Override
  public <T extends SB4Service> List<T> findAll(Class<T> serviceIF) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * You can add a new service to the given context with this operation.
   * 
   * TODO It's not the final API. It will be redesigned with the injection framework implementation.
   * 
   * @param <T> The class of the {@link SB4Service}
   * @param serviceInstance The implementation instance of the interface.
   * @param serviceIF The class of the service interface to implement.
   * @param invocationHandler The invocation handler for interface definition. If it's null then all
   *        the invocations go directly to the serviceInstance. There is no tricky aspect. It's
   *        typical when we add a meta data service because it's a common logic and works only with
   *        the static meta data.
   * @return
   */
  @Override
  public <T extends SB4Service> T add(SB4ServiceInvocationHandler<T> invocationHandler,
      Class<T> serviceIF) {
    if (registry.containsKey(serviceIF)) {
      throw new RuntimeException("Service already registered! (" + serviceIF + ")");
    }
    T result;
    @SuppressWarnings("unchecked")
    T proxy = (T) Proxy.newProxyInstance(serviceIF.getClassLoader(), new Class[] {serviceIF},
        invocationHandler);
    registry.put(serviceIF, new RegistryEntry(proxy, invocationHandler));
    result = proxy;
    return result;
  }

  @Override
  public <T extends SB4Service> void add(Class<T> serviceIF,
      Class<? extends T> implementationClass) {
    if (registry.containsKey(serviceIF)) {
      throw new RuntimeException("Service already registered! (" + serviceIF + ")");
    }
  }

  /**
   * With this function the {@link SB4ServiceInvocationHandler} can place the actual context on the
   * top of the context stack. It will prevent from adding the same context again and again.
   * 
   * @param context
   */
  static final void push(SB4Context context) {
    Deque<SB4Context> deque = contextStack.get();
    if (deque == null) {
      deque = new LinkedList<>();
      contextStack.set(deque);
    }
    currentContext.set(context);
    deque.push(context);
  }

  /**
   * With this function we can remove the actual context from the top of the context stack.
   * 
   * @return
   */
  static final SB4Context pop() {
    Deque<SB4Context> deque = contextStack.get();
    if (deque == null) {
      return null;
    }
    deque.pop();
    if (deque.isEmpty()) {
      // In this case we remove the stack and the currentContext also.
      contextStack.remove();
      currentContext.remove();
      return null;
    } else {
      // The peek will be the current.
      SB4Context peek = deque.peek();
      currentContext.set(peek);
      return peek;
    }
  }

  /**
   * The most important accessor function in SB4 programming API. It retrieves the actual context
   * associated with the current {@link Thread}.
   * 
   * @return
   */
  static final SB4Context getCurrent() {
    return currentContext.get();
  }

}
