package org.smartbit4all.api.applychange;

import java.io.IOException;
import java.net.URI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.api.sample.bean.SampleContainerItem;
import org.smartbit4all.core.io.TestFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {ApplyChangeTestConfig.class})
class ApplyChangeTest {

  private static final String MY_SCHEME = "myScheme";
  @Autowired
  private ApplyChangeApi applyChangeApi;

  @BeforeAll
  static void clearDirectory() throws IOException {
    TestFileUtil.clearTestDirectory();
  }

  @Test
  void testApplyChange() {

    URI branchUri = null;

    SampleCategory rootContainer = new SampleCategory().name("root");

    ApplyChangeRequest applyChangeRequest = applyChangeApi.request(branchUri);
    ObjectChangeRequest ocrContainer = applyChangeRequest.createAsNew(MY_SCHEME, rootContainer);
    ReferenceListChange rlcItems = ocrContainer.referenceList(SampleCategory.CONTAINER_ITEMS);

    for (int i = 0; i < 5; i++) {
      SampleContainerItem item = new SampleContainerItem().name("item " + i);
      ObjectChangeRequest ocrItem = applyChangeRequest.createAsNew(MY_SCHEME, item);
      rlcItems.add(ocrItem);
    }

    ApplyChangeResult result =
        applyChangeApi
            .save(applyChangeRequest);

    Assertions.assertNull(result);

  }

  @Test
  void testApplyChangeWithShadowList() {

    URI branchUri = null;

    SampleCategory rootContainer = new SampleCategory().name("root");

    ApplyChangeRequest applyChangeRequest = applyChangeApi.request(branchUri);
    ObjectChangeRequest ocrContainer = applyChangeRequest.createAsNew(MY_SCHEME, rootContainer);
    ReferenceListChange rlcItems = ocrContainer.referenceList(ApplyChangeTestConfig.SHADOW_ITEMS);

    for (int i = 0; i < 5; i++) {
      SampleContainerItem item = new SampleContainerItem().name("item " + i);
      ObjectChangeRequest ocrItem = applyChangeRequest.createAsNew(MY_SCHEME, item);
      rlcItems.add(ocrItem);
    }

    ApplyChangeResult result =
        applyChangeApi
            .save(applyChangeRequest);

    Assertions.assertNull(result);

  }
}
