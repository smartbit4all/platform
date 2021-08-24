package org.smartbit4all.api.invocation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.invocation.bean.InvocationParameterKind;

/**
 * The builder api for an {@link InvocationRequest}.
 * 
 * TODO Security info for every call
 * 
 * @author Peter Boros
 */
public class InvocationRequest {

  private UUID uuid;

  private Class<?> apiClass;

  private String innerApi;

  private String methodName;

  private String executionApi = Invocations.LOCAL;

  private List<InvocationParameter> parameters = new ArrayList<>();

  public InvocationRequest(Class<?> apiClass) {
    super();
    this.apiClass = apiClass;
  }

  public InvocationRequest innerApi(String innerApiName) {
    setInnerApi(innerApiName);
    return this;
  }

  public InvocationRequest method(String methodName) {
    setMethodName(methodName);
    return this;
  }

  public InvocationRequest exec(String executionApi) {
    setExecutionApi(executionApi);
    return this;
  }

  public InvocationRequest parameter(InvocationParameterKind kind, Object value, String typeClass) {
    InvocationParameter parameter = new InvocationParameter();
    parameter.setKind(kind);
    parameter.setTypeClass(typeClass);
    parameter.setValue(value);
    parameters.add(parameter);
    return this;
  }

  public final Class<?> getApiClass() {
    return apiClass;
  }

  public final void setApiClass(Class<?> apiClass) {
    this.apiClass = apiClass;
  }

  public final String getInnerApi() {
    return innerApi;
  }

  public final void setInnerApi(String innerApi) {
    this.innerApi = innerApi;
  }

  public final String getMethodName() {
    return methodName;
  }

  public final void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public final String getExecutionApi() {
    return executionApi;
  }

  public final void setExecutionApi(String executionApi) {
    this.executionApi = executionApi;
  }

  public final UUID getUuid() {
    return uuid;
  }

  public final List<InvocationParameter> getParameters() {
    return parameters;
  }

}
