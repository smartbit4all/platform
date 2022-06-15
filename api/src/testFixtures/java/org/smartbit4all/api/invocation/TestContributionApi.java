package org.smartbit4all.api.invocation;

import org.smartbit4all.api.contribution.ContributionApi;

public interface TestContributionApi extends ContributionApi {

  void doSomething(String doParam);

  String echoMethod(String p1);

}
