package org.smartbit4all.api.object;

import java.net.URI;
import java.util.Map;
import org.smartbit4all.api.contribution.ContributionApi;
import org.smartbit4all.core.object.ObjectDefinition;

/**
 * This api is responsible for manipulating objects and provide algorithms about modification.
 * 
 * @author Peter Boros
 */
public interface ModifyContributionApi extends ContributionApi {

  URI saveAsNew(ObjectDefinition<?> objectDefinition, String storageScheme,
      Map<String, Object> objectAsMap);

  URI update(URI versionUri, Map<String, Object> objectAsMap);

}
