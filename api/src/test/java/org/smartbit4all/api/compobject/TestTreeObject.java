package org.smartbit4all.api.compobject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class TestTreeObject {

  private URI uri;
  
  private URI parent;
  
  private List<URI> children;

  public TestTreeObject() {
    this.children = new ArrayList<>();
  }
  
  public void addChild(TestTreeObject child) {
    child.setParent(uri);
    children.add(child.getUri());
  }
  
  public URI getUri() {
    return uri;
  }

  public void setUri(URI uri) {
    this.uri = uri;
  }

  public URI getParent() {
    return parent;
  }

  public void setParent(URI parent) {
    this.parent = parent;
  }

  public List<URI> getChildren() {
    return children;
  }

  public void setChildren(List<URI> children) {
    this.children = children;
  }
  
}
