package org.smartbit4all.ui.api.navigation.restserver.test;

import java.net.URI;
import java.util.UUID;
import org.smartbit4all.core.object.ObservablePublisherWrapper;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.api.viewmodel.ViewModelImpl;

/**
 * Instead of creating a separate test bean for model, we use an existing bean.
 *
 */
public class TestViewModelImpl extends ViewModelImpl<NavigationTarget> implements TestViewModel {

  protected TestViewModelImpl(ObservablePublisherWrapper publisherWrapper) {
    super(publisherWrapper, TestDescriptors.TEST_DESCRIPTORS, NavigationTarget.class);
  }

  @Override
  protected void initCommands() {
    registerCommand(TEST_COMMAND, this::execTestCommand);
  }

  @Override
  protected NavigationTarget load(NavigationTarget navigationTarget) {
    return new NavigationTarget()
        .uuid(UUID.randomUUID())
        .title("test title")
        .viewName("data-for-view");
  }

  @Override
  protected URI getUri() {
    return null;
  }

  private void execTestCommand() {
    model.setTitle(model.getTitle() + "+command");
  }
}
