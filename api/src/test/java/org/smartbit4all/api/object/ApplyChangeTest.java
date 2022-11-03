package org.smartbit4all.api.object;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.api.sample.bean.SampleContainerItem;
import org.smartbit4all.core.io.TestFileUtil;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {ApplyChangeTestConfig.class})
class ApplyChangeTest {

  private static final String MY_SCHEME = "myScheme";

  @Autowired
  private ApplyChangeApi applyChangeApi;

  @Autowired
  private RetrievalApi retrievalApi;

  @Autowired
  private ObjectApi objectApi;

  @BeforeAll
  static void clearDirectory() throws IOException {
    TestFileUtil.clearTestDirectory();
  }

  @Test
  void testApplyChange() {

    String referenceToItems = SampleCategory.CONTAINER_ITEMS;
    ObjectDefinition<SampleCategory> definition = objectApi.definition(SampleCategory.class);
    SampleCategoryResult categoryResult = constructSampleCategory(referenceToItems);

    ApplyChangeResult result = categoryResult.result;

    ObjectRetrievalRequest request = retrievalApi.request(SampleCategory.class);
    request.loadBy(definition.getOutgoingReferences().get(referenceToItems));
    request.loadBy(definition.getOutgoingReference(SampleCategory.SUB_CATEGORIES));
    ObjectNode objectNode = retrievalApi.load(request, categoryResult.uri);
    Assertions.assertEquals(result.getProcessedRequests().size(),
        objectNode.allNodes().count());

    // Now we make some modification on the ObjectNode.

    List<ObjectNode> containerItemNodes = objectNode
        .referenceNodeList(definition.getOutgoingReference(referenceToItems));

    for (ObjectNode containerItemNode : containerItemNodes) {
      containerItemNode.setValue(SampleContainerItem.NAME,
          containerItemNode.getObjectAsMap().get(SampleContainerItem.NAME) + "-modified");
    }
    containerItemNodes.remove(containerItemNodes.size() - 1);
    containerItemNodes.add(new ObjectNode(objectApi.definition(SampleContainerItem.class),
        MY_SCHEME, new SampleContainerItem().name("new item")));

    applyChangeApi.applyChanges(objectNode, null);

  }

  @Test
  void testApplyChangeWithShadowList() {

    SampleCategoryResult result = constructSampleCategory(ApplyChangeTestConfig.SHADOW_ITEMS);

  }

  private class SampleCategoryResult {

    ApplyChangeResult result;

    URI uri;

    public SampleCategoryResult(ApplyChangeResult result, URI uri) {
      super();
      this.result = result;
      this.uri = uri;
    }

  }

  private SampleCategoryResult constructSampleCategory(String referenceToItems) {
    URI branchUri = null;

    SampleCategory rootContainer = new SampleCategory().name("root");

    ObjectDefinition<SampleCategory> definition = objectApi.definition(SampleCategory.class);

    ApplyChangeRequest applyChangeRequest = applyChangeApi.request(branchUri);
    ObjectChangeRequest ocrContainer = applyChangeRequest.createAsNew(MY_SCHEME, rootContainer);
    ReferenceListChange rlcItems =
        ocrContainer.referenceList(definition.getOutgoingReference(referenceToItems));

    int itemCounter = 5;
    for (int i = 0; i < itemCounter; i++) {
      SampleContainerItem item = new SampleContainerItem().name("item " + i);
      ObjectChangeRequest ocrItem = applyChangeRequest.createAsNew(MY_SCHEME, item);
      rlcItems.add(ocrItem);
    }

    ReferenceListChange rlcSubCategories =
        ocrContainer.referenceList(definition.getOutgoingReference(SampleCategory.SUB_CATEGORIES));
    int subCategoryCounter = 3;
    for (int i = 0; i < subCategoryCounter; i++) {
      SampleCategory subCategory = new SampleCategory().name("sub category " + i);
      ObjectChangeRequest ocrSubCategory = applyChangeRequest.createAsNew(MY_SCHEME, subCategory);
      rlcSubCategories.add(ocrSubCategory);
    }


    ApplyChangeResult result =
        applyChangeApi
            .save(applyChangeRequest);

    Assertions.assertEquals(itemCounter + 4, result.getProcessedRequests().size());

    URI uri = result.getProcessedRequests().get(ocrContainer);

    return new SampleCategoryResult(result, uri);
  }

}
