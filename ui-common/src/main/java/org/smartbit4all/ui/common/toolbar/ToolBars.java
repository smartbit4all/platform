package org.smartbit4all.ui.common.toolbar;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.smartbit4all.ui.common.action.Action;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class ToolBars implements InitializingBean {

  public static final String GLOBAL_TOOLBAR = "globaltoolbar";

  Map<String, ToolBarController> controllerByName = new HashMap<>();

  @Autowired(required = false)
  List<ToolBarController> controllers;

  @Override
  public void afterPropertiesSet() throws Exception {
    if (controllers != null) {
      for (ToolBarController controller : controllers) {
        controllerByName.put(controller.name(), controller);
      }
    }
  }

  private final ToolBarController controller(URI uri) {
    return controllerByName.get(uri.getScheme());
  }

  public void showActions(List<Action> actions) {
    Map<ToolBarController, List<Action>> actionsToControllers = getControllerActionMap(actions);

    for (Entry<ToolBarController, List<Action>> entry : actionsToControllers.entrySet()) {
      ToolBarController controller = entry.getKey();
      List<Action> actionsToShow = entry.getValue();
      controller.showActions(actionsToShow);
    }
  }
  
  public void hideActions(List<Action> actions) {
    Map<ToolBarController, List<Action>> actionsToControllers = getControllerActionMap(actions);

    for (Entry<ToolBarController, List<Action>> entry : actionsToControllers.entrySet()) {
      ToolBarController controller = entry.getKey();
      List<Action> actionsToShow = entry.getValue();
      controller.hideActions(actionsToShow);
    }
  }

  private Map<ToolBarController, List<Action>> getControllerActionMap(List<Action> actions) {
    Map<ToolBarController, List<Action>> actionsToControllers =
        new HashMap<ToolBarController, List<Action>>();

    for (Action action : actions) {
      List<URI> containers = action.getContainers();
      if (containers == null || containers.isEmpty()) {
        for (ToolBarController controller : controllerByName.values()) {
          addAction(actionsToControllers, controller, action);
        }
      } else {
        for (URI container : containers) {
          addAction(actionsToControllers, controller(container), action);
        }
      }
    }
    return actionsToControllers;
  }

  private void addAction(Map<ToolBarController, List<Action>> actionsToControllers,
      ToolBarController controller, Action action) {
    if (controller == null) {
      return;
    }
    List<Action> list = actionsToControllers.get(controller);

    if (list == null) {
      list = new ArrayList<Action>();
      actionsToControllers.put(controller, list);
    }

    list.add(action);
  }

  public void clearToolBar(String string) {
    controllerByName.get(string).cleatToolBar();    
  }

}
