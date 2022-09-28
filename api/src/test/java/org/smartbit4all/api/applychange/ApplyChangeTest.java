package org.smartbit4all.api.applychange;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.sample.bean.SampleCategory;
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

    SampleCategory newRootCategory = new SampleCategory();

    ApplyChangeResult result =
        applyChangeApi
            .save(applyChangeApi.request().createAsNew(MY_SCHEME, newRootCategory).request());

    Assertions.assertNull(result);

  }

}
