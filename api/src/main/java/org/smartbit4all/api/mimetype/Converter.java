package org.smartbit4all.api.mimetype;

import org.smartbit4all.api.binarydata.BinaryData;

/**
 * The API used for converting documents in certain mimeTypes (docx, pdf).
 * 
 * @author Andras Pallo
 *
 */
public interface Converter {

  /**
   * Converts the given document, if its mimetype is the same as the converter's from field.
   * 
   * @param content The content of the document that will be converted
   * @return The content of the converted document
   */
  BinaryData convert(BinaryData content);

  String getFrom();

  String getTo();
}
