package org.smartbit4all.api.geomap.restserver;

import org.smartbit4all.api.geomap.bean.GeoMapChange;
import org.smartbit4all.api.geomap.bean.GeoMapInteraction;
import org.smartbit4all.api.geomap.bean.GeoMapModel;
import org.smartbit4all.api.geomap.bean.GeoMapViewState;
import org.smartbit4all.api.geomap.bean.GeoMapViewport;
import org.smartbit4all.api.view.ViewContextService;
import org.smartbit4all.api.view.bean.ViewContextChange;
import org.smartbit4all.api.view.geomap.GeoMapApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public class GeoMapApiDelegateImpl implements GeoMapApiDelegate {

  @Autowired
  private GeoMapApi geoMapApi;
  @Autowired
  private ViewContextService viewContextService;

  @Override
  public ResponseEntity<GeoMapModel> load(UUID uuid, String identifier) throws Exception {
    geoMapApi.refreshMap(uuid, identifier);
    return ResponseEntity.ok(geoMapApi.executeMapCall(uuid, identifier, map -> map));
  }

  @Override
  public ResponseEntity<GeoMapChange> move(UUID uuid, String identifier,
                                           GeoMapViewport geoMapViewport) throws Exception {
    geoMapApi.executeMapCall(uuid, identifier, map -> map.viewport(geoMapViewport));
    return ResponseEntity.ok(geoMapApi.refreshMap(uuid, identifier));
  }

  @Override
  public ResponseEntity<GeoMapChange> update(UUID uuid, String identifier,
                                             GeoMapViewState geoMapViewState) throws Exception {
    geoMapApi.executeMapCall(uuid, identifier, map -> map.viewState(geoMapViewState));
    return ResponseEntity.ok(geoMapApi.refreshMap(uuid, identifier));
  }

  @Override
  public ResponseEntity<ViewContextChange> interact(UUID uuid, String identifier,
                                                    GeoMapInteraction geoMapInteraction)
  throws Exception {
    return ResponseEntity.ok(viewContextService.performViewCall(() -> {
      geoMapApi.interact(uuid, identifier, geoMapInteraction);
      return null;
    }, "geoMapInteract"));
  }
}
