package org.smartbit4all.bff.api.mdm;

import java.util.UUID;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.bean.UiActionRequest;

/**
 * This generic page api is responsible for the listing of a given {@link MDMEntryDescriptor}. It
 * contains a grid that will show the list of the objects in the given entry. In an application we
 * have to inherit and register this page api to be able to set the parent view names for the proper
 * routing.
 */
public interface MDMEntryListPageApi extends PageApi<MDMEntryDescriptor> {

  /**
   * Run the query and update the content of the grid.
   */
  public static final String DO_QUERY = "DO_QUERY";

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action cancels all the draft editing object and we will have the published object list.
   */
  public static final String CANCEL_CHANGES = "CANCEL_CHANGES";

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action publishes all the draft editing object and the changes will be available for every user.
   */
  public static final String FINALIZE_CHANGES = "FINALIZE_CHANGES";

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action construct a new entry. Initiate the currently editing branch if it doesn't exist. The
   * new object is going to be registered into the branch as new object.
   */
  public static final String NEW_ENTRY = "NEW_ENTRY";

  /**
   * Populates the list of the MDMEntry with the entries visible for the given user. If
   * administrator then we can see the draft objects also.
   * 
   * @param viewUuid The unique identifier of the view in the current context.
   * @param request The action request that contains every information about the triggering action.
   */
  @ActionHandler(DO_QUERY)
  void performDoQuery(UUID viewUuid, UiActionRequest request);

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action cancels all the draft editing object and we will have the published object list.
   * 
   * @param viewUuid The unique identifier of the view in the current context.
   * @param request The action request that contains every information about the triggering action.
   */
  @ActionHandler(CANCEL_CHANGES)
  void cancelChanges(UUID viewUuid, UiActionRequest request);

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action construct a new entry. Initiate the currently editing branch if it doesn't exist. The
   * new object is going to be registered into the branch as new object.
   * 
   * @param viewUuid The unique identifier of the view in the current context.
   * @param request The action request that contains every information about the triggering action.
   */
  @ActionHandler(NEW_ENTRY)
  void newENtry(UUID viewUuid, UiActionRequest request);

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action publishes all the draft editing object and the changes will be available for every user.
   * 
   * @param viewUuid The unique identifier of the view in the current context.
   * @param request The action request that contains every information about the triggering action.
   */
  @ActionHandler(FINALIZE_CHANGES)
  void finalizeChanges(UUID viewUuid, UiActionRequest request);

  // @WidgetActionHandler(value = OPEN_CATEGORY, widget = CATEGORY_GRID)
  // void performOpenCategory(UUID viewUuid, String gridId, String rowId, UiActionRequest request);
  //
  // @WidgetActionHandler(value = OPEN_PRODUCT, widget = PRODUCT_GRID)
  // void performOpenProduct(UUID viewUuid, String gridId, String rowId, UiActionRequest request);

}
