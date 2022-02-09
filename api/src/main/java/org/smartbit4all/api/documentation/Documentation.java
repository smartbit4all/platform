package org.smartbit4all.api.documentation;

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.documentation.bean.DocumentationData;

/**
 * The domain object of the {@link DocumentationData} that is used by the {@link DocumentationApi}
 * as in memory structure. It manages the cached references to speed up the selections and other
 * functions that support the editing.
 * 
 * @author Peter Boros
 */
public class Documentation {

  /**
   * The {@link DocumentationApi}
   */
  private final DocumentationApi documentationApi;

  /**
   * The {@link Documentation} can be created only by the {@link DocumentationApi} to avoid
   * meaningless objects and persistence problems.
   * 
   * @param documentationApi
   */
  Documentation(DocumentationApi documentationApi) {
    super();
    this.documentationApi = documentationApi;
  }

  /**
   * The paragraphs of the documentation.
   */
  private final List<Paragraph> paragraphs = new ArrayList<>();

  /**
   * @return All the paragraphs we have in this documentation.
   */
  final List<Paragraph> getParagraphs() {
    return paragraphs;
  }

}
