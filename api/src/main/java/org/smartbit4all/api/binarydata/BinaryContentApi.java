package org.smartbit4all.api.binarydata;

import java.io.InputStream;
import java.net.URI;

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
   * NE HASZNÁLD, mert még nincs kész a BinaryContent-ből BinaryData mentése
   * 
   */
  void saveIntoContent(BinaryContent binaryContent, InputStream inputstream, URI dataUri);

  /**
   * The BinaryData will be stored in the BinaryContent. The BinaryContent will be saved the same
   * time as the BinaryContent.
   * 
   * NE HASZNÁLD, mert még nincs kész a BinaryContent-ből BinaryData mentése
   * 
   */
  void saveIntoContent(BinaryContent binaryContent, BinaryData binaryData, URI dataUri);

  /**
   * Save the InputStream as a BinaryData.
   * 
   * @return
   */
  void uploadContent(BinaryContent binaryContent, InputStream inputstream, URI dataUri);

  /**
   * Save the BinaryData.
   * 
   * @return
   */
  void uploadContent(BinaryContent binaryContent, BinaryData data, URI dataUri);
  
  /**
   * Remove the BinaryData from the BinaryContent.
   * 
   * @param binaryContent The BinaryContent which data will be removed
   */
  void removeContent(BinaryContent binaryContent);

}
