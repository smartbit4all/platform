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
package org.smartbit4all.domain.config;

import org.smartbit4all.core.config.CoreConfig;
import org.smartbit4all.domain.meta.MetaConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * The configuration for the platform domain. It's not a business domain it's the platform basic
 * package.
 * 
 * @author Peter Boros
 */
@Configuration
@Import({CoreConfig.class, MetaConfiguration.class, DomainServiceConfig.class})
public class DomainConfig {

  @Bean
  DomainAPI domainAPI() {
    return new DomainAPIImpl();
  }

}
