package org.smartbit4all.api.invocation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.config.ApiConfig;
import org.smartbit4all.api.contribution.ContributionApi;
import org.smartbit4all.api.contribution.PrimaryApi;
import org.smartbit4all.api.invocation.bean.InvocationParameterKind;

/**
 * The {@link InvocationRequest} is the API object of the {@link InvocationApi}. We can setup an Api
 * call with this.
 * 
 * TODO Security info for every call
 * 
 * @author Peter Boros
 */
public class InvocationRequest {

  /**
   * The unique identifier of the invocation. Used for logging purposes and storage identifier.
   */
  private UUID uuid;

  /**
   * The Api class that can be an direct Api to call and can be a {@link PrimaryApi} also. In case
   * of {@link PrimaryApi} we need to specify the inner Api to identify the {@link ContributionApi}
   * to call.
   */
  private Class<?> apiClass;

  /**
   * The inner {@link ContributionApi}.
   */
  private String innerApi;

  /**
   * The method to call.
   */
  private String methodName;

  /**
   * The execution api to use. By default we use the {@link Invocations#LOCAL}. It's always exists
   * because it's configured by the {@link ApiConfig}.
   */
  private String executionApi = Invocations.LOCAL;

  /**
   * The parameters of the invocation.
   */
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
