package org.smartbit4all.ui.api.form.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import org.smartbit4all.ui.api.form.model.PredictiveFormInstance;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * This is an instance of defined by an EntityFormDescriptor. This descriptor defines all the available widget and also the entity stored in the TableData behind the screen.
 */
@ApiModel(description = "This is an instance of defined by an EntityFormDescriptor. This descriptor defines all the available widget and also the entity stored in the TableData behind the screen.")

public class EntityFormInstace   {
  @JsonProperty("uri")
  private URI uri;

  @JsonProperty("predictiveForm")
  private PredictiveFormInstance predictiveForm;

  @JsonProperty("fixedLayoutForm")
  private PredictiveFormInstance fixedLayoutForm;

  public EntityFormInstace uri(URI uri) {
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

  public EntityFormInstace predictiveForm(PredictiveFormInstance predictiveForm) {
    this.predictiveForm = predictiveForm;
    return this;
  }

  /**
   * Get predictiveForm
   * @return predictiveForm
  */
  @ApiModelProperty(value = "")

  @Valid

  public PredictiveFormInstance getPredictiveForm() {
    return predictiveForm;
  }

  public void setPredictiveForm(PredictiveFormInstance predictiveForm) {
    this.predictiveForm = predictiveForm;
  }

  public EntityFormInstace fixedLayoutForm(PredictiveFormInstance fixedLayoutForm) {
    this.fixedLayoutForm = fixedLayoutForm;
    return this;
  }

  /**
   * Get fixedLayoutForm
   * @return fixedLayoutForm
  */
  @ApiModelProperty(value = "")

  @Valid

  public PredictiveFormInstance getFixedLayoutForm() {
    return fixedLayoutForm;
  }

  public void setFixedLayoutForm(PredictiveFormInstance fixedLayoutForm) {
    this.fixedLayoutForm = fixedLayoutForm;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EntityFormInstace entityFormInstace = (EntityFormInstace) o;
    return Objects.equals(this.uri, entityFormInstace.uri) &&
        Objects.equals(this.predictiveForm, entityFormInstace.predictiveForm) &&
        Objects.equals(this.fixedLayoutForm, entityFormInstace.fixedLayoutForm);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, predictiveForm, fixedLayoutForm);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EntityFormInstace {\n");
    
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    predictiveForm: ").append(toIndentedString(predictiveForm)).append("\n");
    sb.append("    fixedLayoutForm: ").append(toIndentedString(fixedLayoutForm)).append("\n");
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

