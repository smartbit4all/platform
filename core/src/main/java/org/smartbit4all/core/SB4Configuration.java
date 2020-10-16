package org.smartbit4all.core;

import java.lang.reflect.Proxy;

public class SB4Configuration {

  protected <T extends SB4Service> T createProxy(Class<T> def, T impl) {
    SB4ServiceInvocationHandler<T> invocationHandler = new SB4ServiceInvocationHandler<T>(impl);
    @SuppressWarnings("unchecked")
    T proxy =
        (T) Proxy.newProxyInstance(def.getClassLoader(), new Class[] {def}, invocationHandler);
    return proxy;
  }

}
