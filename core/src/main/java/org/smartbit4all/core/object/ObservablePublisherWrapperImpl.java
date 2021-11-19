package org.smartbit4all.core.object;

public class ObservablePublisherWrapperImpl implements ObservablePublisherWrapper {

  @Override
  public void accept(Runnable notifications) {
    notifications.run();
  }

}
