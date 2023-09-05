package org.smartbit4all.api.org;

import org.smartbit4all.api.contribution.ContributionApi;

/**
 * The {@link ACLContributionApi} is responsible for introducing new subjects to the access control
 * lists. First of all it provides value sets for the selection of the
 * 
 * @author Peter Boros
 */
public interface ACLContributionApi extends ContributionApi {

  /**
   * Refresh the registry of the ACL
   */
  void refreshRegistry();

}
