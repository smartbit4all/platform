package org.smartbit4all.ui.vaadin.components.navigation;

import java.net.URI;
import java.util.Map;
import com.vaadin.flow.router.BeforeEvent;

public class Navigations {

  private Navigations() {}

  public static final String ENTRY_PARAM = "entry";

  public static URI getUriParameter(BeforeEvent event) {
    return (URI) getParameter(event, ENTRY_PARAM);
  }

  public static Object getParameter(BeforeEvent event, String paramName) {
    Map<String, Object> parameters =
        UIViewParameterVaadinTransition.extract(event.getLocation().getQueryParameters());
    return parameters.get(paramName);
  }

}
