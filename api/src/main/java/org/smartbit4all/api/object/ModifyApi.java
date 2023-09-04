package org.smartbit4all.api.object;

import java.net.URI;
import org.smartbit4all.api.contribution.PrimaryApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectNode;

/**
 * The modification operations and services for the domain objects.
 * 
 * @author Peter Boros
 */
public interface ModifyApi extends PrimaryApi<ModifyContributionApi> {

  URI createNewObject(ObjectDefinition<?> objectDefinition, String storageScheme,
      ObjectNode objectNode);

  URI updateObject(ObjectDefinition<?> objectDefinition, URI objVersionUri,
      ObjectNode objectNode);

}
