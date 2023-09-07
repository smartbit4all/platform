/*
 * User selector api
 * User selector ui api.
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.userselector.bean;

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
 * UserSelfEditingModel
 */
@JsonPropertyOrder({
  UserSelfEditingModel.NAME,
  UserSelfEditingModel.USERNAME,
  UserSelfEditingModel.EMAIL,
  UserSelfEditingModel.OLD_PASSWORD,
  UserSelfEditingModel.NEW_PASSWORD1,
  UserSelfEditingModel.NEW_PASSWORD2
})
@JsonTypeName("UserSelfEditingModel")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class UserSelfEditingModel {
  public static final String NAME = "name";
  private String name;

  public static final String USERNAME = "username";
  private String username;

  public static final String EMAIL = "email";
  private String email;

  public static final String OLD_PASSWORD = "oldPassword";
  private String oldPassword;

  public static final String NEW_PASSWORD1 = "newPassword1";
  private String newPassword1;

  public static final String NEW_PASSWORD2 = "newPassword2";
  private String newPassword2;

  public UserSelfEditingModel() { 
  }

  public UserSelfEditingModel name(String name) {
    
    this.name = name;
    return this;
  }

   /**
   * Get name
   * @return name
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
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


  public UserSelfEditingModel username(String username) {
    
    this.username = username;
    return this;
  }

   /**
   * Get username
   * @return username
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(USERNAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getUsername() {
    return username;
  }


  @JsonProperty(USERNAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setUsername(String username) {
    this.username = username;
  }


  public UserSelfEditingModel email(String email) {
    
    this.email = email;
    return this;
  }

   /**
   * Get email
   * @return email
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(EMAIL)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getEmail() {
    return email;
  }


  @JsonProperty(EMAIL)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setEmail(String email) {
    this.email = email;
  }


  public UserSelfEditingModel oldPassword(String oldPassword) {
    
    this.oldPassword = oldPassword;
    return this;
  }

   /**
   * Get oldPassword
   * @return oldPassword
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(OLD_PASSWORD)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getOldPassword() {
    return oldPassword;
  }


  @JsonProperty(OLD_PASSWORD)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setOldPassword(String oldPassword) {
    this.oldPassword = oldPassword;
  }


  public UserSelfEditingModel newPassword1(String newPassword1) {
    
    this.newPassword1 = newPassword1;
    return this;
  }

   /**
   * Get newPassword1
   * @return newPassword1
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(NEW_PASSWORD1)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getNewPassword1() {
    return newPassword1;
  }


  @JsonProperty(NEW_PASSWORD1)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setNewPassword1(String newPassword1) {
    this.newPassword1 = newPassword1;
  }


  public UserSelfEditingModel newPassword2(String newPassword2) {
    
    this.newPassword2 = newPassword2;
    return this;
  }

   /**
   * Get newPassword2
   * @return newPassword2
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(NEW_PASSWORD2)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getNewPassword2() {
    return newPassword2;
  }


  @JsonProperty(NEW_PASSWORD2)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setNewPassword2(String newPassword2) {
    this.newPassword2 = newPassword2;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserSelfEditingModel userSelfEditingModel = (UserSelfEditingModel) o;
    return Objects.equals(this.name, userSelfEditingModel.name) &&
        Objects.equals(this.username, userSelfEditingModel.username) &&
        Objects.equals(this.email, userSelfEditingModel.email) &&
        Objects.equals(this.oldPassword, userSelfEditingModel.oldPassword) &&
        Objects.equals(this.newPassword1, userSelfEditingModel.newPassword1) &&
        Objects.equals(this.newPassword2, userSelfEditingModel.newPassword2);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, username, email, oldPassword, newPassword1, newPassword2);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserSelfEditingModel {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    oldPassword: ").append(toIndentedString(oldPassword)).append("\n");
    sb.append("    newPassword1: ").append(toIndentedString(newPassword1)).append("\n");
    sb.append("    newPassword2: ").append(toIndentedString(newPassword2)).append("\n");
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
