package org.smartbit4all.api.userselector.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.userselector.bean.UserSelector;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * UserMultiSelector
 */

public class UserMultiSelector   {
  @JsonProperty("selectors")
  @Valid
  private List<UserSelector> selectors = null;

  @JsonProperty("selected")
  @Valid
  private List<UserSelector> selected = null;

  public UserMultiSelector selectors(List<UserSelector> selectors) {
    this.selectors = selectors;
    return this;
  }

  public UserMultiSelector addSelectorsItem(UserSelector selectorsItem) {
    if (this.selectors == null) {
      this.selectors = new ArrayList<>();
    }
    this.selectors.add(selectorsItem);
    return this;
  }

  /**
   * Get selectors
   * @return selectors
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<UserSelector> getSelectors() {
    return selectors;
  }

  public void setSelectors(List<UserSelector> selectors) {
    this.selectors = selectors;
  }

  public UserMultiSelector selected(List<UserSelector> selected) {
    this.selected = selected;
    return this;
  }

  public UserMultiSelector addSelectedItem(UserSelector selectedItem) {
    if (this.selected == null) {
      this.selected = new ArrayList<>();
    }
    this.selected.add(selectedItem);
    return this;
  }

  /**
   * Get selected
   * @return selected
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<UserSelector> getSelected() {
    return selected;
  }

  public void setSelected(List<UserSelector> selected) {
    this.selected = selected;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserMultiSelector userMultiSelector = (UserMultiSelector) o;
    return Objects.equals(this.selectors, userMultiSelector.selectors) &&
        Objects.equals(this.selected, userMultiSelector.selected);
  }

  @Override
  public int hashCode() {
    return Objects.hash(selectors, selected);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserMultiSelector {\n");
    
    sb.append("    selectors: ").append(toIndentedString(selectors)).append("\n");
    sb.append("    selected: ").append(toIndentedString(selected)).append("\n");
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

