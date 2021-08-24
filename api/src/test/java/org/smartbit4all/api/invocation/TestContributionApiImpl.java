package org.smartbit4all.api.invocation;

import org.smartbit4all.api.contribution.ContributionApiImpl;

public class TestContributionApiImpl extends ContributionApiImpl implements TestContributionApi {

  public static String lastDoSomething;

  public TestContributionApiImpl(String apiName) {
    super(apiName);
  }

  @Override
  public void doSomething(String doParam) {
    lastDoSomething = doParam;
  }

  @Override
  public String echoMethod(String p1) {
    return p1;
  }

}
