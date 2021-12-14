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
package org.smartbit4all.core;

import java.util.List;
import org.springframework.stereotype.Service;

/**
 * In the service based programming API the service instances can be retrieved through this access
 * point. Every program (let it be back end or front end solution) has a session context and it is
 * the main entry point to the world of the service API. All other service can be found and
 * retrieved by the context API of the given service. The naming if the services is qualified by the
 * access path. The naming of the services must be resolved by the class definition of their
 * interfaces. So if we need other naming like String or whatever then we need to create a specific
 * service that can serve this request.
 * 
 * TODO SB4ServiceAPI.getContext().get(UserAccountIF.class)
 * 
 * @author Peter Boros
 *
 */
@Service
public interface SB4Context {

  /**
   * The get returns the service belongs to this context item. It doesn't discover the ancestor
   * contexts if the given service is not registered here.
   * 
   * @param <T> The interface type of the service.
   * @param serviceIF The class of the service interface like MyServiceIF.class
   * @return The service instance if it's registered here. There can be only one interface with this
   *         class because the key of the registry is the class itself.
   */
  <T extends SB4Service> T get(Class<T> serviceIF);

  /**
   * The find returns the service belongs to this context and its ancestors. It goes through the
   * ancestors to find the service. It returns the first occurrence.
   * 
   * @param <T> The interface type of the service.
   * @param serviceIF The class of the service interface like MyServiceIF.class
   * @return The service instance if it's found here or in any ancestor.
   */
  <T extends SB4Service> T find(Class<T> serviceIF);

  /**
   * The find returns every service registered all over the contexts. Be careful because you will
   * get all the instances let is be the default version at API level or its redefined version at
   * specific level.
   * 
   * @param <T> The interface type of the service.
   * @param serviceIF The class of the service interface like MyServiceIF.class
   * @return List of service instances registered at any level.
   */
  <T extends SB4Service> List<T> findAll(Class<T> serviceIF);

  /**
   * You can add a new service to the given context with this operation.
   * 
   * TODO It's not the final API. It will be redesigned with the injection framework implementation.
   * 
   * @param <T> The class of the {@link SB4Service}
   * @param invocationHandler The invocation handler for interface definition. If it's null then all
   *        the invocations go directly to the serviceInstance. There is no tricky aspect.
   * @param serviceIF The class of the service interface to implement. The annotation will guide us
   *        about the behavior of the service.
   * @return
   */
  <T extends SB4Service> T add(SB4ServiceInvocationHandler<T> invocationHandler,
      Class<T> serviceIF);

  /**
   * Register a service that is initiated at every get. You always get a new instance from this.
   * 
   * @param <T>
   * @param serviceIF The service interface.
   * @param implementationClass The implementation of the given service. It will be instantiated at
   *        every get.
   */
  <T extends SB4Service> void add(Class<T> serviceIF, Class<? extends T> implementationClass);

  /**
   * The most important accessor function in SB4 programming API. It retrieves the actual context
   * associated with the current {@link Thread}.
   * 
   * @return
   */
  public static SB4Context get() {
    return SB4ContextImpl.getCurrent();
  }

  public static SB4Context init() {
    SB4Context current = SB4ContextImpl.getCurrent();
    if (current == null) {
      current = new SB4ContextImpl();
      SB4ContextImpl.push(current);
    }
    return current;
  }

}
