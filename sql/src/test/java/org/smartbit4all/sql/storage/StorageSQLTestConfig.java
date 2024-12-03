package org.smartbit4all.sql.storage;

import org.smartbit4all.sql.config.SqlTestConfig;
import org.smartbit4all.storage.fs.StorageTestConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    StorageTestConfig.class,
    SqlTestConfig.class
})
public class StorageSQLTestConfig {

}
