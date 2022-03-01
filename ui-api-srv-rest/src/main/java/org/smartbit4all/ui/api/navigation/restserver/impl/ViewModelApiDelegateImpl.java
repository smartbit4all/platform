package org.smartbit4all.ui.api.navigation.restserver.impl;

import java.io.InputStream;
import java.util.UUID;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.core.utility.ReflectionUtility;
import org.smartbit4all.ui.api.navigation.UINavigationApi;
import org.smartbit4all.ui.api.navigation.model.CommandData;
import org.smartbit4all.ui.api.navigation.model.CommandResult;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.api.navigation.model.ViewModelData;
import org.smartbit4all.ui.api.navigation.restserver.ViewModelApiDelegate;
import org.smartbit4all.ui.api.viewmodel.ViewModel;
import org.smartbit4all.ui.api.viewmodel.ViewModelImpl;
import org.smartbit4all.ui.common.api.UINavigationApiHeadless;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ViewModelApiDelegateImpl implements ViewModelApiDelegate {

  @Autowired
  private UINavigationApi uiNavigationApi;

  @Autowired
  private ObjectMapper objectMapper;

  public ViewModelApiDelegateImpl(UINavigationApi uiNavigationApi) {
    UINavigationApi uiApi = ReflectionUtility.getProxyTarget(uiNavigationApi);
    if (!(uiApi instanceof UINavigationApiHeadless)) {
      throw new IllegalArgumentException(
          "ViewModelApi needs a UINavigationApiHeadless, but received"
              + uiApi.getClass().getName());
    }
    this.uiNavigationApi = uiNavigationApi;
  }

  @Override
  public ResponseEntity<ViewModelData> createViewModel(NavigationTarget navigationTarget)
      throws Exception {

    uiNavigationApi.navigateTo(navigationTarget);
    UINavigationApiHeadless.clearUiToOpen();
    return getModel(navigationTarget.getUuid());
  }

  @Override
  public ResponseEntity<Void> setModel(UUID uuid, Object dataMap) throws Exception {
    ViewModel vm = uiNavigationApi.getViewModelByUuid(uuid);
    if (vm instanceof ViewModelImpl) {
      ViewModelImpl<?> vmImpl = (ViewModelImpl<?>) vm;
      Class<?> modelClazz = vmImpl.getModelClazz();
      Object data = objectMapper.convertValue(dataMap, modelClazz);
      vmImpl.setObject(data);
    } else {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(null);
  }

  @Override
  public ResponseEntity<ViewModelData> getModel(UUID uuid) throws Exception {
    ViewModel vm = uiNavigationApi.getViewModelByUuid(uuid);
    if (vm != null) {
      return ResponseEntity.ok(vm.getViewModelData());
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @Override
  public ResponseEntity<CommandResult> executeCommand(UUID uuid, CommandData commandData)
      throws Exception {
    ViewModel vm = uiNavigationApi.getViewModelByUuid(uuid);
    // setModel
    if (commandData.getModel() != null) {
      setModel(uuid, commandData.getModel());
    }
    // execute
    if (vm != null) {
      if (commandData.getParams() == null || commandData.getParams().isEmpty()) {
        vm.executeCommand(commandData.getCommandPath(), commandData.getCommandCode());
      } else {
        Object[] params = commandData.getParams().toArray();
        vm.executeCommand(commandData.getCommandPath(), commandData.getCommandCode(), params);
      }
    } else {
      return ResponseEntity.notFound().build();
    }
    // getModel
    CommandResult result = new CommandResult()
        .view(vm.getViewModelData());
    // getUiToOpen
    result.setUiToOpen(UINavigationApiHeadless.getUiToOpen());
    UINavigationApiHeadless.clearUiToOpen();
    return ResponseEntity.ok(result);
  }

  @Override
  public ResponseEntity<CommandResult> upload(UUID uuid, CommandData command, MultipartFile content)
      throws Exception {
    ViewModel vm = uiNavigationApi.getViewModelByUuid(uuid);
    if (vm == null) {
      return ResponseEntity.notFound().build();
    }
    try (InputStream is = content.getInputStream()) {

      BinaryData binaryData = BinaryData.of(is);
      command.addParamsItem(binaryData);

      return executeCommand(uuid, command);
    }
  }

  @Override
  public ResponseEntity<Resource> download(UUID vmUuid, String dataIdentifier) throws Exception {
    ViewModel vm = uiNavigationApi.getViewModelByUuid(vmUuid);
    if (vm == null) {
      return ResponseEntity.notFound().build();
    }
    BinaryData content = vm.getDownloadData(dataIdentifier);
    if (content == null) {
      return ResponseEntity.notFound().build();
    }
    Resource res = new InputStreamResource(content.inputStream());
    return ResponseEntity
        .ok()
        .body(res);
  }

}
