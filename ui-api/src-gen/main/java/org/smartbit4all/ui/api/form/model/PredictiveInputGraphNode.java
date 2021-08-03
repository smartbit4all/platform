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
 * A single node in the input graph, contains a URI that points to a descriptor. The descriptor can be a simple property but a detail or a reference as well.
 */
@ApiModel(description = "A single node in the input graph, contains a URI that points to a descriptor. The descriptor can be a simple property but a detail or a reference as well.")

public class PredictiveInputGraphNode   {
  @JsonProperty("uri")
  private URI uri;

  /**
   * Describes whether this node references a simple property widget or a form, that possibly contains multiple widgets of even forms.
   */
  public enum KindEnum {
    WIDGET("widget"),
    
    FORM("form");

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

  @JsonProperty("multiplicity")
  private Integer multiplicity;

  @JsonProperty("descriptorUri")
  private URI descriptorUri;

  @JsonProperty("parent")
  private URI parent;

  @JsonProperty("children")
  @Valid
  private List<URI> children = null;

  public PredictiveInputGraphNode uri(URI uri) {
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

  public PredictiveInputGraphNode kind(KindEnum kind) {
    this.kind = kind;
    return this;
  }

  /**
   * Describes whether this node references a simple property widget or a form, that possibly contains multiple widgets of even forms.
   * @return kind
  */
  @ApiModelProperty(value = "Describes whether this node references a simple property widget or a form, that possibly contains multiple widgets of even forms.")


  public KindEnum getKind() {
    return kind;
  }

  public void setKind(KindEnum kind) {
    this.kind = kind;
  }

  public PredictiveInputGraphNode multiplicity(Integer multiplicity) {
    this.multiplicity = multiplicity;
    return this;
  }

  /**
   * Indicates how many of the referenced widget or form can be added at this node of the graph. -1 means the absence of an upper limit.
   * @return multiplicity
  */
  @ApiModelProperty(value = "Indicates how many of the referenced widget or form can be added at this node of the graph. -1 means the absence of an upper limit.")


  public Integer getMultiplicity() {
    return multiplicity;
  }

  public void setMultiplicity(Integer multiplicity) {
    this.multiplicity = multiplicity;
  }

  public PredictiveInputGraphNode descriptorUri(URI descriptorUri) {
    this.descriptorUri = descriptorUri;
    return this;
  }

  /**
   * The uri of the widget descriptor or the form descriptor.
   * @return descriptorUri
  */
  @ApiModelProperty(value = "The uri of the widget descriptor or the form descriptor.")

  @Valid

  public URI getDescriptorUri() {
    return descriptorUri;
  }

  public void setDescriptorUri(URI descriptorUri) {
    this.descriptorUri = descriptorUri;
  }

  public PredictiveInputGraphNode parent(URI parent) {
    this.parent = parent;
    return this;
  }

  /**
   * Get parent
   * @return parent
  */
  @ApiModelProperty(value = "")

  @Valid

  public URI getParent() {
    return parent;
  }

  public void setParent(URI parent) {
    this.parent = parent;
  }

  public PredictiveInputGraphNode children(List<URI> children) {
    this.children = children;
    return this;
  }

  public PredictiveInputGraphNode addChildrenItem(URI childrenItem) {
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

  public List<URI> getChildren() {
    return children;
  }

  public void setChildren(List<URI> children) {
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
    return Objects.equals(this.uri, predictiveInputGraphNode.uri) &&
        Objects.equals(this.kind, predictiveInputGraphNode.kind) &&
        Objects.equals(this.multiplicity, predictiveInputGraphNode.multiplicity) &&
        Objects.equals(this.descriptorUri, predictiveInputGraphNode.descriptorUri) &&
        Objects.equals(this.parent, predictiveInputGraphNode.parent) &&
        Objects.equals(this.children, predictiveInputGraphNode.children);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, kind, multiplicity, descriptorUri, parent, children);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PredictiveInputGraphNode {\n");
    
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    kind: ").append(toIndentedString(kind)).append("\n");
    sb.append("    multiplicity: ").append(toIndentedString(multiplicity)).append("\n");
    sb.append("    descriptorUri: ").append(toIndentedString(descriptorUri)).append("\n");
    sb.append("    parent: ").append(toIndentedString(parent)).append("\n");
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

