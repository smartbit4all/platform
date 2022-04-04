package org.smartbit4all.ui.common.viewmodel.sb4starter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.ui.api.sb4starterui.model.SB4StarterModel;

public class SB4StarterViewModelUtility {

  public static final Map<Class<?>, ApiBeanDescriptor> SB4_STARTER_DESCRIPTOR =
      initSB4StarterDescriptor();

  private static Map<Class<?>, ApiBeanDescriptor> initSB4StarterDescriptor() {
    Set<Class<?>> domainBeans = new HashSet<>();

    domainBeans.add(SB4StarterModel.class);

    return ApiBeanDescriptor.of(domainBeans);
  }
}
