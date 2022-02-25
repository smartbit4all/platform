package org.smartbit4all.ui.api.navigation.restserver.test;

import java.net.URI;
import java.util.UUID;
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
public class TestViewModelImpl extends ViewModelImpl<TestModel> implements TestViewModel {

  @Autowired
  UINavigationApi uiNavigationApi;

  protected TestViewModelImpl(ObservablePublisherWrapper publisherWrapper) {
    super(publisherWrapper, TestDescriptors.TEST_DESCRIPTORS, TestModel.class);
  }

  @Override
  protected void initCommands() {
    registerCommand(TEST_COMMAND, this::execTestCommand);
    registerCommand(MODIFY, this::modify);
    registerCommandWithParams(UPLOAD, this::upload);
  }

  @Override
  protected TestModel load(NavigationTarget navigationTarget) {
    return new TestModel().user(new User()
        .uri(URI.create("test:/user1"))
        .name("Test User")
        .email("test@email.com"));
  }

  @Override
  protected URI getUri() {
    return null;
  }

  private void execTestCommand() {
    model.getUser().setName(model.getUser().getName() + "+command");
  }

  private void modify() {
    uiNavigationApi.navigateTo(new NavigationTarget()
        .viewName(MODIFY_VIEW)
        .objectUri(model.getUser().getUri()));
  }

  private void upload(Object[] params) {
    checkParamNumber(UPLOAD, 1, params);
    BinaryData binaryData = (BinaryData) params[0];
    model.getUser().setName(model.getUser().getName() + "-length:" + binaryData.length());
    UUID dataUuid = UUID.randomUUID();
    registerDownload(dataUuid, () -> binaryData);
    model.setDataUuid(dataUuid);

  }

}
