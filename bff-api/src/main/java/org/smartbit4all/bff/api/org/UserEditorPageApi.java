package org.smartbit4all.bff.api.org;

import java.util.UUID;
import org.smartbit4all.api.userselector.bean.UserEditingModel;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.bean.UiActionRequest;

public interface UserEditorPageApi extends PageApi<UserEditingModel> {

  public static final String SAVE_USER = "SAVE_USER";
  public static final String CANCEL = "CANCEL";
  public static final String CHANGE_PASSWORD = "CHANGE_PASSWORD";

  public static final String PARAM_GROUPS = "GROUPS";

  @ActionHandler(CHANGE_PASSWORD)
  void changePassword(UUID viewUuid, UiActionRequest request);

  @ActionHandler(SAVE_USER)
  void saveUser(UUID viewUuid, UiActionRequest request);

  @ActionHandler(CANCEL)
  void cancelUserEdit(UUID viewUuid, UiActionRequest request);

}
