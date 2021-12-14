package org.smartbit4all.api.invocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.contribution.ContributionApi;
import org.smartbit4all.api.contribution.PrimaryApi;
import org.smartbit4all.api.invocation.bean.InvocationParameterTemplate;
import org.smartbit4all.api.invocation.bean.InvocationRequestTemplate;

/**
 * The {@link InvocationRequest} is the API object of the {@link InvocationApi}. We can setup an Api
 * call with this.
 * 
 * TODO Security info for every call
 * 
 * @author Peter Boros
 */
public class InvocationRequest {


  private static final Logger log = LoggerFactory.getLogger(InvocationRequest.class);

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
   * The Api class that can be an direct Api to call and can be a {@link PrimaryApi} also. In case
   * of {@link PrimaryApi} we need to specify the inner Api to identify the {@link ContributionApi}
   * to call.
   */
  private String apiClassName;

  /**
   * The unique identifier of the API instance.
   */
  private UUID apiInstanceId;

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
   * because it's configured by the {@link PlatformApiConfig}.
   */
  private String executionApi = Invocations.LOCAL;

  /**
   * The parameters of the invocation.
   */
  private LinkedHashMap<String, InvocationParameter> parameters = new LinkedHashMap<>();

  public InvocationRequest(Class<?> apiClass) {
    super();
    setApiClass(apiClass);
    uuid = UUID.randomUUID();
  }

  public InvocationRequest(UUID apiInstanceId) {
    super();
    setApiInstanceId(apiInstanceId);
    uuid = UUID.randomUUID();
  }

  public InvocationRequest(String apiClassName) {
    super();
    this.apiClassName = apiClassName;
    if (apiClassName != null) {
      try {
        apiClass = Class.forName(apiClassName);
      } catch (ClassNotFoundException e) {
        log.debug("Unable to find the Api class", e);
      }
    }
    uuid = UUID.randomUUID();
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
    setExecutionApi(executionApi == null ? Invocations.LOCAL : executionApi);
    return this;
  }

  /**
   * Adds a new parameter to the invocation.
   * 
   * @param name The name of the parameter.
   * @param kind The kind of the parameter.
   * @param value The value object
   * @param typeClass The type of the value.
   * @return
   */
  public InvocationRequest addParameter(String name, InvocationParameter.Kind kind, Object value,
      String typeClass) {
    InvocationParameter parameter = new InvocationParameter();
    parameter.setKind(kind);
    parameter.setTypeClass(
        typeClass != null ? typeClass : (value != null ? value.getClass().getName() : typeClass));
    parameter.setValue(value);
    parameters.put(name, parameter);
    return this;
  }

  /**
   * Adds a new by value parameter to the invocation.
   * 
   * @param name The name of the parameter.
   * @param value The value object
   * @param typeClass The type of the value.
   * @return
   */
  public InvocationRequest addParameter(String name, Object value,
      String typeClass) {
    return addParameter(name, InvocationParameter.Kind.BYVALUE, value, typeClass);
  }

  /**
   * Adds a new by value parameter to the invocation.
   * 
   * @param name The name of the parameter.
   * @param value The value object
   * @return
   */
  public InvocationRequest addParameter(String name, Object value) {
    Objects.requireNonNull(value,
        "value can not be null when there is no typeClass parameter given!");
    return addParameter(name, InvocationParameter.Kind.BYVALUE, value, null);
  }

  public InvocationRequest setParameter(String name, Object value) {
    InvocationParameter parameter = parameters.get(name);
    parameter.setValue(value);
    return this;
  }

  public InvocationRequest setParameters(Object... values) {
    if (values == null) {
      return this;
    }
    int i = 0;
    for (Entry<String, InvocationParameter> entry : parameters.entrySet()) {
      if (i < values.length) {
        entry.getValue().setValue(values[i]);
      } else {
        break;
      }
      i++;
    }
    return this;
  }

  private InvocationRequest() {

  }

  public final Class<?> getApiClass() {
    return apiClass;
  }

  public final void setApiClass(Class<?> apiClass) {
    this.apiClass = apiClass;
    if (apiClass != null) {
      apiClassName = apiClass.getName();
    }
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
    return new ArrayList<>(parameters.values());
  }

  /**
   * This factory method initiates a new {@link InvocationRequest} based on a template.
   * 
   * @param template
   * @return
   */
  public static final InvocationRequest of(InvocationRequestTemplate template) {
    InvocationRequest result = new InvocationRequest(template.getApiClass());
    result.apiClassName = template.getApiClass();
    result.innerApi(template.getInnerApi()).method(template.getMethodName())
        .exec(template.getExecutionApi());
    result.setApiInstanceId(template.getApiInstanceId());
    for (InvocationParameterTemplate parameter : template.getParameters()) {
      result.addParameter(parameter.getName(), InvocationParameter.Kind.BYVALUE, null,
          parameter.getTypeClass());
    }
    return result;
  }

  public final String getApiClassName() {
    return apiClassName;
  }

  public InvocationRequest copy() {
    InvocationRequest copyRequest = new InvocationRequest();
    copyRequest.apiClass = this.apiClass;
    copyRequest.apiClassName = apiClassName;
    copyRequest.uuid = this.uuid;
    copyRequest.executionApi = executionApi;
    copyRequest.methodName = methodName;
    parameters.entrySet().forEach((e) -> {
      InvocationParameter param = e.getValue();
      copyRequest.addParameter(e.getKey(), param.getKind(), param.getValue(), param.getTypeClass());
    });
    return copyRequest;
  }

  @Override
  public String toString() {
    return "InvocationRequest [uuid=" + uuid + ", apiClass=" + apiClass + ", apiClassName="
        + apiClassName + ", innerApi=" + innerApi + ", methodName=" + methodName + ", executionApi="
        + executionApi + ", parameters=" + parameters + "]";
  }

  public final UUID getApiInstanceId() {
    return apiInstanceId;
  }

  public final void setApiInstanceId(UUID apiInstanceId) {
    this.apiInstanceId = apiInstanceId;
  }

}
