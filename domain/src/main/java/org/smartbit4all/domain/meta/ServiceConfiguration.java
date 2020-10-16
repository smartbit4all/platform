package org.smartbit4all.domain.meta;

import java.lang.reflect.Proxy;
import org.smartbit4all.core.SB4Configuration;
import org.smartbit4all.core.SB4ServiceInvocationHandler;
import org.smartbit4all.domain.service.CrudService;

public class ServiceConfiguration extends SB4Configuration {

  /**
   * Creates a CrudService implementation
   * 
   * @param def CrudService definition for an EntityDefinition
   * @param impl Implementation of the CrudService (RemoteCrudService. SQLCrudService)
   * @return
   */
  @SuppressWarnings("unchecked")
  protected <E extends EntityDefinition, T extends CrudService<E>, G extends CrudService<E>> T createCrudProxy(
      Class<T> def, G impl) {
    SB4ServiceInvocationHandler<G> invocationHandler = new SB4ServiceInvocationHandler<>(impl);
    T proxy =
        (T) Proxy.newProxyInstance(def.getClassLoader(), new Class[] {def}, invocationHandler);
    return proxy;
  }

}
