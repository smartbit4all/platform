package org.smartbit4all.api.view;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.smartbit4all.api.contribution.ContributionApiImpl;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.core.object.ObjectNode;

public abstract class ActionSupplierApiImpl extends ContributionApiImpl
    implements ActionSupplierApi {

  protected final Set<String> objectDefinitions;

  protected final Set<String> menus;

  protected ActionSupplierApiImpl(String apiName, Collection<String> managedObjectDefinitions,
      Collection<String> managedMenus) {
    super(apiName);
    this.objectDefinitions = new HashSet<>(managedObjectDefinitions);
    this.menus = new HashSet<>(managedMenus);
  }

  @Override
  public boolean extending(Object item, List<String> menuList) {
    String definitionName;
    if (item instanceof ObjectNode) {
      definitionName = ((ObjectNode) item).getDefinition().getQualifiedName();
    } else {
      definitionName = item.getClass().getName();
    }
    if (!objectDefinitions.contains(definitionName)) {
      return false;
    }
    if (!menuList.isEmpty() && menuList.stream().noneMatch(m -> menus.contains(m))) {
      return false;
    }
    return true;
  }

  @Override
  public List<UiAction> buildActions(Object item, List<String> menus,
      List<UiAction> currentActions) {
    // As a builder function it must return the currentActions itself by default.
    return currentActions;
  }

  @Override
  public List<UiAction> postProcessActions(Object item, List<String> menus,
      List<UiAction> allActions) {
    // As a builder function it must return the allActions itself by default.
    return allActions;
  }

}
