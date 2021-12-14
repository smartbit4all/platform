package org.smartbit4all.api.binarydata;

import java.io.InputStream;
import java.net.URI;

/**
 * The {@link BinaryContent} is a platform level structure to generalize the file attachment for
 * every api. The attachment contains the file name, the size and the mime type of the given
 * content. It can be used as referred object and this api is responsible for storing them.
 * 
 * @author Peter Boros
 */
public interface BinaryContentApi {

  /**
   * Loads the BinaryDatas for the parameter BinaryContents into memory.
   * 
   * @param binaryContents
   */
  void load(BinaryContent... binaryContents);

  /**
   * Returns an InputStream for the BinaryContent. Load into memory first if it's not loaded yet.
   * 
   * @param binaryContent
   * @return
   */
  InputStream getInputStream(BinaryContent binaryContent);

  /**
   * Returns a BinaryData for the BinaryContent. Load into memory first if it's not loaded yet.
   * 
   * @param binaryContent
   * @return
   */
  BinaryData getBinaryData(BinaryContent binaryContent);

  /**
   * The InputStream will be converted into a BinaryData and stored in the BinaryContent. The
   * BinaryContent will be saved the same time as the BinaryContent.
   * 
   * DONT USE, since save is not ready from BinaryContent to BinaryData
   * 
   */
  void saveIntoContent(BinaryContent binaryContent, InputStream inputstream, URI dataUri);

  /**
   * The BinaryData will be stored in the BinaryContent. The BinaryContent will be saved the same
   * time as the BinaryContent.
   * 
   * DONT USE, since save is not ready from BinaryContent to BinaryData
   * 
   */
  void saveIntoContent(BinaryContent binaryContent, BinaryData binaryData, URI dataUri);

  /**
   * Save the InputStream as a BinaryData.
   * 
   */
  void uploadContent(BinaryContent binaryContent, InputStream inputstream, URI dataUri);

  /**
   * Save the BinaryData.
   * 
   */
  void uploadContent(BinaryContent binaryContent, BinaryData data, URI dataUri);

  /**
   * Remove the BinaryData from the BinaryContent.
   * 
   * @param binaryContent The BinaryContent which data will be removed
   */
  void removeContent(BinaryContent binaryContent);

}
