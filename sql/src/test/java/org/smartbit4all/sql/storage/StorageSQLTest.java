package org.smartbit4all.sql.storage;

import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.smartbit4all.storage.fs.FSTestBean;
import org.smartbit4all.storage.fs.StorageTest;
import org.smartbit4all.storage.fs.StorageTestConfig;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = {
        StorageSQLTestConfig.class
    },
    properties = {
        "platform.sql.temptable-autocreate.enabled=true"
    })
// @DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class StorageSQLTest extends StorageTest {

  @BeforeAll
  void init() throws IOException {
    Storage storage = storageApi.get(StorageTestConfig.TESTSCHEME);
    StorageObject<FSTestBean> storageObject = storage.instanceOf(FSTestBean.class);

    storageObject.setObject(new FSTestBean("collectionsTest"));

    collectionsTestUri = storage.save(storageObject);
    System.getenv().forEach((k, v) -> {
      System.out.println(k + ":" + v);
    });
  }

}
