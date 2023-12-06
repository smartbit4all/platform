package org.smartbit4all.bff.api.acl;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.org.bean.ACL;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.bean.UiActionRequest;

public interface AclEditingPageApi extends PageApi<ACL> {
  String CANCEL = "CANCEL";

  @ActionHandler(CANCEL)
  void closeAclEditing(UUID viewUuid, UiActionRequest request);

  String SAVE = "SAVE";

  @ActionHandler(SAVE)
  void saveEditing(UUID viewUuid, UiActionRequest request);

  String OPEN_SUBJECT_SELECTOR = "OPEN_SUBJECT_SELECTOR";

  @ActionHandler(OPEN_SUBJECT_SELECTOR)
  void openSubjectSelector(UUID viewUuid, UiActionRequest request);

  void handleSubjectSelected(UUID viewUuid, List<URI> subjectUriList);

  String REMOVE_MATRIX_ROW = "REMOVE_MATRIX_ROW";

  @ActionHandler(REMOVE_MATRIX_ROW)
  void removeRowFromSubjectMatrix(UUID viewUuid, UiActionRequest request);
}
