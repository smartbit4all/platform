package org.smartbit4all.api.navigation;

import java.util.List;
import org.smartbit4all.api.navigation.bean.NavigationAssociationMeta;
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

  protected List<NavigationAssociationMeta> getAssociationsToNavigate(NavigationEntry entry,
      List<NavigationAssociationMeta> associations) {
    List<NavigationAssociationMeta> result = associations;
    if (result == null) {
      result = entry.getMeta().getAssociations();
    }
    return result;
  }

}
