package org.smartbit4all.api.navigation;

import org.smartbit4all.api.navigation.bean.NavigationEntry;

public final class Navigations {

  public static NavigationEntry of(String id, String name, String correlationId, String icon) {
    NavigationEntry result = new NavigationEntry();
    result.setName(name);
    result.setId(id);
    result.setCorrelationId(correlationId);
    result.setIcon(icon);
    return result;
  }

  public static NavigationEntry of(String id, String name, String correlationId) {
    NavigationEntry result = new NavigationEntry();
    result.setName(name);
    result.setId(id);
    result.setCorrelationId(correlationId);
    return result;
  }

}
