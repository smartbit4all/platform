package org.smartbit4all.api.wellknow.restserver.impl;

import java.net.URI;
import org.smartbit4all.api.binarydata.BinaryDataSorageApi;
import org.smartbit4all.api.wellknown.WellknownApi;
import org.smartbit4all.api.wellknown.bean.WellKnownDefinition;
import org.smartbit4all.api.wellknown.restserver.WellknownApiDelegate;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class WellknownDelegateImpl implements WellknownApiDelegate {

  @Autowired
  private WellknownApi wellknownApi;

  @Autowired
  private BinaryDataSorageApi binaryDataSorageApi;

  @Override
  public ResponseEntity<Resource> getWellknown(String id) throws Exception {
    ObjectNode wellknownDefinitionNode = wellknownApi.getWellknownById(id);
    if (wellknownDefinitionNode == null || wellknownDefinitionNode.getData() == null) {
      return ResponseEntity.notFound().build();
    }
    URI binaryDataObjectUri = wellknownDefinitionNode.getValue(URI.class, WellKnownDefinition.DATA);
    Resource resource =
        new InputStreamResource(binaryDataSorageApi.loadLatest(binaryDataObjectUri).inputStream());
    MediaType parseMediaType = MediaType.parseMediaType(
        wellknownDefinitionNode.getValueAsString(WellKnownDefinition.CONTENTYPE));
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(parseMediaType);
    return ResponseEntity.ok().headers(httpHeaders).body(resource);
  }



}
