package org.smartbit4all.api.collection;

import org.smartbit4all.api.contribution.PrimaryApiImpl;

public class VectorDbManagementApiImpl extends PrimaryApiImpl<VectorDbApi>
    implements VectorDbManagementApi {

  public VectorDbManagementApiImpl() {
    super(VectorDbApi.class);
  }

}
