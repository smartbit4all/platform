package org.smartbit4all.api.object;

import java.net.URI;
import org.smartbit4all.domain.meta.EventSubscription;

public class ReferenceChangeSubscription extends EventSubscription<ReferenceChange> {

  private URI parentURI;

  private String referenceName;

  public ReferenceChangeSubscription property(URI parentURI, String referenceName) {
    this.parentURI = parentURI;
    this.referenceName = referenceName;
    return this;
  }

  public final URI getParentURI() {
    return parentURI;
  }

  public final void setParentURI(URI parentURI) {
    this.parentURI = parentURI;
  }

  public final String getReferenceName() {
    return referenceName;
  }

  public final void setReferenceName(String referenceName) {
    this.referenceName = referenceName;
  }

}
