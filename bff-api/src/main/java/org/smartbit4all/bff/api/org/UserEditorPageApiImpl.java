package org.smartbit4all.bff.api.org;

import java.net.URI;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.PasswordEncoderApi;
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

    view.actions(ADMIN_ACTIONS);

    UserEditingModel pageModel = new UserEditingModel();


    URI userUri = view.getObjectUri();

    User user;
    if (view.getObjectUri() != null) {
      pageModel.user(orgApi.getUser(userUri));
      pageModel.actualGroups(orgApi.getGroupsOfUser(userUri).stream().map(g -> g.getUri())
          .collect(Collectors.toList()));
    } else {
      pageModel.user(new User().name("").email("").username(""));
      pageModel.actualGroups(new ArrayList<>());

    }

    pageModel.getUser().password("");

    pageModel.possibleGroups(orgApi.getAllGroups());


    return pageModel;
  }

  @Override
  public void saveUser(UUID viewUuid, UiActionRequest request) {

    UserEditingModel pageModel =
        objectApi.asType(UserEditingModel.class, request.getParams().get("formModel"));

    User user = pageModel.getUser();

    user.password(passwordEncoderApi.encode(user.getPassword()));

    if (orgApi.getActiveUsers().stream().map(User::getUri).collect(Collectors.toList())
        .contains(user.getUri())) {

      orgApi.updateUser(user);

    }

    else {
      orgApi.saveUser(user);
    }

    pageModel.getPossibleGroups().stream()
        .forEach(g -> orgApi.removeUserFromGroup(user.getUri(), g.getUri()));


    pageModel.getActualGroups().stream()
        .forEach(g -> orgApi.addUserToGroup(user.getUri(), g));



    viewApi.showView(new View().viewName(OrgViewNames.USER_DASHBOARD_PAGE));

  }

  @Override
  public void cancelUserEdit(UUID viewUuid, UiActionRequest request) {
    viewApi.showView(new View().viewName(OrgViewNames.USER_DASHBOARD_PAGE));
  }

}
