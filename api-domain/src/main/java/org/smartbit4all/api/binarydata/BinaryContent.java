package org.smartbit4all.api.binarydata;

import java.net.URI;
import java.util.Objects;
import javax.validation.Valid;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.annotations.ApiModelProperty;

@JsonPropertyOrder({
    BinaryContent.URI,
    BinaryContent.DATAURI,
    BinaryContent.FILENAME,
    BinaryContent.MIMETYPE,
    BinaryContent.EXTENSION,
    BinaryContent.SIZE
})
@JsonTypeName("BinaryContent")
public class BinaryContent {

  public static final String LOCATION_STORAGE = "storage";

  public static final String URI = "uri";
  @JsonProperty("uri")
  private URI uri;

  public static final String DATAURI = "dataUri";
  /**
   * The URI of the BinaryData
   */
  @JsonProperty("dataUri")
  private URI dataUri;

  public static final String FILENAME = "fileName";
  @JsonProperty(FILENAME)
  private String fileName;

  public static final String MIMETYPE = "mimeType";
  @JsonProperty(MIMETYPE)
  private String mimeType;

  public static final String EXTENSION = "extension";
  @JsonProperty(EXTENSION)
  private String extension;

  public static final String SIZE = "size";
  @JsonProperty(SIZE)
  private Long size;

  public static final String LOADED = "loaded";
  /**
   * True if the BinaryData is loaded into memory.
   */
  @JsonIgnore
  private transient boolean loaded = false;

  @JsonIgnore
  private transient BinaryData data;

  /**
   * True if we have to save the BinaryData when we save the BinaryContent
   */
  @JsonIgnore
  private transient boolean saveData = false;


  public BinaryContent uri(URI uri) {
    this.uri = uri;
    return this;
  }

  /**
   * Get uri
   * 
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

  public BinaryContent dataUri(URI dataUri) {
    this.dataUri = dataUri;
    return this;
  }

  /**
   * Get dataUri
   * 
   * @return dataUri
   **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(DATAURI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public URI getDataUri() {
    return dataUri;
  }


  @JsonProperty(DATAURI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setDataUri(URI dataUri) {
    this.dataUri = dataUri;
  }

  public BinaryContent fileName(String fileName) {
    this.fileName = fileName;
    return this;
  }

  /**
   * Get fileName
   * 
   * @return fileName
   **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(FILENAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getFileName() {
    return fileName;
  }


  @JsonProperty(FILENAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public BinaryContent mimeType(String mimeType) {
    this.mimeType = mimeType;
    return this;
  }

  /**
   * Get mimeType
   * 
   * @return mimeType
   **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(MIMETYPE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getMimeType() {
    return mimeType;
  }


  @JsonProperty(MIMETYPE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  public BinaryContent extension(String extension) {
    this.extension = extension;
    return this;
  }

  /**
   * Get extension
   * 
   * @return extension
   **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(EXTENSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getExtension() {
    return extension;
  }


  @JsonProperty(EXTENSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setExtension(String extension) {
    this.extension = extension;
  }

  public BinaryContent size(Long size) {
    this.size = size;
    return this;
  }

  /**
   * Get size
   * 
   * @return size
   **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(SIZE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Long getSize() {
    return size;
  }


  @JsonProperty(SIZE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSize(Long size) {
    this.size = size;
  }

  BinaryContent loaded(boolean loaded) {
    this.loaded = loaded;
    return this;
  }

  public boolean isLoaded() {
    return loaded;
  }

  void setLoaded(boolean loaded) {
    this.loaded = loaded;
  }

  BinaryContent data(BinaryData data) {
    this.data = data;
    return this;
  }

  BinaryData getData() {
    return data;
  }

  void setData(BinaryData data) {
    this.data = data;
  }


  BinaryContent saveData(boolean saveData) {
    this.saveData = saveData;
    return this;
  }

  boolean isSaveData() {
    return saveData;
  }

  void setSaveData(boolean saveData) {
    this.saveData = saveData;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BinaryContent binaryContent = (BinaryContent) o;
    return Objects.equals(this.uri, binaryContent.uri) &&
        Objects.equals(this.dataUri, binaryContent.dataUri) &&
        Objects.equals(this.fileName, binaryContent.fileName) &&
        Objects.equals(this.mimeType, binaryContent.mimeType) &&
        Objects.equals(this.size, binaryContent.size) &&
        this.loaded == binaryContent.loaded &&
        this.saveData == binaryContent.saveData;
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, dataUri, fileName, mimeType, size, loaded, saveData);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BinaryContent {\n");

    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    dataUri: ").append(toIndentedString(dataUri)).append("\n");
    sb.append("    fileName: ").append(toIndentedString(fileName)).append("\n");
    sb.append("    mimeType: ").append(toIndentedString(mimeType)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    loaded: ").append(toIndentedString(loaded)).append("\n");
    sb.append("    saveData: ").append(toIndentedString(saveData)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

  public BinaryContentData contentData() {
    return new BinaryContentData().dataUri(dataUri).extension(extension).fileName(fileName)
        .mimeType(mimeType).location(LOCATION_STORAGE)
        .contentHash(data != null ? data.hashIfPresent() : null);
  }

}
