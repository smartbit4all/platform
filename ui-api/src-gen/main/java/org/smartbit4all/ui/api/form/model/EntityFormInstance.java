/*
 * Form API
 * API for the predictive filling of forms, where a tree structure contains which pieces of data follow other pieces.
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.ui.api.form.model;

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
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.ui.api.form.model.FixedLayoutFormInstance;
import org.smartbit4all.ui.api.form.model.PredictiveFormInstance;
import org.smartbit4all.ui.api.form.model.WidgetDescriptor;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * This is an instance of a form defined by an EntityFormDescriptor. This descriptor defines all the available widget and also the entity stored in the TableData behind the screen.
 */
@ApiModel(description = "This is an instance of a form defined by an EntityFormDescriptor. This descriptor defines all the available widget and also the entity stored in the TableData behind the screen.")
@JsonPropertyOrder({
  EntityFormInstance.URI,
  EntityFormInstance.DESCRIPTOR_URI,
  EntityFormInstance.PREDICTIVE_FORM,
  EntityFormInstance.FIXED_LAYOUT_FORM,
  EntityFormInstance.WIDGETS
})
@JsonTypeName("EntityFormInstance")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class EntityFormInstance {
  public static final String URI = "uri";
  private URI uri;

  public static final String DESCRIPTOR_URI = "descriptorUri";
  private URI descriptorUri;

  public static final String PREDICTIVE_FORM = "predictiveForm";
  private PredictiveFormInstance predictiveForm;

  public static final String FIXED_LAYOUT_FORM = "fixedLayoutForm";
  private FixedLayoutFormInstance fixedLayoutForm;

  public static final String WIDGETS = "widgets";
  private List<WidgetDescriptor> widgets = null;


  public EntityFormInstance uri(URI uri) {
    
    this.uri = uri;
    return this;
  }

   /**
   * Get uri
   * @return uri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getUri() {
    return uri;
  }


  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setUri(URI uri) {
    this.uri = uri;
  }


  public EntityFormInstance descriptorUri(URI descriptorUri) {
    
    this.descriptorUri = descriptorUri;
    return this;
  }

   /**
   * Get descriptorUri
   * @return descriptorUri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(DESCRIPTOR_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getDescriptorUri() {
    return descriptorUri;
  }


  @JsonProperty(DESCRIPTOR_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setDescriptorUri(URI descriptorUri) {
    this.descriptorUri = descriptorUri;
  }


  public EntityFormInstance predictiveForm(PredictiveFormInstance predictiveForm) {
    
    this.predictiveForm = predictiveForm;
    return this;
  }

   /**
   * Get predictiveForm
   * @return predictiveForm
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(PREDICTIVE_FORM)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public PredictiveFormInstance getPredictiveForm() {
    return predictiveForm;
  }


  @JsonProperty(PREDICTIVE_FORM)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setPredictiveForm(PredictiveFormInstance predictiveForm) {
    this.predictiveForm = predictiveForm;
  }


  public EntityFormInstance fixedLayoutForm(FixedLayoutFormInstance fixedLayoutForm) {
    
    this.fixedLayoutForm = fixedLayoutForm;
    return this;
  }

   /**
   * Get fixedLayoutForm
   * @return fixedLayoutForm
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(FIXED_LAYOUT_FORM)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public FixedLayoutFormInstance getFixedLayoutForm() {
    return fixedLayoutForm;
  }


  @JsonProperty(FIXED_LAYOUT_FORM)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setFixedLayoutForm(FixedLayoutFormInstance fixedLayoutForm) {
    this.fixedLayoutForm = fixedLayoutForm;
  }


  public EntityFormInstance widgets(List<WidgetDescriptor> widgets) {
    
    this.widgets = widgets;
    return this;
  }

  public EntityFormInstance addWidgetsItem(WidgetDescriptor widgetsItem) {
    if (this.widgets == null) {
      this.widgets = new ArrayList<>();
    }
    this.widgets.add(widgetsItem);
    return this;
  }

   /**
   * Get widgets
   * @return widgets
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(WIDGETS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<WidgetDescriptor> getWidgets() {
    return widgets;
  }


  @JsonProperty(WIDGETS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setWidgets(List<WidgetDescriptor> widgets) {
    this.widgets = widgets;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EntityFormInstance entityFormInstance = (EntityFormInstance) o;
    return Objects.equals(this.uri, entityFormInstance.uri) &&
        Objects.equals(this.descriptorUri, entityFormInstance.descriptorUri) &&
        Objects.equals(this.predictiveForm, entityFormInstance.predictiveForm) &&
        Objects.equals(this.fixedLayoutForm, entityFormInstance.fixedLayoutForm) &&
        Objects.equals(this.widgets, entityFormInstance.widgets);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, descriptorUri, predictiveForm, fixedLayoutForm, widgets);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EntityFormInstance {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    descriptorUri: ").append(toIndentedString(descriptorUri)).append("\n");
    sb.append("    predictiveForm: ").append(toIndentedString(predictiveForm)).append("\n");
    sb.append("    fixedLayoutForm: ").append(toIndentedString(fixedLayoutForm)).append("\n");
    sb.append("    widgets: ").append(toIndentedString(widgets)).append("\n");
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

