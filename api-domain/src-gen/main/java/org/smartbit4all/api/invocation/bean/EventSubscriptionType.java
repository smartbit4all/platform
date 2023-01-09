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
import io.swagger.annotations.ApiModel;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * ONE_RUNTIME - The invocation of the publisher method implies exactly one invocation for the given consumer. ALL_RUNTIMES - The invocation of the publisher method implies one invocation for the every runtime providing the consumer API. SESSIONS - The invocation of the publisher method implies one invocation for the every session consuming the event. The session storage contains the map for the active sessions and also the sessions that are interested in the given event. 
 */
public enum EventSubscriptionType {
  
  ONE_RUNTIME("ONE_RUNTIME"),
  
  ALL_RUNTIMES("ALL_RUNTIMES"),
  
  SESSIONS("SESSIONS");

  private String value;

  EventSubscriptionType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static EventSubscriptionType fromValue(String value) {
    for (EventSubscriptionType b : EventSubscriptionType.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

