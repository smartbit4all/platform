package org.smartbit4all.api.object;

import java.net.URI;
import org.smartbit4all.api.contribution.ContributionApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectNode;

/**
 * This api is responsible for manipulating objects and provide algorithms about modification.
 *
 * @author Peter Boros
 */
public interface ModifyContributionApi extends ContributionApi {

  URI saveAsNew(ObjectDefinition<?> objectDefinition, String storageScheme,
      ObjectNode objectNode);

  URI update(URI versionUri, ObjectNode objectNode);

}
