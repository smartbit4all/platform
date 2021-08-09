package org.smartbit4all.api.mimetype;

import org.smartbit4all.types.binarydata.BinaryData;

/**
 * The API used for converting documents in certain mimeTypes (docx, pdf).
 * 
 * @author Andras Pallo
 *
 */
public interface Converter {

  /**
   * Converts a document from one given mimeType to another.
   * 
   * @param content The content of the document that will be converted
   * @param from The mimeType of the document before conversion
   * @param to The mimeType of the document after conversion
   * @return The content of the converted document
   */
  BinaryData convert(BinaryData content, String from, String to);

  String getFrom();
  
  String getTo();
}
