package org.smartbit4all.ui.common.userselector.impl;

import java.net.URI;
import java.util.Map;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.userselector.bean.UserSingleSelector;
import org.smartbit4all.api.userselector.util.UserSelectorUtil;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ObjectEditingImpl;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.ObservableObjectImpl;
import org.smartbit4all.ui.api.userselector.UserSingleSelectorViewModel;

public class UserSingleSelectorViewModelImpl extends ObjectEditingImpl
    implements UserSingleSelectorViewModel {

  protected ObservableObjectImpl userSingleSelector;

  private Map<Class<?>, ApiBeanDescriptor> userSelectorDescriptor;

  private OrgApi orgApi;

  private UserSingleSelector userSingleSelectorWrapper;

  public UserSingleSelectorViewModelImpl(OrgApi orgApi,
      Map<Class<?>, ApiBeanDescriptor> userSelectorDescriptor) {
    this.orgApi = orgApi;
    this.userSelectorDescriptor = userSelectorDescriptor;

    initObservableObject();
  }

  @Override
  public void initObservableObject() {
    userSingleSelector = new ObservableObjectImpl();
    ref = null;
    userSingleSelectorWrapper = null;
  }

  @Override
  public void initUserSingleSelectors(URI selectedUserUri) {
    UserSingleSelector singleSelector =
        UserSelectorUtil.createUserSingleSelector(orgApi.getActiveUsers(),
//            orgApi.getAllGroups(),
            selectedUserUri);

    if (userSingleSelectorWrapper == null) {

      ref = new ApiObjectRef(null, singleSelector, userSelectorDescriptor);
      userSingleSelector.setRef(ref);
      userSingleSelectorWrapper = ref.getWrapper(UserSingleSelector.class);

    } else {

      userSingleSelectorWrapper.setSelected(singleSelector.getSelected());
      userSingleSelectorWrapper.setSelectors(singleSelector.getSelectors());

    }
  }

  @Override
  public ObservableObject userSingleSelector() {
    return userSingleSelector;
  }
}
