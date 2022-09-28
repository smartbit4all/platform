package org.smartbit4all.api.object;

import java.net.URI;
import java.util.Map;
import org.smartbit4all.api.contribution.PrimaryApi;
import org.smartbit4all.core.object.ObjectDefinition;

/**
 * The modification operations and services for the domain objects.
 * 
 * @author Peter Boros
 */
public interface ModifyApi extends PrimaryApi<ModifyContributionApi> {

  URI createNewObject(ObjectDefinition<?> objectDefinition, String storageScheme,
      Map<String, Object> objMap,
      URI branchUri);

  URI updateObject(URI objVersionUri, Map<String, Object> objMap, URI branchUri);

}
