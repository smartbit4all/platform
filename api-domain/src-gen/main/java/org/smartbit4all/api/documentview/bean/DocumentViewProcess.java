package org.smartbit4all.api.documentview.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.documentview.bean.DisplayMode;
import org.smartbit4all.api.documentview.bean.ImageWithAlt;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * DocumentViewProcess
 */

public class DocumentViewProcess   {
  @JsonProperty("displayMode")
  private DisplayMode displayMode;

  @JsonProperty("pageIndex")
  private String pageIndex;

  @JsonProperty("pageCount")
  private Integer pageCount;

  @JsonProperty("zoomValue")
  private Integer zoomValue;

  @JsonProperty("text")
  private String text;

  @JsonProperty("message")
  private String message;

  @JsonProperty("leftButtonEnabled")
  private Boolean leftButtonEnabled;

  @JsonProperty("rightButtonEnabled")
  private Boolean rightButtonEnabled;

  @JsonProperty("loadingSpinnerDisplayed")
  private Boolean loadingSpinnerDisplayed;

  @JsonProperty("mainImage")
  private ImageWithAlt mainImage;

  @JsonProperty("thumbnails")
  @Valid
  private List<ImageWithAlt> thumbnails = null;

  public DocumentViewProcess displayMode(DisplayMode displayMode) {
    this.displayMode = displayMode;
    return this;
  }

  /**
   * Get displayMode
   * @return displayMode
  */
  @ApiModelProperty(value = "")

  @Valid

  public DisplayMode getDisplayMode() {
    return displayMode;
  }

  public void setDisplayMode(DisplayMode displayMode) {
    this.displayMode = displayMode;
  }

  public DocumentViewProcess pageIndex(String pageIndex) {
    this.pageIndex = pageIndex;
    return this;
  }

  /**
   * Get pageIndex
   * @return pageIndex
  */
  @ApiModelProperty(value = "")


  public String getPageIndex() {
    return pageIndex;
  }

  public void setPageIndex(String pageIndex) {
    this.pageIndex = pageIndex;
  }

  public DocumentViewProcess pageCount(Integer pageCount) {
    this.pageCount = pageCount;
    return this;
  }

  /**
   * Get pageCount
   * @return pageCount
  */
  @ApiModelProperty(value = "")


  public Integer getPageCount() {
    return pageCount;
  }

  public void setPageCount(Integer pageCount) {
    this.pageCount = pageCount;
  }

  public DocumentViewProcess zoomValue(Integer zoomValue) {
    this.zoomValue = zoomValue;
    return this;
  }

  /**
   * Get zoomValue
   * @return zoomValue
  */
  @ApiModelProperty(value = "")


  public Integer getZoomValue() {
    return zoomValue;
  }

  public void setZoomValue(Integer zoomValue) {
    this.zoomValue = zoomValue;
  }

  public DocumentViewProcess text(String text) {
    this.text = text;
    return this;
  }

  /**
   * Get text
   * @return text
  */
  @ApiModelProperty(value = "")


  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public DocumentViewProcess message(String message) {
    this.message = message;
    return this;
  }

  /**
   * Get message
   * @return message
  */
  @ApiModelProperty(value = "")


  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public DocumentViewProcess leftButtonEnabled(Boolean leftButtonEnabled) {
    this.leftButtonEnabled = leftButtonEnabled;
    return this;
  }

  /**
   * Get leftButtonEnabled
   * @return leftButtonEnabled
  */
  @ApiModelProperty(value = "")


  public Boolean getLeftButtonEnabled() {
    return leftButtonEnabled;
  }

  public void setLeftButtonEnabled(Boolean leftButtonEnabled) {
    this.leftButtonEnabled = leftButtonEnabled;
  }

  public DocumentViewProcess rightButtonEnabled(Boolean rightButtonEnabled) {
    this.rightButtonEnabled = rightButtonEnabled;
    return this;
  }

  /**
   * Get rightButtonEnabled
   * @return rightButtonEnabled
  */
  @ApiModelProperty(value = "")


  public Boolean getRightButtonEnabled() {
    return rightButtonEnabled;
  }

