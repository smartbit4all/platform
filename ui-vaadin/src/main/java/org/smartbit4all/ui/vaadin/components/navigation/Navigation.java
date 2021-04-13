package org.smartbit4all.ui.vaadin.components.navigation;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.vaadin.flow.component.UI;

public class Navigation {

  private static final Logger log = LoggerFactory.getLogger(Navigation.class);

  static final String PREVIOUS_NAVIGATION = "prev_nav";

  private String viewName;
  private Map<String, Object> params;

  private Navigation(String viewName) {
    this.viewName = viewName;
    params = new HashMap<>();
  }

  public static Navigation to(String viewName) {
    return new Navigation(viewName);
  }

  public Navigation param(String key, Object value) {
    params.put(key, value);
    return this;
  }

  public void navigate(UI ui) {
    params.put(PREVIOUS_NAVIGATION, this);
    try (UIViewParameterVaadinTransition param =
        new UIViewParameterVaadinTransition(params)) {
      ui.navigate(viewName, param.construct());
    } catch (Exception e) {
      log.error("Unexpected error", e);
    }
  }

}
