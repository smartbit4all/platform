package org.smartbit4all.api.documentation;

import java.net.URI;

public class TestDocumentedBean {

  private URI uri;

  private String simpleText;

  private URI documentUri;

  public URI getUri() {
    return uri;
  }

  public void setUri(URI uri) {
    this.uri = uri;
  }

  public String getSimpleText() {
    return simpleText;
  }

  public void setSimpleText(String data) {
    this.simpleText = data;
  }

  public final URI getDocumentUri() {
    return documentUri;
  }

  public final void setDocumentUri(URI documentUri) {
    this.documentUri = documentUri;
  }

}
