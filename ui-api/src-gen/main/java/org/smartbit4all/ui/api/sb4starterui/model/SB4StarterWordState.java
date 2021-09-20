/*
 * sb4starter ui api
 * sb4starter ui api
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.ui.api.sb4starterui.model;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.*;
import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets SB4StarterWordState
 */
public enum SB4StarterWordState {
  
  DOWNLOADING("Letöltés folyamatban"),
  
  EDITING("Szerkesztés folyamatban"),
  
  UPLOADED("Szerkesztés befejezve");

  private String value;

  SB4StarterWordState(String value) {
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
  public static SB4StarterWordState fromValue(String value) {
    for (SB4StarterWordState b : SB4StarterWordState.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
