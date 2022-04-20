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
import org.smartbit4all.core.SB4Configuration;
import org.smartbit4all.core.SB4Service;
import org.smartbit4all.core.SB4ServiceInvocationHandler;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

public class EntityConfiguration extends SB4Configuration {

  @Bean
  @Scope(BeanDefinition.SCOPE_PROTOTYPE)
  public <T extends EntityDefinition> EntityService<T> defaultEntityService(T entityDef) {
    return new EntityServiceImpl<>(entityDef);
  }

  @Override
  protected <T extends SB4Service> T createProxy(Class<T> def, T impl) {
    if (impl instanceof EntitySetup) {
      SB4ServiceInvocationHandler<T> invocationHandler = new SB4ServiceInvocationHandler<>(impl);
      @SuppressWarnings("unchecked")
      T proxy = (T) Proxy.newProxyInstance(def.getClassLoader(),
          new Class[] {def, EntitySetup.class}, invocationHandler);
      return proxy;
    }
    return super.createProxy(def, impl);
  }

  @SuppressWarnings("unchecked")
  protected <T extends EntityDefinition> T createEntityProxy(Class<T> def) {
    EntityDefinitionInvocationHandler<T> entityDefInvocationHandler =
        EntityDefinitionInvocationHandler.create(def);
    T entityProxy = (T) Proxy.newProxyInstance(def.getClassLoader(),
        new Class[] {def, EntitySetup.class}, entityDefInvocationHandler);

    SB4ServiceInvocationHandler<T> serviceInvocationHandler =
        new SB4ServiceInvocationHandler<>(entityProxy);
    T proxy = (T) Proxy.newProxyInstance(def.getClassLoader(), new Class[] {def, EntitySetup.class},
        serviceInvocationHandler);
    return proxy;
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
