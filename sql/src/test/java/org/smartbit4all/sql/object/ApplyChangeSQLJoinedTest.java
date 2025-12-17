package org.smartbit4all.sql.object;

import org.smartbit4all.api.object.ApplyChangeTestBase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = {
        ApplyChangeSQLTestConfig.class
    },
    properties = {
        "smartbit4all.objectapi.useReadCache=false",
        "storageSql.useTransactionCache=false",
        "storageSql.useJoinedObjectQuery=true"
    })
class ApplyChangeSQLJoinedTest extends ApplyChangeTestBase {
}
