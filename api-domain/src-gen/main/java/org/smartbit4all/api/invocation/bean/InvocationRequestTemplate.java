/*
 * invocation api
 * The invocation api is a generic possibility to call remote apis.
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.invocation.bean;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.invocation.bean.InvocationParameterTemplate;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * InvocationRequestTemplate
 */
@JsonPropertyOrder({
  InvocationRequestTemplate.EXECUTION_API,
  InvocationRequestTemplate.API_CLASS,
  InvocationRequestTemplate.API_INSTANCE_ID,
  InvocationRequestTemplate.INNER_API,
  InvocationRequestTemplate.METHOD_NAME,
  InvocationRequestTemplate.PARAMETERS
})
@JsonTypeName("InvocationRequestTemplate")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class InvocationRequestTemplate {
  public static final String EXECUTION_API = "executionApi";
  private String executionApi;

  public static final String API_CLASS = "apiClass";
  private String apiClass;

  public static final String API_INSTANCE_ID = "apiInstanceId";
  private String apiInstanceId;

  public static final String INNER_API = "innerApi";
  private String innerApi;

  public static final String METHOD_NAME = "methodName";
  private String methodName;

  public static final String PARAMETERS = "parameters";
  private List<InvocationParameterTemplate> parameters = new ArrayList<>();


  public InvocationRequestTemplate executionApi(String executionApi) {
    
    this.executionApi = executionApi;
    return this;
  }

   /**
   * The name of the execution api. If it is empty then we use the local by default
   * @return executionApi
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The name of the execution api. If it is empty then we use the local by default")
  @JsonProperty(EXECUTION_API)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getExecutionApi() {
    return executionApi;
  }


  @JsonProperty(EXECUTION_API)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setExecutionApi(String executionApi) {
    this.executionApi = executionApi;
  }


  public InvocationRequestTemplate apiClass(String apiClass) {
    
    this.apiClass = apiClass;
    return this;
  }

   /**
   * The fully qualified name of the api. In case of java it is the name of the class.
   * @return apiClass
  **/
  @NotNull
  @ApiModelProperty(required = true, value = "The fully qualified name of the api. In case of java it is the name of the class.")
  @JsonProperty(API_CLASS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getApiClass() {
    return apiClass;
  }


  @JsonProperty(API_CLASS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setApiClass(String apiClass) {
    this.apiClass = apiClass;
  }


  public InvocationRequestTemplate apiInstanceId(String apiInstanceId) {
    
    this.apiInstanceId = apiInstanceId;
    return this;
  }

   /**
   * The unique identifier for the api instance. If it is set then the execution will be routed to the given instance.
   * @return apiInstanceId
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The unique identifier for the api instance. If it is set then the execution will be routed to the given instance.")
  @JsonProperty(API_INSTANCE_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getApiInstanceId() {
    return apiInstanceId;
  }


  @JsonProperty(API_INSTANCE_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setApiInstanceId(String apiInstanceId) {
    this.apiInstanceId = apiInstanceId;
  }


  public InvocationRequestTemplate innerApi(String innerApi) {
    
    this.innerApi = innerApi;
    return this;
  }

   /**
   * If we have a contributed api collecting other apis then it is the identifier of the contributed api.
   * @return innerApi
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "If we have a contributed api collecting other apis then it is the identifier of the contributed api.")
  @JsonProperty(INNER_API)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getInnerApi() {
    return innerApi;
  }


  @JsonProperty(INNER_API)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setInnerApi(String innerApi) {
    this.innerApi = innerApi;
  }


  public InvocationRequestTemplate methodName(String methodName) {
    
    this.methodName = methodName;
    return this;
  }

   /**
   * The name of the method to call
   * @return methodName
  **/
  @NotNull
  @ApiModelProperty(required = true, value = "The name of the method to call")
  @JsonProperty(METHOD_NAME)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getMethodName() {
    return methodName;
  }


  @JsonProperty(METHOD_NAME)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }


  public InvocationRequestTemplate parameters(List<InvocationParameterTemplate> parameters) {
    
    this.parameters = parameters;
    return this;
  }

  public InvocationRequestTemplate addParametersItem(InvocationParameterTemplate parametersItem) {
    this.parameters.add(parametersItem);
    return this;
  }

   /**
   * Get parameters
   * @return parameters
  **/
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(PARAMETERS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<InvocationParameterTemplate> getParameters() {
    return parameters;
  }


  @JsonProperty(PARAMETERS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setParameters(List<InvocationParameterTemplate> parameters) {
    this.parameters = parameters;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InvocationRequestTemplate invocationRequestTemplate = (InvocationRequestTemplate) o;
    return Objects.equals(this.executionApi, invocationRequestTemplate.executionApi) &&
        Objects.equals(this.apiClass, invocationRequestTemplate.apiClass) &&
        Objects.equals(this.apiInstanceId, invocationRequestTemplate.apiInstanceId) &&
        Objects.equals(this.innerApi, invocationRequestTemplate.innerApi) &&
        Objects.equals(this.methodName, invocationRequestTemplate.methodName) &&
        Objects.equals(this.parameters, invocationRequestTemplate.parameters);
  }

  @Override
  public int hashCode() {
    return Objects.hash(executionApi, apiClass, apiInstanceId, innerApi, methodName, parameters);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InvocationRequestTemplate {\n");
    sb.append("    executionApi: ").append(toIndentedString(executionApi)).append("\n");
    sb.append("    apiClass: ").append(toIndentedString(apiClass)).append("\n");
    sb.append("    apiInstanceId: ").append(toIndentedString(apiInstanceId)).append("\n");
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
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

