package org.smartbit4all.api.invocation;

import java.util.List;
import java.util.stream.Collectors;
import org.smartbit4all.api.contribution.PrimaryApiImpl;

public final class TestPrimaryApiImpl extends PrimaryApiImpl<TestContributionApi>
    implements TestPrimaryApi {

  public static final String NAME = TestPrimaryApiImpl.class.getName();

  public TestPrimaryApiImpl(Class<TestContributionApi> innerApiClass) {
    super(innerApiClass);
  }

  @Override
  public List<String> getApis() {
    return apiByName.values().stream().map(api -> api.getApiName()).collect(Collectors.toList());
  }

  @Override
  public String echoMethod(String apiName, String p1) {
    return getContributionApi(apiName).echoMethod(p1);
  }

}
