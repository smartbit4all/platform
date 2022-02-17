package org.smartbit4all.ui.vaadin.components.navigation;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.api.viewmodel.ObjectEditing;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.QueryParameters;

public class Navigations {

  private Navigations() {}

  @Deprecated
  public static final String ENTRY_PARAM = "entry";

  private static final String PARAM = "param";

  public static URI getUriParameter(BeforeEvent event) {
    URI uri = getUriParameter();
    if (uri != null) {
      return uri;
    }
    // fallback - non existent in theory...
    return (URI) getParameter(event, ENTRY_PARAM);
  }

  public static URI getUriParameter() {
    return getUriParameter(ObjectEditing.currentNavigationTarget.get());
  }

  public static URI getUriParameter(NavigationTarget navigationTarget) {
    if (navigationTarget != null) {
      URI uri = navigationTarget.getObjectUri();
      if (uri != null) {
        return uri;
      }
      if (navigationTarget.getParameters().containsKey(ENTRY_PARAM)) {
        return (URI) navigationTarget.getParameters().get(ENTRY_PARAM);
      }
    }
    return null;
  }

  public static Object getParameter(BeforeEvent event, String paramName) {
    Map<String, Object> parameters = getParameters(event.getLocation().getQueryParameters());
    return parameters.get(paramName);
  }

  public static Map<String, Object> getParameters(QueryParameters queryParameters) {
    if (queryParameters == null || queryParameters.getParameters() == null) {
      return Collections.emptyMap();
    }
    List<String> list = queryParameters.getParameters().get(PARAM);
    if (list == null || list.size() != 1) {
      return Collections.emptyMap();
    }
    NavigationTarget navigationTarget = ObjectEditing.currentNavigationTarget.get();
    if (navigationTarget == null) {
      throw new ParameterMissingException("No navigationTarget defined in call!");
    }
    UUID uuid = navigationTarget.getUuid();
    if (uuid == null) {
      throw new ParameterMissingException("NavigationTarget without UUID found!");
    }
    String parameterKey = list.get(0);
    if (uuid.toString().equals(parameterKey)) {
      return navigationTarget.getParameters();
    }
    throw new ParameterMissingException("Parameter " + parameterKey + " is missing");
  }

}
