package org.smartbit4all.api.object;

import java.net.URI;
import java.util.Map;
import org.smartbit4all.api.contribution.PrimaryApiImpl;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.springframework.beans.factory.annotation.Autowired;

public class ModifyApiImpl extends PrimaryApiImpl<ModifyContributionApi>
    implements ModifyApi {

  @Autowired
  private ModifyContributionApiStorageImpl contributionApiImplStorage;

  @Autowired
  ObjectApi objectApi;

  public ModifyApiImpl() {
    super(ModifyContributionApi.class);
  }

  private ModifyContributionApi getContributionApi(ObjectDefinition<?> objectDefinition) {
    if (objectDefinition == null) {
      return contributionApiImplStorage;
    }
    ModifyContributionApi api = getContributionApi(objectDefinition.getQualifiedName());
    return api == null ? contributionApiImplStorage : api;
  }

  @Override
  public URI createNewObject(ObjectDefinition<?> objectDefinition, String storageScheme,
      Map<String, Object> objMap) {
    ModifyContributionApi contributionApi = getContributionApi(objectDefinition);
    return contributionApi.saveAsNew(objectDefinition, storageScheme, objMap);
  }

  @Override
  public URI updateObject(ObjectDefinition<?> objectDefinition, URI objVersionUri,
      Map<String, Object> objMap) {
    ModifyContributionApi contributionApi = getContributionApi(objectDefinition);
    return contributionApi.update(objVersionUri, objMap);
  }


}
