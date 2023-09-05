package org.smartbit4all.api.org;

import org.smartbit4all.api.contribution.ContributionApiImpl;

public class ACLOrgContribution extends ContributionApiImpl implements ACLContributionApi {

  public static final String ORG_API = "org";

  protected ACLOrgContribution() {
    super(ORG_API);
  }

  @Override
  public void refreshRegistry() {}

}
