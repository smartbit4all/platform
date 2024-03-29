/*
 * Security api
 * Security api...
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.security.bean;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * AthenticationResult
 */
@JsonPropertyOrder({
  AthenticationResult.FULLNAME,
  AthenticationResult.USERNAME,
  AthenticationResult.TOKEN
})
@JsonTypeName("AthenticationResult")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class AthenticationResult {
  public static final String FULLNAME = "fullname";
  private String fullname;

  public static final String USERNAME = "username";
  private String username;

  public static final String TOKEN = "token";
  private String token;

  public AthenticationResult() { 
  }

  public AthenticationResult fullname(String fullname) {
    
    this.fullname = fullname;
    return this;
  }

   /**
   * Get fullname
   * @return fullname
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(FULLNAME)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getFullname() {
    return fullname;
  }


  @JsonProperty(FULLNAME)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setFullname(String fullname) {
    this.fullname = fullname;
  }


  public AthenticationResult username(String username) {
    
    this.username = username;
    return this;
  }

   /**
   * Get username
   * @return username
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(USERNAME)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getUsername() {
    return username;
  }


  @JsonProperty(USERNAME)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setUsername(String username) {
    this.username = username;
  }


  public AthenticationResult token(String token) {
    
    this.token = token;
    return this;
  }

   /**
   * Get token
   * @return token
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(TOKEN)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getToken() {
    return token;
  }


  @JsonProperty(TOKEN)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setToken(String token) {
    this.token = token;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AthenticationResult athenticationResult = (AthenticationResult) o;
    return Objects.equals(this.fullname, athenticationResult.fullname) &&
        Objects.equals(this.username, athenticationResult.username) &&
        Objects.equals(this.token, athenticationResult.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fullname, username, token);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AthenticationResult {\n");
    sb.append("    fullname: ").append(toIndentedString(fullname)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    token: ").append(toIndentedString(token)).append("\n");
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

