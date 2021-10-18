package org.smartbit4all.api.binarydata;

import java.net.URI;

/**
 * This object is the api for saving the BinaryData with StorageApi.
 * 
 * @author Peter Boros
 */
public class BinaryDataObject {

  /**
   * The BinaryData itself.
   */
  private BinaryData binaryData;

  /**
   * The uri of the {@link BinaryData}.
   */
  private URI uri;

  public BinaryDataObject(BinaryData binaryData) {
    super();
    this.binaryData = binaryData;
  }

  public final BinaryData getBinaryData() {
    return binaryData;
  }

  public final void setBinaryData(BinaryData binaryData) {
    this.binaryData = binaryData;
  }

  public final URI getUri() {
    return uri;
  }

  public final void setUri(URI uri) {
    this.uri = uri;
  }

}
