package org.smartbit4all.api.org;

import org.smartbit4all.api.contribution.PrimaryApiImpl;

/**
 * The generic implementation of the {@link ACLManagementApi}.
 * 
 * @author Peter Boros
 */
public class ACLManagementApiImpl extends PrimaryApiImpl<ACLContributionApi>
    implements ACLManagementApi {

  public ACLManagementApiImpl() {
    super(ACLContributionApi.class);
  }

}
