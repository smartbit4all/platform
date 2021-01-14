package org.smartbit4all.ui.vaadin.components.toolbar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.ui.common.action.Action;
import org.smartbit4all.ui.common.toolbar.ToolBarView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class ToolBarViewDefault implements ToolBarView{

  HorizontalLayout layout;
  
  Map<Action, Button> buttonsByAction;
  
  public ToolBarViewDefault() {
    super();
    buttonsByAction = new HashMap<Action, Button>();
    layout = new HorizontalLayout();
    layout.setAlignItems(Alignment.CENTER);
    layout.setPadding(true);
  }

  @Override
  public void setActions(List<Action> actions) {
    layout.removeAll();
    
    for (Action toolBarAction : actions) {
      Button button = new Button(toolBarAction.getLabel(), new Icon(toolBarAction.getIconCode()));
      button.addClickListener(c -> toolBarAction.execute());
      layout.add(button);
      buttonsByAction.put(toolBarAction, button);
    }
  }
  
  public HorizontalLayout getLayout() {
    return layout;
  }

  @Override
  public void clearActions() {
    layout.removeAll();    
  }

  @Override
  public void addAction(Action action) {
    Button button = new Button(action.getLabel(), new Icon(action.getIconCode()));
    button.addClickListener(c -> action.execute());
    layout.add(button);
    buttonsByAction.put(action, button);    
  }

  @Override
  public void removeAction(Action action) {
    Button button = buttonsByAction.remove(action);
    layout.remove(button);
  }

}
