package org.smartbit4all.sql.config;

import org.smartbit4all.domain.meta.EntityConfiguration;
import org.smartbit4all.sql.storage.ApplicationRuntimeDef;
import org.smartbit4all.sql.storage.ObjectEntryDef;
import org.smartbit4all.sql.storage.ObjectVersionDef;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SQLObjectStorageEntityConfiguration extends EntityConfiguration {

  @Bean(ObjectEntryDef.ENTITY_NAME)
  public ObjectEntryDef objectEntryDef() {
    return createEntityProxy(ObjectEntryDef.class);
  }

  @Bean(ObjectVersionDef.ENTITY_NAME)
  public ObjectVersionDef objectVersionDef() {
    return createEntityProxy(ObjectVersionDef.class);
  }

  @Bean(ApplicationRuntimeDef.ENTITY_NAME)
  public ApplicationRuntimeDef applicationRuntimeDef() {
    return createEntityProxy(ApplicationRuntimeDef.class);
  }

}
