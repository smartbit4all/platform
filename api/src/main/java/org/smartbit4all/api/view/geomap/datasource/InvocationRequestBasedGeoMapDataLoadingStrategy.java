package org.smartbit4all.api.view.geomap.datasource;

import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.geomap.bean.GeoMapDataSourceDescriptor;
import org.smartbit4all.api.geomap.bean.GeoMapItem;
import org.smartbit4all.api.geomap.bean.GeoMapViewport;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.core.object.ObjectApi;

final class InvocationRequestBasedGeoMapDataLoadingStrategy extends GeoMapDataLoadingStrategy {

  private static final Logger log =
      LoggerFactory.getLogger(InvocationRequestBasedGeoMapDataLoadingStrategy.class);

  private final ObjectApi objectApi;
  private final InvocationApi invocationApi;

  InvocationRequestBasedGeoMapDataLoadingStrategy(
      GeoMapDataSourceDescriptor dataSourceDescriptor,
      ObjectApi objectApi,
      InvocationApi invocationApi) {
    super(dataSourceDescriptor);

    this.objectApi = objectApi;
    this.invocationApi = invocationApi;
  }

  @Override
  public List<GeoMapItem> load(GeoMapViewport viewport) {
    final InvocationRequest request = dataSourceDescriptor.getInvocationRequest();
    try {
      final InvocationParameter res = invocationApi.invoke(request, viewport);
      final Object o = res.getValue();
      if (o instanceof List<?>) {
        final List<?> list = (List<?>) o;
        return objectApi.asList(GeoMapItem.class, list);
      } else {
        return Collections.emptyList();
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return Collections.emptyList();
    }
  }
}
