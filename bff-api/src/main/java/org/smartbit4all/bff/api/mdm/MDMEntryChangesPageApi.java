package org.smartbit4all.bff.api.mdm;

import java.util.UUID;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.bff.api.mdm.utility.MDMActions;

/**
 * Generic page api to show modifications on MDM entries.
 * 
 * @author zslipcsei
 *
 */
public interface MDMEntryChangesPageApi extends PageApi<Object> {

  public static final String PARAM_MDM_DEFINITION = "MDM_DEFINITION";

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action start an editing phase for the entries.
   *
   * @param viewUuid The unique identifier of the view in the current context.
   * @param request The action request that contains every information about the triggering action.
   */
  @ActionHandler(MDMActions.ACTION_START_EDITING)
  void startEditing(UUID viewUuid, UiActionRequest request);

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action cancels all the draft editing object and we will have the published object list.
   *
   * @param viewUuid The unique identifier of the view in the current context.
   * @param request The action request that contains every information about the triggering action.
   */
  @ActionHandler(MDMActions.ACTION_CANCEL_CHANGES)
  void cancelChanges(UUID viewUuid, UiActionRequest request);

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action publishes all the draft editing object and the changes will be available for every user.
   *
   * @param viewUuid The unique identifier of the view in the current context.
   * @param request The action request that contains every information about the triggering action.
   */
  @ActionHandler(MDMActions.ACTION_FINALIZE_CHANGES)
  void finalizeChanges(UUID viewUuid, UiActionRequest request);

  @ActionHandler(MDMActions.ACTION_SEND_FOR_APPROVAL)
  void sendForApproval(UUID viewUuid, UiActionRequest request);

  @ActionHandler(MDMActions.ACTION_ADMIN_APPROVE_OK)
  void adminApproveOk(UUID viewUuid, UiActionRequest request);

  @ActionHandler(MDMActions.ACTION_ADMIN_APPROVE_NOT_OK)
  void adminApproveNotOk(UUID viewUuid, UiActionRequest request);
}
