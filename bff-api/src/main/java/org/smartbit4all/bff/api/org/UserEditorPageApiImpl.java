package org.smartbit4all.bff.api.org;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.PasswordEncoderApi;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.userselector.bean.UserEditingModel;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.springframework.beans.factory.annotation.Autowired;

public class UserEditorPageApiImpl extends PageApiImpl<UserEditingModel>
    implements UserEditorPageApi {

  @Autowired
  OrgApi orgApi;

  @Autowired
  private PasswordEncoderApi passwordEncoderApi;

  public UserEditorPageApiImpl() {
    super(UserEditingModel.class);
  }

  @Override
  public UserEditingModel initModel(View view) {

    view.actions(USER_ACTIONS);

    UserEditingModel pageModel = new UserEditingModel();

    User user = orgApi.getUser(view.getObjectUri());

    user.password("");

    List<Group> groups = orgApi.getGroupsOfUser(view.getObjectUri());

    pageModel.user(user)
        .groups(groups.stream().map(Group::getUri).collect(Collectors.toList()));

    return pageModel;
  }

  @Override
  public void saveUser(UUID viewUuid, UiActionRequest request) {

    setModel(viewUuid, super.extractClientModel(request));

    User user = getModel(viewUuid).getUser();
    user.password(
        passwordEncoderApi.encode(getModel(viewUuid).getUser().getPassword()));

    if (orgApi.getActiveUsers().stream().map(u -> u.getUri()).collect(Collectors.toList())
        .contains(user.getUri())) {

      orgApi.updateUser(user);

    }

    else {
      orgApi.saveUser(user);
    }

    viewApi.showView(new View().viewName(OrgViewNames.USER_DASHBOARD_PAGE));

  }

  @Override
  public void cancelUserEdit(UUID viewUuid, UiActionRequest request) {
    viewApi.showView(new View().viewName(OrgViewNames.USER_DASHBOARD_PAGE));
  }

}
