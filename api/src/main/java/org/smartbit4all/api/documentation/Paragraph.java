package org.smartbit4all.api.documentation;

import org.smartbit4all.api.binarydata.BinaryDataObject;
import org.smartbit4all.api.documentation.bean.ParagraphKind;

/**
 * The paragraph of the documentation is a polimorph object that can be a simple text but a
 * reference to another object also. Only the DocumentationApi can instantiate paragraph.
 * 
 * @author Peter Boros
 */
public class Paragraph {

  /**
   * @param id
   * @param kind
   */
  Paragraph(Integer id, ParagraphKind kind) {
    super();
    this.id = id;
    this.kind = kind;
  }

  /**
   * The unique identifier of the paragraph.
   */
  private Integer id;

  /**
   * The kind of the paragraph that defines if it's a simple text or a reference to another object.
   */
  private ParagraphKind kind;

  /**
   * If the {@link ParagraphKind#IMAGE} then this {@link BinaryDataObject} contains the picture as a
   * readable binary data. We can use this to render the image.
   */
  private BinaryDataObject picture;

  /**
   * If the {@link ParagraphKind#MSWORD} then this {@link BinaryDataObject} contains the picture as
   * a readable binary data. We can use this to render the image.
   */
  private BinaryDataObject word;

  /**
   * If the {@link ParagraphKind#DOCUMENTATIONREF} then this {@link Documentation} is the direct
   * reference.
   */
  private Documentation documentation;

  /**
   * If the {@link ParagraphKind#DOCUMENTATIONREF} then the {@link Documentation} is set. But if we
   * refer to one paragraph only then this is the contained object inside. This object is also
   * included in the {@link #documentation} object but it's faster to have direct reference.
   */
  private Paragraph paragraph;

  /**
   * If it's a simple text / html paragraph then this is the text itself.
   */
  private String text;

}
