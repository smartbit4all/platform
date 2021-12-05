/*
 * storage api
 * The storage api is a generic possibility to store and load objects.
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.storage.bean;

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
import java.time.OffsetDateTime;
import org.smartbit4all.api.storage.bean.TransactionState;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * The transaction data for the storage transaction management. For every new transaction a new transaction data will be created to  have a common entry point for. 
 */
@ApiModel(description = "The transaction data for the storage transaction management. For every new transaction a new transaction data will be created to  have a common entry point for. ")
@JsonPropertyOrder({
  TransactionData.URI,
  TransactionData.USER_URI,
  TransactionData.START_TIME,
  TransactionData.LAST_TOUCH,
  TransactionData.FINISH_TIME,
  TransactionData.STATE
})
@JsonTypeName("TransactionData")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class TransactionData {
  public static final String URI = "uri";
  private URI uri;

  public static final String USER_URI = "userUri";
  private URI userUri;

  public static final String START_TIME = "startTime";
  private OffsetDateTime startTime;

  public static final String LAST_TOUCH = "lastTouch";
  private OffsetDateTime lastTouch;

  public static final String FINISH_TIME = "finishTime";
  private OffsetDateTime finishTime;

  public static final String STATE = "state";
  private TransactionState state;


  public TransactionData uri(URI uri) {
    
    this.uri = uri;
    return this;
  }

   /**
   * The unique identifier of the transaction.
   * @return uri
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "The unique identifier of the transaction.")
  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public URI getUri() {
    return uri;
  }


  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setUri(URI uri) {
    this.uri = uri;
  }


  public TransactionData userUri(URI userUri) {
    
    this.userUri = userUri;
    return this;
  }

   /**
   * The uri of the user who is inititaed the transaction.
   * @return userUri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The uri of the user who is inititaed the transaction.")
  @JsonProperty(USER_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getUserUri() {
    return userUri;
  }


  @JsonProperty(USER_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setUserUri(URI userUri) {
    this.userUri = userUri;
  }


  public TransactionData startTime(OffsetDateTime startTime) {
    
    this.startTime = startTime;
    return this;
  }

   /**
   * The start time of the given transaction.
   * @return startTime
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "The start time of the given transaction.")
  @JsonProperty(START_TIME)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public OffsetDateTime getStartTime() {
    return startTime;
  }


  @JsonProperty(START_TIME)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setStartTime(OffsetDateTime startTime) {
    this.startTime = startTime;
  }


  public TransactionData lastTouch(OffsetDateTime lastTouch) {
    
    this.lastTouch = lastTouch;
    return this;
  }

   /**
   * If a transaction is executing then the server is responsible for updating the transaction record with a given frequency.  This information will be used by the other servers to detect if the server is down and the transaction will never be finished. 
   * @return lastTouch
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "If a transaction is executing then the server is responsible for updating the transaction record with a given frequency.  This information will be used by the other servers to detect if the server is down and the transaction will never be finished. ")
  @JsonProperty(LAST_TOUCH)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public OffsetDateTime getLastTouch() {
    return lastTouch;
  }


  @JsonProperty(LAST_TOUCH)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setLastTouch(OffsetDateTime lastTouch) {
    this.lastTouch = lastTouch;
  }


  public TransactionData finishTime(OffsetDateTime finishTime) {
    
    this.finishTime = finishTime;
    return this;
  }

   /**
   * If a transaction is finished on a proper way then this time is set. 
   * @return finishTime
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "If a transaction is finished on a proper way then this time is set. ")
  @JsonProperty(FINISH_TIME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public OffsetDateTime getFinishTime() {
    return finishTime;
  }


  @JsonProperty(FINISH_TIME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setFinishTime(OffsetDateTime finishTime) {
    this.finishTime = finishTime;
  }


  public TransactionData state(TransactionState state) {
    
    this.state = state;
    return this;
  }

   /**
   * Get state
   * @return state
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(STATE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public TransactionState getState() {
    return state;
  }


  @JsonProperty(STATE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setState(TransactionState state) {
    this.state = state;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TransactionData transactionData = (TransactionData) o;
    return Objects.equals(this.uri, transactionData.uri) &&
        Objects.equals(this.userUri, transactionData.userUri) &&
        Objects.equals(this.startTime, transactionData.startTime) &&
        Objects.equals(this.lastTouch, transactionData.lastTouch) &&
        Objects.equals(this.finishTime, transactionData.finishTime) &&
        Objects.equals(this.state, transactionData.state);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, userUri, startTime, lastTouch, finishTime, state);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransactionData {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    userUri: ").append(toIndentedString(userUri)).append("\n");
    sb.append("    startTime: ").append(toIndentedString(startTime)).append("\n");
    sb.append("    lastTouch: ").append(toIndentedString(lastTouch)).append("\n");
    sb.append("    finishTime: ").append(toIndentedString(finishTime)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
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

