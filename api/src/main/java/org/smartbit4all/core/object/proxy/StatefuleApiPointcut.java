package org.smartbit4all.core.object.proxy;

import java.lang.reflect.Method;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.core.object.NotifyListeners;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;

/**
 * This {@link StaticMethodMatcherPointcut} implementation is responsible for 
 * deciding whether {@link StatefulApiInterceptor} is needed or not when calling a method.
 * 
 * @author Zoltan Suller
 */
public class StatefuleApiPointcut extends StaticMethodMatcherPointcut {

  private static final Logger log = LoggerFactory.getLogger(StatefuleApiPointcut.class);
  
  
    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        return hasAnnotation(method);
    }

    private Boolean hasAnnotation(Method method) {
      // The @NotifyListeners annotation property on the lookup method
      AnnotationAttributes attributes = AnnotatedElementUtils.findMergedAnnotationAttributes(
              method, NotifyListeners.class, false, false);
      boolean result = Objects.nonNull(attributes);
      return result;
    }
}