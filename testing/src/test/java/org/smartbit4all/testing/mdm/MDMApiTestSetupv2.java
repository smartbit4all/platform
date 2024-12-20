package org.smartbit4all.testing.mdm;

import org.smartbit4all.api.setup.ApplicationSetupApi;
import org.smartbit4all.api.setup.ApplicationSetupApiImpl;

public class MDMApiTestSetupv2 extends ApplicationSetupApiImpl {

  protected MDMApiTestSetupv2(Class<? extends ApplicationSetupApi> clazz) {
    super(clazz);
  }

  public static int executionCounter = 0;

  @Override
  public void execute() {
    executionCounter++;
  }

  @Override
  public boolean checkRunAgain() {
    return executionCounter < 3;
  }

}
