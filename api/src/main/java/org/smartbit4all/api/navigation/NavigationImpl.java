package org.smartbit4all.api.navigation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.navigation.bean.NavigationEntry;

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

  protected List<URI> getAssociationsToNavigate(NavigationEntry entry, List<URI> associations) {
    List<URI> result = associations;
    if (result == null) {
      result = entry.getMeta().getAssociations() == null ? null
          : entry.getMeta().getAssociations().stream().map(a -> a.getUri())
              .collect(Collectors.toList());
    }
    return result;
  }

  protected URI extractEntryUri(URI navigationUri) {
    try {
      return new URI(navigationUri.getFragment());
    } catch (URISyntaxException e) {
      log.error("Unable to extract navigation entry uri", e);
    }
    return null;
  }

}
