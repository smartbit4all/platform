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
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.session.bean.AccountInfo;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * SessionInfoData
 */
@JsonPropertyOrder({
  SessionInfoData.SID,
  SessionInfoData.EXPIRATION,
  SessionInfoData.LOCALE,
  SessionInfoData.AUTHENTICATIONS
})
@JsonTypeName("SessionInfoData")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class SessionInfoData {
  public static final String SID = "sid";
  private String sid;

  public static final String EXPIRATION = "expiration";
  private OffsetDateTime expiration;

  public static final String LOCALE = "locale";
  private String locale;

  public static final String AUTHENTICATIONS = "authentications";
  private List<AccountInfo> authentications = null;


  public SessionInfoData sid(String sid) {
    
    this.sid = sid;
    return this;
  }

   /**
   * Get sid
   * @return sid
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(SID)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getSid() {
    return sid;
  }


  @JsonProperty(SID)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setSid(String sid) {
    this.sid = sid;
  }


  public SessionInfoData expiration(OffsetDateTime expiration) {
    
    this.expiration = expiration;
    return this;
  }

   /**
   * Get expiration
   * @return expiration
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(EXPIRATION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public OffsetDateTime getExpiration() {
    return expiration;
  }


  @JsonProperty(EXPIRATION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setExpiration(OffsetDateTime expiration) {
    this.expiration = expiration;
  }


  public SessionInfoData locale(String locale) {
    
    this.locale = locale;
    return this;
  }

   /**
   * Get locale
   * @return locale
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(LOCALE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getLocale() {
    return locale;
  }


  @JsonProperty(LOCALE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setLocale(String locale) {
    this.locale = locale;
  }


  public SessionInfoData authentications(List<AccountInfo> authentications) {
    
    this.authentications = authentications;
    return this;
  }

  public SessionInfoData addAuthenticationsItem(AccountInfo authenticationsItem) {
    if (this.authentications == null) {
      this.authentications = new ArrayList<>();
    }
    this.authentications.add(authenticationsItem);
    return this;
  }

   /**
   * Get authentications
   * @return authentications
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(AUTHENTICATIONS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<AccountInfo> getAuthentications() {
    return authentications;
  }


  @JsonProperty(AUTHENTICATIONS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setAuthentications(List<AccountInfo> authentications) {
    this.authentications = authentications;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SessionInfoData sessionInfoData = (SessionInfoData) o;
    return Objects.equals(this.sid, sessionInfoData.sid) &&
        Objects.equals(this.expiration, sessionInfoData.expiration) &&
        Objects.equals(this.locale, sessionInfoData.locale) &&
        Objects.equals(this.authentications, sessionInfoData.authentications);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sid, expiration, locale, authentications);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SessionInfoData {\n");
    sb.append("    sid: ").append(toIndentedString(sid)).append("\n");
    sb.append("    expiration: ").append(toIndentedString(expiration)).append("\n");
    sb.append("    locale: ").append(toIndentedString(locale)).append("\n");
    sb.append("    authentications: ").append(toIndentedString(authentications)).append("\n");
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
