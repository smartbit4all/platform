package org.smartbit4all.bff.api.acl;

import java.util.UUID;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.annotation.ViewApi;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.bff.api.config.PlatformViewNames;
import org.smartbit4all.bff.api.subjectselector.bean.SubjectSelectorPageModel;

@ViewApi(PlatformViewNames.SUBJECT_SELECTOR_PAGE)
public interface SubjectSelectorPageApi extends PageApi<SubjectSelectorPageModel> {
  String PARAM_SUBJECT_MODEL_NAME = "PARAM_SUBJECT_MODEL_NAME";
  String PARAM_SUBJECT_TYPES = "PARAM_SUBJECT_TYPES";
  String PARAM_SELECTION_CALLBACK = "PARAM_SELECTION_CALLBACK";
  String PARAM_SELECTION_MODE = "SELECTION_MODE";

  String CANCEL = "CANCEL";

  String SEARCH = "SEARCH";

  @ActionHandler(SEARCH)
  void search(UUID viewUuid, UiActionRequest request);

  @ActionHandler(CANCEL)
  void performCancel(UUID viewUuid, UiActionRequest request);

  @ActionHandler(SubjectSelectorPageModel.SELECTION)
  void performChangeSelection(UUID viewUuid, UiActionRequest request);

  String SUBMIT_SELECTION = "SUBMIT_SELECTION";

  @ActionHandler(SUBMIT_SELECTION)
  void performSubmitSelection(UUID viewUuid, UiActionRequest request);

}
