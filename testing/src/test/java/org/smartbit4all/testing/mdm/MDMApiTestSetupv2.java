package org.smartbit4all.testing.mdm;

import org.smartbit4all.api.setup.ApplicationSetupApi;
import org.smartbit4all.api.setup.ApplicationSetupApiImpl;

public class MDMApiTestSetupv2 extends ApplicationSetupApiImpl {


  private boolean hasRun = false;

  protected MDMApiTestSetupv2(Class<? extends ApplicationSetupApi> clazz) {
    super(clazz);
  }

  // should mark as volatile as multiple threads are going to mutate/observe the value
  public static volatile int executionCounter = 0;

  @Override
  public void execute() {
    if (!hasRun) {
      executionCounter = 0;
      hasRun = true;
    }
    executionCounter++;
  }

  @Override
  public boolean checkRunAgain() {
    return executionCounter < 3 || !hasRun;
  }

}
