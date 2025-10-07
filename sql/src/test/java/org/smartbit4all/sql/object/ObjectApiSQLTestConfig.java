package org.smartbit4all.sql.object;

import org.smartbit4all.api.object.ObjectApiTestConfigBase;
import org.smartbit4all.sql.config.SqlTestConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    SqlTestConfig.class
})
public class ObjectApiSQLTestConfig extends ObjectApiTestConfigBase {

}
