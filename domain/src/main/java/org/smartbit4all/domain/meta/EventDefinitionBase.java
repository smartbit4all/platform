/*******************************************************************************
 * Copyright (C) 2020 - 2021 it4all Hungary Kft.
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
package org.smartbit4all.domain.meta;

import java.net.URI;

/**
 * The base implementation of the {@link EventDefinition}s.
 * 
 * @author Peter Boros
 *
 * @param <E>
 */
public abstract class EventDefinitionBase<E> implements EventDefinition<E> {

  private URI uri;

  protected EventDefinitionBase(URI uri) {
    super();
    this.uri = uri;
  }

  @Override
  public URI getUri() {
    return uri;
  }

}
