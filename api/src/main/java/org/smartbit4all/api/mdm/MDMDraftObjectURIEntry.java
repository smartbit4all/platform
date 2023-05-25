package org.smartbit4all.api.mdm;

import java.net.URI;

/**
 * Just a simple record for the object that has a published version and a currently editing draft
 * version.
 * 
 * @author Peter Boros
 *
 */
public class MDMDraftObjectURIEntry {

  private URI sourceURI;

  private URI draftURI;

  public MDMDraftObjectURIEntry(URI sourceURI, URI draftURI) {
    super();
    this.sourceURI = sourceURI;
    this.draftURI = draftURI;
  }

  public final URI getSourceURI() {
    return sourceURI;
  }

  public final void setSourceURI(URI sourceURI) {
    this.sourceURI = sourceURI;
  }

  public final URI getDraftURI() {
    return draftURI;
  }

  public final void setDraftURI(URI draftURI) {
    this.draftURI = draftURI;
  }

}
