// package org.smartbit4all.api.invocation.restclient.apiregister;
//
// import java.lang.reflect.Method;
// import java.util.List;
// import org.smartbit4all.api.invocation.ApiInvocationProxy;
// import org.smartbit4all.api.invocation.InvocationExecutionApiLocal;
// import org.smartbit4all.api.invocation.Invocations;
// import org.smartbit4all.api.invocation.bean.InvocationParameter;
// import org.smartbit4all.api.invocation.bean.InvocationRequest;
//
// public class InvocationExecutionApiRestGen extends InvocationExecutionApiLocal {
//
// private RestGeneratedApiRegistry restGeneratedApiRegistry;
//
// public InvocationExecutionApiRestGen(RestGeneratedApiRegistry restGeneratedApiRegistry) {
// this.restGeneratedApiRegistry = restGeneratedApiRegistry;
// }
//
// @Override
// public String getName() {
// return Invocations.REST_GEN;
// }
//
// @Override
// protected Object getApi(InvocationRequest request) throws Exception {
// String ifTypeName = request.getApiClassName();
// Class<?> interfaceType = Class.forName(ifTypeName);
// Object restStubInstance = restGeneratedApiRegistry.getRestStubInstance(interfaceType);
// if (restStubInstance instanceof ApiInvocationProxy) {
// restStubInstance = ((ApiInvocationProxy) restStubInstance).getOriginalApi();
// }
// return restStubInstance;
// }
//
// @Override
// public InvocationParameter invoke(InvocationRequest request) throws Exception {
// return super.invoke(request);
// }
//
// @Override
// protected List<Object> getParameterObjects(InvocationRequest request, Method method) {
// List<Object> parameterObjects = super.getParameterObjects(request, method);
// // TODO add api instance ID as first parameter if request has it
// return parameterObjects;
// }
//
// }
