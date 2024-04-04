package org.smartbit4all.api.view.action;

import static java.util.stream.Collectors.toList;
import java.util.List;
import org.smartbit4all.api.toolbar.bean.ToolbarDefinition;
import org.smartbit4all.api.toolbar.bean.ToolbarItem;
import org.smartbit4all.api.toolbar.bean.ToolbarItemKind;
import org.smartbit4all.api.toolbar.bean.ToolbarItemOperation;
import org.smartbit4all.api.view.bean.UiAction;

public class ToolbarManagementApiImpl implements ToolbarManagementApi {

  @Override
  public List<UiAction> getActionsForToolbar(ToolbarDefinition toolbar, List<UiAction> actions) {
    if (toolbar == null) {
      return actions;
    }
    // actions to remove
    List<String> toRemove = toolbar.getData().getItems().stream()
        .filter(item -> item.getKind() == ToolbarItemKind.ACTION)
        .filter(item -> item.getOperation() == ToolbarItemOperation.REMOVE)
        .map(ToolbarItem::getAction)
        .collect(toList());
    // TODO handle other kinds / operations
    String toolbarName = toolbar.getData().getQualifiedName();
    return actions.stream()
        .filter(action -> !toRemove.contains(action.getCode()))
        .map(action -> action.toolbar(toolbarName))
        .collect(toList());
  }

}
