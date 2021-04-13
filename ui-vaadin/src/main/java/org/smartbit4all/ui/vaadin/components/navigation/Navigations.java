package org.smartbit4all.ui.vaadin.components.navigation;

import java.net.URI;
import java.util.Map;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEvent;

public class Navigations {

  private Navigations() {}

  public static final String ENTRY_PARAM = "entry";

  /**
   * Shorthand for navigate to a view with an URI parameter passed with {@link #ENTRY_PARAM} key.
   * 
   * @param ui
   * @param viewName
   * @param uriParam
   */
  public static void navigateTo(UI ui, String viewName, URI uriParam) {
    Navigation.to(viewName).param(ENTRY_PARAM, uriParam).navigate(ui);
  }

  public static URI getUriParameter(BeforeEvent event) {
    return (URI) getParameter(event, ENTRY_PARAM);
  }

  public static Object getParameter(BeforeEvent event, String paramName) {
    Map<String, Object> parameters =
        UIViewParameterVaadinTransition.extract(event.getLocation().getQueryParameters());
    return parameters.get(paramName);
  }

  public static Navigation getPreviousNavigation(BeforeEvent event) {
    return (Navigation) getParameter(event, Navigation.PREVIOUS_NAVIGATION);
  }

}
