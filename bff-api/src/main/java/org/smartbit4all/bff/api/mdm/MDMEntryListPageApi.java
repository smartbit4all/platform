package org.smartbit4all.bff.api.mdm;

import java.net.URI;
import java.util.UUID;
import org.smartbit4all.api.grid.bean.GridRow;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.object.bean.BranchedObjectEntry;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.annotation.WidgetActionHandler;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.bff.api.searchpage.bean.SearchPageModel;

/**
 * This generic page api is responsible for the listing of a given {@link MDMEntryDescriptor}. It
 * contains a grid that will show the list of the objects in the given entry. In an application we
 * have to inherit and register this page api to be able to set the parent view names for the proper
 * routing.
 */
public interface MDMEntryListPageApi extends PageApi<SearchPageModel> {

  /**
   * The identifier of of the {@link MDMEntryDescriptor} object parameter.
   */
  static final String PARAM_ENTRY_DESCRIPTOR = "ENTRY_DESCRIPTOR";

  /**
   * The identifier of of the {@link MDMDefinition} object parameter.
   */
  static final String PARAM_MDM_DEFINITION = "MDM_DEFINITION";

  /**
   * The UUID identifier of the list view for the editing view.
   */
  static final String PARAM_MDM_LIST_VIEW = "MDM_LIST_VIEW";

  /**
   * The branched object entry the editing for is working on..
   */
  static final String PARAM_BRANCHED_OBJECT_ENTRY = "BRANCHED_OBJECT_ENTRY";

  /**
   * The identifier of the grid that contains the entries.
   */
  static final String WIDGET_ENTRY_GRID = "RESULT_GRID";

  /**
   * The view parameter to open custom editors with.
   * 
   * <p>
   * This is done to ensure the {@link PageApiImpl#initModel(org.smartbit4all.api.view.bean.View)}
   * method runs for custom editors.
   */
  static final String PARAM_RAW_MODEL = "raw-model";

  /**
   * Run the query and update the content of the grid.
   */
  static final String ACTION_DO_QUERY = "DO_QUERY";

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action start an editing phase for the entries.
   */
  static final String ACTION_START_EDITING = "START_EDITING";

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action cancels all the draft editing object and we will have the published object list.
   */
  static final String ACTION_CANCEL_CHANGES = "CANCEL_CHANGES";

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action publishes all the draft editing object and the changes will be available for every user.
   */
  static final String ACTION_FINALIZE_CHANGES = "FINALIZE_CHANGES";

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action construct a new entry. Initiate the currently editing branch if it doesn't exist. The
   * new object is going to be registered into the branch as new object.
   */
  static final String ACTION_NEW_ENTRY = "NEW_ENTRY";

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action starts the editing of the currently selected entry. Initiate the currently editing
   * branch if it doesn't exist. The edited object is going to be registered into the branch as it
   * is saved.
   */
  static final String ACTION_EDIT_ENTRY = "EDIT_ENTRY";

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action starts the editing of the currently selected entry. The edited object is going to be
   * deleted from the list that contains the currently edited versions of the objects.
   */
  static final String ACTION_DELETE_ENTRY = "DELETE_ENTRY";

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action starts the editing of the currently selected entry. The draft object (if any) is going
   * to be canceled from the list that contains the currently edited versions of the objects. It
   * will be restored to the original object.
   */
  static final String ACTION_CANCEL_DRAFT_ENTRY = "CANCEL_DRAFT_ENTRY";

  /**
   * Populates the list of the MDMEntry with the entries visible for the given user. If
   * administrator then we can see the draft objects also.
   *
   * @param viewUuid The unique identifier of the view in the current context.
   * @param request The action request that contains every information about the triggering action.
   */
  @ActionHandler(ACTION_DO_QUERY)
  void performDoQuery(UUID viewUuid, UiActionRequest request);

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action start an editing phase for the entries.
   *
   * @param viewUuid The unique identifier of the view in the current context.
   * @param request The action request that contains every information about the triggering action.
   */
  @ActionHandler(ACTION_START_EDITING)
  void startEditing(UUID viewUuid, UiActionRequest request);

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action cancels all the draft editing object and we will have the published object list.
   *
   * @param viewUuid The unique identifier of the view in the current context.
   * @param request The action request that contains every information about the triggering action.
   */
  @ActionHandler(ACTION_CANCEL_CHANGES)
  void cancelChanges(UUID viewUuid, UiActionRequest request);

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action construct a new entry. Initiate the currently editing branch if it doesn't exist. The
   * new object is going to be registered into the branch as new object.
   *
   * @param viewUuid The unique identifier of the view in the current context.
   * @param request The action request that contains every information about the triggering action.
   */
  @ActionHandler(ACTION_NEW_ENTRY)
  void newEntry(UUID viewUuid, UiActionRequest request);

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action publishes all the draft editing object and the changes will be available for every user.
   *
   * @param viewUuid The unique identifier of the view in the current context.
   * @param request The action request that contains every information about the triggering action.
   */
  @ActionHandler(ACTION_FINALIZE_CHANGES)
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
  @WidgetActionHandler(value = ACTION_EDIT_ENTRY, widget = WIDGET_ENTRY_GRID)
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
  @WidgetActionHandler(value = ACTION_DELETE_ENTRY, widget = WIDGET_ENTRY_GRID)
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
  @WidgetActionHandler(value = ACTION_CANCEL_DRAFT_ENTRY, widget = WIDGET_ENTRY_GRID)
  void performCancelDraftEntry(UUID viewUuid, String gridId, String rowId, UiActionRequest request);

  /**
   * This callback function is responsible for saving an object into the MDM entry list we are
   * working on currently.
   *
   * @param viewUuid The list view instance
   * @param objectUri The uri of the edited object.
   * @param editingObject The object or map value of the newly create object.
   * @param branchedObjectEntry The branching information about the given entry.
   */
  void saveObject(UUID viewUuid, URI objectUri, Object editingObject,
      BranchedObjectEntry branchedObjectEntry);

  GridRow addWidgetEntryGridActions(GridRow page, UUID viewUuid);

}
