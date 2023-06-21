package org.smartbit4all.bff.api.mdm;

import java.util.UUID;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.annotation.WidgetActionHandler;
import org.smartbit4all.api.view.bean.UiActionRequest;

/**
 * This generic page api is responsible for the listing of a given {@link MDMEntryDescriptor}. It
 * contains a grid that will show the list of the objects in the given entry. In an application we
 * have to inherit and register this page api to be able to set the parent view names for the proper
 * routing.
 */
public interface MDMEntryListPageApi extends PageApi<MDMEntryDescriptor> {

  /**
   * The identifier of of the {@link MDMEntryDescriptor} object parameter.
   */
  static final String ENTRY_DESCRIPTOR = "ENTRY_DESCRIPTOR";

  /**
   * The identifier of of the {@link MDMDefinition} object parameter.
   */
  static final String MDM_DEFINITION = "MDM_DEFINITION";

  /**
   * The identifier of the grid that contains the entries.
   */
  static final String ENTRY_GRID = "ENTRY_GRID";

  /**
   * Run the query and update the content of the grid.
   */
  static final String DO_QUERY = "DO_QUERY";

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action cancels all the draft editing object and we will have the published object list.
   */
  static final String CANCEL_CHANGES = "CANCEL_CHANGES";

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action publishes all the draft editing object and the changes will be available for every user.
   */
  static final String FINALIZE_CHANGES = "FINALIZE_CHANGES";

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action construct a new entry. Initiate the currently editing branch if it doesn't exist. The
   * new object is going to be registered into the branch as new object.
   */
  static final String NEW_ENTRY = "NEW_ENTRY";

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action starts the editing of the currently selected entry. Initiate the currently editing
   * branch if it doesn't exist. The edited object is going to be registered into the branch as it
   * is saved.
   */
  static final String EDIT_ENTRY = "EDIT_ENTRY";

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action starts the editing of the currently selected entry. The edited object is going to be
   * deleted from the list that contains the currently edited versions of the objects.
   */
  static final String DELETE_ENTRY = "DELETE_ENTRY";

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action starts the editing of the currently selected entry. The draft object (if any) is going
   * to be canceled from the list that contains the currently edited versions of the objects. It
   * will be restored to the original object.
   */
  static final String CANCEL_DRAFT_ENTRY = "CANCEL_DRAFT_ENTRY";

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
  void newEntry(UUID viewUuid, UiActionRequest request);

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action publishes all the draft editing object and the changes will be available for every user.
   * 
   * @param viewUuid The unique identifier of the view in the current context.
   * @param request The action request that contains every information about the triggering action.
   */
  @ActionHandler(FINALIZE_CHANGES)
  void finalizeChanges(UUID viewUuid, UiActionRequest request);

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action starts the editing of the currently selected entry. The edited object is going to be
   * deleted from the list that contains the currently edited versions of the objects.
   * 
   * @param viewUuid The unique identifier of the view in the current context.
   * @param gridId The identifier of the grid widget in the given page.
   * @param rowId The identifier of the row.
   * @param request The action request that contains every information about the triggering action.
   */
  @WidgetActionHandler(value = EDIT_ENTRY, widget = ENTRY_GRID)
  void performEditEntry(UUID viewUuid, String gridId, String rowId, UiActionRequest request);

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action starts the editing of the currently selected entry. The edited object is going to be
   * deleted from the list that contains the currently edited versions of the objects.
   * 
   * @param viewUuid The unique identifier of the view in the current context.
   * @param gridId The identifier of the grid widget in the given page.
   * @param rowId The identifier of the row.
   * @param request The action request that contains every information about the triggering action.
   */
  @WidgetActionHandler(value = DELETE_ENTRY, widget = ENTRY_GRID)
  void performDeleteEntry(UUID viewUuid, String gridId, String rowId, UiActionRequest request);

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action cancels all the draft editing object and we will have the published object list.
   * 
   * @param viewUuid The unique identifier of the view in the current context.
   * @param gridId The identifier of the grid widget in the given page.
   * @param rowId The identifier of the row.
   * @param request The action request that contains every information about the triggering action.
   */
  @WidgetActionHandler(value = CANCEL_DRAFT_ENTRY, widget = ENTRY_GRID)
  void performCancelDraftEntry(UUID viewUuid, String gridId, String rowId, UiActionRequest request);

}
