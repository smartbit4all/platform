package org.smartbit4all.api.collection;

import java.io.IOException;
import java.util.List;
import org.smartbit4all.api.collection.bean.VectorSearchResultItem;
import org.smartbit4all.api.collection.bean.VectorValue;
import org.smartbit4all.api.contribution.PrimaryApi;
import org.smartbit4all.api.invocation.bean.ServiceConnection;

public interface VectorDBApi extends PrimaryApi<VectorDBContibutionApi> {

  static final String VECTOR_DB_TYPE_QDRANT = "qdrant";

  String addPoint(ServiceConnection dbConnection, String collectionName,
      VectorValue value) throws IOException;

  List<String> addPoints(ServiceConnection dbConnection, String collectionName,
      List<VectorValue> values) throws IOException;

  void deletePoint(ServiceConnection dbConnection, String collectionName, String id)
      throws IOException;

  void deletePoints(ServiceConnection dbConnection, String collectionName, List<String> ids)
      throws IOException;

  void createCollection(ServiceConnection dbConnection, String name) throws IOException;

  void deleteCollection(ServiceConnection dbConnection, String name) throws IOException;

  boolean collectionExists(ServiceConnection dbConnection, String name) throws IOException;

  List<VectorSearchResultItem> search(ServiceConnection dbConnection, String collectionName,
      VectorValue searchVector, int limit) throws IOException;

}
