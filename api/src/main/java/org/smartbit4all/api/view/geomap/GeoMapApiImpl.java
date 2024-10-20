package org.smartbit4all.api.view.geomap;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.FilterExpressionApi;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.collection.StoredList;
import org.smartbit4all.api.collection.StoredMap;
import org.smartbit4all.api.collection.bean.StoredCollectionDescriptor;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionList;
import org.smartbit4all.api.geomap.bean.GPSPosition;
import org.smartbit4all.api.geomap.bean.GeoMapChange;
import org.smartbit4all.api.geomap.bean.GeoMapDataSourceDescriptor;
import org.smartbit4all.api.geomap.bean.GeoMapItem;
import org.smartbit4all.api.geomap.bean.GeoMapItemKind;
import org.smartbit4all.api.geomap.bean.GeoMapLayer;
import org.smartbit4all.api.geomap.bean.GeoMapLayerChange;
import org.smartbit4all.api.geomap.bean.GeoMapLayerDescriptor;
import org.smartbit4all.api.geomap.bean.GeoMapModel;
import org.smartbit4all.api.geomap.bean.GeoMapSelectionMode;
import org.smartbit4all.api.geomap.bean.GeoMapServerModel;
import org.smartbit4all.api.geomap.bean.GeoMapViewState;
import org.smartbit4all.api.geomap.bean.GeoMapViewport;
import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.view.ViewApi;
import org.smartbit4all.api.view.geomap.datasource.GeoMapDataLoadingStrategyFactory;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.service.query.QueryInput;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class GeoMapApiImpl implements GeoMapApi {

  private static final Logger log = LoggerFactory.getLogger(GeoMapApiImpl.class);

  @Autowired
  private ViewApi             viewApi;
  @Autowired
  private InvocationApi       invocationApi;
  @Autowired
  private GeoMapDataLoadingStrategyFactory dataLoadingStrategyFactory;

  @Override
  public GeoMapModel createMapModel(GeoMapViewport viewport, GeoMapViewState initialState) {
    Objects.requireNonNull(viewport, "viewport cannot be null!");
    if (initialState == null) {
      initialState = new GeoMapViewState();
    }

    if (initialState.getSelectedLayers() == null || initialState.getSelectedLayers().isEmpty()) {
      initialState.addSelectedLayersItem(LAYER_DEFAULT);
    }

    if (initialState.getLayerDescriptors() == null
        || initialState.getLayerDescriptors().isEmpty()) {
      initialState.addLayerDescriptorsItem(new GeoMapLayerDescriptor()
          .code(LAYER_DEFAULT)
          .selectionMode(GeoMapSelectionMode.NONE)
          .preserveSelection(false));
    }

    return new GeoMapModel()
        .viewport(viewport)
        .viewState(initialState);
  }

  @Override
  public void initMapInView(UUID viewUuid, String mapId, GeoMapModel mapModel) {
    Objects.requireNonNull(viewUuid, "viewUuid cannot be null!");
    Objects.requireNonNull(mapId, "mapId cannot be null!");
    Objects.requireNonNull(mapModel, "mapModel cannot be null!");

    mapModel.setViewUuid(viewUuid);
    mapModel.setIdentifier(mapId);

    viewApi.setWidgetModelInView(GeoMapModel.class, viewUuid, mapId, mapModel);
    viewApi.setWidgetServerModelInView(GeoMapServerModel.class, viewUuid, mapId,
        new GeoMapServerModel().selectedLayers(new ArrayList<>(mapModel
            .getViewState()
            .getSelectedLayers())));
  }

  @Override
  public void addDataSource(
      UUID viewUuid,
      String mapId,
      GeoMapDataSourceDescriptor dataSourceDescriptor) {
    Objects.requireNonNull(viewUuid, "viewUuid cannot be null!");
    Objects.requireNonNull(mapId, "mapId cannot be null!");
    Objects.requireNonNull(dataSourceDescriptor, "dataSourceDescriptor cannot be null!");

    final GeoMapServerModel serverModel = getServerModel(viewUuid, mapId);
    serverModel.putDataSourcesItem(dataSourceDescriptor.getId(), dataSourceDescriptor);
    setServerModel(viewUuid, mapId, serverModel);
  }

  @Override
  public GeoMapChange refreshMap(UUID viewUuid, String mapId) {
    return executeMapCall(viewUuid, mapId, model -> {
      // THIS IS A VERY NAIVE, RUDIMENTARY IMPLEMENTATION:
      final GeoMapServerModel serverModel = getServerModel(viewUuid, mapId);
      final List<String> selectedLayers = model.getViewState().getSelectedLayers();
      final GeoMapViewport viewport = model.getViewport();
      final Map<String, GeoMapLayer> layersToShow = fetchData(
          new HashSet<>(selectedLayers),
          serverModel,
          viewport);
      model.setLayers(new ArrayList<>(layersToShow.values()));
      return layersToShow.values().stream()
          .map(it -> new GeoMapLayerChange()
              .code(it.getCode())
              .toAdd(it.getItems()))
          .collect(collectingAndThen(toList(), layerChanges -> new GeoMapChange()
                  .code(mapId)
                  .items(layerChanges)));
    });
  }

  private Map<String, GeoMapLayer> fetchData(
      final Set<String> selectedLayers,
      final GeoMapServerModel serverModel,
      final GeoMapViewport viewport) {
    return serverModel.getDataSources().values().stream()
        .filter(src -> selectedLayers.contains(src.getTargetLayer()))
        .collect(groupingBy(GeoMapDataSourceDescriptor::getTargetLayer))
        .entrySet().stream()
        .map(e -> new Pair<>(e.getKey(), e.getValue()))
        .map(it -> new Pair<>(it.a, it.b.stream()
            .flatMap(src -> loadDataSource(src, viewport).stream())
            .collect(toList())))
        .map(it -> new Pair<>(it.a, new GeoMapLayer().code(it.a).items(it.b)))
        .collect(toMap(it -> it.a, it -> it.b));
  }

  private List<GeoMapItem> loadDataSource(
      final GeoMapDataSourceDescriptor dataSourceDescriptor,
      final GeoMapViewport viewport) {
    return dataLoadingStrategyFactory.create(dataSourceDescriptor).load(viewport);
  }

  @Override
  public <T> T executeMapCall(UUID viewUuid, String mapId, Function<GeoMapModel, T> mapCall) {
    GeoMapModel model = getModel(viewUuid, mapId);
    if (model == null) {
      // view exists, geoMap doesn't -> try to initialize model
      viewApi.getModel(viewUuid, null);
      model = getModel(viewUuid, mapId);
    }

    if (model == null) {
      return null;
    }

    model.setViewUuid(viewUuid);
    model.setIdentifier(mapId);
    final T result = mapCall.apply(model);
    setModel(viewUuid, mapId, model);
    return result;
  }

  private GeoMapModel getModel(UUID viewUuid, String mapId) {
    return viewApi.getWidgetModelFromView(GeoMapModel.class, viewUuid, mapId);
  }

  private void setModel(UUID viewUuid, String mapId, GeoMapModel mapModel) {
    viewApi.setWidgetModelInView(GeoMapModel.class, viewUuid, mapId, mapModel);
  }

  private GeoMapServerModel getServerModel(UUID viewUuid, String mapId) {
    return viewApi.getWidgetServerModelFromView(GeoMapServerModel.class, viewUuid, mapId);
  }

  private void setServerModel(UUID viewUuid, String mapId, GeoMapServerModel serverModel) {
    viewApi.setWidgetServerModelInView(GeoMapServerModel.class, viewUuid, mapId, serverModel);
  }

  private static final class Pair<A, B> {
    private final A a;
    private final B b;

    private Pair(A a, B b) {
      this.a = a;
      this.b = b;
    }
  }

}
