package org.smartbit4all.api.view;

import java.util.List;
import org.smartbit4all.api.contribution.ContributionApi;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.core.object.ObjectNode;

/**
 * This supplier can extend the object menus functionality via adding action to the its menu. The
 * context is an object or an {@link ObjectNode} and using its values the action are calculated by
 * these suppliers. Even a new module can define a supplier like this and can add actions to the
 * items. Like a new generator or an integration using the dossiers, folders and files or any other
 * objects.
 */
public interface ActionSupplierApi extends ContributionApi {

  /**
   * Returns all the global actions available by the current supplier. These actions are
   * hierarchical so menus and sub menus are also added. In the next round when we have every action
   * collected in a single list then the {@link ActionManagementApiImpl} is going to call the
   * {@link #postProcessActions(Object, List, List)} where the supplier can fine tune the actions
   * and can add new actions to the existing sub menus and more.
   * 
   * @param item The related object the menu is build for.
   * @param menus The menus to build.
   * @param currentActions The current list of actions till this supplier.
   * @return The result after.
   */
  List<UiAction> buildActions(Object item, List<String> menus, List<UiAction> currentActions);

  /**
   * The apply context can modify the resulting {@link UiAction}s using the information of all the
   * actions available globally.
   * 
   * @param item The item that is the context.
   * @param allActions The actions calculated by the {@link #buildActions(Object, List, List)}
   *        calls.
   * @return The resulting action list.
   */
  List<UiAction> postProcessActions(Object item, List<String> menus, List<UiAction> allActions);

  /**
   * The supplier can answer the question if it extends the given item with the menus.
   * 
   * @param item The item to be extended. We always have to provide it to have a default item as
   *        context to check if the actions available or not.
   * @param menus The name of the menus that must be defined at abstract design phase to be able to
   *        extend later on.
   * @return
   */
  boolean extending(Object item, List<String> menus);

}
