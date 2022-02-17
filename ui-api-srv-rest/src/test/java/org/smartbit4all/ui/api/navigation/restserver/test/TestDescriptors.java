package org.smartbit4all.ui.api.navigation.restserver.test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;

public class TestDescriptors {

  private TestDescriptors() {}

  static final Map<Class<?>, ApiBeanDescriptor> TEST_DESCRIPTORS =
      initTestDescriptors();

  private static Map<Class<?>, ApiBeanDescriptor> initTestDescriptors() {
    Set<Class<?>> domainBeans = collectDomainBeans();

    return ApiBeanDescriptor.of(domainBeans);
  }

  public static Set<Class<?>> collectDomainBeans() {
    Set<Class<?>> domainBeans = new HashSet<>();

    domainBeans.add(NavigationTarget.class);
    return domainBeans;
  }
}
