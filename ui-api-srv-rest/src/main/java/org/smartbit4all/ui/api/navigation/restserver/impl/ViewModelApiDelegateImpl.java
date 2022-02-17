package org.smartbit4all.ui.api.navigation.restserver.impl;

import java.util.UUID;
import org.smartbit4all.ui.api.navigation.UINavigationApi;
import org.smartbit4all.ui.api.navigation.model.CommandData;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.api.navigation.restserver.ViewModelApiDelegate;
import org.smartbit4all.ui.api.viewmodel.ViewModel;
import org.smartbit4all.ui.api.viewmodel.ViewModelImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ViewModelApiDelegateImpl implements ViewModelApiDelegate {

  @Autowired
  private UINavigationApi uiNavigationApi;

  @Autowired
  private ObjectMapper objectMapper;

  @Override
  public ResponseEntity<NavigationTarget> createViewModel(NavigationTarget navigationTarget)
      throws Exception {

    uiNavigationApi.navigateTo(navigationTarget);
    return ResponseEntity.ok(navigationTarget);
  }

  @Override
  public ResponseEntity<Void> setData(UUID uuid, Object dataMap) throws Exception {
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
  public ResponseEntity<Object> getData(UUID uuid) throws Exception {
    ViewModel vm = uiNavigationApi.getViewModelByUuid(uuid);
    if (vm instanceof ViewModelImpl) {
      return ResponseEntity.ok(((ViewModelImpl<?>) vm).getObject());
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @Override
  public ResponseEntity<Void> executeCommand(UUID uuid, CommandData commandData) throws Exception {
    ViewModel vm = uiNavigationApi.getViewModelByUuid(uuid);
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
    return ResponseEntity.ok(null);
  }
}
