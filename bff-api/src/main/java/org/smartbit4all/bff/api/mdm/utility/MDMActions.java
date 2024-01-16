package org.smartbit4all.bff.api.mdm.utility;

public class MDMActions {
  private MDMActions() {}

  public static final String REFRESH = "REFRESH";

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action start an editing phase for the entries.
   */
  public static final String ACTION_START_EDITING = "START_EDITING";

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action cancels all the draft editing object and we will have the published object list.
   */
  public static final String ACTION_CANCEL_CHANGES = "CANCEL_CHANGES";

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action publishes all the draft editing object and the changes will be available for every user.
   */
  public static final String ACTION_FINALIZE_CHANGES = "FINALIZE_CHANGES";

  public static final String ACTION_SEND_FOR_APPROVAL = "SEND_FOR_APPROVAL";
  public static final String ACTION_ADMIN_APPROVE_OK = "ADMIN_APPROVE_OK";
  public static final String ACTION_ADMIN_APPROVE_NOT_OK = "ADMIN_APPROVE_NOT_OK";
}
