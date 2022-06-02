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
package org.smartbit4all.domain.meta;

import java.lang.reflect.Proxy;
import java.util.Map;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

public class EntityConfiguration {

  @SuppressWarnings("unchecked")
  protected <T extends EntityDefinition> T createEntityProxy(Class<T> def) {
    EntityDefinitionInvocationHandler<T> entityDefInvocationHandler =
        EntityDefinitionInvocationHandler.create(def);
    T entityProxy = (T) Proxy.newProxyInstance(def.getClassLoader(),
        new Class[] {def, EntitySetup.class}, entityDefInvocationHandler);

    return entityProxy;
  }

  @EventListener
  public void onApplicationEvent(ContextRefreshedEvent event) {
    Map<String, EntitySetup> setups =
        event.getApplicationContext().getBeansOfType(EntitySetup.class);
    // for (EntitySetup entity : setups.values()) {
    // entity.initContext();
    // }
    for (EntitySetup entity : setups.values()) {
      entity.setupProperties();
    }
    for (EntitySetup entity : setups.values()) {
      entity.setupReferences();
    }
    for (EntitySetup entity : setups.values()) {
      entity.setupReferredProperties();
    }
    for (EntitySetup entity : setups.values()) {
      entity.finishSetup();
    }
  }

}
