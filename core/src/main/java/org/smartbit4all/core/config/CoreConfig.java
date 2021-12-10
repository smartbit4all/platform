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
package org.smartbit4all.core.config;

import org.smartbit4all.core.object.ObservablePublisherWrapper;
import org.smartbit4all.core.object.ObservablePublisherWrapperImpl;
import org.smartbit4all.core.object.proxy.ProxyStatefulApiConfiguration;
import org.smartbit4all.core.reactive.ObjectChangePublisher;
import org.smartbit4all.core.reactive.ObjectChangePublisherImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;


/**
 * The configuration for the platform domain. It's not a business domain it's the platform basic
 * package.
 * 
 * @author Peter Boros
 */

@Configuration
@Import({CoreServiceConfig.class, ProxyStatefulApiConfiguration.class})
public class CoreConfig {

  @Bean
  @ConditionalOnMissingBean(type = "ObservablePublisherWrapper")
  public ObservablePublisherWrapper publisherWrapperFallback() {
    return new ObservablePublisherWrapperImpl();
  }

  @Bean
  @Scope("prototype")
  @ConditionalOnMissingBean(type = "ObjectChangePublisher")
  public ObjectChangePublisher<?> objectChangePublisherDefault() {
    return new ObjectChangePublisherImpl<>();
  }

}
