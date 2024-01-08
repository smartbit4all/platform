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
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * This is a generic Service connection parameter object. It can be used genericly to identify a service with all the necessary parameters. It can be managed as MDM entry for different purposes. 
 */
@ApiModel(description = "This is a generic Service connection parameter object. It can be used genericly to identify a service with all the necessary parameters. It can be managed as MDM entry for different purposes. ")
@JsonPropertyOrder({
  ServiceConnection.URI,
  ServiceConnection.NAME,
  ServiceConnection.API_NAME,
  ServiceConnection.AUTH_TOKEN,
  ServiceConnection.API_VERSION,
  ServiceConnection.ENDPOINT,
  ServiceConnection.PARAMETERS
})
@JsonTypeName("ServiceConnection")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class ServiceConnection {
  public static final String URI = "uri";
  private URI uri;

  public static final String NAME = "name";
  private String name;

  public static final String API_NAME = "apiName";
  private String apiName;

  public static final String AUTH_TOKEN = "authToken";
  private String authToken;

  public static final String API_VERSION = "apiVersion";
  private String apiVersion;

  public static final String ENDPOINT = "endpoint";
  private String endpoint;

  public static final String PARAMETERS = "parameters";
  private Map<String, Object> parameters = null;

  public ServiceConnection() { 
  }

  public ServiceConnection uri(URI uri) {
    
    this.uri = uri;
    return this;
  }

   /**
   * Get uri
   * @return uri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getUri() {
    return uri;
  }


  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setUri(URI uri) {
    this.uri = uri;
  }


  public ServiceConnection name(String name) {
    
    this.name = name;
    return this;
  }

   /**
   * The identifier name of the AI service.
   * @return name
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The identifier name of the AI service.")
  @JsonProperty(NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getName() {
    return name;
  }


  @JsonProperty(NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setName(String name) {
    this.name = name;
  }


  public ServiceConnection apiName(String apiName) {
    
    this.apiName = apiName;
    return this;
  }

   /**
   * The name of the local api to access the given endpoint. Usually it is the name of a contribution api. 
   * @return apiName
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The name of the local api to access the given endpoint. Usually it is the name of a contribution api. ")
  @JsonProperty(API_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getApiName() {
    return apiName;
  }


  @JsonProperty(API_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setApiName(String apiName) {
    this.apiName = apiName;
  }


  public ServiceConnection authToken(String authToken) {
    
    this.authToken = authToken;
    return this;
  }

   /**
   * As a crucial parameter for the access the authentication token (bearer token or similar). 
   * @return authToken
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "As a crucial parameter for the access the authentication token (bearer token or similar). ")
  @JsonProperty(AUTH_TOKEN)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getAuthToken() {
    return authToken;
  }


  @JsonProperty(AUTH_TOKEN)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setAuthToken(String authToken) {
    this.authToken = authToken;
  }


  public ServiceConnection apiVersion(String apiVersion) {
    
    this.apiVersion = apiVersion;
    return this;
  }

   /**
   * The optional version parameter of the api endpoint. 
   * @return apiVersion
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The optional version parameter of the api endpoint. ")
  @JsonProperty(API_VERSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getApiVersion() {
    return apiVersion;
  }


  @JsonProperty(API_VERSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }


  public ServiceConnection endpoint(String endpoint) {
    
    this.endpoint = endpoint;
    return this;
  }

   /**
   * The endpoint URL typically. 
   * @return endpoint
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The endpoint URL typically. ")
  @JsonProperty(ENDPOINT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getEndpoint() {
    return endpoint;
  }


  @JsonProperty(ENDPOINT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }


  public ServiceConnection parameters(Map<String, Object> parameters) {
    
    this.parameters = parameters;
    return this;
  }

  public ServiceConnection putParametersItem(String key, Object parametersItem) {
    if (this.parameters == null) {
      this.parameters = new HashMap<>();
    }
    this.parameters.put(key, parametersItem);
    return this;
  }

   /**
   * Get parameters
   * @return parameters
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(PARAMETERS)
  @JsonInclude(content = JsonInclude.Include.ALWAYS, value = JsonInclude.Include.USE_DEFAULTS)

  public Map<String, Object> getParameters() {
    return parameters;
  }


  @JsonProperty(PARAMETERS)
  @JsonInclude(content = JsonInclude.Include.ALWAYS, value = JsonInclude.Include.USE_DEFAULTS)
  public void setParameters(Map<String, Object> parameters) {
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
    ServiceConnection serviceConnection = (ServiceConnection) o;
    return Objects.equals(this.uri, serviceConnection.uri) &&
        Objects.equals(this.name, serviceConnection.name) &&
        Objects.equals(this.apiName, serviceConnection.apiName) &&
        Objects.equals(this.authToken, serviceConnection.authToken) &&
        Objects.equals(this.apiVersion, serviceConnection.apiVersion) &&
        Objects.equals(this.endpoint, serviceConnection.endpoint) &&
        Objects.equals(this.parameters, serviceConnection.parameters);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, name, apiName, authToken, apiVersion, endpoint, parameters);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServiceConnection {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    apiName: ").append(toIndentedString(apiName)).append("\n");
    sb.append("    authToken: ").append(toIndentedString(authToken)).append("\n");
    sb.append("    apiVersion: ").append(toIndentedString(apiVersion)).append("\n");
    sb.append("    endpoint: ").append(toIndentedString(endpoint)).append("\n");
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

