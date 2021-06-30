package org.smartbit4all.ui.common.userselector.impl;

import java.net.URI;
import java.util.Map;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.userselector.bean.UserSelectors;
import org.smartbit4all.api.userselector.util.UserSelectorUtil;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ObjectEditingImpl;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.ObservableObjectImpl;
import org.smartbit4all.ui.api.userselector.UserSelectorViewModel;

public class UserSelectorViewModelImpl extends ObjectEditingImpl implements UserSelectorViewModel {

  protected ObservableObjectImpl userSelectors;

  private Map<Class<?>, ApiBeanDescriptor> userSelectorsDescriptor;

  private OrgApi orgApi;

  private UserSelectors userSelectorsWrapper;
  
  public UserSelectorViewModelImpl(OrgApi orgApi, Map<Class<?>, ApiBeanDescriptor> userSelectorsDescriptor) {
    this.orgApi = orgApi;
    this.userSelectorsDescriptor = userSelectorsDescriptor;
    
    userSelectors = new ObservableObjectImpl();
  }

  @Override
  public void initUserSelectors(URI selectedUserUri) {
    UserSelectors selectors =
        UserSelectorUtil.createUserSelectors(orgApi.getAllUsers(), orgApi.getAllGroups(), selectedUserUri);

    ref = new ApiObjectRef(null, selectors, userSelectorsDescriptor);
    userSelectors.setRef(ref);
    userSelectorsWrapper = ref.getWrapper(UserSelectors.class);
  }

  @Override
  public ObservableObject userSelectors() {
    return userSelectors;
  }
}
