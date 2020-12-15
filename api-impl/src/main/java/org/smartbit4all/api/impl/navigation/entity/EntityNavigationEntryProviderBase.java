package org.smartbit4all.api.impl.navigation.entity;

import java.net.URI;

public abstract class EntityNavigationEntryProviderBase implements EntityNavigationEntryProvider {

  abstract boolean supports(URI entityUri);
}
