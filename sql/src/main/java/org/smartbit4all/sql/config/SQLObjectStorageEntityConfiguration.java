package org.smartbit4all.sql.config;

import org.smartbit4all.domain.meta.EntityConfiguration;
import org.smartbit4all.sql.storage.ObjectEntryDef;
import org.smartbit4all.sql.storage.ObjectVersionDef;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SQLObjectStorageEntityConfiguration extends EntityConfiguration {

  @Bean(ObjectEntryDef.ENTITY_NAME)
  public ObjectEntryDef objectEntryDef() {
    ObjectEntryDef definition = createEntityProxy(ObjectEntryDef.class);
    return definition;
  }

  @Bean(ObjectVersionDef.ENTITY_NAME)
  public ObjectVersionDef objectVersionDef() {
    ObjectVersionDef definition = createEntityProxy(ObjectVersionDef.class);
    return definition;
  }

}
