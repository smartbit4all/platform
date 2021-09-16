package org.smartbit4all.api.invocation;

public abstract class InvocationExecutionApiImpl implements InvocationExecutionApi {

  private final String name;

  private InvocationApi invocationApi;

  public InvocationExecutionApiImpl(String name) {
    super();
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  void setInvocationApi(InvocationApi invocationApi) {
    this.invocationApi = invocationApi;
  }

  protected final InvocationApi getInvocationApi() {
    return invocationApi;
  }

}
