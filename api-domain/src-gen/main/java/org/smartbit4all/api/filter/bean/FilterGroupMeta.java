/*
 * Filter API
 * Filter API
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.filter.bean;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.filter.bean.FilterFieldMeta;
import org.smartbit4all.api.filter.bean.FilterGroupType;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * FilterGroupMeta
 */
@JsonPropertyOrder({
  FilterGroupMeta.ID,
  FilterGroupMeta.FILTER_GROUP_METAS,
  FilterGroupMeta.FILTER_FIELD_METAS,
  FilterGroupMeta.LABEL_CODE,
  FilterGroupMeta.ICON_CODE,
  FilterGroupMeta.STYLE,
  FilterGroupMeta.TYPE
})
@JsonTypeName("FilterGroupMeta")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class FilterGroupMeta {
  public static final String ID = "id";
  private String id;

  public static final String FILTER_GROUP_METAS = "filterGroupMetas";
  private List<FilterGroupMeta> filterGroupMetas = new ArrayList<>();

  public static final String FILTER_FIELD_METAS = "filterFieldMetas";
  private List<FilterFieldMeta> filterFieldMetas = new ArrayList<>();

  public static final String LABEL_CODE = "labelCode";
  private String labelCode;

  public static final String ICON_CODE = "iconCode";
  private String iconCode;

  public static final String STYLE = "style";
  private String style;

  public static final String TYPE = "type";
  private FilterGroupType type;

  public FilterGroupMeta() { 
  }

  public FilterGroupMeta id(String id) {
    
    this.id = id;
    return this;
  }

   /**
   * Identifier of the filter group metadata. Not mandatory, specify only if in use.
   * @return id
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Identifier of the filter group metadata. Not mandatory, specify only if in use.")
  @JsonProperty(ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getId() {
    return id;
  }


  @JsonProperty(ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setId(String id) {
    this.id = id;
  }


  public FilterGroupMeta filterGroupMetas(List<FilterGroupMeta> filterGroupMetas) {
    
    this.filterGroupMetas = filterGroupMetas;
    return this;
  }

  public FilterGroupMeta addFilterGroupMetasItem(FilterGroupMeta filterGroupMetasItem) {
    this.filterGroupMetas.add(filterGroupMetasItem);
    return this;
  }

   /**
   * Get filterGroupMetas
   * @return filterGroupMetas
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(FILTER_GROUP_METAS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<FilterGroupMeta> getFilterGroupMetas() {
    return filterGroupMetas;
  }


  @JsonProperty(FILTER_GROUP_METAS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setFilterGroupMetas(List<FilterGroupMeta> filterGroupMetas) {
    this.filterGroupMetas = filterGroupMetas;
  }


  public FilterGroupMeta filterFieldMetas(List<FilterFieldMeta> filterFieldMetas) {
    
    this.filterFieldMetas = filterFieldMetas;
    return this;
  }

  public FilterGroupMeta addFilterFieldMetasItem(FilterFieldMeta filterFieldMetasItem) {
    this.filterFieldMetas.add(filterFieldMetasItem);
    return this;
  }

   /**
   * Get filterFieldMetas
   * @return filterFieldMetas
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(FILTER_FIELD_METAS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<FilterFieldMeta> getFilterFieldMetas() {
    return filterFieldMetas;
  }


  @JsonProperty(FILTER_FIELD_METAS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setFilterFieldMetas(List<FilterFieldMeta> filterFieldMetas) {
    this.filterFieldMetas = filterFieldMetas;
  }


  public FilterGroupMeta labelCode(String labelCode) {
    
    this.labelCode = labelCode;
    return this;
  }

   /**
   * Get labelCode
   * @return labelCode
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(LABEL_CODE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getLabelCode() {
    return labelCode;
  }


  @JsonProperty(LABEL_CODE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setLabelCode(String labelCode) {
    this.labelCode = labelCode;
  }


  public FilterGroupMeta iconCode(String iconCode) {
    
    this.iconCode = iconCode;
    return this;
  }

   /**
   * Get iconCode
   * @return iconCode
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(ICON_CODE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getIconCode() {
    return iconCode;
  }


  @JsonProperty(ICON_CODE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setIconCode(String iconCode) {
    this.iconCode = iconCode;
  }


  public FilterGroupMeta style(String style) {
    
    this.style = style;
    return this;
  }

   /**
   * Get style
   * @return style
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(STYLE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getStyle() {
    return style;
  }


  @JsonProperty(STYLE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setStyle(String style) {
    this.style = style;
  }


  public FilterGroupMeta type(FilterGroupType type) {
    
    this.type = type;
    return this;
  }

   /**
   * Get type
   * @return type
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(TYPE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public FilterGroupType getType() {
    return type;
  }


  @JsonProperty(TYPE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setType(FilterGroupType type) {
    this.type = type;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FilterGroupMeta filterGroupMeta = (FilterGroupMeta) o;
    return Objects.equals(this.id, filterGroupMeta.id) &&
        Objects.equals(this.filterGroupMetas, filterGroupMeta.filterGroupMetas) &&
        Objects.equals(this.filterFieldMetas, filterGroupMeta.filterFieldMetas) &&
        Objects.equals(this.labelCode, filterGroupMeta.labelCode) &&
        Objects.equals(this.iconCode, filterGroupMeta.iconCode) &&
        Objects.equals(this.style, filterGroupMeta.style) &&
        Objects.equals(this.type, filterGroupMeta.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, filterGroupMetas, filterFieldMetas, labelCode, iconCode, style, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FilterGroupMeta {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    filterGroupMetas: ").append(toIndentedString(filterGroupMetas)).append("\n");
    sb.append("    filterFieldMetas: ").append(toIndentedString(filterFieldMetas)).append("\n");
    sb.append("    labelCode: ").append(toIndentedString(labelCode)).append("\n");
    sb.append("    iconCode: ").append(toIndentedString(iconCode)).append("\n");
    sb.append("    style: ").append(toIndentedString(style)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
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

