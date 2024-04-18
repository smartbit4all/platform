package org.smartbit4all.bff.api.acl;

import java.util.UUID;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.annotation.WidgetActionHandler;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.bff.api.generic.GenericPageApi;

public interface UserAclSubscriptionPageApi extends PageApi<Object> {

  static final String ACL_PREFIX = "acl";
  static final String DELETE_SUBSCRIPTION = "DELETE_SUBSCRIPTION";
  static final String ACTION_CLOSE_VIEW = GenericPageApi.ACTION_CLOSE_VIEW;
  static final String PARAM_USER = "PARAM_USER";
  static final String PARAM_SUBJECT_MODELS = "PARAM_SUBJECT_MODELS";

  /**
   * The identifier of the grid that contains the result of the search index query.
   */
  static final String WIDGET_RESULT_GRID = "RESULT_GRID";

  GridPage addGridActions(GridPage page, UUID viewUuid, String gridId);

  @ActionHandler(ACTION_CLOSE_VIEW)
  void closeView(UUID viewUuid, UiActionRequest request);

  @WidgetActionHandler(DELETE_SUBSCRIPTION)
  void performDeleteSubject(UUID viewUuid, String gridId, String rowId, UiActionRequest request);

}
