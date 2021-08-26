package org.smartbit4all.api.invocation;

public abstract class InvocationExecutionApiImpl implements InvocationExecutionApi {

  private final String name;

  public InvocationExecutionApiImpl(String name) {
    super();
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

}
