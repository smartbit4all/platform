package org.smartbit4all.api.view.restserver.impl;

import static java.util.stream.Collectors.toList;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.view.ViewApi;
import org.smartbit4all.api.view.ViewContextService;
import org.smartbit4all.api.view.bean.ComponentModel;
import org.smartbit4all.api.view.bean.DataChange;
import org.smartbit4all.api.view.bean.MessageResult;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.UploadedFile;
import org.smartbit4all.api.view.bean.ViewConstraint;
import org.smartbit4all.api.view.bean.ViewContextChange;
import org.smartbit4all.api.view.bean.ViewContextData;
import org.smartbit4all.api.view.bean.ViewContextUpdate;
import org.smartbit4all.api.view.restserver.ViewApiDelegate;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public class ViewApiDelegateImpl implements ViewApiDelegate {

  private static final ViewConstraint EMPTY_CONSTRAINT = new ViewConstraint();

  @Autowired
  private ViewContextService viewContextService;

  @Autowired
  private ViewApi viewApi;

  @Autowired
  private ObjectApi objectApi;

  @Override
  public ResponseEntity<ViewContextData> createViewContext() throws Exception {
    return ResponseEntity.ok(viewContextService.createViewContext());
  }

  @Override
  public ResponseEntity<ViewContextData> updateViewContext(ViewContextUpdate viewContextUpdate)
      throws Exception {
    viewContextService.updateViewContext(viewContextUpdate);
    return getViewContext(viewContextUpdate.getUuid());
  }

  @Override
  public ResponseEntity<ViewContextData> getViewContext(UUID uuid) throws Exception {
    return ResponseEntity.ok(viewContextService.getViewContext(uuid));
  }

  @Override
  public ResponseEntity<ViewContextChange> message(UUID viewUuid, UUID messageUuid,
      MessageResult messageResult)
      throws Exception {
    return ResponseEntity
        .ok(viewContextService.handleMessage(viewUuid, messageUuid, messageResult));
  }

  @Override
  public ResponseEntity<ViewConstraint> getViewConstraint(UUID uuid) throws Exception {
    ViewConstraint constraint = viewApi.getView(uuid).getConstraint();
    return ResponseEntity.ok(constraint == null ? EMPTY_CONSTRAINT : constraint);
  }

  @Override
  public ResponseEntity<Void> showPublishedView(String channel, UUID uuid) {
    viewApi.showPublishedView(channel, uuid);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Resource> downloadItem(UUID uuid, String item) throws Exception {
    BinaryData data = viewApi.downloadItem(uuid, item);
    if (data == null) {
      return ResponseEntity.notFound().build();
    }
    Resource resource = new InputStreamResource(data.inputStream());
    return ResponseEntity.ok(resource);
  }

  @Override
  public ResponseEntity<Resource> downloadItemDeprecated(UUID uuid, String item) throws Exception {
    return downloadItem(uuid, item);
  }

  @Override
  public ResponseEntity<ComponentModel> getComponentModel(UUID uuid) throws Exception {
    return ResponseEntity.ok(viewContextService.getComponentModel(uuid));
  }

  @Override
  public ResponseEntity<ViewContextChange> getComponentModel2(UUID uuid) throws Exception {
    return ResponseEntity.ok(viewContextService.getComponentModel2(uuid));
  }

  @Override
  public ResponseEntity<ViewContextChange> performAction(UUID uuid, UiActionRequest request)
      throws Exception {
    return ResponseEntity.ok(viewContextService.performAction(uuid, request));
  }

  @Override
  public ResponseEntity<ViewContextChange> performWidgetMainAction(UUID uuid, String widgetId,
      UiActionRequest request) throws Exception {
    return ResponseEntity
        .ok(viewContextService.performWidgetAction(uuid, widgetId, null, request));
  }

  @Override
  public ResponseEntity<ViewContextChange> performWidgetAction(UUID uuid, String widgetId,
      String nodeId, UiActionRequest request) throws Exception {
    return ResponseEntity
        .ok(viewContextService.performWidgetAction(uuid, widgetId, nodeId, request));
  }

  @Override
  public ResponseEntity<ViewContextChange> dataChanged(UUID uuid, DataChange dataChangeEvent)
      throws Exception {
    return ResponseEntity
        .ok(viewContextService.performDataChanged(uuid, dataChangeEvent));
  }

  @Override
  public ResponseEntity<ViewContextChange> uploadAction(UUID uuid, String uiActionRequest,
      String param, MultipartFile content) throws Exception {
    Objects.requireNonNull(param, "param must be specified");
    UiActionRequest request = convertStringToUiActionRequest(uiActionRequest);
    request.putParamsItem(param, convertMultipartFileToUploadedFile(content));
    return ResponseEntity.ok(viewContextService.performAction(uuid, request));
  }

  @Override
  public ResponseEntity<ViewContextChange> uploadMultipleAction(UUID uuid, String uiActionRequest,
      String param, List<MultipartFile> contents) throws Exception {
    UiActionRequest request = convertStringToUiActionRequest(uiActionRequest);
    request.putParamsItem(param, contents.stream()
        .map(this::convertMultipartFileToUploadedFile)
        .collect(toList()));
    return ResponseEntity.ok(viewContextService.performAction(uuid, request));
  }

  private UiActionRequest convertStringToUiActionRequest(String request) {
    return objectApi.asType(UiActionRequest.class, request);
  }

  private UploadedFile convertMultipartFileToUploadedFile(MultipartFile file) {
    try {
      return new UploadedFile()
          .filename(file.getOriginalFilename())
          .size(file.getSize())
          .data(BinaryData.of(file.getInputStream()));
    } catch (IOException e) {
      throw new IllegalArgumentException("Invalid file", e);
    }
  }
}
