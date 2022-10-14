package org.smartbit4all.api.view;

public interface ViewRegistryApi {

  void add(String viewName, String parentView);

  String getParentViewName(String viewName);

}
