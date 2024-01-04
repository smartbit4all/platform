package org.smartbit4all.api.collection;

import java.util.List;
import org.smartbit4all.api.collection.bean.VectorSearchResultItem;
import org.smartbit4all.api.collection.bean.VectorValue;
import org.smartbit4all.api.contribution.PrimaryApiImpl;
import org.smartbit4all.api.invocation.bean.ServiceConnection;

public final class VectorDDBApiImpl extends PrimaryApiImpl<VectorDBContibutionApi>
    implements VectorDBApi {

  public VectorDDBApiImpl() {
    super(VectorDBContibutionApi.class);
  }

  @Override
  public String addPoint(ServiceConnection dbConnection, String collectionName, String id,
      VectorValue value) {
    return getContributionApi(dbConnection.getApiName()).addPoint(dbConnection, collectionName,
        id, value);
  }

  @Override
  public String createCollection(ServiceConnection dbConnection, String name) {
    return getContributionApi(dbConnection.getApiName()).createCollection(dbConnection, name);
  }

  @Override
  public String deleteCollection(ServiceConnection dbConnection, String name) {
    return getContributionApi(dbConnection.getApiName()).deleteCollection(dbConnection, name);
  }

  @Override
  public boolean collectionExists(ServiceConnection dbConnection, String name) {
    return getContributionApi(dbConnection.getApiName()).collectionExists(dbConnection, name);
  }

  @Override
  public List<VectorSearchResultItem> search(ServiceConnection dbConnection, String collectionName,
      VectorValue searchVector, int limit) {
    return getContributionApi(dbConnection.getApiName()).search(dbConnection, collectionName,
        searchVector, limit);
  }

}
