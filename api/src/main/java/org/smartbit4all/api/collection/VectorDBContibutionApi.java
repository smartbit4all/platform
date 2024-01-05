package org.smartbit4all.api.collection;

import java.util.List;
import org.smartbit4all.api.collection.bean.VectorSearchResultItem;
import org.smartbit4all.api.collection.bean.VectorValue;
import org.smartbit4all.api.contribution.ContributionApi;
import org.smartbit4all.api.invocation.bean.ServiceConnection;

public interface VectorDBContibutionApi extends ContributionApi {

  String addPoint(ServiceConnection dbConnection, String collectionName, String id,
      VectorValue value);

  // String addPoints(ServiceConnection dbConnection, String collectionName, String id,
  // VectorValue value);

  String deletePoint(ServiceConnection dbConnection, String collectionName, String id);

  String deletePoints(ServiceConnection dbConnection, String collectionName, List<String> ids);

  String createCollection(ServiceConnection dbConnection, String name);

  String deleteCollection(ServiceConnection dbConnection, String name);

  boolean collectionExists(ServiceConnection dbConnection, String name);

  List<VectorSearchResultItem> search(ServiceConnection dbConnection, String collectionName,
      VectorValue searchVector, int limit);

}
