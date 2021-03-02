package org.smartbit4all.api.object.proxy;

import org.springframework.aop.framework.autoproxy.InfrastructureAdvisorAutoProxyCreator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Role;

@Configuration
public class ProxyStatefulApiConfiguration {

    /**
    * Define Faces
    * Be sure to specify the @Role comment here
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

    /**
    * Be sure to declare Infrastructure AdvisorAutoProxyCreator for post-processing of bean s
    *
    * @return
    */
    @Bean
    public InfrastructureAdvisorAutoProxyCreator infrastructureAdvisorAutoProxyCreator() {
        return new InfrastructureAdvisorAutoProxyCreator();
    }
}