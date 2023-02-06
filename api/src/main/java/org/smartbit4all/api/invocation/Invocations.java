package org.smartbit4all.api.invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.core.object.ObjectApi;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The developer api for the invocation.
 *
 * @author Peter Boros
 */
public class Invocations {

  /**
   * The logical schema of the invocation requests.
   */
  public static final String INVOCATION_SCHEME = "invocation";

  /**
   * The logical name fo the async channels registry.
   */
  public static final String ASYNC_CHANNEL_REGISTRY = "asyncChannelRegistry";

  /**
   * The logical schema of the api registry.
   */
  public static final String APIREGISTRATION_SCHEME = "apis";

  private Invocations() {
    super();
  }

  /**
   * Creates an InvocationRequest from the method and the parameters.
   *
   * @param method
   * @param args
   * @param interfaceClass
   * @param qualifiedName
   * @return
   */
  public static InvocationRequest createInvocationRequest(Method method, Object[] args,
      Class<?> interfaceClass, String qualifiedName) {
    Parameter[] methodParamters = method.getParameters();
    List<InvocationParameter> params = new ArrayList<>();

    if (args != null) {
      for (int i = 0; i < args.length; ++i) {
        Parameter methodParam = methodParamters[i];
        Object arg = args[i];

        InvocationParameter invocationParam = new InvocationParameter()
            .name(methodParam.getName())
            .value(arg)
            .typeClass(methodParam.getType().getName());
        params.add(invocationParam);
      }

    }
    return new InvocationRequest()
        .interfaceClass(interfaceClass.getName())
        .name(qualifiedName)
        .methodName(method.getName())
        .parameters(params);
  }

  // FacekomApi:hu.it4all.kh.FacekomApi.statusUpdate(processId::@@processId@@::java.lang.Long,StatusOk::”false”::java.lang.Boolean,
  // status::@@root@@/lastFacekomCall#errorCode::java.net.URI)
  // FacekomApi:hu.it4all.kh.FacekomApi.statusUpdate
  // processId::@@processId@@::java.lang.Long,StatusOk::”false”:java.lang.Boolean,status::@/lastFacekomCall#errorCode::java.net.URI
  public static InvocationRequest createInvocationRequest(String invocationRequestText) {
    String[] invocationSplit = invocationRequestText.split("\\(");
    String functionName = invocationSplit[0];
    String[] funcitonNameSplit = functionName.split(":");
    String interfaceName = null;
    String methodName = null;
    String name = null;
    if (funcitonNameSplit.length == 1) {
      String methodFullName = funcitonNameSplit[0];
      int dotIndex = methodFullName.lastIndexOf(".");
      interfaceName = methodFullName.substring(0, dotIndex);
      methodName = methodFullName.substring(dotIndex + 1, methodFullName.length());
      name = interfaceName;
    } else {
      String methodFullName = funcitonNameSplit[1];
      int dotIndex = methodFullName.lastIndexOf(".");
      interfaceName = methodFullName.substring(0, dotIndex);
      methodName = methodFullName.substring(dotIndex + 1, methodFullName.length());
      name = funcitonNameSplit[0];
    }

    String funcitonParamText = invocationSplit[1];
    funcitonParamText = funcitonParamText.substring(0, funcitonParamText.length() - 1);
    String[] funcitonParams = funcitonParamText.split(",");
    List<InvocationParameter> params = new ArrayList<>();
    for (String functionParam : funcitonParams) {
      String[] functionParts = functionParam.split("::");
      String paramName = functionParts[0];
      String paramValue = functionParts[1];
      String paramType = functionParts[2];
      params.add(new InvocationParameter().name(paramName).typeClass(paramType).value(paramValue));
    }


    return new InvocationRequest()
        .interfaceClass(interfaceName)
        .name(name)
        .methodName(methodName)
        .parameters(params);
  }

