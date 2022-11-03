package org.smartbit4all.api.view;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewRegistryApiImpl implements ViewRegistryApi {

  private static final Logger log = LoggerFactory.getLogger(ViewRegistryApiImpl.class);

  private Map<String, String> parentViewByViewName = new HashMap<>();

  @Override
  public void add(String viewName, String parentView) {
    log.debug("Adding to ViewRegistry: {}, parent: {}", viewName, parentView);
    parentViewByViewName.put(viewName, parentView);
  }

  @Override
  public String getParentViewName(String viewName) {
    String result = parentViewByViewName.get(viewName);
    return result == null ? "" : result;
  }

}
