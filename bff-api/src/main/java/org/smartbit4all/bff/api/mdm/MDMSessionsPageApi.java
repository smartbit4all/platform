package org.smartbit4all.bff.api.mdm;

import java.util.UUID;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.mdm.bean.MDMBranchingStrategy;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.annotation.WidgetActionHandler;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.bff.api.mdm.utility.MDMActions;
import org.smartbit4all.bff.api.search.SearchPageApi;

/**
 * Generic page api to show modifications on MDM entries.
 *
 * @author zslipcsei
 *
 */
public interface MDMSessionsPageApi extends SearchPageApi {

  public static final String PARAM_MDM_DEFINITION = "MDM_DEFINITION";

  /**
   * If the current user is administrator of the given MDM definition then this action can be
   * performed. The action starts an editing session for the entries. If branching strategy is
   * {@link MDMBranchingStrategy#GLOBAL}, this will be a global branch, otherwise if strategy is
   * {@link MDMBranchingStrategy#STRICT_PARALLEL}, it will be a new branch.
   *
   * @param viewUuid The unique identifier of the view in the current context.
   * @param request The action request that contains every information about the triggering action.
   */
  @ActionHandler(MDMActions.ACTION_START_EDITING)
  void startEditing(UUID viewUuid, UiActionRequest request);

  /**
   * Open selected editing from sessionGrid.
   *
   * @param viewUuid
   * @param request
   */
  @WidgetActionHandler(MDMActions.ACTION_OPEN_EDITING)
  void openEditing(UUID viewUuid, String widgetId, String nodeId, UiActionRequest request);

  @WidgetActionHandler(MDMActions.ACTION_RENAME_EDITING)
  void renameEditing(UUID viewUuid, String widgetId, String nodeId, UiActionRequest request);

  // TODO we may need this to have a consistent behavior between different strategies.
  // /**
  // * Open editing on global session / branch.
  // *
  // * @param viewUuid
  // * @param request
  // */
  // @ActionHandler(MDMActions.ACTION_OPEN_EDITING)
  // void openGlobalEditing(UUID viewUuid, UiActionRequest request);


  GridPage onGridPageRender(GridPage page, UUID viewUuid);
}
