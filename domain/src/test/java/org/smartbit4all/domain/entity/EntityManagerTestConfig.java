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
package org.smartbit4all.domain.entity;

import java.util.List;
import org.smartbit4all.domain.config.DomainConfig;
import org.smartbit4all.domain.security.SecurityEntityConfiguration;
import org.smartbit4all.domain.service.entity.ConfigEntitySource;
import org.smartbit4all.domain.service.entity.EntityManager;
import org.smartbit4all.domain.service.entity.EntitySource;
import org.smartbit4all.domain.service.entity.SourceBasedEntityManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration

@Import({DomainConfig.class, SecurityEntityConfiguration.class})
public class EntityManagerTestConfig {

  public static final String ENTITY_SOURCE_SEC = "org.smartbit4all.domain.security";

  @Bean
  public EntityManager entityManager(List<EntitySource> sources) {
    return new SourceBasedEntityManager(sources);
  }

  @Bean
  public EntitySource securityEntitySource(ApplicationContext appCtx) {
    return new ConfigEntitySource(ENTITY_SOURCE_SEC, SecurityEntityConfiguration.class, appCtx);
  }

}
