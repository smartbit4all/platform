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
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationResult;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * If the invocation is executed asynchronously we need to save te request itself and the result of the call as one object. The asynchronous call is managed by a runtime that is also saved into this object. If the invocation failed then the exception is also part of this object. This is the whole histoty of the call. If the result of the call is awaited by a logic then it can subscribe for the execution result. 
 */
@ApiModel(description = "If the invocation is executed asynchronously we need to save te request itself and the result of the call as one object. The asynchronous call is managed by a runtime that is also saved into this object. If the invocation failed then the exception is also part of this object. This is the whole histoty of the call. If the result of the call is awaited by a logic then it can subscribe for the execution result. ")
@JsonPropertyOrder({
  AsyncInvocationRequest.URI,
  AsyncInvocationRequest.RUNTIME_URI,
  AsyncInvocationRequest.CHANNEL,
  AsyncInvocationRequest.REQUEST,
  AsyncInvocationRequest.EVALUATE,
  AsyncInvocationRequest.AND_THEN,
  AsyncInvocationRequest.RESULTS
})
@JsonTypeName("AsyncInvocationRequest")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class AsyncInvocationRequest {
  public static final String URI = "uri";
  private URI uri;

  public static final String RUNTIME_URI = "runtimeUri";
  private URI runtimeUri;

  public static final String CHANNEL = "channel";
  private String channel;

  public static final String REQUEST = "request";
  private InvocationRequest request;

  public static final String EVALUATE = "evaluate";
  private InvocationRequest evaluate;

  public static final String AND_THEN = "andThen";
  private List<URI> andThen = null;

  public static final String RESULTS = "results";
  private List<InvocationResult> results = null;

  public AsyncInvocationRequest() { 
  }

  public AsyncInvocationRequest uri(URI uri) {
    
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


  public AsyncInvocationRequest runtimeUri(URI runtimeUri) {
    
    this.runtimeUri = runtimeUri;
    return this;
  }

   /**
   * The URI of the appliocation runtime that is currently responsible for the givan invocation.
   * @return runtimeUri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The URI of the appliocation runtime that is currently responsible for the givan invocation.")
  @JsonProperty(RUNTIME_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getRuntimeUri() {
    return runtimeUri;
  }


  @JsonProperty(RUNTIME_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setRuntimeUri(URI runtimeUri) {
    this.runtimeUri = runtimeUri;
  }


  public AsyncInvocationRequest channel(String channel) {
    
    this.channel = channel;
    return this;
  }

   /**
   * The name of the channel handles the given invocation.
   * @return channel
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The name of the channel handles the given invocation.")
  @JsonProperty(CHANNEL)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getChannel() {
    return channel;
  }


  @JsonProperty(CHANNEL)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setChannel(String channel) {
    this.channel = channel;
  }


  public AsyncInvocationRequest request(InvocationRequest request) {
    
    this.request = request;
    return this;
  }

   /**
   * Get request
   * @return request
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(REQUEST)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public InvocationRequest getRequest() {
    return request;
  }


  @JsonProperty(REQUEST)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setRequest(InvocationRequest request) {
    this.request = request;
  }


  public AsyncInvocationRequest evaluate(InvocationRequest evaluate) {
    
    this.evaluate = evaluate;
    return this;
  }

   /**
   * Get evaluate
   * @return evaluate
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(EVALUATE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public InvocationRequest getEvaluate() {
    return evaluate;
  }


  @JsonProperty(EVALUATE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setEvaluate(InvocationRequest evaluate) {
    this.evaluate = evaluate;
  }


  public AsyncInvocationRequest andThen(List<URI> andThen) {
    
    this.andThen = andThen;
    return this;
  }

  public AsyncInvocationRequest addAndThenItem(URI andThenItem) {
    if (this.andThen == null) {
      this.andThen = new ArrayList<>();
    }
    this.andThen.add(andThenItem);
    return this;
  }

   /**
   * The URI of the next asyncronous invocations. This invocation automatically consumes the result of current invocation. 
   * @return andThen
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The URI of the next asyncronous invocations. This invocation automatically consumes the result of current invocation. ")
  @JsonProperty(AND_THEN)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<URI> getAndThen() {
    return andThen;
  }


  @JsonProperty(AND_THEN)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setAndThen(List<URI> andThen) {
    this.andThen = andThen;
  }


  public AsyncInvocationRequest results(List<InvocationResult> results) {
    
    this.results = results;
    return this;
  }

  public AsyncInvocationRequest addResultsItem(InvocationResult resultsItem) {
    if (this.results == null) {
      this.results = new ArrayList<>();
    }
    this.results.add(resultsItem);
    return this;
  }

   /**
   * Contains the inline list of the results in historic order. If the invocation was executed more then one times  then we have all of them here. 
   * @return results
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "Contains the inline list of the results in historic order. If the invocation was executed more then one times  then we have all of them here. ")
  @JsonProperty(RESULTS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<InvocationResult> getResults() {
    return results;
  }


  @JsonProperty(RESULTS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setResults(List<InvocationResult> results) {
    this.results = results;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AsyncInvocationRequest asyncInvocationRequest = (AsyncInvocationRequest) o;
    return Objects.equals(this.uri, asyncInvocationRequest.uri) &&
        Objects.equals(this.runtimeUri, asyncInvocationRequest.runtimeUri) &&
        Objects.equals(this.channel, asyncInvocationRequest.channel) &&
        Objects.equals(this.request, asyncInvocationRequest.request) &&
        Objects.equals(this.evaluate, asyncInvocationRequest.evaluate) &&
        Objects.equals(this.andThen, asyncInvocationRequest.andThen) &&
        Objects.equals(this.results, asyncInvocationRequest.results);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, runtimeUri, channel, request, evaluate, andThen, results);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AsyncInvocationRequest {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    runtimeUri: ").append(toIndentedString(runtimeUri)).append("\n");
    sb.append("    channel: ").append(toIndentedString(channel)).append("\n");
    sb.append("    request: ").append(toIndentedString(request)).append("\n");
    sb.append("    evaluate: ").append(toIndentedString(evaluate)).append("\n");
    sb.append("    andThen: ").append(toIndentedString(andThen)).append("\n");
    sb.append("    results: ").append(toIndentedString(results)).append("\n");
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

