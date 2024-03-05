package org.smartbit4all.bff.api.acl;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.org.bean.Subject;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.annotation.WidgetActionHandler;
import org.smartbit4all.api.view.bean.UiActionRequest;

public interface AclGenericPageApi extends PageApi<Object> {

  static final String PARAM_ACL_PAGE_CONFIG = "PARAM_ACL_PAGE_CONFIG";

  static final String ADD_SUBJECT = "ADD_SUBJECT";
  static final String DELETE_SUBJECT = "DELETE_SUBJECT";
  static final String EDIT_COMMENT = "EDIT_COMMENT";
  static final String SAVE_COMMENT = "SAVE_COMMENT";

  GridPage addGridActions(GridPage page, UUID viewUuid, String gridId);

  @ActionHandler(ADD_SUBJECT)
  void performAddSubject(UUID viewUuid, UiActionRequest request);

  @WidgetActionHandler(DELETE_SUBJECT)
  void performDeleteSubject(UUID viewUuid, String gridId, String rowId, UiActionRequest request);

  @WidgetActionHandler(EDIT_COMMENT)
  void performEditComment(UUID viewUuid, String gridId, String rowId, UiActionRequest request);

  void handleSubjectSelected(UUID viewUuid, List<Subject> subjects, String gridId);

  void saveSubjectSelectedWithComment(UUID dialogUuid, UiActionRequest request,
      UUID viewUuid, List<Subject> subjects, String gridId);

  void handleUserSelected(UUID viewUuid, List<URI> userUriList, String gridId);

  void saveComment(UUID dialogUuid, UiActionRequest request, UUID viewUuid, String gridId,
      String rowId);

}
