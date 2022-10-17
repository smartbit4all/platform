package org.smartbit4all.api.applychange;

import java.io.IOException;
import java.net.URI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.retrieval.ObjectModel;
import org.smartbit4all.api.retrieval.ObjectRetrievalRequest;
import org.smartbit4all.api.retrieval.RetrievalApi;
import org.smartbit4all.api.retrieval.RetrievalRequest;
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

  @Autowired
  private RetrievalApi retrievalApi;

  @BeforeAll
  static void clearDirectory() throws IOException {
    TestFileUtil.clearTestDirectory();
  }

  @Test
  void testApplyChange() {

    String referenceToItems = SampleCategory.CONTAINER_ITEMS;
    ApplyChangeResult result = constructSampleCategory(referenceToItems);

    result.getProcessedRequests().entrySet().stream()
        .filter(e -> e.getKey().getDefinition().getClazz().equals(SampleCategory.class)).findFirst()
        .ifPresent(e -> {
          RetrievalRequest request = retrievalApi.request();
          ObjectRetrievalRequest startWith =
              request.startWith(e.getKey().getDefinition(), e.getValue());
          startWith
              .loadBy(e.getKey().getDefinition().getOutgoingReferences().get(referenceToItems));
          ObjectModel objectModel = request.load();
          Assertions.assertEquals(result.getProcessedRequests().size(), objectModel.size());
        });

  }

  @Test
  void testApplyChangeWithShadowList() {

    ApplyChangeResult result = constructSampleCategory(ApplyChangeTestConfig.SHADOW_ITEMS);

  }

  private ApplyChangeResult constructSampleCategory(String referenceToItems) {
    URI branchUri = null;

    SampleCategory rootContainer = new SampleCategory().name("root");

    ApplyChangeRequest applyChangeRequest = applyChangeApi.request(branchUri);
    ObjectChangeRequest ocrContainer = applyChangeRequest.createAsNew(MY_SCHEME, rootContainer);
    ReferenceListChange rlcItems = ocrContainer.referenceList(referenceToItems);

    int itemCounter = 5;
    for (int i = 0; i < itemCounter; i++) {
      SampleContainerItem item = new SampleContainerItem().name("item " + i);
      ObjectChangeRequest ocrItem = applyChangeRequest.createAsNew(MY_SCHEME, item);
      rlcItems.add(ocrItem);
    }

    ApplyChangeResult result =
        applyChangeApi
            .save(applyChangeRequest);

    Assertions.assertEquals(itemCounter + 1, result.getProcessedRequests().size());

    return result;
  }
}
