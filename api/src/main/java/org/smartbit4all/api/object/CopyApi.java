package org.smartbit4all.api.object;

import java.net.URI;
import org.smartbit4all.api.contribution.PrimaryApi;

/**
 * The copy operations and services for the domain objects.
 * 
 * @author Peter Boros
 */
public interface CopyApi extends PrimaryApi<CopyContributionApi> {

  /**
   * Constructs a brand new object based on the URI. It uses the contained references downward. All
   * the contained referred objects will be copied and set.
   * 
   * @param rootObject The original object that is the root of the operation.
   * @return The result will have a URI (it's a new instance) and all the references will be
   *         replaced with copied new instances.
   */
  URI deepCopyByContainment(URI rootObject);

}
