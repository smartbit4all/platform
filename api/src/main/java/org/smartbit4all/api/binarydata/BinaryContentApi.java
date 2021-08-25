package org.smartbit4all.api.binarydata;

import java.io.InputStream;

public interface BinaryContentApi {

  /**
   * Loads the BinaryDatas for the parameter BinaryContents into memory.
   * 
   * @param binaryContents
   */
  void load(BinaryContent... binaryContents);

  /**
   * Return an InputStream for the BinaryContent. Load into memory first if it's not loaded yet.
   * 
   * @param binaryContent
   * @return
   */
  InputStream getInputStream(BinaryContent binaryContent);

  /**
   * The InputStream will be converted into a BinaryData and stored in the BinaryContent. The
   * BinaryContent will be saved the same time as the BinaryContent.
   * 
   * @param binaryContent
   * @param inputstream
   * @return
   */
  BinaryContent contentOf(BinaryContent binaryContent, InputStream inputstream);

}
