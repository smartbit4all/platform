package org.smartbit4all.ui.api.navigation.restserver.impl;

import java.util.UUID;
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
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ViewModelApiDelegateImpl implements ViewModelApiDelegate {

  @Autowired
  private UINavigationApi uiNavigationApi;

  @Autowired
  private ObjectMapper objectMapper;

  @Override
  public ResponseEntity<ViewModelData> createViewModel(NavigationTarget navigationTarget)
      throws Exception {

    uiNavigationApi.navigateTo(navigationTarget);
    UINavigationApi uiHackApi = ReflectionUtility.getProxyTarget(uiNavigationApi);
    if (uiHackApi instanceof UINavigationApiHeadless) {
      ((UINavigationApiHeadless) uiHackApi).clearUiToOpen();
    }

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
    UINavigationApi uiHackApi = ReflectionUtility.getProxyTarget(uiNavigationApi);
    if (uiHackApi instanceof UINavigationApiHeadless) {
      NavigationTarget uiToOpen = ((UINavigationApiHeadless) uiHackApi).getUiToOpen();
      result.setUiToOpen(uiToOpen);
      ((UINavigationApiHeadless) uiHackApi).clearUiToOpen();
    }
    return ResponseEntity.ok(result);
  }
}
