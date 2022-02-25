package org.smartbit4all.ui.api.navigation.restserver.test;

import java.net.URI;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.core.object.ObservablePublisherWrapper;
import org.smartbit4all.ui.api.navigation.UINavigationApi;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.api.viewmodel.ViewModelImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Instead of creating a separate test bean for model, we use an existing bean.
 *
 */
public class TestViewModelImpl extends ViewModelImpl<User> implements TestViewModel {

  @Autowired
  UINavigationApi uiNavigationApi;

  protected TestViewModelImpl(ObservablePublisherWrapper publisherWrapper) {
    super(publisherWrapper, TestDescriptors.TEST_DESCRIPTORS, User.class);
  }

  @Override
  protected void initCommands() {
    registerCommand(TEST_COMMAND, this::execTestCommand);
    registerCommand(MODIFY, this::modify);
    registerCommandWithParams(UPLOAD, this::upload);
  }

  @Override
  protected User load(NavigationTarget navigationTarget) {
    return new User()
        .uri(URI.create("test:/user1"))
        .name("Test User")
        .email("test@email.com");
  }

  @Override
  protected URI getUri() {
    return null;
  }

  private void execTestCommand() {
    model.setName(model.getName() + "+command");
  }

  private void modify() {
    uiNavigationApi.navigateTo(new NavigationTarget()
        .viewName(MODIFY_VIEW)
        .objectUri(model.getUri()));
  }

  private void upload(Object[] params) {
    checkParamNumber(UPLOAD, 1, params);
    BinaryData binaryData = (BinaryData) params[0];
    model.setName(model.getName() + "-length:" + binaryData.length());
  }

}
