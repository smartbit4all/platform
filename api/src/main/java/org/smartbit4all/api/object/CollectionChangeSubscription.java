package org.smartbit4all.api.object;

import java.net.URI;
import org.smartbit4all.domain.meta.EventSubscription;

public class CollectionChangeSubscription extends EventSubscription<CollectionChange> {

  private URI parentURI;

  private String collectionName;

  public CollectionChangeSubscription collection(URI parentURI, String collectionName) {
    this.parentURI = parentURI;
    this.collectionName = collectionName;
    return this;
  }

  public final URI getParentURI() {
    return parentURI;
  }

  public final void setParentURI(URI parentURI) {
    this.parentURI = parentURI;
  }

  public final String getCollectionName() {
    return collectionName;
  }

  public final void setCollectionName(String collectionName) {
    this.collectionName = collectionName;
  }

}
