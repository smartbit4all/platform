// package org.smartbit4all.api.invocation;
//
// import java.lang.reflect.InvocationHandler;
// import java.lang.reflect.Method;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import org.smartbit4all.api.invocation.bean.InvocationParameter;
// import org.smartbit4all.api.invocation.bean.InvocationRequest;
//
/// **
// * If we would like to use the given api remotely then we need to add it to the configuration with
// a
// * Proxy that contains the actual available providers. This must have a static registration
// * structure to be able to collect all the instances and offer this registration for the
// * {@link InvocationRegisterApi} at after initialization time.
// *
// * @author Peter Boros
// */
// public class RemoteApiInvocationHandler implements InvocationHandler {
//
// /**
// * Contains all the interfaces that are required as remote api.
// */
// private static final Map<Class<?>, List<RemoteApiInvocationHandler>> remoteApisByInterfaceClass =
// new HashMap<>();
//
// private final String qualifiedName;
//
//
// private final Class<?> interfaceClass;
//
// private InvocationApi invocationApi;
//
// private ApiDescriptor descriptor;
//
// RemoteApiInvocationHandler(ApiDescriptor descriptor, Class<?> interfaceClass, String name,
// InvocationApi invocationApi) {
// super();
// this.descriptor = descriptor;
// this.interfaceClass = interfaceClass;
// this.qualifiedName = name;
// this.invocationApi = invocationApi;
// }
//
// @Override
// public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
// InvocationRequest invocationRequest =
// Invocations.createInvocationRequest(method, args, interfaceClass, qualifiedName);
// InvocationParameter result = invoke(invocationRequest);
// return result.getValue();
// }
//
// protected InvocationParameter invoke(InvocationRequest invocationRequest) throws Exception {
// InvocationParameter result =
// invocationApi.invoke(descriptor, invocationRequest);
// return result;
// }
//
//
//
// static final Map<Class<?>, List<RemoteApiInvocationHandler>> getRemoteApisByInterfaceclass() {
// return remoteApisByInterfaceClass;
// }
//
// }
