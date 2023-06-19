package org.smartbit4all.core.object;

import java.net.URI;
import org.smartbit4all.api.object.bean.ObjectDefinitionData;
import org.smartbit4all.api.object.bean.PropertyDefinitionData;

/**
 * The api that contains the provided api calls for the {@link ObjectDefinitionData} and
 * {@link PropertyDefinitionData}.
 */
public interface ObjectDefinitionProvidedApi {

  static String constructUriFromQualifiedName = "constructUriFromQualifiedName";

  URI constructUriFromQualifiedName(ObjectDefinitionData objectDefinitionData);

  static String synchronizeOutgoingReferences = "synchronizeOutgoingReferences";

  ObjectDefinitionData synchronizeOutgoingReferences(ObjectDefinitionData objectDefinitionData);

}
