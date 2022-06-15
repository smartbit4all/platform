package org.smartbit4all.api.invocation.bean;

import java.net.URI;

public class TestDataBean {

  public static final String URI = "uri";
  private URI uri;

  public static final String DATA = "data";
  private String data;

  public static final String BOOL = "bool";
  private boolean bool;

  public URI getUri() {
    return uri;
  }


  public void setUri(URI uri) {
    this.uri = uri;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public TestDataBean data(String data) {
    this.data = data;
    return this;
  }

  public boolean isBool() {
    return bool;
  }

  public void setBool(boolean bool) {
    this.bool = bool;
  }

  public TestDataBean bool(boolean bool) {
    this.bool = bool;
    return this;
  }

}
