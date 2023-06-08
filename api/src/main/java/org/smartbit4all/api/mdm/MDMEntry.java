package org.smartbit4all.api.mdm;

import java.net.URI;

/**
 * Just a simple record for the object that has a published version and a currently editing draft
 * version. Contains URIs of published and draft versions.
 *
 * @author Peter Boros
 *
 */
public class MDMEntry {

  private URI published;

  private URI draft;

  public MDMEntry(URI published, URI draft) {
    super();
    this.published = published;
    this.draft = draft;
  }

  public final URI getPublished() {
    return published;
  }

  public final void setPublished(URI published) {
    this.published = published;
  }

  public final URI getDraft() {
    return draft;
  }

  public final void setDraft(URI draft) {
    this.draft = draft;
  }

}
