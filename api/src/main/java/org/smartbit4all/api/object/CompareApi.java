package org.smartbit4all.api.object;

import java.net.URI;
import org.smartbit4all.api.contribution.PrimaryApi;

/**
 * The compare api is responsible for comparing the domain objects with different algorithms. There
 * are functions for deep equals and many more.
 * 
 * @author Peter Boros
 */
public interface CompareApi extends PrimaryApi<CompareContributionApi> {

  boolean deepEquals(URI uri, URI prevUri);

}
