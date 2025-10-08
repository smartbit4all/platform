package org.smartbit4all.sql.collection;

import org.smartbit4all.api.collection.CollectionTestBaseConfig;
import org.smartbit4all.sql.config.SqlTestConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    SqlTestConfig.class
})
public class CollectionSQLTestConfig extends CollectionTestBaseConfig {

}
