package org.smartbit4all.api.view;

import java.util.HashMap;
import java.util.Map;

public class ViewRegistryApiImpl implements ViewRegistryApi {

  private Map<String, String> parentViewByViewName = new HashMap<>();

  @Override
  public void add(String viewName, String parentView) {
    parentViewByViewName.put(viewName, parentView);
  }

  @Override
  public String getParentViewName(String viewName) {
    String result = parentViewByViewName.get(viewName);
    return result == null ? "" : result;
  }

}
