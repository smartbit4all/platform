package org.smartbit4all.api.invocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The developer api for the invocation.
 * 
 * @author Peter Boros
 */
public class Invocations {

  public static final String INVOCATION_SCHEME = "invocation";
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

  // FacekomApi:FacekomApi.statusUpdate(processId=@@proc:/#processId@@, StatusOk=”false”Ö
  // FacekomApi:hu.it4all.kh.FacekomApi.statusUpdate(processId=@@processId@@:java.lang.Long,StatusOk=”false”:java.lang.Boolean,
  // status=@@root@@/lastFacekomCall#errorCode:java.net.URI)
  // FacekomApi:hu.it4all.kh.FacekomApi.statusUpdate
  // processId=@@processId@@:java.lang.Long,StatusOk=”false”:java.lang.Boolean,status=@/lastFacekomCall#errorCode:java.net.URI
  public static InvocationRequest createInvocationRequest(String invocationRequestText) {
    String[] invocationSplit = invocationRequestText.split("\\(");
    String funcitonName = invocationSplit[0];
    String[] funcitonNameSplit = funcitonName.split(":");
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
    for (String funcitonParam : funcitonParams) {
      int equalsLetterIndex = funcitonParam.indexOf("=");
      int éetterIndex = funcitonParam.indexOf(":");
      String paramName = funcitonParam.substring(0, equalsLetterIndex);
      String paramValue = funcitonParam.substring(equalsLetterIndex + 1, éetterIndex);
      String paramType = funcitonParam.substring(éetterIndex + 1, funcitonParam.length());
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

  public static List<Object> getParameterObjects(InvocationRequest request, Method method) {
    // Transfer the parameters for the call. Convert the primitives and the objects by the
    List<Object> parameterObjects = new ArrayList<>();
    // int i = 0;
    for (InvocationParameter parameter : request.getParameters()) {
      parameterObjects.add(parameter.getValue());
      // switch (parameter.getKind()) {
      // case BYVALUE:
      // // In case of primitive
      // parameterObjects.add(parameter.getValue());
      // break;
      // case BYREFERENCE:
      // // In this case we have a direct URI to an object.
      // Storage storage = storageApi.get(Invocations.INVOCATION_SCHEME);
      // try {
      // parameterObjects.add(storage.read(URI.create(parameter.getStringValue())));
      // } catch (Exception e) {
      // throw new IllegalArgumentException(
      // "Invalid URI parameter " + parameter.getValue() + " in the request: " + request, e);
      // }
      //
      // default:
      // break;
      // }
      // i++;
    }
    return parameterObjects;
  }

  /**
   * 
   * 
   * @param request
   * @param api
   * @param method
   * @return
   */
  public static InvocationParameter invokeMethod(InvocationRequest request, Object api,
      Method method) {
    List<Object> parameterObjects = getParameterObjects(request, method);
    try {
      Object result = method.invoke(api, parameterObjects.toArray());
      InvocationParameter invocationResult = new InvocationParameter();
      invocationResult.setValue(result);
      // invocationResult.setKind(Kind.BYVALUE);
      if (result != null) {
        invocationResult.setTypeClass(result.getClass().getName());
        // invocationResult.setStringValue(result.toString());
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
    try {
      Class<?> typeClass = Class.forName(parameter.getTypeClass());
      Object data = objectMapper.convertValue(parameter.getValue(), typeClass);
      parameter.setValue(data);
    } catch (ClassNotFoundException | IllegalArgumentException e) {
      throw new IllegalArgumentException("Error while resolving invocation parameter!" + parameter,
          e);
    }
  }

}
