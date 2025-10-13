package org.smartbit4all.api.object;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = {ObjectApiTestConfig.class},
    properties = {
        "smartbit4all.objectapi.useReadCache=true",
        "storage.strategy=INDEXED"
    })
class ObjectApiTestIndexedStrategy extends ObjectApiTestBase {

}
