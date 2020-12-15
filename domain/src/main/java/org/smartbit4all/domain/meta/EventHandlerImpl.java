/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.domain.meta;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.core.SB4FunctionImpl;
import org.smartbit4all.core.utility.ReflectionUtility;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * This basic implementation is responsible for identifying the dependent properties.
 * 
 * @author Peter Boros
 */
public abstract class EventHandlerImpl extends SB4FunctionImpl<EventParameter, EventParameter>
    implements EventHandler {

  private static final Logger log = LoggerFactory.getLogger(EventHandlerImpl.class);

  /**
   * The root entity of the computation. The naming starts from here.
   */
  final EntityDefinition entity;

  /**
   * Creates a new logic based on an entity.
   * 
   * @param entity
   */
  protected EventHandlerImpl(EntityDefinition entity) {
    super();
    this.entity = entity;
  }

  /**
   * This cache contains the pre-processed dependency descriptors for the {@link EventHandler}s that
   * was already used in the JVM before.
   */
  private static final Cache<Class<? extends EventHandler>, EventParameterMeta> dependenciesByComputations =
      CacheBuilder.newBuilder().build();

  /**
   * TODO Store it in the input/output
   * 
   * The instance level parameter object that contains the related parameters and logics.
   */
  private EventParameter parameter;

  /**
   * It contains all the meta level parameters of the
   */
  private EventParameterMeta meta;

  @Override
  public EventParameter input() {
    return parameter();
  }

  @Override
  public EventParameter output() {
    return parameter();
  }

  @Override
  public EventParameterMeta meta() {
    return meta;
  }

  @Override
  public EventParameter parameter() {
    if (parameter != null) {
      return parameter;
    }
    try {
      parameter = new EventParameter(this,
          dependenciesByComputations.get(this.getClass(), new Callable<EventParameterMeta>() {

            @Override
            public EventParameterMeta call() {
              // TODO Could be problematic if the there is field overriding in the type hierarchy!
              Set<Field> properyFields = ReflectionUtility.allFields(this.getClass(),
                  f -> (f.getAnnotationsByType(PropertyWired.class).length
                      + f.getAnnotationsByType(PropertyDynamic.class).length) > 0);
              return new EventParameterMeta(properyFields);
            }

          }));
    } catch (ExecutionException e) {
      log.error("Error occured while analyzing the dependencies of the {} computation logic.", this,
          e);
    }
    return EventParameter.voidParameter;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(this.getClass().getName());
    // TODO Detail analytics about the current values of the dependencies.
    return sb.toString();
  }

  @Override
  public EntityDefinition entity() {
    return entity;
  }

}
