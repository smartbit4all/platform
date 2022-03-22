package org.smartbit4all.ui.api.navigation.restserver.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.core.utility.ReflectionUtility;
import org.smartbit4all.ui.api.navigation.UINavigationApi;
import org.smartbit4all.ui.api.navigation.model.CommandData;
import org.smartbit4all.ui.api.navigation.model.CommandResult;
import org.smartbit4all.ui.api.navigation.model.Message;
import org.smartbit4all.ui.api.navigation.model.MessageResult;
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

  private Map<UUID, Message> messagesByUuid = new HashMap<>();
  private Map<UUID, Consumer<MessageResult>> messageHandlersByUuid = new HashMap<>();

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
    UINavigationApiHeadless.clearMessageToOpen();
    UINavigationApiHeadless.clearMessageToOpenHandler();
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
    CommandResult result = createCommandResult(vm);
    return ResponseEntity.ok(result);
  }

  private CommandResult createCommandResult(ViewModel vm) {
    CommandResult result = new CommandResult();
    if (vm != null) {
      result.setView(vm.getViewModelData());
    }
    // getUiToOpen
    result.setUiToOpen(UINavigationApiHeadless.getUiToOpen());
    Message message = UINavigationApiHeadless.getMessageToOpen();
    result.setMessageToOpen(message);
    if (result.getMessageToOpen() != null) {
      messagesByUuid.put(message.getUuid(), message);
      messageHandlersByUuid.put(message.getUuid(),
          UINavigationApiHeadless.getMessageToOpenHandler());
    }
    UINavigationApiHeadless.clearUiToOpen();
    UINavigationApiHeadless.clearMessageToOpen();
    UINavigationApiHeadless.clearMessageToOpenHandler();
    return result;
  }

  @Override
  public ResponseEntity<CommandResult> upload(String uuidStr, CommandData command,
      MultipartFile content)
      throws Exception {
    UUID uuid = UUID.fromString(uuidStr);
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

  @Override
  public ResponseEntity<CommandResult> message(UUID uuid, MessageResult messageResult)
      throws Exception {
    Message message = messagesByUuid.get(uuid);
    if (message == null) {
      return ResponseEntity.notFound().build();
    }
    if (!message.getPossibleResults().contains(messageResult)) {
      return ResponseEntity.badRequest().build();
    }
    Consumer<MessageResult> handler = messageHandlersByUuid.get(uuid);
    if (handler != null) {
      handler.accept(messageResult);
      messageHandlersByUuid.remove(uuid);
    }
    messagesByUuid.remove(uuid);
    ViewModel vm = null;
    if (message.getViewModelUuid() != null) {
      vm = uiNavigationApi.getViewModelByUuid(message.getViewModelUuid());
    }
    return ResponseEntity.ok(createCommandResult(vm));
  }

  @Override
  public ResponseEntity<Void> close(UUID uuid) throws Exception {
    ViewModel vm = uiNavigationApi.getViewModelByUuid(uuid);
    if (vm == null) {
      return ResponseEntity.notFound().build();
    }
    uiNavigationApi.close(uuid);
    return ResponseEntity.ok().build();
  }

}
