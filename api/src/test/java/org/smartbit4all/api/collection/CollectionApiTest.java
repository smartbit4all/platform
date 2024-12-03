package org.smartbit4all.api.collection;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.boot.test.context.SpringBootTest;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(classes = {
    CollectionTestConfig.class
})
public class CollectionApiTest extends CollectionApiTestBase {

}
