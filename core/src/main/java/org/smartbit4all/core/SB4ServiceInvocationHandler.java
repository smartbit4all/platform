package org.smartbit4all.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * This is the super class of all {@link InvocationHandler} created for the SmartBit4All service
 * programming API. The basic functionality is the {@link SB4ContextScope} handling. If a service is
 * a scope on its own then the call will continue with its context as current. Therefore the
 * {@link SB4Context#get()} always retrieve the proper instance.
 * 
 * @author Peter Boros
 *
 */
public class SB4ServiceInvocationHandler<S extends SB4Service> implements InvocationHandler {

  /**
   * This is original implementation of the service. From programming level we cann't reach this.
   */
  protected S serviceInstance;

  /**
   * If the given {@link SB4Service} is an {@link SB4ContextScope} then it's true;
   */
  protected boolean scope = false;

  /**
   * New instance of the {@link InvocationHandler} that wraps the {@link #serviceInstance}.
   * 
   * @param serviceInstance The non null service instance.
   */
  public SB4ServiceInvocationHandler(S serviceInstance) {
    super();
    if (serviceInstance == null) {
      throw new NullPointerException("Unable to initiate null service!");
    }
    if (serviceInstance instanceof SB4ContextScope) {
      scope = true;
    }

    this.serviceInstance = serviceInstance;
  }

  /**
   * The most important function in the SB4 universe. It handles the {@link SB4Context} and
   * {@link SB4ContextScope}
   */
  @Override
  public final Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if (!scope) {
      return doInvoke(proxy, method, args);
    }
    /*
     * If this service is a context scope then it will be the actual context and we push it onto the
     * top of context stack. We check if it's a new context or it's the same that we currently have.
     * 
     * TODO Think it over how to protect the long stack in case of a recursion. It might be OK, but
     * let's talk about.
     */
    SB4ContextImpl.push(((SB4ContextScope) serviceInstance).context());
    try {
      return doInvoke(proxy, method, args);
    } finally {
      SB4ContextImpl.pop();
    }
  }

  /**
   * There can be specialties in every {@link SB4Service} that can be organized by a specific
   * {@link SB4ServiceInvocationHandler} and developed in the implementation of this function. By
   * default we call the method normally on the {@link #serviceInstance}.
   * 
   * @param proxy All the normal parameters of the
   *        {@link InvocationHandler#invoke(Object, Method, Object[])} methos.
   * @param method
   * @param args
   * @return
   * @throws Throwable
   */
  protected Object doInvoke(Object proxy, Method method, Object[] args) throws Throwable {
    return method.invoke(serviceInstance, args);
  }

  /**
   * We can get the instance object of the service.
   * 
   * @return
   */
  final S getServiceInstance() {
    return serviceInstance;
  }

  /**
   * If the given service is a scope. So it contains an embedding context.
   * 
   * @return
   */
  final boolean isScope() {
    return scope;
  }

}
