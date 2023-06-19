package org.smartbit4all.core.object;

import java.net.URI;
import org.smartbit4all.api.object.bean.ObjectDefinitionData;
import org.springframework.beans.factory.annotation.Autowired;

public class ObjectDefinitionProvidedApiImpl implements ObjectDefinitionProvidedApi {

  @Autowired
  private ObjectDefinitionApi objectDefinitionApi;

  @Override
  public URI constructUriFromQualifiedName(ObjectDefinitionData objectDefinitionData) {
    return ObjectDefinition.uriOf(objectDefinitionData.getQualifiedName());
  }

  @Override
  public ObjectDefinitionData synchronizeOutgoingReferences(
      ObjectDefinitionData objectDefinitionData) {
    return ObjectDefinitionApiImpl.synchronizeOutgoingReferences(objectDefinitionData);
  }

}
