package org.smartbit4all.api.view.geomap.datasource;

import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.FilterExpressionApi;
import org.smartbit4all.api.geomap.bean.GeoMapDataSourceDescriptor;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;

public class GeoMapDataLoadingStrategyFactoryImpl implements GeoMapDataLoadingStrategyFactory {

  @Autowired
  private ObjectApi           objectApi;
  @Autowired
  private CollectionApi       collectionApi;
  @Autowired
  private FilterExpressionApi filterExpressionApi;
  @Autowired
  private InvocationApi       invocationApi;

  @Override
  public GeoMapDataLoadingStrategy create(GeoMapDataSourceDescriptor dataSourceDescriptor) {
    if (dataSourceDescriptor == null || dataSourceDescriptor.getSourceType() == null) {
      throw new IllegalArgumentException(
          "dataSourceDescriptor has no source type: " + dataSourceDescriptor);
    }

    switch (dataSourceDescriptor.getSourceType()) {
      case STORED_COLLECTION:
        return new StoredCollectionBasedGeoMapDataLoadingStrategy(
            dataSourceDescriptor,
            objectApi,
            collectionApi,
            invocationApi);
      case SEARCH_INDEX:
        return new SearchIndexBasedGeoMapDataLoadingStrategy(
            dataSourceDescriptor,
            objectApi,
            collectionApi,
            filterExpressionApi);
      case INVOCATION_REQUEST:
        return new InvocationRequestBasedGeoMapDataLoadingStrategy(
            dataSourceDescriptor,
            objectApi,
            invocationApi);
      default:
        throw new AssertionError("Unknown data source type:" + dataSourceDescriptor);
    }
  }
}
