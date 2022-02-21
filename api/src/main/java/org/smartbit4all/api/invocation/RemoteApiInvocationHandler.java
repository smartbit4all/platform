package org.smartbit4all.api.invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.core.utility.StringConstant;

/**
 * If we would like to use the given api remotely then we need to add it to the configuration with a
 * Proxy that contains the actual available providers. This must have a static registration
 * structure to be able to collect all the instances and offer this registration for the
 * {@link InvocationRegisterApi} at after initialization time.
 * 
 * @author Peter Boros
 */
public class RemoteApiInvocationHandler implements InvocationHandler {

  /**
   * Contains all the interfaces that are required as remote api.
   */
  private static final Map<Class<?>, List<RemoteApiInvocationHandler>> remoteApisByInterfaceClass =
      new HashMap<>();

  private final String qualifiedName;

  private final String module;

  private final Class<?> interfaceClass;

  RemoteApiInvocationHandler(String module, Class<?> interfaceClass, String name) {
    super();
    this.module = module;
    this.interfaceClass = interfaceClass;
    this.qualifiedName = module + StringConstant.COLON + interfaceClass.getName();
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    return null;
  }

  static final Map<Class<?>, List<RemoteApiInvocationHandler>> getRemoteApisByInterfaceclass() {
    return remoteApisByInterfaceClass;
  }

}
