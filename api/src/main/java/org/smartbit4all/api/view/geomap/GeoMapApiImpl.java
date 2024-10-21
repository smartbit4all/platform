package org.smartbit4all.api.view.geomap;


import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.geomap.bean.GeoMapChange;
import org.smartbit4all.api.geomap.bean.GeoMapDataSourceDescriptor;
import org.smartbit4all.api.geomap.bean.GeoMapInteraction;
import org.smartbit4all.api.geomap.bean.GeoMapItem;
import org.smartbit4all.api.geomap.bean.GeoMapLayer;
import org.smartbit4all.api.geomap.bean.GeoMapLayerChange;
import org.smartbit4all.api.geomap.bean.GeoMapLayerDescriptor;
import org.smartbit4all.api.geomap.bean.GeoMapModel;
import org.smartbit4all.api.geomap.bean.GeoMapSelectionMode;
import org.smartbit4all.api.geomap.bean.GeoMapServerModel;
import org.smartbit4all.api.geomap.bean.GeoMapViewState;
import org.smartbit4all.api.geomap.bean.GeoMapViewport;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.view.ViewApi;
import org.smartbit4all.api.view.WidgetCallbackApi;
import org.smartbit4all.api.view.geomap.datasource.GeoMapDataLoadingStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.base.Strings;

public class GeoMapApiImpl implements GeoMapApi {

  private static final Logger log = LoggerFactory.getLogger(GeoMapApiImpl.class);

  private static final String POSTFIX_LAYER_ADDED = "_mapLayerAddedCallback";
  private static final String POSTFIX_LAYER_REMOVED = "_mapLayerRemovedCallback";
  private static final String POSTFIX_ITEM = "_mapItemAddedCallback";
  private static final String POSTFIX_SELECTION = "_mapSelectionChangedCallback";
  private static final String POSTFIX_PLACEMENT = "_mapPlacementChangedCallback";

  @Autowired
  private ViewApi viewApi;
  @Autowired
  private WidgetCallbackApi widgetCallbackApi;
  @Autowired
  private GeoMapDataLoadingStrategyFactory dataLoadingStrategyFactory;

  @Override
  public GeoMapModel createMapModel(GeoMapViewport viewport, GeoMapViewState initialState) {
    Objects.requireNonNull(viewport, "viewport cannot be null!");
    if (initialState == null) {
      initialState = new GeoMapViewState();
    }

    if (initialState.getVisibleLayers() == null || initialState.getVisibleLayers().isEmpty()) {
      initialState.addVisibleLayersItem(LAYER_DEFAULT);
    }

    if (initialState.getLayerDescriptors() == null
        || initialState.getLayerDescriptors().isEmpty()) {
      initialState.addLayerDescriptorsItem(new GeoMapLayerDescriptor()
          .code(LAYER_DEFAULT)
          .selectionMode(GeoMapSelectionMode.SINGLE)
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
            .getVisibleLayers())));
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
  public void clearPendingItems(UUID viewUuid, String mapId) {
    executeMapCall(viewUuid, mapId, model -> {
      final GeoMapServerModel serverModel = getServerModel(viewUuid, mapId);
      serverModel.setPendingItems(new HashMap<>());
      model.setPendingItems(new ArrayList<>());
      return model;
    });
  }

