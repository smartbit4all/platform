/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.core.object.proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.core.event.EventPublisher;
import org.smartbit4all.core.event.EventPublisherImpl;
import org.smartbit4all.core.object.NotifyListeners;
import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.core.object.PublishEvents;
import org.smartbit4all.core.utility.ReflectionUtility;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * This {@link MethodInterceptor} implementation is responsible for the {@link ObjectEditing}
 * interface. It manages the {@link PublishEvents} and {@link NotifyListeners} annotated methods.
 * Collects all the {@link EventPublisher}s and notify their listeners when calling the
 * {@link NotifyListeners} annotated methods.
 * 
 * @author Zoltan Suller
 */
public class StatefulApiInterceptor implements MethodInterceptor {

  private static final Logger log = LoggerFactory.getLogger(StatefulApiInterceptor.class);

  /**
   * The cache of the publishers.
   */
  private static final Cache<Class<?>, Map<String, Method>> cache =
      CacheBuilder.newBuilder().build();

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    Object result = invocation.proceed();
    Map<String, Method> publishers =
        cache.get(invocation.getMethod().getDeclaringClass(), () -> getPublishers(invocation));
    NotifyListeners notifiyListeners = invocation.getMethod().getAnnotation(NotifyListeners.class);
    Collection<Method> publishersToCall;
    
    if (notifiyListeners == null || notifiyListeners.value() == null || notifiyListeners.value().length == 0) {
      publishersToCall = publishers.values();
    } else {
      publishersToCall = new ArrayList<>(publishers.size());
      for (int i = 0; i < notifiyListeners.value().length; i++) {
        publishersToCall.add(publishers.get(notifiyListeners.value()[i]));
      }
    }
    
    for (Method publisherImpl : publishersToCall) {
      EventPublisher publisher = (EventPublisher) publisherImpl.invoke(invocation.getThis());
      if (publisher instanceof EventPublisherImpl) {
        ((EventPublisherImpl) publisher).notifyListeners();
      }
    }
    return result;
  }

  private Map<String, Method> getPublishers(MethodInvocation invocation) {
    Map<String, Method> publishers = new HashMap<>();
    Set<Method> methods = ReflectionUtility.allMethods(invocation.getMethod().getDeclaringClass(),
        m -> m.isAnnotationPresent(PublishEvents.class) && m.getParameterCount() == 0);
    for (Method method : methods) {
      try {
        PublishEvents publishEvents = method.getAnnotation(PublishEvents.class);
        Method existingPublisherMethod = publishers.get(publishEvents.value());
        if (existingPublisherMethod != null) {
          throw new IllegalArgumentException("More than one publisher found with the same name. ("
              + invocation.getMethod().getDeclaringClass() + ": " + publishEvents.value() + ")");
        }
        publishers.put(publishEvents.value(), method);
      } catch (Exception e) {
        log.error("Unable to retrieve publisher " + method, e);
      }
    }
    return publishers;
  }

}
