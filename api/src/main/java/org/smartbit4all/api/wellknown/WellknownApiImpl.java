package org.smartbit4all.api.wellknown;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.wellknown.bean.WellKnownDefinition;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;

public class WellknownApiImpl implements WellknownApi {

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  private ObjectApi objectApi;

  @Override
  public ObjectNode getWellknownById(String id) {
    List<URI> wellknownUris = collectionApi.list(WELLKNOWN_SCHEMA, SL_WELLKNOWN).uris();
    Optional<ObjectNode> wellknownObjById =
        wellknownUris.stream().map(wu -> objectApi.loadLatest(wu))
            .filter(wo -> wo.getValue(WellKnownDefinition.ID).equals(id))
            .findFirst();
    if (wellknownObjById.isPresent()) {
      return wellknownObjById.get();
    }
    return null;
  }

}
