package org.smartbit4all.testing.mdm;

import java.util.concurrent.atomic.AtomicInteger;
import org.smartbit4all.api.setup.ApplicationSetupApi;
import org.smartbit4all.api.setup.ApplicationSetupApiImpl;

public class MDMApiTestSetupv2 extends ApplicationSetupApiImpl {


  private boolean hasRun = false;

  protected MDMApiTestSetupv2(Class<? extends ApplicationSetupApi> clazz) {
    super(clazz);
  }

  // should mark as volatile as multiple threads are going to mutate/observe the value
  public static final AtomicInteger executionCounter = new AtomicInteger(0);
  public static final int LIMIT = 3;

  @Override
  public void execute() {
    if (!hasRun) {
      executionCounter.set(0);;
      hasRun = true;
    }
    executionCounter.updateAndGet(i -> (i < LIMIT) ? i + 1 : i);
  }

  @Override
  public boolean checkRunAgain() {
    return !hasRun || executionCounter.get() < LIMIT;
  }

}
