package org.smartbit4all.api.collection;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.smartbit4all.core.object.ObjectNode;

public class SearchIndexObject {

  private URI objectUri;

  private ObjectNode objectNode;

  private Map<String, Object> values = new HashMap<>();

  public URI getObjectUri() {
    return objectUri;
  }

  public Map<String, Object> getValues() {
    return values;
  }

  public ObjectNode getObjectNode() {
    return objectNode;
  }

  public void setObjectNode(ObjectNode objectNode) {
    this.objectNode = objectNode;
  }

  public void setObjectUri(URI objectUri) {
    this.objectUri = objectUri;
  }

  public void setValues(Map<String, Object> values) {
    this.values = values;
  }

  public SearchIndexObject values(Map<String, Object> values) {
    this.values = values;
    return this;
  }

  public SearchIndexObject objectUri(URI objectUri) {
    this.objectUri = objectUri;
    return this;
  }

  public SearchIndexObject objectNode(ObjectNode objectNode) {
    this.objectNode = objectNode;
    return this;
  }
}
