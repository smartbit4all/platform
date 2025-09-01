package org.smartbit4all.api.invocation;

public interface TestRunHelperApi {

  Boolean invocationReturnBooleanInput(Boolean input);

  Object invocationReturnInput(Object input);

  Integer invocationIncrementCounter(Integer input);

  Boolean invocationCounterBelowThree(Integer input);
}
