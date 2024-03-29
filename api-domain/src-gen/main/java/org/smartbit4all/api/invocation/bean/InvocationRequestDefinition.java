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
import org.smartbit4all.api.invocation.bean.InvocationParameterResolver;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * The definition contains a request as a template and the definitions of the input parameters. It can be used to initiate a new InvocationRequest by resolving the necessary input parameters. The result of the invocation can be direct (the return value) and indirect, when the invocation itself modifies the related objects. 
 */
@ApiModel(description = "The definition contains a request as a template and the definitions of the input parameters. It can be used to initiate a new InvocationRequest by resolving the necessary input parameters. The result of the invocation can be direct (the return value) and indirect, when the invocation itself modifies the related objects. ")
@JsonPropertyOrder({
  InvocationRequestDefinition.REQUEST,
  InvocationRequestDefinition.RESOLVERS
})
@JsonTypeName("InvocationRequestDefinition")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class InvocationRequestDefinition {
  public static final String REQUEST = "request";
  private InvocationRequest request;

  public static final String RESOLVERS = "resolvers";
  private List<InvocationParameterResolver> resolvers = null;

  public InvocationRequestDefinition() { 
  }

  public InvocationRequestDefinition request(InvocationRequest request) {
    
    this.request = request;
    return this;
  }

   /**
   * Get request
   * @return request
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(REQUEST)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public InvocationRequest getRequest() {
    return request;
  }


  @JsonProperty(REQUEST)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setRequest(InvocationRequest request) {
    this.request = request;
  }


  public InvocationRequestDefinition resolvers(List<InvocationParameterResolver> resolvers) {
    
    this.resolvers = resolvers;
    return this;
  }

  public InvocationRequestDefinition addResolversItem(InvocationParameterResolver resolversItem) {
    if (this.resolvers == null) {
      this.resolvers = new ArrayList<>();
    }
    this.resolvers.add(resolversItem);
    return this;
  }

   /**
   * The resolvers are used to setup parameters of the newly created invocation request.
   * @return resolvers
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The resolvers are used to setup parameters of the newly created invocation request.")
  @JsonProperty(RESOLVERS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<InvocationParameterResolver> getResolvers() {
    return resolvers;
  }


  @JsonProperty(RESOLVERS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setResolvers(List<InvocationParameterResolver> resolvers) {
    this.resolvers = resolvers;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InvocationRequestDefinition invocationRequestDefinition = (InvocationRequestDefinition) o;
    return Objects.equals(this.request, invocationRequestDefinition.request) &&
        Objects.equals(this.resolvers, invocationRequestDefinition.resolvers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(request, resolvers);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InvocationRequestDefinition {\n");
    sb.append("    request: ").append(toIndentedString(request)).append("\n");
    sb.append("    resolvers: ").append(toIndentedString(resolvers)).append("\n");
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

