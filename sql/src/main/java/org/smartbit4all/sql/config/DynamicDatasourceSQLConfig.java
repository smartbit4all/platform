package org.smartbit4all.sql.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import org.smartbit4all.api.object.DataSourceContextHolder;
import org.smartbit4all.sql.DynamicRoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

// Egyelőre az nem lett megoldva, hogy ez a működés és a platformos kölün adatbázis használata az
// EntityDef-ekhez működés egyszerre menjen.
@Configuration
@Import(SQLConfig.class)
public class DynamicDatasourceSQLConfig {

  @Autowired
  private Environment environment;

  @Bean
  @Primary
  public DataSource routingDataSource(DataSource primaryDataSource) {

    Map<Object, Object> targetDataSources = new HashMap<>();
    DataSource defaultDataSource = primaryDataSource;
    targetDataSources.put(DataSourceContextHolder.DATASOURCE_DEFAULT, primaryDataSource);

    Set<String> dataSourceNames = extractDataSourceNames(environment);

    for (String name : dataSourceNames) {
      HikariConfig config = createHikariConfig(name);

      HikariDataSource dataSource = new HikariDataSource(config);
      targetDataSources.put(name, dataSource);

      if (defaultDataSource == null) {
        defaultDataSource = dataSource;
      }
    }

    DynamicRoutingDataSource routingDataSource = new DynamicRoutingDataSource();
    routingDataSource.setTargetDataSources(targetDataSources);
    routingDataSource.setDefaultTargetDataSource(defaultDataSource);
    routingDataSource.afterPropertiesSet();
    return routingDataSource;
  }

  @Bean
  @ConfigurationProperties(prefix = "spring.datasource.hikari")
  public DataSource primaryDataSource() {

    HikariDataSource dataSource = new HikariDataSource();
    dataSource.setDriverClassName(environment.getProperty("spring.datasource.driver-class-name"));
    dataSource.setJdbcUrl(environment.getProperty("spring.datasource.url"));
    dataSource.setUsername(environment.getProperty("spring.datasource.username"));
    dataSource.setPassword(environment.getProperty("spring.datasource.password"));
    String poolName = environment.getProperty("spring.datasource.hikari.pool-name");
    if (StringUtils.hasText(poolName)) {
      dataSource.setPoolName(poolName);
    }
    return dataSource;
  }

  private HikariConfig createHikariConfig(String name) {
    String prefix = "spring." + name + "-datasource";

    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(environment.getProperty(prefix + ".url")); // maps to jdbcUrl
    config.setUsername(environment.getProperty(prefix + ".username"));
    config.setPassword(environment.getProperty(prefix + ".password"));
    config.setDriverClassName(environment.getProperty(prefix + ".driver-class-name"));

    String maxPoolSize = environment.getProperty(prefix + ".hikari.maximum-pool-size");
    if (maxPoolSize != null) {
      config.setMaximumPoolSize(Integer.parseInt(maxPoolSize));
    }

    String minIdle = environment.getProperty(prefix + ".hikari.minimum-idle");
    if (minIdle != null) {
      config.setMinimumIdle(Integer.parseInt(minIdle));
    }

    String idleTimeout = environment.getProperty(prefix + ".hikari.idle-timeout");
    if (idleTimeout != null) {
      config.setIdleTimeout(Long.parseLong(idleTimeout));
    }

    String maxLifeTime = environment.getProperty(prefix + ".hikari.max-lifetime");
    if (maxLifeTime != null) {
      config.setMaxLifetime(Long.parseLong(maxLifeTime));
    }

    String connectionTimeout = environment.getProperty(prefix + ".hikari.connection-timeout");
    if (connectionTimeout != null) {
      config.setConnectionTimeout(Long.parseLong(connectionTimeout));
    }

    String poolName = environment.getProperty(prefix + ".hikari.pool-name");
    if (poolName != null) {
      config.setPoolName(poolName);
    }
    return config;
  }

  private Set<String> extractDataSourceNames(Environment env) {
    Set<String> names = new HashSet<>();

    for (PropertySource<?> ps : ((AbstractEnvironment) env).getPropertySources()) {
      if (ps instanceof EnumerablePropertySource<?> eps) {
        for (String key : eps.getPropertyNames()) {
          if (key.startsWith("spring.") && key.contains("-datasource.url")) {
            String name = key.substring("spring.".length(), key.indexOf("-datasource.url"));
            names.add(name);
          }
        }
      }
    }

    return names;
  }
}
