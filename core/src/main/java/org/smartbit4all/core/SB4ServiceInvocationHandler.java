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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * This is the super class of all {@link InvocationHandler} created for the SmartBit4All service
 * programming API.
 * 
 * @author Peter Boros
 *
 */
public class SB4ServiceInvocationHandler<S extends SB4Service> implements InvocationHandler {

  /**
   * This is original implementation of the service. From programming level we cann't reach this.
   */
  protected S serviceInstance;

  /**
   * New instance of the {@link InvocationHandler} that wraps the {@link #serviceInstance}.
   * 
   * @param serviceInstance The non null service instance.
   */
  public SB4ServiceInvocationHandler(S serviceInstance) {
    super();
    if (serviceInstance == null) {
      throw new NullPointerException("Unable to initiate null service!");
    }

    this.serviceInstance = serviceInstance;
  }

  /**
   * The most important function in the SB4 universe.
   */
  @Override
  public final Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    return method.invoke(serviceInstance, args);
  }

  /**
   * We can get the instance object of the service.
   * 
   * @return
   */
  final S getServiceInstance() {
    return serviceInstance;
  }

}
