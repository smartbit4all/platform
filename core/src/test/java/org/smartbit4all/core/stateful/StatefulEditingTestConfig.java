package org.smartbit4all.core.stateful;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class StatefulEditingTestConfig {

  private Map<Class<?>, ApiBeanDescriptor> approvalRuleDescriptor;

  public StatefulEditingTestConfig() {
    Set<Class<?>> domainBeans = new HashSet<>();
    domainBeans.add(StatefulBean.class);
    approvalRuleDescriptor = ApiBeanDescriptor.of(domainBeans);
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public StatefulBeanEditing statefulBeanEditing() {
    StatefulBeanEditing statefulBeanEditing = new StatefulBeanEditingImpl(approvalRuleDescriptor);
    return statefulBeanEditing;
  }
}
