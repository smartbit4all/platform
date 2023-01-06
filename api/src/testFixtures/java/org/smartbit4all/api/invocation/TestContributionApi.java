package org.smartbit4all.api.invocation;

import org.smartbit4all.api.contribution.ContributionApi;
import org.smartbit4all.domain.data.storage.TransactionalStorage;

@TransactionalStorage
public interface TestContributionApi extends ContributionApi {

  void doSomething(String doParam);

  String echoMethod(String p1);

}