  public void setRightButtonEnabled(Boolean rightButtonEnabled) {
    this.rightButtonEnabled = rightButtonEnabled;
  }

  public DocumentViewProcess loadingSpinnerDisplayed(Boolean loadingSpinnerDisplayed) {
    this.loadingSpinnerDisplayed = loadingSpinnerDisplayed;
    return this;
  }

  /**
   * Get loadingSpinnerDisplayed
   * @return loadingSpinnerDisplayed
  */
  @ApiModelProperty(value = "")


  public Boolean getLoadingSpinnerDisplayed() {
    return loadingSpinnerDisplayed;
  }

  public void setLoadingSpinnerDisplayed(Boolean loadingSpinnerDisplayed) {
    this.loadingSpinnerDisplayed = loadingSpinnerDisplayed;
  }

  public DocumentViewProcess mainImage(ImageWithAlt mainImage) {
    this.mainImage = mainImage;
    return this;
  }

  /**
   * Get mainImage
   * @return mainImage
  */
  @ApiModelProperty(value = "")

  @Valid

  public ImageWithAlt getMainImage() {
    return mainImage;
  }

  public void setMainImage(ImageWithAlt mainImage) {
    this.mainImage = mainImage;
  }

  public DocumentViewProcess thumbnails(List<ImageWithAlt> thumbnails) {
    this.thumbnails = thumbnails;
    return this;
  }

  public DocumentViewProcess addThumbnailsItem(ImageWithAlt thumbnailsItem) {
    if (this.thumbnails == null) {
      this.thumbnails = new ArrayList<>();
    }
    this.thumbnails.add(thumbnailsItem);
    return this;
  }

  /**
   * Get thumbnails
   * @return thumbnails
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<ImageWithAlt> getThumbnails() {
    return thumbnails;
  }

  public void setThumbnails(List<ImageWithAlt> thumbnails) {
    this.thumbnails = thumbnails;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DocumentViewProcess documentViewProcess = (DocumentViewProcess) o;
    return Objects.equals(this.displayMode, documentViewProcess.displayMode) &&
        Objects.equals(this.pageIndex, documentViewProcess.pageIndex) &&
        Objects.equals(this.pageCount, documentViewProcess.pageCount) &&
        Objects.equals(this.zoomValue, documentViewProcess.zoomValue) &&
        Objects.equals(this.text, documentViewProcess.text) &&
        Objects.equals(this.message, documentViewProcess.message) &&
        Objects.equals(this.leftButtonEnabled, documentViewProcess.leftButtonEnabled) &&
        Objects.equals(this.rightButtonEnabled, documentViewProcess.rightButtonEnabled) &&
        Objects.equals(this.loadingSpinnerDisplayed, documentViewProcess.loadingSpinnerDisplayed) &&
        Objects.equals(this.mainImage, documentViewProcess.mainImage) &&
        Objects.equals(this.thumbnails, documentViewProcess.thumbnails);
  }

  @Override
  public int hashCode() {
    return Objects.hash(displayMode, pageIndex, pageCount, zoomValue, text, message, leftButtonEnabled, rightButtonEnabled, loadingSpinnerDisplayed, mainImage, thumbnails);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DocumentViewProcess {\n");
    
    sb.append("    displayMode: ").append(toIndentedString(displayMode)).append("\n");
    sb.append("    pageIndex: ").append(toIndentedString(pageIndex)).append("\n");
    sb.append("    pageCount: ").append(toIndentedString(pageCount)).append("\n");
    sb.append("    zoomValue: ").append(toIndentedString(zoomValue)).append("\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    leftButtonEnabled: ").append(toIndentedString(leftButtonEnabled)).append("\n");
    sb.append("    rightButtonEnabled: ").append(toIndentedString(rightButtonEnabled)).append("\n");
    sb.append("    loadingSpinnerDisplayed: ").append(toIndentedString(loadingSpinnerDisplayed)).append("\n");
    sb.append("    mainImage: ").append(toIndentedString(mainImage)).append("\n");
    sb.append("    thumbnails: ").append(toIndentedString(thumbnails)).append("\n");
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

