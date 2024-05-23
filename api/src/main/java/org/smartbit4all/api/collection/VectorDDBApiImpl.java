package org.smartbit4all.api.collection;

import java.io.IOException;
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
  public String addPoint(ServiceConnection dbConnection, String collectionName,
      VectorValue value) throws IOException {
    return getContributionApi(dbConnection.getApiName()).addPoint(dbConnection, collectionName,
        value);
  }

  @Override
  public List<String> addPoints(ServiceConnection dbConnection, String collectionName,
      List<VectorValue> values) throws IOException {
    return getContributionApi(dbConnection.getApiName()).addPoints(dbConnection, collectionName,
        values);
  }

  @Override
  public void deletePoint(ServiceConnection dbConnection, String collectionName, String id)
      throws IOException {
    getContributionApi(dbConnection.getApiName()).deletePoint(dbConnection, collectionName,
        id);
  }

  @Override
  public void deletePoints(ServiceConnection dbConnection, String collectionName,
      List<String> ids) throws IOException {
    getContributionApi(dbConnection.getApiName()).deletePoints(dbConnection, collectionName,
        ids);
  }

  @Override
  public void createCollection(ServiceConnection dbConnection, String name) throws IOException {
    getContributionApi(dbConnection.getApiName()).createCollection(dbConnection, name);
  }

  @Override
  public void deleteCollection(ServiceConnection dbConnection, String name) throws IOException {
    getContributionApi(dbConnection.getApiName()).deleteCollection(dbConnection, name);
  }

  @Override
  public boolean collectionExists(ServiceConnection dbConnection, String name) throws IOException {
    return getContributionApi(dbConnection.getApiName()).collectionExists(dbConnection, name);
  }

  @Override
  public List<VectorSearchResultItem> search(ServiceConnection dbConnection, String collectionName,
      VectorValue searchVector, int limit) throws IOException {
    return getContributionApi(dbConnection.getApiName()).search(dbConnection, collectionName,
        searchVector, limit);
  }

}
