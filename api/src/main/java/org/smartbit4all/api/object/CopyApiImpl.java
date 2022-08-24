package org.smartbit4all.api.object;

import java.net.URI;
import org.smartbit4all.api.contribution.PrimaryApiImpl;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.springframework.beans.factory.annotation.Autowired;

public class CopyApiImpl extends PrimaryApiImpl<CopyContributionApi>
    implements CopyApi {

  @Autowired
  private CopyContributionApiStorageImpl copyContributionApiImplStorage;

  @Autowired
  ObjectApi objectApi;

  public CopyApiImpl(Class<CopyContributionApi> innerApiClass) {
    super(innerApiClass);
  }

  private CopyContributionApi getContributionApi(URI uri) {
    ObjectDefinition<?> definition = objectApi.definition(uri);
    if (definition == null) {
      return copyContributionApiImplStorage;
    }
    CopyContributionApi api = getContributionApi(definition.getQualifiedName());
    return api == null ? copyContributionApiImplStorage : api;
  }

  @Override
  public URI deepCopyByContainment(URI rootObject) {
    if (rootObject == null) {
      return null;
    }
    return getContributionApi(rootObject).deepCopy(rootObject);
  }

}
