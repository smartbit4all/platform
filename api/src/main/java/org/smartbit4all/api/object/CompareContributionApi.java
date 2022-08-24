package org.smartbit4all.api.object;

import java.net.URI;
import org.smartbit4all.api.contribution.ContributionApi;

/**
 * This api is responsible for manipulating objects and provide algorithms about copying, comparing
 * and so on.
 * 
 * @author Peter Boros
 */
public interface CompareContributionApi extends ContributionApi {

  boolean deepEquals(URI uri, URI prevUri);

}
