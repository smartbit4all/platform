package org.smartbit4all.api.view.geomap;


import org.checkerframework.checker.units.qual.A;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.geomap.bean.GeoMapDataSourceDescriptor;
import org.smartbit4all.api.geomap.bean.GeoMapModel;
import org.smartbit4all.api.geomap.bean.GeoMapViewState;
import org.smartbit4all.api.geomap.bean.GeoMapViewport;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

public class GeoMapApiImpl implements GeoMapApi {

  @Autowired
  private ObjectApi objectApi;
  @Autowired
  private CollectionApi collectionApi;

  @Override
  public GeoMapModel createMapModel(GeoMapViewport viewport, GeoMapViewState initialState) {
    return null;
  }

  @Override
  public void initMapInView(UUID viewUuid, String mapId, GeoMapModel mapModel) {

  }

  @Override
  public void addDataSource(
      UUID viewUuid,
      String mapId,
      GeoMapDataSourceDescriptor dataSourceDescriptor) {
  }

  @Override
  public void refreshMap(UUID viewUuid, String mapId) {

  }

  @Override
  public <T> T executeMapCall(UUID viewUuid, String gridId, Function<GridModel, T> mapCall) {
    return null;
  }
}
