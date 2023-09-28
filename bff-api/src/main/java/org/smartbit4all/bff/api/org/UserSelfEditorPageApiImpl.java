package org.smartbit4all.bff.api.org;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.userselector.bean.UserSelfEditingModel;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.bean.MessageData;
import org.smartbit4all.api.view.bean.MessageType;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;

public class UserSelfEditorPageApiImpl extends PageApiImpl<UserSelfEditingModel>
    implements UserSelfEditorPageApi {

  private static final Logger log = LoggerFactory.getLogger(UserSelfEditorPageApiImpl.class);

  @Autowired
  private SessionApi sessionApi;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private LocaleSettingApi localeApi;

  public UserSelfEditorPageApiImpl() {
    super(UserSelfEditingModel.class);
  }

  @Override
  public UserSelfEditingModel initModel(View view) {

    view.actions(getUserEditorActions());

    User user = sessionApi.getUser();
    return new UserSelfEditingModel()
        .email(user.getEmail())
        .name(user.getName())
        .username(user.getUsername());
  }

  @Override
  public void cancelUserEdit(UUID viewUuid, UiActionRequest request) {
    viewApi.closeView(viewUuid);
  }

  @Override
  public void saveUser(UUID viewUuid, UiActionRequest request) {
    UserSelfEditingModel clientModel = extractClientModel(request);

    String oldPassword = clientModel.getOldPassword();
    if (ObjectUtils.isEmpty(oldPassword)) {
      showErrorMessage("oldpassword.missing", viewUuid);
      return;
    }

    User user = sessionApi.getUser();
    if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
      showErrorMessage("oldpassword.incorrect", viewUuid);
      return;
    }

    String newPassword2 = clientModel.getNewPassword2();
    String newPassword1 = clientModel.getNewPassword1();
    boolean hasPasswordChange = !ObjectUtils.isEmpty(newPassword1);
    if ((hasPasswordChange || !ObjectUtils.isEmpty(newPassword2))
        && !ObjectUtils.nullSafeEquals(newPassword1, newPassword2)) {
      showErrorMessage("newpassword.notmatching", viewUuid);
      return;
    }

    ObjectNode userNode = objectApi.loadLatest(user.getUri());

    if (hasPasswordChange) {
      if (!checkNewPassword(newPassword1)) {
        showErrorMessage("newpassword.weak", viewUuid);
        return;
      } else {
        userNode.setValue(passwordEncoder.encode(newPassword1), User.PASSWORD);
      }
    }

    List<String> errorCodes = new ArrayList<>();
    errorCodes.add(setUserField(userNode, user::getName, clientModel::getName, User.NAME,
        "name.weak", this::checkNewName));
    errorCodes.add(setUserField(userNode, user::getEmail, clientModel::getEmail, User.EMAIL,
        "email.weak", this::checkNewEmail));
    errorCodes
        .add(setUserField(userNode, user::getUsername, clientModel::getUsername, User.USERNAME,
            "username.weak", this::checkNewUsername));
    errorCodes.removeIf(e -> e == null);
    if (!errorCodes.isEmpty()) {
      showErrorMessages(errorCodes, viewUuid);
      return;
    }

    objectApi.save(userNode);
    handleSuccessfulSave(viewUuid);
    log.debug("User self editing successfull on user: {}", user);
    viewApi.closeView(viewUuid);
  }

  /*
   * Method called when saveUser is successful.
   */
  protected void handleSuccessfulSave(UUID viewUuid) {
    // Override as needed
  }

  protected String setUserField(ObjectNode userNode, Supplier<String> userGetter,
      Supplier<String> modelGetter, String fieldName, String errorCode,
      Predicate<String> fieldChecker) {
    String fieldValue = modelGetter.get();
    boolean hasNewValue =
        !ObjectUtils.isEmpty(fieldValue)
            && !ObjectUtils.nullSafeEquals(fieldValue, userGetter.get());
    if (hasNewValue) {
      if (!fieldChecker.test(fieldValue)) {
        return errorCode;
      } else {
        userNode.setValue(fieldValue, fieldName);
      }
    }
    return null;
  }

  /**
   * Returns true when the new password matches all the requirements.
   */
  protected boolean checkNewPassword(String newPassword) {
    return true;
  }

  /**
   * Returns true when the new email matches all the requirements.
   */
  protected boolean checkNewEmail(String email) {
    return true;
  }

  /**
   * Returns true when the new name matches all the requirements.
   */
  protected boolean checkNewName(String name) {
    return true;
  }

  /**
   * Returns true when the new username matches all the requirements.
   */
  protected boolean checkNewUsername(String username) {
    return true;
  }

  protected void showErrorMessage(String textCode, UUID viewUuid) {
    viewApi.showMessage(new MessageData()
        .text(getLocaleCode(textCode))
        .viewUuid(viewUuid)
        .header(getLocaleCode("error"))
        .type(MessageType.ERROR));
  }

  protected void showErrorMessages(List<String> textCodes, UUID viewUuid) {
    viewApi.showMessage(new MessageData()
        .text(textCodes.stream().map(this::getLocaleCode).collect(Collectors.joining("\n")))
        .viewUuid(viewUuid)
        .header(getLocaleCode("error"))
        .type(MessageType.ERROR));
  }

  protected List<UiAction> getUserEditorActions() {
    return Arrays.asList(new UiAction().code(SAVE),
        new UiAction().code(CANCEL));
  }

  private String getLocaleCode(String code) {
    return localeApi.get("UserSelfEditorPageApi." + code);
  }

}
