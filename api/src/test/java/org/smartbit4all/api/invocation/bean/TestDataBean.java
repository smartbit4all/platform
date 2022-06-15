package org.smartbit4all.api.invocation.bean;

import java.net.URI;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.annotations.ApiModelProperty;

@JsonPropertyOrder({
    TestDataBean.URI,
    TestDataBean.DATA,
    TestDataBean.BOOL
})
@JsonTypeName("TestDataBean")
public class TestDataBean {

  public static final String URI = "uri";
  private URI uri;

  public static final String DATA = "data";
  private String data;

  public static final String BOOL = "bool";
  private boolean bool;

  /**
   * Get uri
   * 
   * @return uri
   **/
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public URI getUri() {
    return uri;
  }


  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setUri(URI uri) {
    this.uri = uri;
  }

  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(DATA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getData() {
    return data;
  }

  @JsonProperty(DATA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setData(String data) {
    this.data = data;
  }

  public TestDataBean data(String data) {
    this.data = data;
    return this;
  }

  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(BOOL)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public boolean isBool() {
    return bool;
  }

  @JsonProperty(BOOL)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setBool(boolean bool) {
    this.bool = bool;
  }

  public TestDataBean bool(boolean bool) {
    this.bool = bool;
    return this;
  }

}
