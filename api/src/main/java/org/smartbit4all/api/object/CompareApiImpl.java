package org.smartbit4all.api.object;

import java.net.URI;
import org.smartbit4all.api.contribution.PrimaryApiImpl;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.springframework.beans.factory.annotation.Autowired;

public class CompareApiImpl extends PrimaryApiImpl<CompareContributionApi>
    implements CompareApi {

  public CompareApiImpl(Class<CompareContributionApi> innerApiClass) {
    super(innerApiClass);
  }

  @Autowired
  private CompareContributionApiStorageImpl compareContributionApiImplStorage;

  @Autowired
  ObjectApi objectApi;

  private CompareContributionApi getContributionApi(URI uri) {
    ObjectDefinition<?> definition = objectApi.definition(uri);
    if (definition == null) {
      return compareContributionApiImplStorage;
    }
    CompareContributionApi api = getContributionApi(definition.getQualifiedName());
    return api == null ? compareContributionApiImplStorage : api;
  }

  @Override
  public boolean deepEquals(URI uri, URI prevUri) {
    return getContributionApi(uri == null ? prevUri : uri).deepEquals(uri, prevUri);
  }

}
