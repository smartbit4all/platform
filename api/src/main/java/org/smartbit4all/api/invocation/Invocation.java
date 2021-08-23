package org.smartbit4all.api.invocation;

import java.util.UUID;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationParameterKind;
import org.smartbit4all.api.invocation.bean.InvocationRequest;

/**
 * The builder api for an {@link InvocationRequest}.
 * 
 * TODO Security info for every call
 * 
 * @author Peter Boros
 */
public class Invocation {

  private final InvocationRequest request;

  private Class<?> apiClass;

  public Invocation(Class<?> apiClass) {
    super();
    request = new InvocationRequest();
    request.setUuid(UUID.randomUUID());
    request.setApiClass(apiClass.getName());
    this.apiClass = apiClass;
  }

  public Invocation innerApi(String innerApiName) {
    request.setInnerApi(innerApiName);
    return this;
  }

  public Invocation method(String methodName) {
    request.setMethodName(methodName);
    return this;
  }

  public Invocation exec(String executionApi) {
    request.setExecutionApi(executionApi);
    return this;
  }

  public Invocation parameter(InvocationParameterKind kind, String value, String typeClass) {
    InvocationParameter parameter = new InvocationParameter();
    parameter.setKind(kind);
    parameter.setTypeClass(typeClass);
    parameter.setValue(value);
    request.getParameters().add(parameter);
    return this;
  }

  public InvocationRequest build() {
    return request;
  }

}
