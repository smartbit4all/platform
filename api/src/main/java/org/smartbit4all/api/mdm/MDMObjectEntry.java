package org.smartbit4all.api.mdm;

/**
 * Just a simple record for the object that has a published version and a currently editing draft
 * version.
 * 
 * @author Peter Boros
 * 
 * @param <O>
 */
public class MDMObjectEntry<O> {

  private O published;

  private O draft;

  public MDMObjectEntry(O published, O draft) {
    super();
    this.published = published;
    this.draft = draft;
  }

  public final O getPublished() {
    return published;
  }

  public final void setPublished(O published) {
    this.published = published;
  }

  public final O getDraft() {
    return draft;
  }

  public final void setDraft(O draft) {
    this.draft = draft;
  }

}
