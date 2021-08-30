package org.smartbit4all.ui.common.userselector.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.userselector.bean.UserMultiSelector;
import org.smartbit4all.api.userselector.bean.UserSingleSelector;
import org.smartbit4all.api.userselector.util.UserSelectorUtil;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ObjectEditingImpl;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.ObservableObjectImpl;
import org.smartbit4all.ui.api.userselector.UserSelectorViewModel;

public class UserSelectorViewModelImpl extends ObjectEditingImpl implements UserSelectorViewModel {

  private OrgApi orgApi;

  private ObservableObjectImpl singleSelector;
  private ApiObjectRef singleSelectorRef;
  private UserSingleSelector singleSelectorWrapper;

  private ObservableObjectImpl multiSelector;
  private ApiObjectRef multiSelectorRef;
  private UserMultiSelector multiSelectorWrapper;

  private ObservableObjectImpl commands;
  private ApiObjectRef commandsRef;
  private UserSelectorCommands commandsWrapper;

  private Map<Class<?>, ApiBeanDescriptor> userSelectorDescriptor;

  private Map<String, Consumer<String[]>> commandMethodsByCode;
  
  private List<URI> selectedUris;

  public UserSelectorViewModelImpl(OrgApi orgApi,
      Map<Class<?>, ApiBeanDescriptor> userSelectorDescriptor) {

    this.orgApi = orgApi;
    this.userSelectorDescriptor = userSelectorDescriptor;

    commandMethodsByCode = new HashMap<>();
    selectedUris = new ArrayList<>();

    initObservables();
    initRefs();
  }

  @Override
  public ObservableObject singleSelector() {
    return singleSelector;
  }

  @Override
  public ObservableObject multiSelector() {
    return multiSelector;
  }


  @Override
  public ObservableObject commands() {
    return commands;
  }

  @Override
  public void setSingleSelectorRef(URI selectedUserUri) {
    UserSingleSelector userSingleSelector = UserSelectorUtil
        .createUserSingleSelector(orgApi.getAllUsers(), orgApi.getAllGroups(), selectedUserUri);

    singleSelectorRef.setObject(userSingleSelector);
    
    singleSelector.notifyListeners();
  }

  @Override
  public void setMultiSelectorRef(List<URI> selectedUserUris) {
    UserMultiSelector userMultiSelector = UserSelectorUtil
        .createUserMultiSelector(orgApi.getAllUsers(), orgApi.getAllGroups(), selectedUserUris);

    multiSelectorRef.setObject(userMultiSelector);
    
    multiSelector.notifyListeners();
  }

  @Override
  public void executeCommand(String code, String... param) {
    Consumer<String[]> commandMethod = commandMethodsByCode.get(code);
    if (commandMethod == null) {
      throw new IllegalArgumentException("Unknown command code: " + code);
    }
    commandMethod.accept(param);
  }
  
  @Override
  public void removeCommand(String commandCode) {
    commandsWrapper.getCommands().remove(commandCode);
    commandMethodsByCode.remove(commandCode);
  }

  private void initObservables() {
    singleSelector = new ObservableObjectImpl();
    multiSelector = new ObservableObjectImpl();
    commands = new ObservableObjectImpl();
  }

  private void initRefs() {
    singleSelectorRef = new ApiObjectRef(null, new UserSingleSelector(), userSelectorDescriptor);
    singleSelector.setRef(singleSelectorRef);
    singleSelectorWrapper = singleSelectorRef.getWrapper(UserSingleSelector.class);

    multiSelectorRef = new ApiObjectRef(null, new UserMultiSelector(), userSelectorDescriptor);
    multiSelector.setRef(multiSelectorRef);
    multiSelectorWrapper = multiSelectorRef.getWrapper(UserMultiSelector.class);

    commandsRef = new ApiObjectRef(null, new UserSelectorCommands(), userSelectorDescriptor);
    commands.setRef(commandsRef);
    commandsWrapper = commandsRef.getWrapper(UserSelectorCommands.class);
    
    addCommand(UserSelectorViewModel.CLOSE_CMD, this::closeAndSaveSelector);
    addCommand(UserSelectorViewModel.SAVE_CMD, this::saveSelector);
  }
  
  private void addCommand(String code, Consumer<String[]> commandMethod) {
    commandsWrapper.getCommands().add(code);
    commandMethodsByCode.put(code, commandMethod);
  }
  
  private void closeAndSaveSelector(String... params) {
    multiSelectorWrapper.setIsSaving(true);
    notifyAllListeners();
  }

  private void saveSelector(String... params) {
    multiSelectorWrapper.setIsSaving(false);
  }
  
  private void notifyAllListeners() {
    singleSelector.notifyListeners();
    multiSelector.notifyListeners();
    commands.notifyListeners();
  }
}
