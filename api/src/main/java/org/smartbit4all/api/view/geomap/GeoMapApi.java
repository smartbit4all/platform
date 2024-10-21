package org.smartbit4all.api.view.geomap;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import org.smartbit4all.api.geomap.bean.GeoMapChange;
import org.smartbit4all.api.geomap.bean.GeoMapDataSourceDescriptor;
import org.smartbit4all.api.geomap.bean.GeoMapInteraction;
import org.smartbit4all.api.geomap.bean.GeoMapItem;
import org.smartbit4all.api.geomap.bean.GeoMapModel;
import org.smartbit4all.api.geomap.bean.GeoMapViewState;
import org.smartbit4all.api.geomap.bean.GeoMapViewport;
import org.smartbit4all.api.invocation.bean.InvocationRequest;

/**
 * 
 * @author Szabolcs Bazil Papp
 *
 */
public interface GeoMapApi {

  String LAYER_DEFAULT = "default";

  // -----------------------------------------------------------------------------------------------
  // INITIALISATION

  GeoMapModel createMapModel(final GeoMapViewport viewport, final GeoMapViewState initialState);

  void initMapInView(final UUID viewUuid, final String mapId, final GeoMapModel mapModel);

  void addDataSource(
      final UUID viewUuid,
      final String mapId,
      final GeoMapDataSourceDescriptor dataSourceDescriptor);

  void clearPendingItems(final UUID viewUuid, final String mapId);

  // -----------------------------------------------------------------------------------------------
  // CLIENT FACING BEHAVIOUR

  GeoMapChange refreshMap(final UUID viewUuid, String mapId);

  <T> T executeMapCall(UUID viewUuid, String mapId, Function<GeoMapModel, T> mapCall);

  void interact(final UUID viewUuid, final String mapId, final GeoMapInteraction interaction);

  void selectItem(UUID viewUuid, String mapId, String layerId, String itemId, boolean select);

  // -----------------------------------------------------------------------------------------------
  // CALLBACKS

  void addLayerCallback(UUID viewUuid, String mapId,
      InvocationRequest onLayerAdded,
      InvocationRequest onLayerRemoved);

  void addItemCallback(UUID viewUuid, String mapId, InvocationRequest onItemAdded);

  void addSelectionChangeListener(UUID viewUuid, String mapId,
      InvocationRequest onSelectionChanged);

  void addMapItemPlacementListener(UUID viewUuid, String mapId, InvocationRequest onMapItemPlaced);


  // -----------------------------------------------------------------------------------------------
  // DATA_ACCESS

  List<GeoMapItem> getSelectedItems(UUID viewUuid, String mapId);

  List<GeoMapItem> getPendingItems(UUID viewUuid, String mapId);

}
