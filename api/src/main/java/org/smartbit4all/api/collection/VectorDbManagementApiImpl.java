package org.smartbit4all.api.collection;

import java.util.List;
import org.smartbit4all.api.collection.bean.VectorSearchResultItem;
import org.smartbit4all.api.collection.bean.VectorValue;
import org.smartbit4all.api.contribution.PrimaryApiImpl;
import org.smartbit4all.api.invocation.bean.ServiceConnection;

public class VectorDbManagementApiImpl extends PrimaryApiImpl<VectorDbApi>
    implements VectorDbManagementApi {

  public VectorDbManagementApiImpl() {
    super(VectorDbApi.class);
  }

  @Override
  public String addPoint(ServiceConnection dbConnection, String collectionName, VectorValue value) {
    return getContributionApi(dbConnection.getApiName()).addPoint(dbConnection, collectionName,
        value);
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
