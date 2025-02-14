package org.smartbit4all.testing.mdm;

import org.smartbit4all.api.setup.ApplicationSetupApi;
import org.smartbit4all.api.setup.ApplicationSetupApiImpl;

public class MDMApiTestSetupv1 extends ApplicationSetupApiImpl {

  protected MDMApiTestSetupv1(Class<? extends ApplicationSetupApi> clazz) {
    super(clazz);
  }

  // should mark as volatile as multiple threads are going to mutate/observe the value
  public static volatile int executionCounter = 0;

  @Override
  public void execute() {
    // the Class itself may not be unloaded between test runs!!! This one may set the value only!
    executionCounter = 1;
  }

}
