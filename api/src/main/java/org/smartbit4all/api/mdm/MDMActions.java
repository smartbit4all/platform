package org.smartbit4all.api.mdm;

public class MDMActions {
  private MDMActions() {}

  public static final String REFRESH = "REFRESH";

  /**
   * If the current user is administrator of the given entry then this action can be performed. The
   * action start an editing phase for the entries.
   */
  public static final String ACTION_START_EDITING = "START_EDITING";

  /**
   * Opens an already created editing session.
   */
  public static final String ACTION_OPEN_EDITING = "OPEN_EDITING";
  public static final String ACTION_RENAME_EDITING = "RENAME_EDITING";
  public static final String ACTION_CLOSE_EDITING = "CLOSE_EDITING";

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

  public static final String ACTION_ADD_COMMENT_TO_ENTRY = "ADD_COMMENT_TO_ENTRY";
  public static final String ACTION_OPEN_COMMENTS_TO_ENTRY = "OPEN_COMMENTS_TO_ENTRY";
  public static final String ACTION_REJECT_ENTRY = "REJECT_ENTRY";
  public static final String ACTION_APPROVE_ENTRY = "APPROVE_ENTRY";
  public static final String ACTION_FIX_ENTRY = "FIX_ENTRY";

  public static final String ACTION_DO_QUERY = "DO_QUERY";
  public static final String ACTION_TOGGLE_INACTIVES = "TOGGLE_INACTIVES";
  public static final String ACTION_NEW_ENTRY = "NEW_ENTRY";
  public static final String ACTION_EDIT_ENTRY = "EDIT_ENTRY";
  public static final String ACTION_VIEW_ENTRY = "VIEW_ENTRY";
  public static final String ACTION_VIEW_ORIGINAL_ENTRY = "VIEW_ORIGINAL_ENTRY";
  public static final String ACTION_DELETE_ENTRY = "DELETE_ENTRY";
  public static final String ACTION_INACTIVATE_ENTRY = "INACTIVATE_ENTRY";
  public static final String ACTION_CANCEL_DRAFT_ENTRY = "CANCEL_DRAFT_ENTRY";
  public static final String ACTION_RESTORE_ENTRY = "RESTORE_ENTRY";
  public static final String ACTION_SHOW_ENTRY_DESCRIPTOR_PAGE = "SHOW_ENTRY_DESCRIPTOR_PAGE";
  public static final String ACTION_RECREATE_INDEX = "ACTION_RECREATE_INDEX";
  public static final String ACTION_IMPORT_ENTRIES = "IMPORT_ENTRIES";
}
