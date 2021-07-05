package org.smartbit4all.ui.api.form.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.ui.api.form.model.DetailWidgetDescriptor;
import org.smartbit4all.ui.api.form.model.PropertyWidgetDescriptor;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * The form ui meta descriptor of an entity.
 */
@ApiModel(description = "The form ui meta descriptor of an entity.")

public class EntityDescriptor   {
  @JsonProperty("uri")
  private URI uri;

  @JsonProperty("widgets")
  @Valid
  private List<PropertyWidgetDescriptor> widgets = null;

  @JsonProperty("details")
  @Valid
  private List<DetailWidgetDescriptor> details = null;

  public EntityDescriptor uri(URI uri) {
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

  public EntityDescriptor widgets(List<PropertyWidgetDescriptor> widgets) {
    this.widgets = widgets;
    return this;
  }

  public EntityDescriptor addWidgetsItem(PropertyWidgetDescriptor widgetsItem) {
    if (this.widgets == null) {
      this.widgets = new ArrayList<>();
    }
    this.widgets.add(widgetsItem);
    return this;
  }

  /**
   * The list of the available property widgets for the entity.
   * @return widgets
  */
  @ApiModelProperty(value = "The list of the available property widgets for the entity.")

  @Valid

  public List<PropertyWidgetDescriptor> getWidgets() {
    return widgets;
  }

  public void setWidgets(List<PropertyWidgetDescriptor> widgets) {
    this.widgets = widgets;
  }

  public EntityDescriptor details(List<DetailWidgetDescriptor> details) {
    this.details = details;
    return this;
  }

  public EntityDescriptor addDetailsItem(DetailWidgetDescriptor detailsItem) {
    if (this.details == null) {
      this.details = new ArrayList<>();
    }
    this.details.add(detailsItem);
    return this;
  }

  /**
   * The list of the available detail widgets for the entity. Can refere the same entity more than once like postal and temporary address of a customer.
   * @return details
  */
  @ApiModelProperty(value = "The list of the available detail widgets for the entity. Can refere the same entity more than once like postal and temporary address of a customer.")

  @Valid

  public List<DetailWidgetDescriptor> getDetails() {
    return details;
  }

  public void setDetails(List<DetailWidgetDescriptor> details) {
    this.details = details;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EntityDescriptor entityDescriptor = (EntityDescriptor) o;
    return Objects.equals(this.uri, entityDescriptor.uri) &&
        Objects.equals(this.widgets, entityDescriptor.widgets) &&
        Objects.equals(this.details, entityDescriptor.details);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, widgets, details);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EntityDescriptor {\n");
    
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    widgets: ").append(toIndentedString(widgets)).append("\n");
    sb.append("    details: ").append(toIndentedString(details)).append("\n");
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

