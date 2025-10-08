package org.smartbit4all.api.collection;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.bean.VectorSearchResultItem;
import org.smartbit4all.api.collection.bean.VectorValue;
import org.smartbit4all.api.contribution.PrimaryApiImpl;
import org.smartbit4all.api.invocation.bean.ServiceConnection;

public final class VectorDBApiImpl extends PrimaryApiImpl<VectorDBContributionApi>
    implements VectorDBApi {

  public VectorDBApiImpl() {
    super(VectorDBContributionApi.class);
  }

  private static final Logger log = LoggerFactory.getLogger(VectorDBApiImpl.class);

  @Override
  public String addPoint(ServiceConnection dbConnection, String collectionName,
      VectorValue value) {
    Objects.requireNonNull(dbConnection);
    VectorDBContributionApi contributionApi = getContributionApi(dbConnection.getApiName());
    Objects.requireNonNull(contributionApi);
    return contributionApi.addPoint(dbConnection, collectionName,
        value);
  }

  @Override
  public List<String> addPoints(ServiceConnection dbConnection, String collectionName,
      List<VectorValue> values) {
    Objects.requireNonNull(dbConnection);
    VectorDBContributionApi contributionApi = getContributionApi(dbConnection.getApiName());
    Objects.requireNonNull(contributionApi);
    return contributionApi.addPoints(dbConnection, collectionName,
        values);
  }

  @Override
  public void deletePoint(ServiceConnection dbConnection, String collectionName, String id) {
    Objects.requireNonNull(dbConnection);
    VectorDBContributionApi contributionApi = getContributionApi(dbConnection.getApiName());
    Objects.requireNonNull(contributionApi);
    contributionApi.deletePoint(dbConnection, collectionName, id);
  }

  @Override
  public void deletePoints(ServiceConnection dbConnection, String collectionName,
      List<String> ids) {
    Objects.requireNonNull(dbConnection);
    VectorDBContributionApi contributionApi = getContributionApi(dbConnection.getApiName());
    Objects.requireNonNull(contributionApi);
    contributionApi.deletePoints(dbConnection, collectionName, ids);
  }

  @Override
  public void createCollection(ServiceConnection dbConnection, String name) {
    Objects.requireNonNull(dbConnection);
    VectorDBContributionApi contributionApi = getContributionApi(dbConnection.getApiName());
    Objects.requireNonNull(contributionApi);
    contributionApi.createCollection(dbConnection, name);
  }

  @Override
  public void deleteCollection(ServiceConnection dbConnection, String name) {
    Objects.requireNonNull(dbConnection);
    VectorDBContributionApi contributionApi = getContributionApi(dbConnection.getApiName());
    Objects.requireNonNull(contributionApi);
    contributionApi.deleteCollection(dbConnection, name);
  }

  @Override
  public boolean collectionExists(ServiceConnection dbConnection, String name) {
    Objects.requireNonNull(dbConnection);
    VectorDBContributionApi contributionApi = getContributionApi(dbConnection.getApiName());
    Objects.requireNonNull(contributionApi);
    return contributionApi.collectionExists(dbConnection, name);
  }

  @Override
  public List<VectorSearchResultItem> search(ServiceConnection dbConnection, String collectionName,
      VectorValue searchVector, int limit, Map<String, Object> parameters) {
    Objects.requireNonNull(dbConnection);
    VectorDBContributionApi contributionApi = getContributionApi(dbConnection.getApiName());
    Objects.requireNonNull(contributionApi);
    return contributionApi.search(dbConnection, collectionName,
        searchVector, limit, parameters);
  }

}
