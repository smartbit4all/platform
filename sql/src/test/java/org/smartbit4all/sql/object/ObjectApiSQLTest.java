package org.smartbit4all.sql.object;

import org.smartbit4all.api.object.ObjectApiTestBase;
import org.smartbit4all.sql.config.SqlTestConfig;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {
    ObjectApiSQLTestConfig.class,
    SqlTestConfig.class
})
public class ObjectApiSQLTest extends ObjectApiTestBase {

}
