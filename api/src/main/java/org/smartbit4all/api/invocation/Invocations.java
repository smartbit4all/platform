package org.smartbit4all.api.invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.utility.StringConstant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import static java.util.stream.Collectors.toList;

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
   * The logical schema of the invocation requests.
   */
  public static final String ASYNCINVOCATION_SCHEME = "asyncInvocation";

  /**
   * The logical name fo the async channels registry.
   */
  public static final String ASYNC_CHANNEL_REGISTRY = "asyncChannelRegistry-sv";

  /**
   * The logical schema of the api registry.
   */
  public static final String APIREGISTRATION_SCHEME = "apis";

  private static final String regexStackTrace = "^(.*)\\.([^(]+)\\(([^:]*):?([0-9]*)\\)$";

  private static final Pattern pattern = Pattern.compile(regexStackTrace);

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

  /**
   * Checks whether the given request is a script or not.
   * 
   * @param request
   * @return
   */
  public static boolean isScript(InvocationRequest request) {
    if (request != null && !Strings.isNullOrEmpty(request.getScriptBody())
        && !Strings.isNullOrEmpty(request.getScriptKind())) {
      return true;
    }
    return false;
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

  public static List<Object> getParameterObjects(ObjectApi objectApi, InvocationRequest request) {
    // Transfer the parameters for the call. Convert the primitives and the objects by the
    List<Object> parameterObjects = new ArrayList<>();
    for (InvocationParameter parameter : request.getParameters()) {
      Object value = convertParameterValue(objectApi, request, parameter);
      parameterObjects.add(value);
    }
    return parameterObjects;
  }

  @SuppressWarnings("unchecked")
  public static Object convertParameterValue(ObjectApi objectApi, InvocationRequest request,
      InvocationParameter parameter) {
    Object value = parameter.getValue();
    if (value != null && !value.getClass().getName().equals(parameter.getTypeClass())) {
      Class<?> typeClass = getTypeClassByName(request, parameter.getTypeClass());
      // If the innerTypeClass is missing then assume we have strings.
      String innerType = parameter.getInnerTypeClass() == null ? String.class.getName()
          : parameter.getInnerTypeClass();
      if (List.class.isAssignableFrom(typeClass)) {
        value = objectApi.asList(getTypeClassByName(request, innerType),
            (List<?>) value);
      } else if (Map.class.isAssignableFrom(typeClass)) {
        value = objectApi.asMap(getTypeClassByName(request, innerType),
            (Map<String, ?>) value);
      } else {
        value = objectApi.asType(typeClass, value);
      }

    }
    return value;
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
    List<Object> parameterObjects = getParameterObjects(objectApi, request);
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
    } catch (InvocationTargetException ite) {
      if (ite.getCause() instanceof RuntimeException) {
        throw (RuntimeException) ite.getCause();
      } else {
        throw new RuntimeException("Unable to call the method for the " + request, ite);
      }
    } catch (IllegalAccessException | IllegalArgumentException e) {
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


  /**
   * Constructs an {@link ApiInvocationHandler} implemeneted {@link Proxy} for the given interface.
   * Can be used to provide the same interface with remote implementation. The remote means that it
   * is remote in the same cluster by default.
   * 
   * @param <T>
   * @param interfaceClass
   * @param invocationApi
   * @return
   */
  public static <T> T asProxy(Class<T> interfaceClass, InvocationApi invocationApi) {
    return ApiInvocationHandler.createProxy(interfaceClass, interfaceClass.getName(),
        invocationApi);
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
    if (parameter.getTypeClass() == null || "void".equals(parameter.getTypeClass())) {
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

  /**
   * Stringify the request
   *
   * @param objectMapper
   * @param request
   */
  public static String stringifyRequest(ObjectMapper objectMapper, InvocationRequest request) {
    try {
      return objectMapper.writeValueAsString(request);
    } catch (IllegalArgumentException | JsonProcessingException e) {
      throw new IllegalArgumentException("Error while serializing invocation request!" + request,
          e);
    }
  }

  /**
   * Resolve the request
   *
   * @param objectMapper
   * @param request
   */
  public static String resolveRequest(ObjectMapper objectMapper, InvocationRequest request) {
    try {
      return objectMapper.writeValueAsString(request);
    } catch (IllegalArgumentException | JsonProcessingException e) {
      throw new IllegalArgumentException("Error while serializing invocation request!" + request,
          e);
    }
  }

  /**
   * Converts the parameter to its original type
   *
   * @param objectMapper
   * @param parameters
   */
  public static void resolveParams(ObjectMapper objectMapper,
      List<InvocationParameter> parameters) {
    for (InvocationParameter param : parameters) {
      resolveParam(objectMapper, param);
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

  /**
   * Try to find the first parameter with the type of the value and set it.
   * 
   * @param request The request
   * @param value The value to set. If it is null then there will be no change.
   * @return The request.
   */
  public static final InvocationRequest setParameterFirstWithType(InvocationRequest request,
      Object value) {
    if (request != null
        && value != null) {
      String typeName = value.getClass().getName();
      request.getParameters().stream().filter(p -> typeName.equals(p.getTypeClass())).findFirst()
          .ifPresent(p -> p.setValue(value));
    }
    return request;
  }

  public static <T> URI uriOf(String interfaceClassName, String name) {
    return URI
        .create(Invocations.APIREGISTRATION_SCHEME + StringConstant.COLON + StringConstant.SLASH
            + interfaceClassName.replace(StringConstant.DOT, StringConstant.SLASH)
            + StringConstant.SLASH + name);
  }

  public static InvocationParameter invokeTestMethod(Object apiObject,
      InvocationRequest request) {
    Class<?> parameters[] = new Class[request.getParameters().size()];
    Arrays.fill(parameters, InvocationParameter.class);
    try {
      Method method = apiObject.getClass().getMethod(request.getMethodName(), parameters);
      Object result = method.invoke(apiObject, request.getParameters().toArray());
      return (InvocationParameter) result;
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException
        | InvocationTargetException e) {
      throw new IllegalArgumentException("Unable to find call the " + request, e);
    }
  }

  public static StackTraceElement stackTraceElementFromString(String stackTraceString) {
    if (stackTraceString == null || stackTraceString.isEmpty()) {
      throw new IllegalArgumentException("Stack trace string cannot be null or empty");
    }
    Matcher matcher = pattern.matcher(stackTraceString);

    if (!matcher.matches()) {
      return new StackTraceElement(stackTraceString, StringConstant.UNKNOWN, StringConstant.UNKNOWN,
          -1);
    }

    String className = matcher.group(1);
    String methodName = matcher.group(2);
    String fileName = matcher.group(3).equals("Unknown Source") ? null : matcher.group(3);
    int lineNumber = matcher.group(4).isEmpty() ? -1 : Integer.parseInt(matcher.group(4));

    return new StackTraceElement(className, methodName, fileName, lineNumber);
  }

  public static StackTraceElement[] stackTraceElementsFromString(
      List<String> stackTraceStringList) {
    return stackTraceStringList.stream()
        .map(Invocations::stackTraceElementFromString)
        .toArray(StackTraceElement[]::new);
  }

  public static List<String> listOfStackTrace(
      Exception ex) {
    List<String> result;
    if (ex.getStackTrace() != null) {
      result = Stream.of(ex.getStackTrace()).map(s -> s.toString()).collect(toList());
    } else {
      result = Collections.emptyList();
    }
    return result;
  }

  public static String createFQN(InvocationRequest request) {
    // TODO Add the types of the parameters.
    return request.getInterfaceClass() + StringConstant.DOT + request.getMethodName();
  }

}
