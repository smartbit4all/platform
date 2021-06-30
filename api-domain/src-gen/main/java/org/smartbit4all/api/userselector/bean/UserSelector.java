package org.smartbit4all.api.userselector.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * UserSelector
 */

public class UserSelector   {
  /**
   * Gets or Sets kind
   */
  public enum KindEnum {
    USER("USER"),
    
    GROUP("GROUP");

    private String value;

    KindEnum(String value) {
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
    public static KindEnum fromValue(String value) {
      for (KindEnum b : KindEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("kind")
  private KindEnum kind;

  @JsonProperty("uri")
  private URI uri;

  @JsonProperty("displayName")
  private String displayName;

  public UserSelector kind(KindEnum kind) {
    this.kind = kind;
    return this;
  }

  /**
   * Get kind
   * @return kind
  */
  @ApiModelProperty(value = "")


  public KindEnum getKind() {
    return kind;
  }

  public void setKind(KindEnum kind) {
    this.kind = kind;
  }

  public UserSelector uri(URI uri) {
    this.uri = uri;
    return this;
  }

  /**
   * Get uri
   * @return uri
  */
  @ApiModelProperty(value = "")

  @Valid

  public URI getUri() {
    return uri;
  }

  public void setUri(URI uri) {
    this.uri = uri;
  }

  public UserSelector displayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

  /**
   * Get displayName
   * @return displayName
  */
  @ApiModelProperty(value = "")


  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserSelector userSelector = (UserSelector) o;
    return Objects.equals(this.kind, userSelector.kind) &&
        Objects.equals(this.uri, userSelector.uri) &&
        Objects.equals(this.displayName, userSelector.displayName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(kind, uri, displayName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserSelector {\n");
    
    sb.append("    kind: ").append(toIndentedString(kind)).append("\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

