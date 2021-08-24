package org.smartbit4all.api.invocation;

import org.smartbit4all.api.contribution.PrimaryApiImpl;

public final class TestPrimaryApiImpl extends PrimaryApiImpl<TestContributionApi>
    implements TestPrimaryApi {

  public TestPrimaryApiImpl(Class<?> primaryApiClass, Class<TestContributionApi> innerApiClass) {
    super(primaryApiClass, innerApiClass);
    // TODO Auto-generated constructor stub
  }

}
