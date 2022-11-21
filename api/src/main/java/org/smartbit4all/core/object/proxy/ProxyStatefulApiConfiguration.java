package org.smartbit4all.core.object.proxy;

import org.springframework.aop.config.AopConfigUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

@Configuration
public class ProxyStatefulApiConfiguration implements BeanDefinitionRegistryPostProcessor {

  /**
   * Define Faces Be sure to specify the @Role comment here
   *
   * @return
   */
  @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
  @Bean
  public BeanFactoryStatefulApiAdvisor beanFactoryStatefulApiAdvisor() {
    BeanFactoryStatefulApiAdvisor advisor = new BeanFactoryStatefulApiAdvisor();

    advisor.setAdvice(statefulApiInvocationHandler());

    return advisor;
  }

  /**
   * Define Notifications
   *
   * @return
   */
  @Bean
  public StatefulApiInterceptor statefulApiInvocationHandler() {
    return new StatefulApiInterceptor();
  }

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
      throws BeansException {}

  @Override
  public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
      throws BeansException {
    
    // Ensure an auto-proxy creator is registered.
    AopConfigUtils.registerAutoProxyCreatorIfNecessary(registry);
  }

}
