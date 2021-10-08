package org.smartbit4all.api.navigation;

import java.net.URI;

public class NavigationRootNode {

  private URI entryMetaUri;

  private URI objectUri;

  public NavigationRootNode(URI entryMetaUri, URI objectUri) {
    super();
    this.entryMetaUri = entryMetaUri;
    this.objectUri = objectUri;
  }

  public final URI getEntryMetaUri() {
    return entryMetaUri;
  }

  public final void setEntryMetaUri(URI entryMetaUri) {
    this.entryMetaUri = entryMetaUri;
  }

  public final URI getObjectUri() {
    return objectUri;
  }

  public final void setObjectUri(URI objectUri) {
    this.objectUri = objectUri;
  }



}
