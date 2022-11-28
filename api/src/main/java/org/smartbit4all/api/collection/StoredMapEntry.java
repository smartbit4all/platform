package org.smartbit4all.api.collection;

import java.net.URI;

public class StoredMapEntry {

  private final String key;

  private final URI value;

  public StoredMapEntry(String key, URI value) {
    super();
    this.key = key;
    this.value = value;
  }

  final String getKey() {
    return key;
  }

  final URI getValue() {
    return value;
  }



}
