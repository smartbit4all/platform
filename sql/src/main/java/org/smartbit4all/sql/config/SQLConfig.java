package org.smartbit4all.sql.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.domain.config.DomainConfig;
import org.smartbit4all.sql.service.SQLCrudServiceConfiguration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({DomainConfig.class, SQLCrudServiceConfiguration.class, SQLServiceConfig.class})
public class SQLConfig implements InitializingBean {

  /**
   * The database configurations by name. The name in this case means that we add specific
   * configurations for the runtime and we set the configuration name for every module. We can use
   * the same configuration for all module but all of the modules can use different ones also. If we
   * have the same configuration with two module then we van pass a single select as a query using
   * entities from both module.
   */
  Map<String, SQLDBParameter> databaseConfigs = new HashMap<>();

  /**
   * This list is autowired by Spring. All the {@link SQLDBParameter} based beans will be added to
   * the list.
   */
  @Autowired
  private List<SQLDBParameter> databaseConfigList;

  /**
   * Initializing the databaseConfigs based on the autowired {@link #databaseConfigList}.
   */
  @Override
  public void afterPropertiesSet() {
    databaseConfigList.forEach(db -> {
      databaseConfigs.put(db.getName(), db);
    });
  }

  public SQLDBParameter db(String dbConfigName) {
    return databaseConfigs.get(dbConfigName);
  }

}
