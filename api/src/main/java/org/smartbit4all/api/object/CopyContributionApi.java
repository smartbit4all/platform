package org.smartbit4all.api.object;

import java.net.URI;
import org.smartbit4all.api.contribution.ContributionApi;

/**
 * This api is responsible for manipulating objects and provide algorithms about copying, comparing
 * and so on.
 * 
 * @author Peter Boros
 */
public interface CopyContributionApi extends ContributionApi {

  /**
   * Constructs a brand new object based on the URI.
   * 
   * @param rootObject The original object that is the root of the operation.
   * @return The result will have a URI (it's a new instance) and all the references will be
   *         replaced with copied new instances.
   */
  URI deepCopy(URI rootObject);


}
