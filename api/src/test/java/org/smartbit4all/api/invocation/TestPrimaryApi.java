package org.smartbit4all.api.invocation;

import java.util.List;
import org.smartbit4all.api.contribution.PrimaryApi;

public interface TestPrimaryApi extends PrimaryApi<TestContributionApi> {

  List<String> getApis();

  String echoMethod(String apiName, String p1);

}
