package org.smartbit4all.api.navigation;

import java.net.URI;
import java.util.List;

public class TestBean {

  private URI uri;
  private String name;
  private List<String> addresses;

  public URI getUri() {
    return uri;
  }

  public void setUri(URI uri) {
    this.uri = uri;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getAddresses() {
    return addresses;
  }

  public void setAddresses(List<String> addresses) {
    this.addresses = addresses;
  }

}
