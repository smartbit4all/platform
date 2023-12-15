package org.smartbit4all.api.view;

import java.util.List;
import org.smartbit4all.api.contribution.PrimaryApi;
import org.smartbit4all.api.view.bean.UiAction;

/**
 * The primary api responsible for collecting all the actions available on a given set of object.
 * The api manages all the menus of the application. So for calculating the actions we should
 * provide the menu names and the related objects.
 */
public interface ActionManagementApi extends PrimaryApi<ActionSupplierApi> {

  /**
   * Calculates all the actions available on the given item. It is based on the registered
   * {@link ActionSupplierApi}s and also on the context based on the item itself and the view the
   * given calculation is applied on. The actions are a hierarchical structure representing a menus
   * and sub menus.
   * 
   * @param item The object the action are calculated for.
   * @param menus The list of menu actions to calculate.
   * @return
   */
  List<UiAction> calculateActions(Object item, String... menus);

}
