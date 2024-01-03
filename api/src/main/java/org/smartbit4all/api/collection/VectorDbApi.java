package org.smartbit4all.api.collection;

import java.util.List;
import java.util.Map;
import org.smartbit4all.api.contribution.ContributionApi;
import org.smartbit4all.api.invocation.bean.ServiceConnection;

public interface VectorDbApi extends ContributionApi {

  String addPoint(String collectionName, List<Float> vector, Map<String, Object> payload,
      ServiceConnection dbConnection);

  String createCollection(String name, ServiceConnection dbConnection);

  String deleteCollection(String name, ServiceConnection dbConnection);

  boolean collectionExists(String name, ServiceConnection dbConnection);

  String search(String collectionName, List<Float> searchVector, ServiceConnection dbConnection);

}
