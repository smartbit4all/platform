package org.smartbit4all.sql.collection;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.smartbit4all.api.collection.CollectionApiTestBase;
import org.smartbit4all.sql.config.SqlTestConfig;
import org.springframework.boot.test.context.SpringBootTest;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(classes = {
    CollectionSQLTestConfig.class,
    SqlTestConfig.class
})
public class CollectionApiSQLTest extends CollectionApiTestBase {

}
