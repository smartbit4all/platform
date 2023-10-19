package org.smartbit4all.bff.api.acl;

import java.util.UUID;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.bff.api.subjectselector.bean.SubjectSelectorPageModel;

public interface SubjectSelectorPageApi extends PageApi<SubjectSelectorPageModel> {
  String SUBJECT_MODEL_NAME = "SUBJECT_MODEL_NAME";
  String SUBJECT_GRID_ID = "SUBJECT_GRID";
  String INVOCATION = "INVOCATION";

  String CANCEL = "CANCEL";

  @ActionHandler(CANCEL)
  void performCancel(UUID viewUuid, UiActionRequest request);

  String SELECT_SUBJECT = "SELECT_SUBJECT";

  @ActionHandler(SELECT_SUBJECT)
  void peformSelectSubject(UUID viewUuid, UiActionRequest request);

  String SUBMIT_SELECTION = "SUBMIT_SELECTION";

  @ActionHandler(SUBMIT_SELECTION)
  void performSubmitSelection(UUID viewUuid, UiActionRequest request);
}
