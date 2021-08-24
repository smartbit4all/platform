package org.smartbit4all.api.invocation.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.invocation.bean.InvocationParameterData;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * InvocationRequestData
 */

public class InvocationRequestData   {
  @JsonProperty("uuid")
  private UUID uuid;

  @JsonProperty("executionApi")
  private String executionApi = "local";

  @JsonProperty("apiClass")
  private String apiClass;

  @JsonProperty("innerApi")
  private String innerApi;

  @JsonProperty("methodName")
  private String methodName;

  @JsonProperty("parameters")
  @Valid
  private List<InvocationParameterData> parameters = new ArrayList<>();

  public InvocationRequestData uuid(UUID uuid) {
    this.uuid = uuid;
    return this;
  }

  /**
   * The unique identifier of the request.
   * @return uuid
  */
  @ApiModelProperty(required = true, value = "The unique identifier of the request.")
  @NotNull

  @Valid

  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public InvocationRequestData executionApi(String executionApi) {
    this.executionApi = executionApi;
    return this;
  }

  /**
   * The name of the execution api. By default it is local.
   * @return executionApi
  */
  @ApiModelProperty(required = true, value = "The name of the execution api. By default it is local.")
  @NotNull


  public String getExecutionApi() {
    return executionApi;
  }

  public void setExecutionApi(String executionApi) {
    this.executionApi = executionApi;
  }

  public InvocationRequestData apiClass(String apiClass) {
    this.apiClass = apiClass;
    return this;
  }

  /**
   * The fully qualified name of the api. In case of java it is the name of the class.
   * @return apiClass
  */
  @ApiModelProperty(required = true, value = "The fully qualified name of the api. In case of java it is the name of the class.")
  @NotNull


  public String getApiClass() {
    return apiClass;
  }

  public void setApiClass(String apiClass) {
    this.apiClass = apiClass;
  }

  public InvocationRequestData innerApi(String innerApi) {
    this.innerApi = innerApi;
    return this;
  }

  /**
   * If we have a contributed api collecting other apis then it is the identifier of the contributed api.
   * @return innerApi
  */
  @ApiModelProperty(value = "If we have a contributed api collecting other apis then it is the identifier of the contributed api.")


  public String getInnerApi() {
    return innerApi;
  }

  public void setInnerApi(String innerApi) {
    this.innerApi = innerApi;
  }

  public InvocationRequestData methodName(String methodName) {
    this.methodName = methodName;
    return this;
  }

  /**
   * The name of the method to call
   * @return methodName
  */
  @ApiModelProperty(required = true, value = "The name of the method to call")
  @NotNull


  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public InvocationRequestData parameters(List<InvocationParameterData> parameters) {
    this.parameters = parameters;
    return this;
  }

  public InvocationRequestData addParametersItem(InvocationParameterData parametersItem) {
    this.parameters.add(parametersItem);
    return this;
  }

  /**
   * Get parameters
   * @return parameters
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public List<InvocationParameterData> getParameters() {
    return parameters;
  }

  public void setParameters(List<InvocationParameterData> parameters) {
    this.parameters = parameters;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InvocationRequestData invocationRequestData = (InvocationRequestData) o;
    return Objects.equals(this.uuid, invocationRequestData.uuid) &&
        Objects.equals(this.executionApi, invocationRequestData.executionApi) &&
        Objects.equals(this.apiClass, invocationRequestData.apiClass) &&
        Objects.equals(this.innerApi, invocationRequestData.innerApi) &&
        Objects.equals(this.methodName, invocationRequestData.methodName) &&
        Objects.equals(this.parameters, invocationRequestData.parameters);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, executionApi, apiClass, innerApi, methodName, parameters);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InvocationRequestData {\n");
    
    sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
    sb.append("    executionApi: ").append(toIndentedString(executionApi)).append("\n");
    sb.append("    apiClass: ").append(toIndentedString(apiClass)).append("\n");
    sb.append("    innerApi: ").append(toIndentedString(innerApi)).append("\n");
    sb.append("    methodName: ").append(toIndentedString(methodName)).append("\n");
    sb.append("    parameters: ").append(toIndentedString(parameters)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

