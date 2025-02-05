package org.smartbit4all.bff.api.org;

import static java.util.stream.Collectors.toList;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.textfield;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.widgetKey;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.formdefinition.bean.SmartFormWidgetType;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.invocation.InvocationApi;
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
import org.smartbit4all.api.view.bean.MessageData;
import org.smartbit4all.api.view.bean.MessageOption;
import org.smartbit4all.api.view.bean.MessageOptionType;
import org.smartbit4all.api.view.bean.MessageType;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionInputType;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewConstraint;
import org.smartbit4all.core.object.ObjectLayoutApi;
import org.smartbit4all.core.object.ObjectLayoutBuilder;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.smartbit4all.core.object.ObjectNode;
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
  @Autowired
  private InvocationApi invocationApi;

  public UserEditorPageApiImpl() {
    super(UserEditingModel.class);
  }

  @Override
  public UserEditingModel initModel(View view) {
    UserEditingModel pageModel = new UserEditingModel();


    URI userUri = view.getObjectUri();
    view.actions(getUserEditorActions(userUri));

    if (userUri != null) {
      pageModel.user(orgApi.getUser(userUri));
      pageModel.actualGroups(orgApi.getGroupsOfUser(userUri).stream().map(Group::getUri)
          .collect(toList()));
      putConstraintIntoView(view, false);
    } else {
      pageModel.user(new User().name("").email("").username(""));
      pageModel.actualGroups(new ArrayList<>());
      putConstraintIntoView(view, true);
    }
    pageModel.getUser().password("");
    pageModel.possibleGroups(orgApi.getAllGroups());

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
    SmartComponentLayoutDefinition layout = ObjectLayoutBuilder.form(LayoutDirection.VERTICAL,
        textfield(widgetKey(UserEditingModel.USER, User.NAME),
            localeSettingApi.get(UserEditingModel.USER, User.NAME)),
        textfield(widgetKey(UserEditingModel.USER, User.USERNAME),
            localeSettingApi.get(UserEditingModel.USER, User.USERNAME)),
        textfield(widgetKey(UserEditingModel.USER, User.PASSWORD),
            localeSettingApi.get(UserEditingModel.USER, User.PASSWORD)),
        textfield(widgetKey(UserEditingModel.USER, User.EMAIL),
            localeSettingApi.get(UserEditingModel.USER, User.EMAIL)),
        new SmartWidgetDefinition().type(SmartFormWidgetType.SELECT_MULTIPLE)
            .key(UserEditingModel.ACTUAL_GROUPS)
            .label(UserEditingModel.ACTUAL_GROUPS)
            .values(orgApi.getAllGroups().stream()
                .map(g -> new Value().code(g.getUri().toString()).displayValue(g.getTitle()))
                .collect(toList())));
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
    ObjectNode userNode = null;

    if (!ObjectUtils.isEmpty(clientPassword)) {
      String password =
          passwordEncoder == null ? clientPassword : passwordEncoder.encode(clientPassword);
      user.password(password);
    } else {
      userNode = objectApi.loadLatest(user.getUri());
      user.password(userNode.getValueAsString(User.PASSWORD));
    }
    URI userUri;

    if ((!ObjectUtils.isEmpty(userNode)
        && !userNode.getValueAsString(User.USERNAME).equals(user.getUsername()))
        || ObjectUtils.isEmpty(userNode)) {
      if (orgApi.getUserByUsername(user.getUsername()) != null) {
        viewApi.showMessage(new MessageData()
            .viewUuid(viewUuid)
            .type(MessageType.WARNING)
            .header(localeSettingApi.get("username.error"))
            .text(localeSettingApi.get("need.unique.username"))
            .options(
                List.of(new MessageOption().code("CONFIRM")
                    .label(localeSettingApi.get("Okay"))
                    .type(MessageOptionType.CONFIRM))));
        return;
      }
      if (!ObjectUtils.isEmpty(userNode)) {
        orgApi.updateUsername(userNode.getObject(User.class),
            user.getUsername());
      }
    }

    if (orgApi.getActiveUsers().stream().map(User::getUri).collect(toList())
        .contains(user.getUri())) {
      updateUserWithGroups(clientModel, user);
    } else {
      userUri = orgApi.saveUser(user);
      updateUserWithGroups(clientModel, objectApi.loadLatest(userUri).getObject(User.class));
    }

    viewApi.closeView(viewUuid);
  }

  private void updateUserWithGroups(UserEditingModel clientModel, User user) {
    List<URI> actualGroupUris = clientModel.getActualGroups();
    orgApi.getGroupsOfUser(user.getUri()).stream().forEach(g -> {
      if (actualGroupUris.stream().noneMatch(u -> u.equals(g.getUri()))) {
        orgApi.removeUserFromGroup(user.getUri(), g.getUri());
      }
    });
    actualGroupUris.forEach(gu -> {
      if (orgApi.getUsersOfGroup(gu).stream().noneMatch(u -> u.getUri().equals(user.getUri()))) {
        OrgUtils.applyGroupByName(orgApi, orgApi.getGroup(gu), orgApi.getUser(user.getUri()));
      }
    });
    orgApi.updateUser(user);
  }

  @Override
  public void cancelUserEdit(UUID viewUuid, UiActionRequest request) {
    viewApi.closeView(viewUuid);
  }

  protected String getUserListName() {
    return OrgViewNames.USER_LIST_PAGE;
  }

  protected List<UiAction> getUserEditorActions(URI userUri) {
    List<UiAction> uiActions = new ArrayList<>();
    uiActions.add(new UiAction().code(SAVE_USER).submit(true));
    uiActions.add(new UiAction().code(CANCEL));
    if (userUri != null) {
      String ssoUser =
          objectApi.loadLatest(userUri).getObject(User.class).getAttributes().get(OrgApi.SSO_USER);
      if (!Boolean.TRUE.toString().equals(ssoUser)) {
        uiActions.add(new UiAction().code(CHANGE_PASSWORD).input2Type(UiActionInputType.TEXTFIELD));
      }
    }
    return uiActions;
  }

}
