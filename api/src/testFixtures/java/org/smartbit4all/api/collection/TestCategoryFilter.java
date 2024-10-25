package org.smartbit4all.api.collection;

import java.net.URI;
import java.util.List;

public class TestCategoryFilter {

  public static final String URI = "uri";
  private URI uri;

  public static final String NAME = "name";
  private String name;

  public static final String KEYWORDS = "keyWords";
  private List<String> keyWords;

  public final String getName() {
    return name;
  }

  public final TestCategoryFilter setName(String name) {
    this.name = name;
    return this;
  }

  public final List<String> getKeyWords() {
    return keyWords;
  }

  public final TestCategoryFilter setKeyWords(List<String> keywords) {
    this.keyWords = keywords;
    return this;
  }

  public final URI getUri() {
    return uri;
  }

  public final TestCategoryFilter setUri(URI uri) {
    this.uri = uri;
    return this;
  }

}
