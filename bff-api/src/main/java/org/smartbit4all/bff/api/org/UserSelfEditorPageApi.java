package org.smartbit4all.bff.api.org;

import java.util.UUID;
import org.smartbit4all.api.userselector.bean.UserSelfEditingModel;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.bean.UiActionRequest;

public interface UserSelfEditorPageApi extends PageApi<UserSelfEditingModel> {

  public static final String SAVE = "SAVE";
  public static final String CANCEL = "CANCEL";

  @ActionHandler(SAVE)
  void saveUser(UUID viewUuid, UiActionRequest request);

  @ActionHandler(CANCEL)
  void cancelUserEdit(UUID viewUuid, UiActionRequest request);

}
