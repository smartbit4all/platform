package org.smartbit4all.core.object.proxy;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;

public class BeanFactoryStatefulApiAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    /**
    * Define the point of tangency
    */
    private final StatefuleApiPointcut point = new StatefuleApiPointcut();

    @Override
    public Pointcut getPointcut() {
        return this.point;
    }
}