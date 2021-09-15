package org.smartbit4all.api.binarydata;

import java.net.URI;
import java.util.Optional;

public interface BinaryDataApi {

  /**
   * Save the BinaryData with the given URI.
   */
  void save(BinaryData data, URI dataUri);

  /**
   * Load the BinaryData with the given URI.
   */
  Optional<BinaryData> load(URI dataUri);

  String name();

  /**
   * Remove the BinaryData with the given URI.
   */
  void remove(URI dataUri);
}
