package org.smartbit4all.api.object;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.GroupsOfUser;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.core.io.TestFileUtil;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {ObjectDeepCopyApiTestConfig.class})
class ObjectDeepCopyApiTest {

  private static final Logger log = LoggerFactory.getLogger(ObjectDeepCopyApiTest.class);

  @Autowired
  private CopyApi copyApi;

  @Autowired
  private CompareApi compareApi;

  @Autowired
  private StorageApi storageApi;

  /**
   * This is the OrgApi scheme where we save the settings for the notify.
   */
  private Supplier<Storage> storage = new Supplier<Storage>() {

    private Storage storageInstance;

    @Override
    public Storage get() {
      if (storageInstance == null) {
        storageInstance = storageApi.get("testscheme");
      }
      return storageInstance;
    }
  };

  @BeforeAll
  static void clearDirectory() throws IOException {
    TestFileUtil.clearTestDirectory();
  }

  @Test
  void testDeepCopy() throws IOException {

    // Save the object hierarchy.
    long p1 = System.currentTimeMillis();
    URI rootUri;
    {
      User user = new User().name("User").email("user@company.hu");
      URI userUri = storage.get().saveAsNew(user);
      List<URI> groups = new ArrayList<>();
      for (int i = 0; i < 10; i++) {
        groups
            .add(storage.get().saveAsNew(new Group().name("group " + i).description("Group " + i)));
      }
      rootUri = storage.get().saveAsNew(new GroupsOfUser().userUri(userUri).groups(groups));
    }

    long p2 = System.currentTimeMillis();
    URI copyUri = copyApi.deepCopyByContainment(rootUri);

    long p3 = System.currentTimeMillis();
    assertEquals(true, compareApi.deepEquals(rootUri, copyUri));
    long p4 = System.currentTimeMillis();

    log.info("create: {}, copy: {}, compare: {}", p2 - p1, p3 - p2, p4 - p3);

  }

}
