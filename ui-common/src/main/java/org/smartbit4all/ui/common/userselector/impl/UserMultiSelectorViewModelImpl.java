package org.smartbit4all.ui.common.userselector.impl;

import java.net.URI;
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

public class UserMultiSelectorViewModelImpl extends ObjectEditingImpl
    implements UserMultiSelectorViewModel {

  protected ObservableObjectImpl userMultiSelector;

  private Map<Class<?>, ApiBeanDescriptor> userSelectorDescriptor;

  private OrgApi orgApi;

  private UserMultiSelector userMultiSelectorWrapper;

  public UserMultiSelectorViewModelImpl(OrgApi orgApi,
      Map<Class<?>, ApiBeanDescriptor> userSelectorDescriptor) {
    this.orgApi = orgApi;
    this.userSelectorDescriptor = userSelectorDescriptor;

    initObservableObject();
  }

  @Override
  public void initObservableObject() {
    userMultiSelector = new ObservableObjectImpl();
  }

  @Override
  public void initUserMultiSelectors(List<URI> selected) {
    UserMultiSelector multiSelector = UserSelectorUtil.createUserMultiSelector(orgApi.getAllUsers(),
        orgApi.getAllGroups(), selected);

    if (userMultiSelectorWrapper == null) {
      
      ref = new ApiObjectRef(null, multiSelector, userSelectorDescriptor);
      userMultiSelector.setRef(ref);
      userMultiSelectorWrapper = ref.getWrapper(UserMultiSelector.class);
      
    } else {
      
      userMultiSelectorWrapper.setSelectors(multiSelector.getSelectors());
      userMultiSelectorWrapper.setSelected(multiSelector.getSelected());
      
    }
  }

  @Override
  public ObservableObject userMultiSelector() {
    return userMultiSelector;
  }
}
