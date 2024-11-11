package org.smartbit4all.storage.fs;

import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {StorageFSTestConfigNoCache.class})
@TestInstance(Lifecycle.PER_CLASS)
public class StorageTestFSNoCache extends StorageTest {

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
