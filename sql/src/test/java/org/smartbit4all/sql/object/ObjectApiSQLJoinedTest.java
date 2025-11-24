package org.smartbit4all.sql.object;

import org.smartbit4all.api.object.ObjectApiTestBase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = {
        ObjectApiSQLTestConfig.class
    },
    properties = {
        "smartbit4all.objectapi.useReadCache=true",
        "storageSql.useJoinedObjectQuery=true"
    })
public class ObjectApiSQLJoinedTest extends ObjectApiTestBase {

  public ObjectApiSQLJoinedTest() {
    super();
    checkPhysicalId = true;
  }

}
