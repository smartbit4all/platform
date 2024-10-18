package org.smartbit4all.api.view.geomap;

import org.smartbit4all.api.geomap.bean.GeoMapDataSourceDescriptor;
import org.smartbit4all.api.geomap.bean.GeoMapModel;
import org.smartbit4all.api.geomap.bean.GeoMapViewState;
import org.smartbit4all.api.geomap.bean.GeoMapViewport;
import org.smartbit4all.api.grid.bean.GridModel;

import java.util.UUID;
import java.util.function.Function;

/**
 * 
 * @author Szabolcs Bazil Papp
 *
 */
public interface GeoMapApi {

  String LAYER_DEFAULT = "default";

  GeoMapModel createMapModel(final GeoMapViewport viewport, final GeoMapViewState initialState);

  void initMapInView(final UUID viewUuid, final String mapId, final GeoMapModel mapModel);

  void addDataSource(
      final UUID viewUuid,
      final String mapId,
      final GeoMapDataSourceDescriptor dataSourceDescriptor);

  void refreshMap(final UUID viewUuid, String mapId);

  <T> T executeMapCall(UUID viewUuid, String gridId, Function<GridModel, T> mapCall);

  // TODO: Selection and its callbacks!

}
