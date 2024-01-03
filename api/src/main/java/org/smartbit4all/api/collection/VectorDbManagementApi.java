package org.smartbit4all.api.collection;

import org.smartbit4all.api.contribution.PrimaryApi;

public interface VectorDbManagementApi extends PrimaryApi<VectorDbApi> {

  static final String VECTOR_DB_TYPE_QDRANT = "qdrant";

}
