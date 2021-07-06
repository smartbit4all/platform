package org.smartbit4all.ui.api.form.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * An instanciated widget on a form refering its descriptor and containing the widget instance data.
 */
@ApiModel(description = "An instanciated widget on a form refering its descriptor and containing the widget instance data.")

public class WidgetInstance   {
  @JsonProperty("descriptorUri")
  private URI descriptorUri;

  public WidgetInstance descriptorUri(URI descriptorUri) {
    this.descriptorUri = descriptorUri;
    return this;
  }

  /**
   * The reference to the widget descriptor.
   * @return descriptorUri
  */
  @ApiModelProperty(value = "The reference to the widget descriptor.")

  @Valid

  public URI getDescriptorUri() {
    return descriptorUri;
  }

  public void setDescriptorUri(URI descriptorUri) {
    this.descriptorUri = descriptorUri;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WidgetInstance widgetInstance = (WidgetInstance) o;
    return Objects.equals(this.descriptorUri, widgetInstance.descriptorUri);
  }

  @Override
  public int hashCode() {
    return Objects.hash(descriptorUri);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WidgetInstance {\n");
    
    sb.append("    descriptorUri: ").append(toIndentedString(descriptorUri)).append("\n");
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

