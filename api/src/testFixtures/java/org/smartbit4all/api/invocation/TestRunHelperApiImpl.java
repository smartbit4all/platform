package org.smartbit4all.api.invocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestRunHelperApiImpl implements TestRunHelperApi {

  protected static final Logger log =
      LoggerFactory.getLogger(TestRunHelperApiImpl.class);

  @Override
  public Boolean invocationReturnBooleanInput(Boolean input) {
    log.debug("invocationReturnBooleanInput: {}", input);
    return input;
  }

  @Override
  public Object invocationReturnInput(Object input) {
    log.debug("invocationReturnInput: {}", input);
    return input;
  }

  @Override
  public Integer invocationIncrementCounter(Integer input) {
    log.debug("invocationIncrementCounter: {}", input);
    return input + 1;
  }

  @Override
  public Boolean invocationCounterBelowThree(Integer input) {
    log.debug("invocationCounterBelowThree: {}", input);
    return input < 3;
  }

}
