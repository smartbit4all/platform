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
package org.smartbit4all.api.navigation;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import org.smartbit4all.api.navigation.bean.NavigationReferenceEntry;
import org.smartbit4all.core.object.ApiObjectRef;

/**
 * The basic implementation of the {@link NavigationApi}.
 * 
 * @author Peter Boros
 */
public abstract class NavigationImpl implements NavigationApi {

  protected String name;

  public NavigationImpl(String name) {
    super();
    this.name = name;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public Map<URI, List<NavigationReferenceEntry>> navigate(URI objectUri,
      List<URI> associationMetaUris, Consumer<URI> nodeChangedListener) {
    return navigate(objectUri, associationMetaUris);
  }

  @Override
  public Optional<ApiObjectRef> loadObject(URI entryMetaUri, URI objectUri) {
    return Optional.empty();
  }

}
