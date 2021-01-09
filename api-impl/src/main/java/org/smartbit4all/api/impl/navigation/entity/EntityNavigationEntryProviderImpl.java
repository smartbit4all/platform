/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.api.impl.navigation.entity;

import java.net.URI;
import java.util.List;
import org.smartbit4all.api.navigation.bean.NavigationEntry;
import org.smartbit4all.api.navigation.bean.NavigationView;
import org.smartbit4all.domain.service.entity.EntityUris;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class EntityNavigationEntryProviderImpl implements EntityNavigationEntryProvider {

  private List<EntityNavigationEntryProvider> providers;
  
  private EntityNavigationEntryProvider defaultProvider;
  
  public EntityNavigationEntryProviderImpl(List<EntityNavigationEntryProvider> providers) {
    this.providers = providers;
  }

  @Override
  public NavigationEntry getEntry(URI entityObjectUri) {
    EntityNavigationEntryProvider provider = providers.stream()
        .filter(p -> p.supports(entityObjectUri))
        .findFirst()
        .orElseGet(this::getDefaultProvider);
    return provider.getEntry(entityObjectUri);
  }
  
  private EntityNavigationEntryProvider getDefaultProvider() {
    if(defaultProvider == null) {
      defaultProvider = new EntityNavigationEntryProvider() {
        
        @Override
        public NavigationEntry getEntry(URI entityObjectUri) {
          String objectId = EntityUris.getEntityObjectId(entityObjectUri);
          String entityName = EntityUris.getEntityName(entityObjectUri);
          NavigationView naviView = new NavigationView()
              .name(entityName)
              .putParametersItem("id", objectId);
              
          
          NavigationEntry entry = new NavigationEntry()
              .objectUri(entityObjectUri)
              .name(entityName + ": " + objectId)
              .icon("cube")
              .addViewsItem(naviView);
          return entry;
        }

        @Override
        public boolean supports(URI entityUri) {
          return true;
        }
      };
    }
    return defaultProvider;
  }

  @Override
  public boolean supports(URI entityUri) {
    return false;
  }
}
