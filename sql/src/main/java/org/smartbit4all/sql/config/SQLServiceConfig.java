package org.smartbit4all.sql.config;

import org.smartbit4all.domain.application.TimeManagementService;
import org.smartbit4all.sql.application.TimeManagementServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The basic service for the SQL layer to provide database specific configurations.
 * 
 * @author Peter Boros
 */
@Configuration
@EnableScheduling
public class SQLServiceConfig {

  @Bean
  public TimeManagementService timeManagementService() {
    return new TimeManagementServiceImpl();
  }

}
