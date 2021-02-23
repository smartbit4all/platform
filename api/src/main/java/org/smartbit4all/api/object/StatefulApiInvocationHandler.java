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
package org.smartbit4all.api.object;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.core.utility.ReflectionUtility;
import org.smartbit4all.domain.meta.EventPublisher;
import org.smartbit4all.domain.meta.EventPublisherImpl;

/**
 * This {@link InvocationHandler} implementation is responsible for the {@link ObjectEditing}
 * interface. It manages the {@link PublishEvents} and {@link NotifyListeners} annotated methods.
 * Collects all the {@link EventPublisher}s and notify their listeners when calling the
 * {@link NotifyListeners} annotated methods.
 * 
 * @author Zoltan Suller
 */
public class StatefulApiInvocationHandler implements InvocationHandler {

  private static final Logger log = LoggerFactory.getLogger(StatefulApiInvocationHandler.class);

  protected Object apiInstance;

  /**
   * Contains the publishers from the api by the annotated methods of the interface.
   */
  private final Map<String, EventPublisherImpl> publishers = new HashMap<>();

  public StatefulApiInvocationHandler(Object instance) {
    super();
    this.apiInstance = instance;
    Set<Method> methods = ReflectionUtility.allMethods(instance.getClass(),
        m -> m.isAnnotationPresent(PublishEvents.class) && m.getParameterCount() == 0);
    for (Method method : methods) {
      try {
        EventPublisher publisher = (EventPublisher) method.invoke(instance);
        PublishEvents publishEvents = method.getAnnotation(PublishEvents.class);
        EventPublisher existingPublisher = publishers.get(publishEvents.value());
        if (existingPublisher != null) {
          throw new IllegalArgumentException("More than one publisher found with the same name. ("
              + instance.getClass() + ": " + publishEvents.value() + ")");
        }
        if (publisher instanceof EventPublisherImpl) {
          publishers.put(publishEvents.value(), (EventPublisherImpl) publisher);
        }
      } catch (Exception e) {
        log.error("Unable to retrieve publisher " + method, e);
      }
    }
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    Object result = method.invoke(apiInstance, args);
    NotifyListeners notifiyListeners = method.getAnnotation(NotifyListeners.class);
    if (notifiyListeners != null) {
      Collection<EventPublisherImpl> publishersToCall;
      if (notifiyListeners.value() == null || notifiyListeners.value().length == 0) {
        publishersToCall = publishers.values();
      } else {
        publishersToCall = new ArrayList<>(publishers.size());
        for (int i = 0; i < notifiyListeners.value().length; i++) {
          publishersToCall.add(publishers.get(notifiyListeners.value()[i]));
        }
      }
      for (EventPublisherImpl publisherImpl : publishersToCall) {
        publisherImpl.notifyListeners();
      }

    }
    return result;
  }
}
