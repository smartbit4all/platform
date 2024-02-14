package org.smartbit4all.bff.api.acl;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.annotation.WidgetActionHandler;
import org.smartbit4all.api.view.bean.UiActionRequest;

public interface AclGenericPageApi extends PageApi<Object> {

  static final String PARAM_ACL_PAGE_CONFIG = "PARAM_ACL_PAGE_CONFIG";

  static final String ADD_SUBJECT = "ADD_SUBJECT";
  static final String DELETE_SUBJECT = "DELETE_SUBJECT";

  GridPage addGridActions(GridPage page, UUID viewUuid, String gridId);

  @ActionHandler(ADD_SUBJECT)
  void performAddSubject(UUID viewUuid, UiActionRequest request);

  @WidgetActionHandler(DELETE_SUBJECT)
  void performDeleteSubject(UUID viewUuid, String widgetId, String nodeId, UiActionRequest request);

  void handleSubjectSelected(UUID viewUuid, List<URI> subjectUriList, String gridId);

}
