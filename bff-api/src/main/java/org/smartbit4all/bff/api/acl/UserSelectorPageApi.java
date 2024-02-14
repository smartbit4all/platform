package org.smartbit4all.bff.api.acl;

import java.util.UUID;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.annotation.ViewApi;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.bff.api.config.PlatformViewNames;
import org.smartbit4all.bff.api.subjectselector.bean.UserSelectorPageModel;

@ViewApi(PlatformViewNames.USER_SELECTOR_PAGE)
public interface UserSelectorPageApi extends PageApi<UserSelectorPageModel> {
  String SUBJECT_VALUES = "SUBJECT_VALUES";
  String SELECTION_CALLBACK = "SELECTION_CALLBACK";
  String SELECTION_MODE = "SELECTION_MODE";

  String CANCEL = "CANCEL";

  String SEARCH = "SEARCH";

  @ActionHandler(SEARCH)
  void search(UUID viewUuid, UiActionRequest request);

  @ActionHandler(CANCEL)
  void performCancel(UUID viewUuid, UiActionRequest request);

  @ActionHandler(UserSelectorPageModel.SELECTION)
  void performChangeSelection(UUID viewUuid, UiActionRequest request);

  String SUBMIT_SELECTION = "SUBMIT_SELECTION";

  @ActionHandler(SUBMIT_SELECTION)
  void performSubmitSelection(UUID viewUuid, UiActionRequest request);

}
