/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
