/*
 * Session api
 * Session api...
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.session.bean;

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
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * When a user executes an activity this entry is created based on the current session. It contains the information we know about the user and the time of the execution. The user is not necessarily user of the system so the user uri is optional and the user is a copied value from the session. 
 */
@ApiModel(description = "When a user executes an activity this entry is created based on the current session. It contains the information we know about the user and the time of the execution. The user is not necessarily user of the system so the user uri is optional and the user is a copied value from the session. ")
@JsonPropertyOrder({
  UserActivityLog.USER_URI,
  UserActivityLog.USER_NAME,
  UserActivityLog.ROLE,
  UserActivityLog.NAME,
  UserActivityLog.TIMESTAMP
})
@JsonTypeName("UserActivityLog")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class UserActivityLog {
  public static final String USER_URI = "userUri";
  private URI userUri;

  public static final String USER_NAME = "userName";
  private String userName;

  public static final String ROLE = "role";
  private String role;

  public static final String NAME = "name";
  private String name;

  public static final String TIMESTAMP = "timestamp";
  private OffsetDateTime timestamp;

  public UserActivityLog() { 
  }

  public UserActivityLog userUri(URI userUri) {
    
    this.userUri = userUri;
    return this;
  }

   /**
   * The uri reference of the user.
   * @return userUri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The uri reference of the user.")
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


  public UserActivityLog userName(String userName) {
    
    this.userName = userName;
    return this;
  }

   /**
   * The login name of the user.
   * @return userName
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The login name of the user.")
  @JsonProperty(USER_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getUserName() {
    return userName;
  }


  @JsonProperty(USER_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setUserName(String userName) {
    this.userName = userName;
  }


  public UserActivityLog role(String role) {
    
    this.role = role;
    return this;
  }

   /**
   * The role of the user.
   * @return role
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The role of the user.")
  @JsonProperty(ROLE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getRole() {
    return role;
  }


  @JsonProperty(ROLE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setRole(String role) {
    this.role = role;
  }


  public UserActivityLog name(String name) {
    
    this.name = name;
    return this;
  }

   /**
   * The natural name of the user.
   * @return name
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The natural name of the user.")
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


  public UserActivityLog timestamp(OffsetDateTime timestamp) {
    
    this.timestamp = timestamp;
    return this;
  }

   /**
   * Get timestamp
   * @return timestamp
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(TIMESTAMP)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public OffsetDateTime getTimestamp() {
    return timestamp;
  }


  @JsonProperty(TIMESTAMP)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setTimestamp(OffsetDateTime timestamp) {
    this.timestamp = timestamp;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserActivityLog userActivityLog = (UserActivityLog) o;
    return Objects.equals(this.userUri, userActivityLog.userUri) &&
        Objects.equals(this.userName, userActivityLog.userName) &&
        Objects.equals(this.role, userActivityLog.role) &&
        Objects.equals(this.name, userActivityLog.name) &&
        Objects.equals(this.timestamp, userActivityLog.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userUri, userName, role, name, timestamp);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserActivityLog {\n");
    sb.append("    userUri: ").append(toIndentedString(userUri)).append("\n");
    sb.append("    userName: ").append(toIndentedString(userName)).append("\n");
    sb.append("    role: ").append(toIndentedString(role)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
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

