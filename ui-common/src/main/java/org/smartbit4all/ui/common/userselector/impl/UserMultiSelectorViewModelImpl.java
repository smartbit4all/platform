package org.smartbit4all.ui.common.userselector.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.userselector.bean.UserMultiSelector;
import org.smartbit4all.api.userselector.util.UserSelectorUtil;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ObjectEditingImpl;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.ObservableObjectImpl;
import org.smartbit4all.ui.api.userselector.UserMultiSelectorViewModel;
import io.reactivex.rxjava3.functions.Consumer;

public class UserMultiSelectorViewModelImpl extends ObjectEditingImpl
    implements UserMultiSelectorViewModel {

  protected ObservableObjectImpl userMultiSelector;

  private Map<Class<?>, ApiBeanDescriptor> userSelectorDescriptor;
  private Map<Class<?>, ApiBeanDescriptor> commandsDescriptor;

  private OrgApi orgApi;

  private UserMultiSelector userMultiSelectorWrapper;

  private List<URI> selectedUris;

  private ObservableObjectImpl commandObservable;
  private UserSelectorCommands commands;
  private UserSelectorCommands commandsWrapper;
  public Map<String, Consumer<String[]>> commandMethodsByCode = new HashMap<>();

  public UserMultiSelectorViewModelImpl(OrgApi orgApi,
      Map<Class<?>, ApiBeanDescriptor> userSelectorDescriptor, Map<Class<?>, ApiBeanDescriptor> commandsDescriptor) {
    this.orgApi = orgApi;
    this.userSelectorDescriptor = userSelectorDescriptor;
    this.commandsDescriptor = commandsDescriptor;
    selectedUris = Arrays.asList();
    initObservableObject();
  }

  @Override
  public void init() {
    initUserMultiSelectors(selectedUris);
    initCommands();
    notifyAllListeners();
  }

  @Override
  public void initObservableObject() {
    userMultiSelector = new ObservableObjectImpl();
    commandObservable = new ObservableObjectImpl();
    ref = null;
    userMultiSelectorWrapper = null;
  }

  private void notifyAllListeners() {
    userMultiSelector.notifyListeners();
    commandObservable.notifyListeners();
  }

  private void initCommands() {
    commands = new UserSelectorCommands();
    ApiObjectRef ref = new ApiObjectRef(null, commands, commandsDescriptor);
    commandObservable.setRef(ref);
    commandsWrapper = ref.getWrapper(UserSelectorCommands.class);
    addCommand(UserMultiSelectorViewModel.CLOSE_CMD, this::closeAndSaveSelector);
//    addCommand(UserMultiSelectorViewModel.SAVE_CMD, this::saveSelector);
  }

  private void addCommand(String code, Consumer<String[]> commandMethod) {
    commandsWrapper.getCommands().add(code);
    commandMethodsByCode.put(code, commandMethod);
  }

  private void closeAndSaveSelector(String... params) {
    userMultiSelectorWrapper.setIsSaving(true);
    notifyAllListeners();
  }

//  private void saveSelector(String... params) {
//    userMultiSelectorWrapper.setIsSaving(true);
//    notifyAllListeners();
//  }

  @Override
  public void initUserMultiSelectors(List<URI> selected) {
    UserMultiSelector multiSelector = UserSelectorUtil.createUserMultiSelector(orgApi.getAllUsers(),
//        orgApi.getAllGroups(),
        selected);

    if (userMultiSelectorWrapper == null) {

      ref = new ApiObjectRef(null, multiSelector, userSelectorDescriptor);
      userMultiSelector.setRef(ref);
      userMultiSelectorWrapper = ref.getWrapper(UserMultiSelector.class);

    } else {

      userMultiSelectorWrapper.setSelectors(multiSelector.getSelectors());
      if (multiSelector.getSelected().size() > 0) {
        userMultiSelectorWrapper.setSelected(multiSelector.getSelected());
      } else {
        userMultiSelectorWrapper.setSelected(new ArrayList<>());
      }
      notifyAllListeners();

    }
  }

  @Override
  public ObservableObject userMultiSelector() {
    return userMultiSelector;
  }

  @Override
  public void executeCommand(String code, String... param) throws Throwable {
    Consumer<String[]> commandMethod = commandMethodsByCode.get(code);
    if (commandMethod == null) {
      throw new IllegalArgumentException("Unknown command code: " + code);
    }
    commandMethod.accept(param);
  }
}
