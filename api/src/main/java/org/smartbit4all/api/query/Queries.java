package org.smartbit4all.api.query;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * The utility functions for the {@link QueryApi} and its implementations.
 * 
 * @author Peter Boros
 */
public class Queries {

  /**
   * To avoid on demand instantiation.
   */
  private Queries() {
    super();
  }

  public static final URI constructQueryURI(String categoryPath) {
    try {
      return new URI("query", null, categoryPath, UUID.randomUUID().toString());
    } catch (URISyntaxException e) {
      throw new RuntimeException(
          "Unable to construct the URI for the " + categoryPath + " query path", e);
    }
  }

}
