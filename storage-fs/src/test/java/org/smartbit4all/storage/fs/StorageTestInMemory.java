package org.smartbit4all.storage.fs;

import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageObject;

// @SpringBootTest(classes = {StorageInMemoryTestConfig.class})
@TestInstance(Lifecycle.PER_CLASS)
@Disabled
public class StorageTestInMemory extends StorageTest {

  @BeforeAll
  void init() throws IOException {
    Storage storage = storageApi.get(StorageTestConfig.TESTSCHEME);
    StorageObject<FSTestBean> storageObject = storage.instanceOf(FSTestBean.class);

    storageObject.setObject(new FSTestBean("collectionsTest"));

    collectionsTestUri = storage.save(storageObject);

  }

}