  @Override
  public GeoMapChange refreshMap(UUID viewUuid, String mapId) {
    return executeMapCall(viewUuid, mapId, model -> {
      // THIS IS A VERY NAIVE, RUDIMENTARY IMPLEMENTATION:
      final GeoMapServerModel serverModel = getServerModel(viewUuid, mapId);
      final Set<String> visibleLayers = new HashSet<>(model.getViewState().getVisibleLayers());
      final GeoMapViewport viewport = model.getViewport();
      final Map<String, GeoMapLayer> layersToShow = fetchData(
          visibleLayers,
          serverModel,
          viewport);
      final GeoMapChange change = layersToShow.values().stream()
          .map(it -> new GeoMapLayerChange()
              .code(it.getCode())
              .toAdd(new ArrayList<>(it.getItems())))
          .collect(collectingAndThen(
              toCollection(ArrayList::new),
              layerChanges -> new GeoMapChange()
                  .code(mapId)
                  .items(layerChanges)));

      // always include pending items:
      serverModel.getPendingItems().entrySet().stream()
          .filter(e -> visibleLayers.contains(e.getKey()))
          .forEach(e -> {
            final String layerId = e.getKey();
            final List<GeoMapItem> pendingItems = e.getValue();
            final Optional<GeoMapLayerChange> pertainingLayerChange = change.getItems().stream()
                .filter(layerChange -> layerId.equals(layerChange.getCode()))
                .findFirst();
            if (pertainingLayerChange.isPresent()) {
              pertainingLayerChange.get().getToAdd().addAll(pendingItems);
            } else {
              change.addItemsItem(new GeoMapLayerChange()
                  .code(layerId)
                  .toAdd(new ArrayList<>(pendingItems)));
            }
            layersToShow.computeIfAbsent(layerId, k -> new GeoMapLayer().code(k))
                .getItems().addAll(pendingItems);
          });
      model.setLayers(new ArrayList<>(layersToShow.values()));
      return change;
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

  @Override
  public void interact(UUID viewUuid, String mapId, GeoMapInteraction interaction) {
    Objects.requireNonNull(viewUuid, "viewUuid cannot be null!");
    Objects.requireNonNull(mapId, "mapId cannot be null!");
    Objects.requireNonNull(interaction, "interaction cannot be null!");

    switch (interaction.getOperationMode()) {
      case PLACEMENT:
        placeItem(
            viewUuid,
            mapId,
            interaction.getTargetLayer(),
            interaction.getTargetItem(),
            !interaction.getInverse());
        break;
      case SELECTION:
        selectItem(
            viewUuid,
            mapId,
            interaction.getTargetLayer(),
            interaction.getTargetItem().getId(),
            !interaction.getInverse());
        break;
      default:
        throw new IllegalArgumentException(
            "Operation mode "
                + interaction.getOperationMode() +
                " is not directly supported on the GeoMap API!");
    }
    refreshMap(viewUuid, mapId);
  }

  @Override
  public void selectItem(
      UUID viewUuid, String mapId, String layerId, String itemId, boolean select) {
    final GeoMapModel model = getModel(viewUuid, mapId);
    final GeoMapViewState viewState = model.getViewState();
    final boolean selectionSupported;
    final GeoMapLayer targetLayer;
    if (Strings.isNullOrEmpty(layerId)) {

      final List<String> visibleLayers = viewState.getVisibleLayers();
      targetLayer = viewState.getLayerDescriptors().stream()
          .filter(it -> GeoMapSelectionMode.NONE != it.getSelectionMode())
          .filter(it -> Boolean.TRUE.equals(it.getPreserveSelection()
              || visibleLayers.contains(it.getCode())))
          .map(GeoMapLayerDescriptor::getCode)
          .filter(Objects::nonNull)
          .flatMap(it -> model.getLayers().stream()
              .filter(layer -> it.equals(layer.getCode()))
              .findFirst()
              .map(Stream::of)
              .orElseGet(Stream::empty))
          .findFirst()
          .orElse(null);
    } else {
      targetLayer = model.getLayers().stream()
          .filter(it -> layerId.equals(it.getCode()))
          .findFirst()
          .orElse(null);
    }
    selectionSupported = targetLayer != null;
    if (!selectionSupported) {
      return;
    }

    final GeoMapLayerDescriptor targetLayerDescriptor = viewState
        .getLayerDescriptors().stream()
        .filter(it -> Objects.equals(it.getCode(), targetLayer.getCode()))
        .findFirst()
        .orElse(null);
    if (targetLayerDescriptor == null) {
      return;
    }

    final GeoMapServerModel serverModel = getServerModel(viewUuid, mapId);
    final Map<String, GeoMapItem> selection = serverModel.getSelectedItems();
    if (GeoMapSelectionMode.SINGLE == targetLayerDescriptor.getSelectionMode()) {
      selection.clear();
    }

    if (!select) {
      selection.remove(itemId);
      return;
    }

    final GeoMapItem selectedItem = targetLayer.getItems().stream()
        .filter(it -> itemId.equals(it.getId()))
        .findFirst()
        .orElseGet(() -> getPendingItems(serverModel).stream()
            .filter(it -> itemId.equals(it.getId()))
            .findFirst()
            .orElse(null));
    if (selectedItem == null) {
      throw new IllegalArgumentException(
          "GeoMap Item [ " + itemId + " ] not found on layer [ "
              + targetLayer.getCode() + " ]!");
    }

    if (Boolean.TRUE.equals(selectedItem.getSelectable())) {
      return;
    }

    serverModel.putSelectedItemsItem(itemId, selectedItem);
    selectedItem.setSelected(true);
    widgetCallbackApi.executeVoidCallbacks(
        widgetCallbackApi.getCallbacks(viewUuid, mapId, POSTFIX_SELECTION),
        viewUuid, mapId);
  }

  private void placeItem(
      UUID viewUuid, String mapId, String layerId, GeoMapItem item, boolean place) {
    final GeoMapModel model = getModel(viewUuid, mapId);
    final Set<String> pendingItemIds = new HashSet<>(model.getPendingItems() == null
        ? Collections.emptySet()
        : model.getPendingItems());
    final GeoMapServerModel serverModel = getServerModel(viewUuid, mapId);

    final String targetLayer;
    if (Strings.isNullOrEmpty(layerId)) {
      targetLayer = LAYER_DEFAULT;
    } else {
      targetLayer = layerId;
    }

    final boolean modified; // this sentinel is only here until every branch is implemented...
    if (Strings.isNullOrEmpty(item.getId())) {
      // we are trying to place a new element:
      if (place && model.getViewState().getLayerDescriptors().stream()
          .filter(it -> targetLayer.equals(it.getCode()))
          .findFirst()
          .map(GeoMapLayerDescriptor::getSelectionMode)
          .map(selectionMode -> GeoMapSelectionMode.MULTIPLE != selectionMode)
          .orElse(true)) {
        // the targeted layer is not explicitly multi-select -> clear its pending items:
        pendingItemIds.clear();
        serverModel.getPendingItems()
            .computeIfAbsent(targetLayer, k -> new ArrayList<>())
            .clear();
      }

      final String newItemId = UUID.randomUUID().toString();
      pendingItemIds.add(newItemId);
      serverModel.getPendingItems()
          .computeIfAbsent(targetLayer, k -> new ArrayList<>())
          .add(item.id(newItemId));

      final List<String> visibleLayers = model.getViewState().getVisibleLayers();
      if (!visibleLayers.contains(targetLayer)) {
        visibleLayers.add(targetLayer);
      }
      modified = true;
    } else if (getPendingItems(serverModel).stream()
        .map(GeoMapItem::getId)
        .anyMatch(item.getId()::equals)) {
      // we are interacting with a pending element, let's remove it:
      pendingItemIds.remove(item.getId());
      serverModel.getPendingItems()
          .computeIfAbsent(targetLayer, k -> new ArrayList<>())
          .removeIf(it -> item.getId().equals(it.getId()));
      modified = true;
    } else {
      modified = false;
    }

    // TODO: handle place=TRUE for existing item -> this is a drag:
    // TODO: handle place=TRUE for existing item -> this is a pending removal:
    if (!modified) {
      return;
    }

    model.setPendingItems(new ArrayList<>(pendingItemIds));
    widgetCallbackApi.executeVoidCallbacks(
        widgetCallbackApi.getCallbacks(viewUuid, mapId, POSTFIX_PLACEMENT),
        viewUuid, mapId);
  }

  @Override
  public void addLayerCallback(UUID viewUuid, String mapId, InvocationRequest onLayerAdded,
      InvocationRequest onLayerRemoved) {
    Objects.requireNonNull(viewUuid, "viewUuid cannot be null!");
    Objects.requireNonNull(mapId, "mapId cannot be null!");

    if (onLayerAdded != null) {
      widgetCallbackApi.addCallback(viewUuid, mapId, onLayerAdded, POSTFIX_LAYER_ADDED);
    }

    if (onLayerRemoved != null) {
      widgetCallbackApi.addCallback(viewUuid, mapId, onLayerRemoved, POSTFIX_LAYER_REMOVED);
    }
  }

  @Override
  public void addItemCallback(UUID viewUuid, String mapId, InvocationRequest onItemAdded) {
    Objects.requireNonNull(viewUuid, "viewUuid cannot be null!");
    Objects.requireNonNull(mapId, "mapId cannot be null!");
    Objects.requireNonNull(onItemAdded, "onItemAdded cannot be null!");

    widgetCallbackApi.addCallback(viewUuid, mapId, onItemAdded, POSTFIX_ITEM);
  }

  @Override
  public void addSelectionChangeListener(UUID viewUuid, String mapId,
      InvocationRequest onSelectionChanged) {
    widgetCallbackApi.addCallback(viewUuid, mapId, onSelectionChanged, POSTFIX_SELECTION);
  }

  @Override
  public void addMapItemPlacementListener(UUID viewUuid, String mapId,
      InvocationRequest onMapItemPlaced) {
    widgetCallbackApi.addCallback(viewUuid, mapId, onMapItemPlaced, POSTFIX_PLACEMENT);
  }

  @Override
  public List<GeoMapItem> getSelectedItems(UUID viewUuid, String mapId) {
    Objects.requireNonNull(viewUuid, "viewUuid cannot be null!");
    Objects.requireNonNull(mapId, "mapId cannot be null!");

    final GeoMapServerModel serverModel = getServerModel(viewUuid, mapId);
    return (serverModel != null)
        ? new ArrayList<>(serverModel.getSelectedItems().values())
        : Collections.emptyList();
  }

  @Override
  public List<GeoMapItem> getPendingItems(UUID viewUuid, String mapId) {
    Objects.requireNonNull(viewUuid, "viewUuid cannot be null!");
    Objects.requireNonNull(mapId, "mapId cannot be null!");
    final GeoMapServerModel serverModel = getServerModel(viewUuid, mapId);
    return getPendingItems(serverModel);
  }

  private List<GeoMapItem> getPendingItems(GeoMapServerModel serverModel) {
    return (serverModel != null)
        ? serverModel.getPendingItems().values().stream()
            .flatMap(List::stream)
            .collect(toList())
        : Collections.emptyList();
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
