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
