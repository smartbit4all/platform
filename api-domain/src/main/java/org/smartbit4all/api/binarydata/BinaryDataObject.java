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

  /**
   * If true then StorageSQL will compress the data on save. By default we DONT enable
   * BinaryDataObject-s to compress, since these are typically files.
   */
  private final boolean compressOnSave;

  public BinaryDataObject(BinaryData binaryData) {
    this(binaryData, false);
  }

  public BinaryDataObject(BinaryData binaryData, boolean compressOnSave) {
    super();
    this.binaryData = binaryData;
    this.compressOnSave = compressOnSave;
    this.binaryData.setCompressOnSave(this.compressOnSave);
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

  public boolean isCompressOnSave() {
    return compressOnSave;
  }

}