  /**
   * Identify the method for the invocation.
   *
   * @param api The api.
   * @param request The invocation request that contains all the parameters for the call.
   * @return The {@link Method} of the Api.
   */
  public static final Method getMethodToCall(Object api, InvocationRequest request) {
    Class<? extends Object> clazz = api.getClass();
    Class<?> parameterArray[];
    if (request.getParameters() == null) {
      parameterArray = new Class<?>[0];
    } else {
      parameterArray = new Class<?>[request.getParameters().size()];
      int i = 0;
      for (InvocationParameter p : request.getParameters()) {
        try {
          parameterArray[i++] = Class.forName(p.getTypeClass());
        } catch (ClassNotFoundException e) {
          throw new IllegalArgumentException(
              "The parameter type class is not found for the " + request, e);
        }
      }
    }
    try {
      if (request.getMethodName() != null) {
        return clazz.getMethod(request.getMethodName(), parameterArray);
      } else {
        // Try to identify the method by it's parameters.
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
          // Check the parameter types.
          Class<?>[] parameterTypes = method.getParameterTypes();
          int i = 0;
          boolean matching = true;
          for (Class<?> methodParamType : parameterTypes) {
            Class<?> parameterType = parameterArray[i];
            if (!methodParamType.equals(parameterType)) {
              matching = false;
              break;
            }
          }
          // In this way we can identify the method of the lambdas also.
          if (matching && !Object.class.equals(method.getDeclaringClass())) {
            return method;
          }
        }
        throw new UnsupportedOperationException("The method is not accessible for the " + request);
      }
    } catch (NoSuchMethodException | SecurityException e) {
      throw new UnsupportedOperationException("The method is not accessible for the " + request, e);
    }
  }

  @SuppressWarnings({"unchecked"})
  public static List<Object> getParameterObjects(ObjectApi objectApi, InvocationRequest request,
      Method method) {
    // Transfer the parameters for the call. Convert the primitives and the objects by the
    List<Object> parameterObjects = new ArrayList<>();
    for (InvocationParameter parameter : request.getParameters()) {
      Object value = parameter.getValue();
      if (value != null && !value.getClass().getName().equals(parameter.getTypeClass())) {
        Class<?> typeClass = getTypeClassByName(request, parameter.getTypeClass());
        if (List.class.isAssignableFrom(typeClass)) {
          value = objectApi.asList(getTypeClassByName(request, parameter.getInnerTypeClass()),
              (List<?>) value);
        } else if (Map.class.isAssignableFrom(typeClass)) {
          value = objectApi.asMap(getTypeClassByName(request, parameter.getInnerTypeClass()),
              (Map<String, ?>) value);
        } else {
          value = objectApi.asType(typeClass, value);
        }

      }
      parameterObjects.add(value);
    }
    return parameterObjects;
  }

  private static final Class<?> getTypeClassByName(InvocationRequest request, String className) {
    try {
      return Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException("Invalid " + className + " parameter type in " + request,
          e);
    }
  }

  /**
   *
   *
   * @param request
   * @param api
   * @param method
   * @return
   */
  public static InvocationParameter invokeMethod(ObjectApi objectApi, InvocationRequest request,
      Object api,
      Method method) {
    List<Object> parameterObjects = getParameterObjects(objectApi, request, method);
    try {
      Object result = method.invoke(api, parameterObjects.toArray());
      InvocationParameter invocationResult = new InvocationParameter();
      invocationResult.setValue(result);
      invocationResult.setTypeClass(method.getReturnType().getName());
      Optional<Object> firtsNotNull = Optional.empty();
      if (result instanceof List) {
        firtsNotNull = ((List) result).stream().filter(o -> o != null).findFirst();
      } else if (result instanceof Map) {
        firtsNotNull =
            ((Map) result).values().stream().filter(o -> o != null).findFirst();
      }
      if (firtsNotNull.isPresent()) {
        invocationResult.setInnerTypeClass(firtsNotNull.get().getClass().getName());
      }
      return invocationResult;
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException("Unable to call the method for the " + request, e);
    }
  }

  /**
   * Constructs a new provider api instance for the configuration. Should be used from the Java
   * based configurations when constructing the instances.
   *
   * @param <T> The type of the interface
   * @param name The name of the api if it means anything. For singleton apis it influences nothing.
   * @param apiInstance The api instance that will serve the requests at the end.
   */
  public static <T> ProviderApiInvocationHandler<T> asProvider(Class<T> interfaceClass, String name,
      T apiInstance) {
    return ProviderApiInvocationHandler.providerOf(interfaceClass, name, apiInstance);
  }

  /**
   * Constructs a new provider api instance for the configuration. Should be used from the Java
   * based configurations when constructing the instances.
   *
   * @param <T> The type of the interface
   * @param apiInstance The api instance that will serve the requests at the end.
   */
  public static <T> ProviderApiInvocationHandler<T> asProvider(Class<T> interfaceClass,
      T apiInstance) {
    return ProviderApiInvocationHandler.providerOf(interfaceClass, apiInstance);
  }

  public static InvocationRequest invoke(Class<?> class1) {
    return new InvocationRequest().interfaceClass(class1.getName());
  }

  /**
   * Converts the parameter to its original type
   *
   * @param objectMapper
   * @param parameter
   */
  public static void resolveParam(ObjectMapper objectMapper, InvocationParameter parameter) {
    if (parameter.getTypeClass() == null) {
      return;
    }
    try {
      Class<?> typeClass = Class.forName(parameter.getTypeClass());
      Object data = objectMapper.convertValue(parameter.getValue(), typeClass);
      parameter.setValue(data);
    } catch (ClassNotFoundException | IllegalArgumentException e) {
      throw new IllegalArgumentException("Error while resolving invocation parameter!" + parameter,
          e);
    }
  }

  public static class ListWrapper implements InvocationHandler {

    private List<?> list;

    private Class<?> innerType;

    public ListWrapper(List<?> list, Class<?> innerType) {
      super();
      this.list = list;
      this.innerType = innerType;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      return method.invoke(list, args);
    }

    public final List<?> getList() {
      return list;
    }

    public final void setList(List<?> list) {
      this.list = list;
    }

    public final Class<?> getInnerType() {
      return innerType;
    }

    public final void setInnerType(Class<?> innerType) {
      this.innerType = innerType;
    }

  }

  @SuppressWarnings("unchecked")
  public static final <T> List<T> listOf(List<T> list, Class<T> clazz) {
    return (List<T>) Proxy.newProxyInstance(Invocations.class.getClassLoader(),
        new Class<?>[] {List.class},
        new ListWrapper(list, clazz));
  }

  public static class MapWrapper implements InvocationHandler {

    private Map<String, ?> map;

    private Class<?> innerType;

    public MapWrapper(Map<String, ?> map, Class<?> innerType) {
      super();
      this.map = map;
      this.innerType = innerType;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      return method.invoke(map, args);
    }

    public final Class<?> getInnerType() {
      return innerType;
    }

    public final void setInnerType(Class<?> innerType) {
      this.innerType = innerType;
    }

    public final Map<String, ?> getMap() {
      return map;
    }

    public final void setMap(Map<String, ?> map) {
      this.map = map;
    }

  }

  @SuppressWarnings("unchecked")
  public static final <T> Map<String, T> mapOf(Map<String, T> map, Class<T> clazz) {
    return (Map<String, T>) Proxy.newProxyInstance(Invocations.class.getClassLoader(),
        new Class<?>[] {Map.class},
        new MapWrapper(map, clazz));
  }

  public static final InvocationRequest setParameter(InvocationRequest request, int idx,
      Object value) {
    if (request == null
        || request.getParameters() == null
        || request.getParameters().size() <= idx) {
      String method = request == null ? "null" : request.getMethodName();
      String api = request == null ? "null" : request.getInterfaceClass();
      throw new IllegalArgumentException(
          "Parameter " + idx + " of " + api + "." + method + "not found");
    }
    request.getParameters().get(idx).setValue(value);
    return request;
  }
}
