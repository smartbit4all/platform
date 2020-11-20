package org.smartbit4all.api.navigation;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.smartbit4all.api.navigation.bean.NavigationEntry;

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

  protected List<URI> getAssociationsToNavigate(NavigationEntry entry, List<URI> associations) {
    List<URI> result = associations;
    if (result == null) {
      result = entry.getMeta().getAssociations() == null ? null
          : entry.getMeta().getAssociations().stream().map(a -> a.getUri())
              .collect(Collectors.toList());
    }
    return result;
  }

}
