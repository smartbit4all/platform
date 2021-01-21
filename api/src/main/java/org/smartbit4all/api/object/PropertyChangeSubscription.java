package org.smartbit4all.api.object;

import java.net.URI;
import org.smartbit4all.domain.meta.EventSubscription;

public class PropertyChangeSubscription extends EventSubscription<PropertyChange<?>> {

  private URI parentURI;

  private String propertyName;

  public PropertyChangeSubscription property(URI parentURI, String propertyName) {
    this.parentURI = parentURI;
    this.propertyName = propertyName;
    return this;
  }

  public final URI getParentURI() {
    return parentURI;
  }

  public final void setParentURI(URI parentURI) {
    this.parentURI = parentURI;
  }

  public final String getPropertyName() {
    return propertyName;
  }

  public final void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }

}
