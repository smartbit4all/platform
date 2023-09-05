package org.smartbit4all.api.org;

import org.smartbit4all.api.contribution.PrimaryApi;

/**
 * The ACL management api is responsible for integration of the {@link ACLContributionApi}s and
 * produce a common value set definition for all the subjects.
 * 
 * @author Peter Boros
 */
public interface ACLManagementApi extends PrimaryApi<ACLContributionApi> {

}
