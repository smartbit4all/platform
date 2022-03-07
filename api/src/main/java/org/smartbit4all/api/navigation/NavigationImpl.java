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
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.navigation.bean.NavigationEntry;
import org.smartbit4all.core.object.DomainObjectRef;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * The basic implementation of the {@link NavigationApi}.
 * 
 * @author Peter Boros
 */
public abstract class NavigationImpl implements NavigationApi {

  private static final Logger log = LoggerFactory.getLogger(NavigationImpl.class);

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
  public Optional<DomainObjectRef> loadObject(URI entryMetaUri, URI objectUri) {
    return Optional.empty();
  }

  @Override
  public Navigation start(String navigationConfigName) {
    return null;
  }

  @Override
  public Disposable subscribeEntryForChanges(NavigationEntry entry,
      Consumer<URI> nodeChangeListener) {
    return null;
  }
}
