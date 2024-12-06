package org.smartbit4all.sql.object;

import org.smartbit4all.api.object.ApplyChangeTestBase;
import org.smartbit4all.sql.config.SqlTestConfig;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {
    ApplyChangeSQLTestConfig.class,
    SqlTestConfig.class
})
class ApplyChangeSQLTest extends ApplyChangeTestBase {
}
