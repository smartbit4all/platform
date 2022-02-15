package org.smartbit4all.api.documentation;

import java.net.URI;
import org.smartbit4all.api.binarydata.BinaryDataObject;
import org.smartbit4all.api.documentation.bean.ParagraphData;
import org.smartbit4all.api.documentation.bean.ParagraphKind;
import org.smartbit4all.core.utility.StringConstant;

/**
 * The paragraph of the documentation is a polimorph object that can be a simple text but a
 * reference to another object also. Only the DocumentationApi can instantiate paragraph.
 * 
 * @author Peter Boros
 */
public class Paragraph {

  private ParagraphData data;

  /**
   * @param id
   * @param kind
   */
  Paragraph(Integer id, ParagraphKind kind) {
    data = new ParagraphData().id(id).kind(kind);
  }

  /**
   * The paragraph is initiated with the {@link ParagraphData}.
   * 
   * @param data
   */
  public Paragraph(ParagraphData data) {
    super();
    this.data = data;
  }

  /**
   * If the {@link ParagraphKind#IMAGE} then this {@link BinaryDataObject} contains the picture as a
   * readable binary data. We can use this to render the image.
   */
  private BinaryDataObject image;

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
   * @return If the {@link ParagraphKind#IMAGE} is the kind then we get the image as
   *         {@link BinaryDataObject}. Return null in any other case.
   */
  public final BinaryDataObject getImage() {
    return image;
  }

  /**
   * If we set the image as {@link BinaryDataObject} then it implicitly set the kind to
   * {@link ParagraphKind#IMAGE}.
   * 
   * @param image The image as {@link BinaryDataObject}.
   */
  public final void setImage(BinaryDataObject image) {
    this.image = image;
    data.setContentUri(image.getUri());
  }

  /**
   * @return If the {@link ParagraphKind#MSWORD} is the kind then we get the MS Word as
   *         {@link BinaryDataObject}. Return null in any other case.
   */
  public final BinaryDataObject getWord() {
    return word;
  }

  /**
   * If we set the word as {@link BinaryDataObject} then it implicitly set the kind to
   * {@link ParagraphKind#MSWORD}.
   * 
   * @param word The word as {@link BinaryDataObject}.
   */
  public final void setWord(BinaryDataObject word) {
    this.word = word;
    data.setContentUri(word.getUri());
  }

  /**
   * @return If the {@link ParagraphKind#DOCUMENTATIONREF} is the kind then we get the
   *         {@link Documentation} as is. Return null in any other case.
   */
  public final Documentation getDocumentation() {
    return documentation;
  }

  /**
   * If we set the documentation as Object then it implicitly set the kind to
   * {@link ParagraphKind#DOCUMENTATIONREF}.
   * 
   * @param documentation The {@link Documentation} as object.
   */
  public final void setDocumentation(Documentation documentation) {
    this.documentation = documentation;
    data.setContentUri(documentation.getUri());
  }

  /**
   * @return The paragraph inside the {@link Documentation} if we refer to a paragraph inside.
   */
  public final Paragraph getParagraph() {
    return paragraph;
  }

  /**
   * @param paragraph We can set the referred paragraph inside the {@link Documentation}.
   */
  public final void setParagraph(Paragraph paragraph) {
    this.paragraph = paragraph;
    data.setContentUri(paragraph.getUri());
  }

  /**
   * @return The text of the paragraph.
   */
  public final String getText() {
    return data.getText();
  }

  /**
   * Set the text of the paragraph. It doesn't change the kind of the {@link Paragraph} because the
   * text can exist even if we have any other kind.
   * 
   * @param text The text to set.
   */
  public final void setText(String text) {
    this.getData().setText(text);
  }

  /**
   * @return The {@link ParagraphData} ready to save.
   */
  public final ParagraphData getData() {
    return data;
  }

  /**
   * @return The URI of the paragraph. It's generated from the URI of the documentation + the id as
   *         fragment.
   */
  public final URI getUri() {
    return URI.create(documentation.getUri().toString() + StringConstant.HASH + data.getId());
  }

}
