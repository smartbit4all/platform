package org.smartbit4all.api.documentation;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.documentation.bean.DocumentationData;
import org.smartbit4all.api.documentation.bean.ParagraphKind;

/**
 * The domain object of the {@link DocumentationData} that is used by the {@link DocumentationApi}
 * as in memory structure. It manages the cached references to speed up the selections and other
 * functions that support the editing.
 * 
 * @author Peter Boros
 */
public final class Documentation {

  /**
   * The documentation data. Updated by modifying the {@link Documentation} itself.
   */
  private final DocumentationData data;

  /**
   * The paragraphs of the documentation.
   */
  private final List<Paragraph> paragraphs = new ArrayList<>();

  /**
   * To generate id for the paragraphs.
   */
  private int lastId = 0;

  /**
   * The quick access for the paragraphs by unique id.
   */
  private final Map<Integer, Paragraph> paragraphsById = new HashMap<>();

  /**
   * The {@link DocumentationApi} to access the storage of the {@link DocumentationData}s.
   */
  private final DocumentationApi documentationApi;

  /**
   * The {@link Documentation} can be created only by the {@link DocumentationApi} to avoid
   * meaningless objects and persistence problems.
   * 
   * @param documentationApi
   */
  Documentation(DocumentationApi documentationApi, DocumentationData data) {
    super();
    this.documentationApi = documentationApi;
    this.data = data;
  }

  /**
   * @return All the paragraphs we have in this documentation.
   */
  public final List<Paragraph> getParagraphs() {
    return Collections.unmodifiableList(paragraphs);
  }

  /**
   * The modifiable paragraph list for the Apis.
   * 
   * @return The modifiable list.
   */
  final List<Paragraph> paragraphList() {
    return paragraphs;
  }

  /**
   * Adds a new paragraph at the end of the paragraph list.
   * 
   * @param kind The kind of the paragraph.
   * @return The new instance.
   */
  public final Paragraph addParagraph(ParagraphKind kind) {
    Paragraph result = constructParagraph(kind);
    result.getData().setOrderNo(paragraphs.size());
    paragraphs.add(result);
    return result;
  }

  /**
   * Adds a new paragraph after the paragraph identified by the given id.
   * 
   * @param id The id of the paragraph. Must be an existing one else the function throws an
   *        exception.
   * @param kind The new paragraph kind.
   * @return The newly created paragraph.
   */
  public final Paragraph addParagraphAfter(Integer id, ParagraphKind kind) {
    Paragraph predecessor = paragraphsById.get(id);
    if (predecessor == null) {
      throw new IllegalArgumentException(
          "Unable to add new paragraph because the predecessor " + id + " has not been found.");
    }
    Paragraph result = constructParagraph(kind);
    int resultOrderNo = predecessor.getData().getOrderNo() + 1;
    result.getData().setOrderNo(resultOrderNo);
    paragraphs.add(resultOrderNo, result);
    for (int i = resultOrderNo; i < paragraphs.size(); i++) {
      paragraphs.get(i).getData().setOrderNo(i);
    }
    return result;
  }

  /**
   * Remove the paragraph.
   * 
   * @param id The id of the paragraph to remove. If it doesn't exists the function throws an
   *        {@link IllegalArgumentException}.
   * @return
   */
  public final Paragraph removeParagraph(Integer id) {
    Paragraph removed = paragraphsById.remove(id);
    if (removed == null) {
      throw new IllegalArgumentException(
          "Unable to add new paragraph because the predecessor " + id + " has not been found.");
    }
    paragraphs.remove(removed.getData().getOrderNo().intValue());
    for (int i = removed.getData().getOrderNo(); i < paragraphs.size(); i++) {
      paragraphs.get(i).getData().setOrderNo(i);
    }
    return removed;
  }

  /**
   * Constructs a new paragraph with the given kind.
   * 
   * @param kind The kind
   * @return The result paragraph that is already registered into the map by id.
   */
  private final Paragraph constructParagraph(ParagraphKind kind) {
    Paragraph result = new Paragraph(++lastId, kind);
    paragraphsById.put(result.getData().getId(), result);
    data.addParagraphsItem(result.getData());
    return result;
  }

  /**
   * @return The the uri of the {@link #data}.
   */
  public final URI getUri() {
    return data.getUri();
  }

  /**
   * The data of the documentation that is always ready to save if you don't modify it outside of
   * the {@link Documentation} services.
   * 
   * @return The data object itself.
   */
  public final DocumentationData getData() {
    return data;
  }

  /**
   * When loading the {@link Documentation} the api can set the last id.
   * 
   * @param lastId
   */
  final void setLastId(int lastId) {
    this.lastId = lastId;
  }

}
