package org.smartbit4all.api.navigation;

import org.smartbit4all.api.invocation.bean.InvocationRequestTemplate;

public interface NavigationOnChangePublisher {

  void subscribeOnChange(InvocationRequestTemplate onChange);

}
