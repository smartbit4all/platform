package org.smartbit4all.api.navigation;

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

}
