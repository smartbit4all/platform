package org.smartbit4all.ui.common.sb4starter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.ui.api.sb4starterui.model.SB4StarterWordFormModel;

public class SB4StarterViewModelUtility {

  public static final Map<Class<?>, ApiBeanDescriptor> WORD_FORM_DESCRIPTOR =
      initSB4StartWordFormDescriptor();

  private static Map<Class<?>, ApiBeanDescriptor> initSB4StartWordFormDescriptor() {
    Set<Class<?>> domainBeans = new HashSet<>();

    domainBeans.add(SB4StarterWordFormModel.class);

    return ApiBeanDescriptor.of(domainBeans);
  }
}
