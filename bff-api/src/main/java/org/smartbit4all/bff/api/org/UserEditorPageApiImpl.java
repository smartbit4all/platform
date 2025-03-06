package org.smartbit4all.bff.api.org;

import static java.util.stream.Collectors.toList;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.textfield;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.widgetKey;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.smartbit4all.api.formdefinition.bean.SmartFormWidgetType;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.OrgUtils;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.LayoutDirection;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.api.userselector.bean.UserEditingModel;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.ComponentConstraint;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionInputType;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewConstraint;
import org.smartbit4all.core.object.ObjectLayoutApi;
import org.smartbit4all.core.object.ObjectLayoutBuilder;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;

public class UserEditorPageApiImpl extends PageApiImpl<UserEditingModel>
    implements UserEditorPageApi {

  @Autowired
  protected OrgApi orgApi;
  @Autowired(required = false)
  protected PasswordEncoder passwordEncoder;
  @Autowired
  LocaleSettingApi localeSettingApi;

  public UserEditorPageApiImpl() {
    super(UserEditingModel.class);
  }

  @Override
  public UserEditingModel initModel(View view) {
    UserEditingModel pageModel = new UserEditingModel();

    view.actions(getUserEditorActions());

    URI userUri = view.getObjectUri();

    if (userUri != null) {
      pageModel.user(orgApi.getUser(userUri));

      List<Group> groups = parameters(view).getAsList(PARAM_GROUPS, Group.class);
      if (!ObjectUtils.isEmpty(groups)) {
        pageModel.actualGroups(orgApi.getGroupsOfUser(userUri).stream()
            .filter(ug -> groups.stream()
                .anyMatch(g -> objectApi.equalsIgnoreVersion(ug.getUri(), g.getUri())))
            .map(Group::getUri)
            .collect(Collectors.toList()));
      }

      putConstraintIntoView(view, false);
    } else {
      pageModel.user(new User().name("").email("").username(""));
      pageModel.actualGroups(new ArrayList<>());
      putConstraintIntoView(view, true);
    }
    pageModel.getUser().password("");

    putLayoutIntoView(view);

    return pageModel;
  }



  @Override
  public void changePassword(UUID viewUuid, UiActionRequest request) {
    ObjectMapHelper actionRequestHelper = actionRequestHelper(request);
    String newPassword = actionRequestHelper.get(UiActions.INPUT2, String.class);
    UserEditingModel model = getModel(viewUuid);
    model.getUser().password(newPassword);
    setModel(viewUuid, model);
  }

  public void putLayoutIntoView(View view) {
    List<SmartWidgetDefinition> widgets = new ArrayList<>(Arrays.asList(
        textfield(widgetKey(UserEditingModel.USER, User.NAME),
            localeSettingApi.get(UserEditingModel.USER, User.NAME)),
        textfield(widgetKey(UserEditingModel.USER, User.USERNAME),
            localeSettingApi.get(UserEditingModel.USER, User.USERNAME)),
        textfield(widgetKey(UserEditingModel.USER, User.PASSWORD),
            localeSettingApi.get(UserEditingModel.USER, User.PASSWORD)),
        textfield(widgetKey(UserEditingModel.USER, User.EMAIL),
            localeSettingApi.get(UserEditingModel.USER, User.EMAIL))));

    List<Group> groups = parameters(view).getAsList(PARAM_GROUPS, Group.class);
    if (!ObjectUtils.isEmpty(groups)) {

      List<Value> values = groups.stream()
          .map(g -> new Value().code(g.getUri().toString()).displayValue(g.getTitle()))
          .collect(Collectors.toList());

      widgets.add(new SmartWidgetDefinition().type(SmartFormWidgetType.SELECT_MULTIPLE)
          .key(UserEditingModel.ACTUAL_GROUPS)
          .label(UserEditingModel.ACTUAL_GROUPS)
          .values(values));
    }

    SmartComponentLayoutDefinition layout = ObjectLayoutBuilder.form(LayoutDirection.VERTICAL,
        widgets.toArray(new SmartWidgetDefinition[widgets.size()]));
    view.putComponentLayoutsItem(ObjectLayoutApi.DEFAULT_LAYOUT, layout);
  }

  private void putConstraintIntoView(View view, boolean passwordIsVisible) {
    ViewConstraint viewConstraint = new ViewConstraint().componentConstraints(Arrays.asList(
        new ComponentConstraint().dataName(widgetKey(UserEditingModel.USER, User.NAME))
            .enabled(true).mandatory(true).visible(true),
        new ComponentConstraint().dataName(widgetKey(UserEditingModel.USER, User.USERNAME))
            .enabled(true).mandatory(true).visible(true),
        new ComponentConstraint().dataName(widgetKey(UserEditingModel.USER, User.EMAIL))
            .enabled(true).mandatory(true).visible(true),
        new ComponentConstraint().dataName(widgetKey(UserEditingModel.USER, User.PASSWORD))
            .enabled(true).mandatory(true).visible(passwordIsVisible)));
    view.constraint(viewConstraint);
  }

  @Override
  public void saveUser(UUID viewUuid, UiActionRequest request) {
    UserEditingModel clientModel = extractClientModel(request);
    User user = clientModel.getUser();
    String clientPassword = user.getPassword();

    if (!ObjectUtils.isEmpty(clientPassword)) {
      String password =
          passwordEncoder == null ? clientPassword : passwordEncoder.encode(clientPassword);
      user.password(password);
    } else {
      user.password(objectApi.loadLatest(user.getUri()).getValueAsString(User.PASSWORD));
    }
    URI userUri;
    if (orgApi.getActiveUsers().stream().map(User::getUri).collect(toList())
        .contains(user.getUri())) {
      userUri = orgApi.updateUser(user);
    } else {
      userUri = orgApi.saveUser(user);
    }

    List<Group> groups = parameters(viewUuid).getAsList(PARAM_GROUPS, Group.class);
    if (!ObjectUtils.isEmpty(groups)) {
      List<URI> groupsFromModel = clientModel.getActualGroups();
      // We remove the user from the unnecessary groups
      orgApi.getGroupsOfUser(userUri).stream()
          .filter(ug -> groups.stream()
              .anyMatch(g -> objectApi.equalsIgnoreVersion(ug.getUri(), g.getUri())))
          .forEach(g -> {
            if (groupsFromModel.stream().noneMatch(u -> u.equals(g.getUri()))) {
              orgApi.removeUserFromGroup(userUri, g.getUri());
              orgApi.getSubGroups(g.getUri())
                  .forEach(sg -> orgApi.removeUserFromGroup(userUri, sg.getUri()));
            }
          });
      // We add the user to the necessary groups
      groupsFromModel.forEach(gu -> {
        if (orgApi.getUsersOfGroup(gu).stream()
            .noneMatch(u -> objectApi.equalsIgnoreVersion(u.getUri(), userUri))) {
          OrgUtils.applyGroupByName(orgApi, orgApi.getGroup(gu), orgApi.getUser(userUri));
        }
      });
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
    return Arrays.asList(new UiAction().code(SAVE_USER).submit(true),
        new UiAction().code(CANCEL),
        new UiAction().code(CHANGE_PASSWORD).input2Type(UiActionInputType.TEXTFIELD));
  }

}
