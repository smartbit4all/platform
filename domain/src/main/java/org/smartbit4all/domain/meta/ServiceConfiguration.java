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

import java.lang.reflect.Proxy;
import org.smartbit4all.core.SB4Configuration;
import org.smartbit4all.core.SB4ServiceInvocationHandler;
import org.smartbit4all.domain.service.CrudService;

public class ServiceConfiguration extends SB4Configuration {

  /**
   * Creates a CrudService implementation
   * 
   * @param def CrudService definition for an EntityDefinition
   * @param impl Implementation of the CrudService (RemoteCrudService. SQLCrudService)
   * @return
   */
  @SuppressWarnings("unchecked")
  protected <E extends EntityDefinition, T extends CrudService<E>, G extends CrudService<E>> T createCrudProxy(
      Class<T> def, G impl) {
    SB4ServiceInvocationHandler<G> invocationHandler = new SB4ServiceInvocationHandler<>(impl);
    T proxy =
        (T) Proxy.newProxyInstance(def.getClassLoader(), new Class[] {def}, invocationHandler);
    return proxy;
  }

}
