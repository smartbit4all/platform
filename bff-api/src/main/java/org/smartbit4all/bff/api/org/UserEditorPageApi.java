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
  public static final String CANCEL_USER_EDIT = "CANCEL_USER_EDIT";

  public static final String USER_LIST_PAGE = "USER_LIST_PAGE";

  List<UiAction> USER_ACTIONS =
      Arrays.asList(new UiAction().code(SAVE_USER).submit(true), new UiAction().code(CANCEL_USER_EDIT));

  @ActionHandler(SAVE_USER)
  void saveUser(UUID viewUuid, UiActionRequest request);

  @ActionHandler(CANCEL_USER_EDIT)
  void cancelUserEdit(UUID viewUuid, UiActionRequest request);

}
