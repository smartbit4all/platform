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

  private List<EntityNavigationEntryProviderBase> providers;
  
  private EntityNavigationEntryProviderBase defaultProvider;
  
  public EntityNavigationEntryProviderImpl(List<EntityNavigationEntryProviderBase> providers) {
    this.providers = providers;
  }

  @Override
  public NavigationEntry getEntry(URI entityObjectUri) {
    EntityNavigationEntryProviderBase provider = providers.stream()
        .filter(p -> p.supports(entityObjectUri))
        .findFirst()
        .orElseGet(this::getDefaultProvider);
    return provider.getEntry(entityObjectUri);
  }
  
  private EntityNavigationEntryProviderBase getDefaultProvider() {
    if(defaultProvider == null) {
      defaultProvider = new EntityNavigationEntryProviderBase() {
        
        @Override
        public NavigationEntry getEntry(URI entityObjectUri) {
          String objectId = EntityUris.getEntityObjectId(entityObjectUri);
          String entityName = EntityUris.getEntityName(entityObjectUri);
          NavigationView naviView = new NavigationView()
              .name(entityObjectUri.toString())
              .putParametersItem("id", objectId);
          
          NavigationEntry entry = new NavigationEntry()
              .objectUri(entityObjectUri)
              .name(entityName + ": " + objectId)
              .icon("cube")
              .addViewsItem(naviView);
          return entry;
        }
        
        @Override
        boolean supports(URI entityUri) {
          return true;
        }
      };
    }
    return defaultProvider;
  }
  
}
