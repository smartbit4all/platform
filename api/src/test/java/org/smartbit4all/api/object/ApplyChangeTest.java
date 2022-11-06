package org.smartbit4all.api.object;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.api.sample.bean.SampleContainerItem;
import org.smartbit4all.api.sample.bean.SampleDataSheet;
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
    ObjectDefinition<SampleContainerItem> definitionItem =
        objectApi.definition(SampleContainerItem.class);
    SampleCategoryResult categoryResult = constructSampleCategory(referenceToItems);

    ApplyChangeResult result = categoryResult.result;

    ObjectRetrievalRequest request = retrievalApi.request(SampleCategory.class);
    request.loadBy(definition.getOutgoingReferences().get(referenceToItems));
    request.loadBy(definition.getOutgoingReference(SampleCategory.SUB_CATEGORIES));
    request.loadBy(definitionItem.getOutgoingReference(SampleContainerItem.DATASHEET));
    ObjectNode objectNode = retrievalApi.load(request, categoryResult.uri);

    ObjectProperties objectProperties =
        new ObjectProperties().property(SampleCategory.class, SampleCategory.NAME)
            .property(SampleContainerItem.class, SampleContainerItem.NAME)
            .property(SampleDataSheet.class, SampleDataSheet.NAME);

    System.out.println(ObjectNodes.versionTree(objectNode,
        objectProperties));

    Assertions.assertEquals(result.getProcessedRequests().size(),
        objectNode.allNodes().count());

    // Now we make some modification on the ObjectNode.

    List<ObjectNode> subCategoryNodes = objectNode
        .referenceNodeList(definition.getOutgoingReference(SampleCategory.SUB_CATEGORIES));

    subCategoryNodes.get(0).setValue(SampleCategory.NAME, "modified sub category");


    List<ObjectNode> containerItemNodes = objectNode
        .referenceNodeList(definition.getOutgoingReference(referenceToItems));

    int i = 0;
    for (ObjectNode containerItemNode : containerItemNodes) {
      containerItemNode.setValue(SampleContainerItem.NAME,
          containerItemNode.getObjectAsMap().get(SampleContainerItem.NAME) + "-modified");
      if (i % 2 == 0) {
        ObjectNode datasheetNode = containerItemNode
            .referenceNode(definitionItem.getOutgoingReference(SampleContainerItem.DATASHEET));
        datasheetNode.setValue(SampleDataSheet.NAME, "modified");
      }
      i++;
    }
    containerItemNodes.remove(containerItemNodes.size() - 1);
    containerItemNodes.add(new ObjectNode(objectApi.definition(SampleContainerItem.class),
        MY_SCHEME, new SampleContainerItem().name("new item")));

    URI changedUri = applyChangeApi.applyChanges(objectNode, null);

    ObjectNode objectNodeAfterSave = retrievalApi.load(request, changedUri);

    System.out.println(ObjectNodes.versionTree(objectNodeAfterSave,
        objectProperties));

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

      ObjectChangeRequest ocrDatasheet =
          applyChangeRequest.createAsNew(MY_SCHEME, new SampleDataSheet().name("datasheet " + i));
      ReferenceValueChange referenceValue =
          ocrItem
              .referenceValue(
                  ocrItem.getDefinition().getOutgoingReference(SampleContainerItem.DATASHEET))
              .value(ocrDatasheet);

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

    Assertions.assertEquals(itemCounter * 2 + 4, result.getProcessedRequests().size());

    URI uri = result.getProcessedRequests().get(ocrContainer);

    return new SampleCategoryResult(result, uri);
  }

}