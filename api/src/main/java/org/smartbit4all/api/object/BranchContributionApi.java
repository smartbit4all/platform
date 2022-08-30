package org.smartbit4all.api.object;

import java.net.URI;
import org.smartbit4all.api.contribution.ContributionApi;

/**
 * This api is responsible for manipulating objects and provide algorithms about copying, comparing
 * and so on.
 * 
 * @author Peter Boros
 */
public interface BranchContributionApi extends ContributionApi {

  /**
   * Initiate a new object from the original object version identified by the URI.
   * 
   * @param versionUri The version URI of the source object.
   * @return The version URI of the newly created or the existing branched object.
   */
  URI saveAsNew(URI versionUri);

}
