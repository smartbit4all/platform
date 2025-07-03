package org.smartbit4all.api.invocation;

import org.smartbit4all.api.contribution.ContributionApi;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface TestContributionApi extends ContributionApi {

  void doSomething(String doParam);

  String echoMethod(String p1);

}
