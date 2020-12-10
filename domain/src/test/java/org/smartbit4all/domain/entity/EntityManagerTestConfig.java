package org.smartbit4all.domain.entity;

import java.util.List;
import org.smartbit4all.domain.security.SecurityEntityConfiguration;
import org.smartbit4all.domain.service.entity.ConfigEntitySource;
import org.smartbit4all.domain.service.entity.EntityManager;
import org.smartbit4all.domain.service.entity.EntitySource;
import org.smartbit4all.domain.service.entity.SourceBasedEntityManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EntityManagerTestConfig {

  public static final String ENTITY_SOUCE_SEC = "security";

  @Bean
  public EntityManager entityManager(List<EntitySource> sources) {
    return new SourceBasedEntityManager(sources);
  }
  
  @Bean
  public EntitySource securityEntitySource(ApplicationContext appCtx) {
    return new ConfigEntitySource(ENTITY_SOUCE_SEC, SecurityEntityConfiguration.class, appCtx);
  }
  
}
