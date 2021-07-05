package org.smartbit4all.ui.api.form.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.ui.api.form.model.FormDataDetailContent;
import org.smartbit4all.ui.api.form.model.InputValue;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Data object, that describes the already gathered pieces of data in a key-value mapping.
 */
@ApiModel(description = "Data object, that describes the already gathered pieces of data in a key-value mapping.")

public class FormDataContent   {
  @JsonProperty("id")
  private UUID id;

  @JsonProperty("active")
  private Boolean active;

  @JsonProperty("entityUri")
  private URI entityUri;

  @JsonProperty("values")
  @Valid
  private List<InputValue> values = null;

  @JsonProperty("details")
  @Valid
  private List<FormDataDetailContent> details = null;

  public FormDataContent id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * The UUID of the content.
   * @return id
  */
  @ApiModelProperty(value = "The UUID of the content.")

  @Valid

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public FormDataContent active(Boolean active) {
    this.active = active;
    return this;
  }

  /**
   * Active flag of the content.
   * @return active
  */
  @ApiModelProperty(value = "Active flag of the content.")


  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public FormDataContent entityUri(URI entityUri) {
    this.entityUri = entityUri;
    return this;
  }

  /**
   * The uri reference for the entity definition belong to the given record.
   * @return entityUri
  */
  @ApiModelProperty(value = "The uri reference for the entity definition belong to the given record.")

  @Valid

  public URI getEntityUri() {
    return entityUri;
  }

  public void setEntityUri(URI entityUri) {
    this.entityUri = entityUri;
  }

  public FormDataContent values(List<InputValue> values) {
    this.values = values;
    return this;
  }

  public FormDataContent addValuesItem(InputValue valuesItem) {
    if (this.values == null) {
      this.values = new ArrayList<>();
    }
    this.values.add(valuesItem);
    return this;
  }

  /**
   * Get values
   * @return values
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<InputValue> getValues() {
    return values;
  }

  public void setValues(List<InputValue> values) {
    this.values = values;
  }

  public FormDataContent details(List<FormDataDetailContent> details) {
    this.details = details;
    return this;
  }

  public FormDataContent addDetailsItem(FormDataDetailContent detailsItem) {
    if (this.details == null) {
      this.details = new ArrayList<>();
    }
    this.details.add(detailsItem);
    return this;
  }

  /**
   * Get details
   * @return details
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<FormDataDetailContent> getDetails() {
    return details;
  }

  public void setDetails(List<FormDataDetailContent> details) {
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
    FormDataContent formDataContent = (FormDataContent) o;
    return Objects.equals(this.id, formDataContent.id) &&
        Objects.equals(this.active, formDataContent.active) &&
        Objects.equals(this.entityUri, formDataContent.entityUri) &&
        Objects.equals(this.values, formDataContent.values) &&
        Objects.equals(this.details, formDataContent.details);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, active, entityUri, values, details);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FormDataContent {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    active: ").append(toIndentedString(active)).append("\n");
    sb.append("    entityUri: ").append(toIndentedString(entityUri)).append("\n");
    sb.append("    values: ").append(toIndentedString(values)).append("\n");
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

