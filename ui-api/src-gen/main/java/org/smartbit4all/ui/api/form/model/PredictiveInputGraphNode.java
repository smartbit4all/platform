package org.smartbit4all.ui.api.form.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * A single node in the input graph, contains an URI that points to a descriptor. The descriptor can be a simple property but detail and reference as well.
 */
@ApiModel(description = "A single node in the input graph, contains an URI that points to a descriptor. The descriptor can be a simple property but detail and reference as well.")

public class PredictiveInputGraphNode   {
  /**
   * Gets or Sets kind
   */
  public enum KindEnum {
    WIDGET("widget"),
    
    DETAIL("detail");

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

  @JsonProperty("descriptorUri")
  private URI descriptorUri;

  @JsonProperty("children")
  @Valid
  private List<PredictiveInputGraphNode> children = null;

  public PredictiveInputGraphNode kind(KindEnum kind) {
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

  public PredictiveInputGraphNode descriptorUri(URI descriptorUri) {
    this.descriptorUri = descriptorUri;
    return this;
  }

  /**
   * The uri of the widget descriptor or the detail descriptor.
   * @return descriptorUri
  */
  @ApiModelProperty(value = "The uri of the widget descriptor or the detail descriptor.")

  @Valid

  public URI getDescriptorUri() {
    return descriptorUri;
  }

  public void setDescriptorUri(URI descriptorUri) {
    this.descriptorUri = descriptorUri;
  }

  public PredictiveInputGraphNode children(List<PredictiveInputGraphNode> children) {
    this.children = children;
    return this;
  }

  public PredictiveInputGraphNode addChildrenItem(PredictiveInputGraphNode childrenItem) {
    if (this.children == null) {
      this.children = new ArrayList<>();
    }
    this.children.add(childrenItem);
    return this;
  }

  /**
   * Get children
   * @return children
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<PredictiveInputGraphNode> getChildren() {
    return children;
  }

  public void setChildren(List<PredictiveInputGraphNode> children) {
    this.children = children;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PredictiveInputGraphNode predictiveInputGraphNode = (PredictiveInputGraphNode) o;
    return Objects.equals(this.kind, predictiveInputGraphNode.kind) &&
        Objects.equals(this.descriptorUri, predictiveInputGraphNode.descriptorUri) &&
        Objects.equals(this.children, predictiveInputGraphNode.children);
  }

  @Override
  public int hashCode() {
    return Objects.hash(kind, descriptorUri, children);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PredictiveInputGraphNode {\n");
    
    sb.append("    kind: ").append(toIndentedString(kind)).append("\n");
    sb.append("    descriptorUri: ").append(toIndentedString(descriptorUri)).append("\n");
    sb.append("    children: ").append(toIndentedString(children)).append("\n");
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

