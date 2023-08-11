package org.smartbit4all.bff.api.org;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.userselector.bean.UserEditingModel;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserEditorPageApiImpl extends PageApiImpl<UserEditingModel>
    implements UserEditorPageApi {

  @Autowired
  OrgApi orgApi;

  @Autowired(required = false)
  private PasswordEncoder passwordEncoder;

  public UserEditorPageApiImpl() {
    super(UserEditingModel.class);
  }

  @Override
  public UserEditingModel initModel(View view) {

    view.actions(getUserEditorActions());

    UserEditingModel pageModel = new UserEditingModel();

    URI userUri = view.getObjectUri();

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

    user.password(passwordEncoder.encode(user.getPassword()));

    if (orgApi.getActiveUsers().stream().map(User::getUri).collect(Collectors.toList())
        .contains(user.getUri())) {
      orgApi.updateUser(user);
    } else {
      orgApi.saveUser(user);
    }

    pageModel.getPossibleGroups().stream()
        .forEach(g -> orgApi.removeUserFromGroup(user.getUri(), g.getUri()));

    pageModel.getActualGroups().stream()
        .forEach(g -> orgApi.addUserToGroup(user.getUri(), g));

    viewApi.showView(new View().viewName(getUserListName()));
  }

  @Override
  public void cancelUserEdit(UUID viewUuid, UiActionRequest request) {
    viewApi.showView(new View().viewName(getUserListName()));
  }

  protected String getUserListName() {
    return OrgViewNames.USER_DASHBOARD_PAGE;
  }

  protected List<UiAction> getUserEditorActions() {
    return Arrays.asList(new UiAction().code(SAVE_USER),
        new UiAction().code(CANCEL_EDITING));
  }

}
