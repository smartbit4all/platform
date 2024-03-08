package org.smartbit4all.bff.api.acl;

import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.org.bean.ACL;
import org.smartbit4all.api.org.bean.Subject;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.annotation.ViewApi;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.bff.api.config.PlatformViewNames;

@ViewApi(PlatformViewNames.ACL_MATRIX_PAGE)
public interface AclEditingPageApi extends PageApi<ACL> {
  static final String PARAM_SUBJECT_TYPES = "PARAM_SUBJECT_TYPES";
  static final String PARAM_OPERATIONS = "PARAM_OPERATIONS";

  String CANCEL = "CANCEL";

  @ActionHandler(CANCEL)
  void closeAclEditing(UUID viewUuid, UiActionRequest request);

  String SAVE = "SAVE";

  @ActionHandler(SAVE)
  void saveEditing(UUID viewUuid, UiActionRequest request);

  String OPEN_SUBJECT_SELECTOR = "OPEN_SUBJECT_SELECTOR";

  @ActionHandler(OPEN_SUBJECT_SELECTOR)
  void openSubjectSelector(UUID viewUuid, UiActionRequest request);

  void handleSubjectSelected(UUID viewUuid, List<Subject> subjectUriList);

  String REMOVE_MATRIX_ROW = "REMOVE_MATRIX_ROW";

  @ActionHandler(REMOVE_MATRIX_ROW)
  void removeRowFromSubjectMatrix(UUID viewUuid, UiActionRequest request);
}
