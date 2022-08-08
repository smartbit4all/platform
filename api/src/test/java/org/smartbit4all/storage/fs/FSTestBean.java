package org.smartbit4all.storage.fs;

import java.net.URI;

public class FSTestBean {

  private String title;

  private URI uri;

  public FSTestBean() {
    super();
  }

  public FSTestBean(String title) {
    super();
    this.title = title;
  }

  public final String getTitle() {
    return title;
  }

  public final void setTitle(String title) {
    this.title = title;
  }

  public final URI getUri() {
    return uri;
  }

  public final void setUri(URI uri) {
    this.uri = uri;
  }

}
