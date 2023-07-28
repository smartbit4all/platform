package org.smartbit4all.bff.api.org;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.userselector.bean.UserEditingModel;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;


public interface UserEditorPageApi extends PageApi<UserEditingModel> {

  public static final String SAVE_USER = "SAVE_USER";
  public static final String CANCEL_USER = "CANCEL_USER";

  List<UiAction> ADMIN_ACTIONS =
      Arrays.asList(new UiAction().code(SAVE_USER),
          new UiAction().code(CANCEL_USER));

  @ActionHandler(SAVE_USER)
  void saveUser(UUID viewUuid, UiActionRequest request);

  @ActionHandler(CANCEL_USER)
  void cancelUserEdit(UUID viewUuid, UiActionRequest request);

}
