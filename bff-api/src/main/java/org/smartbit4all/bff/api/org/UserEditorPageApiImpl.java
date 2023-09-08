package org.smartbit4all.bff.api.org;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.bean.Group;
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
  protected OrgApi orgApi;

  @Autowired(required = false)
  protected PasswordEncoder passwordEncoder;

  public UserEditorPageApiImpl() {
    super(UserEditingModel.class);
  }

  @Override
  public UserEditingModel initModel(View view) {

    view.actions(getUserEditorActions());

    UserEditingModel pageModel = new UserEditingModel();

    URI userUri = view.getObjectUri();

    if (userUri != null) {
      pageModel.user(orgApi.getUser(userUri));
      pageModel.actualGroups(orgApi.getGroupsOfUser(userUri).stream().map(Group::getUri)
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

    UserEditingModel pageModel = getModel(viewUuid);

    User user = pageModel.getUser();

    String password =
        passwordEncoder == null ? user.getPassword() : passwordEncoder.encode(user.getPassword());
    user.password(password);

    if (orgApi.getActiveUsers().stream().map(User::getUri).collect(Collectors.toList())
        .contains(user.getUri())) {
      orgApi.updateUser(user);
    } else {
      orgApi.saveUser(user);
    }

    viewApi.closeView(viewUuid);
  }

  @Override
  public void cancelUserEdit(UUID viewUuid, UiActionRequest request) {
    viewApi.closeView(viewUuid);
  }

  protected String getUserListName() {
    return OrgViewNames.USER_LIST_PAGE;
  }

  protected List<UiAction> getUserEditorActions() {
    return Arrays.asList(new UiAction().code(SAVE_USER),
        new UiAction().code(CANCEL_EDITING));
  }

}
