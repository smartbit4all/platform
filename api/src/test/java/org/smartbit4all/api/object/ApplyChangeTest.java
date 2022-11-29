package org.smartbit4all.api.object;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.net.URI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.object.bean.RetrievalMode;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.api.sample.bean.SampleContainerItem;
import org.smartbit4all.api.sample.bean.SampleDataSheet;
import org.smartbit4all.core.io.TestFileUtil;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectNodeList;
import org.smartbit4all.core.object.ObjectNodeReference;
import org.smartbit4all.core.object.ObjectNodes;
import org.smartbit4all.core.object.ObjectProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {ApplyChangeTestConfig.class})
class ApplyChangeTest {

  private static final String MY_SCHEME = "myScheme";

  @Autowired
  private ApplyChangeApi applyChangeApi;

  @Autowired
  private ObjectApi objectApi;

  @BeforeAll
  static void clearDirectory() throws IOException {
    TestFileUtil.clearTestDirectory();
  }

  @Test
  void testApplyChange() {

    URI categoryUri = constructSampleCategory(SampleCategory.CONTAINER_ITEMS);

    RetrievalRequest request = objectApi.request(SampleCategory.class)
        .add(SampleCategory.SUB_CATEGORIES)
        .add(SampleCategory.CONTAINER_ITEMS, SampleContainerItem.DATASHEET);

    Assertions.assertEquals(4, request.all().count());

    request.get(SampleCategory.SUB_CATEGORIES)
        .add(SampleCategory.CONTAINER_ITEMS, SampleContainerItem.DATASHEET);

    Assertions.assertEquals(6, request.all().count());

    ObjectNode objectNode = request.load(categoryUri);

    request.get(SampleCategory.SUB_CATEGORIES)
        .add(SampleCategory.CONTAINER_ITEMS, SampleContainerItem.DATASHEET);

    Assertions.assertEquals(6, request.all().count());

    RetrievalRequest req2 = objectApi.request(SampleCategory.class)
        .add(SampleCategory.SUB_CATEGORIES);
    req2.all()
        .filter(r -> r.getDefinition().getClazz() == SampleCategory.class)
        .forEach(r -> r.add(
            SampleCategory.CONTAINER_ITEMS, SampleContainerItem.DATASHEET));
    Assertions.assertEquals(6, request.all().count());

    ObjectProperties objectProperties =
        new ObjectProperties().property(SampleCategory.class, SampleCategory.NAME)
            .property(SampleContainerItem.class, SampleContainerItem.NAME)
            .property(SampleDataSheet.class, SampleDataSheet.NAME);

    System.out.println(ObjectNodes.versionTree(objectNode,
        objectProperties));

    // Assertions.assertEquals(result.getProcessedRequests().size(),
    // objectNode.allNodes().count());

    // Now we make some modification on the ObjectNode.

    ObjectNodeList subCategoryNodes = objectNode.list(SampleCategory.SUB_CATEGORIES);

    ObjectNodeReference subCatNodeRef = subCategoryNodes.get(0);
    assertTrue(subCatNodeRef.isLoaded());
    ObjectNode subCatNode = subCatNodeRef.get();
    subCatNode.setValue(SampleCategory.NAME, "modified sub category");
    SampleCategory modifiedSubCategory = (SampleCategory) subCatNode.getObject();
    assertEquals("modified sub category", modifiedSubCategory.getName());

    modifiedSubCategory.setName("even more modification");
    assertNotEquals("even more modification", subCatNode.getValue(SampleCategory.NAME));
    subCatNode.setObject(modifiedSubCategory);
    assertEquals("even more modification", subCatNode.getValue(SampleCategory.NAME));

    ObjectNodeList containerItemNodes = objectNode.list(SampleCategory.CONTAINER_ITEMS);

    int i = 0;
    for (ObjectNodeReference containerItemNodeRef : containerItemNodes.references()) {
      assertTrue(containerItemNodeRef.isLoaded());
      ObjectNode containerItemNode = containerItemNodeRef.get();
      containerItemNode.setValue(SampleContainerItem.NAME,
          containerItemNode.getObjectAsMap().get(SampleContainerItem.NAME) + "-modified");
      if (i % 2 == 0) {
        ObjectNodeReference datasheetNodeRef =
            containerItemNode.ref(SampleContainerItem.DATASHEET);
        assertTrue(datasheetNodeRef.isLoaded());
        ObjectNode datasheetNode = datasheetNodeRef.get();
        datasheetNode.setValue(SampleDataSheet.NAME, "modified");

        Object dataSheetValue = containerItemNode.getValue(SampleContainerItem.DATASHEET);
        assertTrue(dataSheetValue instanceof ObjectNodeReference);
        Object dataSheetName =
            containerItemNode.getValue(SampleContainerItem.DATASHEET, SampleDataSheet.NAME);
        assertEquals(datasheetNode, ((ObjectNodeReference) dataSheetValue).get());
        assertEquals("modified", dataSheetName);
      }
      i++;
    }
    containerItemNodes.get(containerItemNodes.size() - 1).clear();
    containerItemNodes.add(objectApi.create(
        MY_SCHEME,
        new SampleContainerItem().name("new item")));

    URI changedUri = objectApi.save(objectNode, null);

    ObjectNode objectNodeAfterSave = request.root().load(changedUri);

    System.out.println(ObjectNodes.versionTree(objectNodeAfterSave,
        objectProperties));

  }

  @Test
  void testApplyChangeWithShadowList() {

    constructSampleCategory(ApplyChangeTestConfig.SHADOW_ITEMS);

  }

  @Test
  void testObjectNodeOnly() {
    ObjectNode rootNode = objectApi.create(
        MY_SCHEME,
        new SampleCategory().name("root testObjectNodeOnly"));
    ObjectNodeList items = rootNode.list(SampleCategory.CONTAINER_ITEMS);
    int itemCounter = 5;
    for (int i = 0; i < itemCounter; i++) {
      ObjectNode itemNode = items.addNewObject(new SampleContainerItem().name("item " + i));
      itemNode
          .ref(SampleContainerItem.DATASHEET)
          .setNewObject(new SampleDataSheet().name("datasheet " + i));
    }
    URI rootCategoryUri = objectApi.save(rootNode);
    assertNotNull(rootCategoryUri);

    ObjectNode rootNodeLoaded = objectApi.request(SampleCategory.class)
        .add(SampleCategory.CONTAINER_ITEMS, SampleContainerItem.DATASHEET)
        .load(rootCategoryUri);

    Object item2datasheetName = rootNodeLoaded.getValue(
        SampleCategory.CONTAINER_ITEMS, "2",
        SampleContainerItem.DATASHEET,
        SampleDataSheet.NAME);
    assertEquals("datasheet 2", item2datasheetName);

    ObjectProperties objectProperties =
        new ObjectProperties().property(SampleCategory.class, SampleCategory.NAME)
            .property(SampleContainerItem.class, SampleContainerItem.NAME)
            .property(SampleDataSheet.class, SampleDataSheet.NAME);
    System.out.println(ObjectNodes.versionTree(rootNodeLoaded,
        objectProperties));

    ObjectNode rootNodeOnly = objectApi.load(rootCategoryUri);
    // first nothing is loaded
    assertFalse(rootNodeOnly.list(SampleCategory.CONTAINER_ITEMS).get(0).isLoaded());
    assertFalse(rootNodeOnly.list(SampleCategory.CONTAINER_ITEMS).get(1).isLoaded());
    // automatic navigation
    Object item0datasheetName = rootNodeOnly.getValue(
        SampleCategory.CONTAINER_ITEMS, "0",
        SampleContainerItem.DATASHEET,
        SampleDataSheet.NAME);
    assertEquals("datasheet 0", item0datasheetName);
    // after automatic navigation, first item is loaded, second is not
    assertTrue(rootNodeOnly.list(SampleCategory.CONTAINER_ITEMS).get(0).isLoaded());
    assertFalse(rootNodeOnly.list(SampleCategory.CONTAINER_ITEMS).get(1).isLoaded());

    // TODO this won't work since the ObjectNodeList partial update doesn't work yet.
    // ObjectNode item3datasheetNode =
    // rootNodeOnly.list(SampleCategory.CONTAINER_ITEMS).get(3).get()
    // .ref(SampleContainerItem.DATASHEET).get();
    // item3datasheetNode.setValue(SampleDataSheet.NAME, "datasheet 3 modified");
    // URI rootUriUpdated = objectApi.save(rootNodeOnly);
    //
    // Object item3datasheetName = objectApi.load(rootUriUpdated).getValue(
    // SampleCategory.CONTAINER_ITEMS, "3",
    // SampleContainerItem.DATASHEET,
    // SampleDataSheet.NAME);
    // assertEquals("datasheet 3 modified", item3datasheetName);

  }

  @Test
  void testReferenceChangeUri() {
    SampleCategory root = new SampleCategory().name("root testChangeUriOnly");
    SampleContainerItem item = new SampleContainerItem().name("container item");
    SampleDataSheet dataSheet = new SampleDataSheet().name("data sheet testReferenceChangeUri");

    ObjectNode rootNode = objectApi.create(MY_SCHEME, root);
    rootNode.list(SampleCategory.CONTAINER_ITEMS).addNewObject(item);
    URI rootUri = objectApi.save(rootNode);

    ObjectNode dataSheetNode = objectApi.create(MY_SCHEME, dataSheet);
    URI dataSheetUri = objectApi.save(dataSheetNode);

    RetrievalRequest requestRootAndContainerItems = objectApi.request(SampleCategory.class)
        .add(SampleCategory.CONTAINER_ITEMS);

    rootNode = requestRootAndContainerItems.load(rootUri);
    ObjectNodeList items = rootNode.list(SampleCategory.CONTAINER_ITEMS);
    assertNotNull(items);
    assertTrue(!items.isEmpty());
    ObjectNodeReference firstItemRef = items.get(0);

    URI containerItemOriginal = firstItemRef.getObjectUri();

    assertNotNull(firstItemRef);
    assertTrue(firstItemRef.isLoaded());
    assertTrue(firstItemRef.isPresent());

    ObjectNode firstItemNode = firstItemRef.get();
    ObjectNodeReference dataSheetRef = firstItemNode.ref(SampleContainerItem.DATASHEET);
    assertNotNull(dataSheetRef);
    assertFalse(dataSheetRef.isLoaded());
    assertFalse(dataSheetRef.isPresent());

    dataSheetRef.set(dataSheetUri);
    assertTrue(dataSheetRef.isPresent());
    dataSheetRef.clear();
    assertFalse(dataSheetRef.isPresent());
    dataSheetRef.set(dataSheetUri);

    URI rootUriAfterUpdate = objectApi.save(rootNode);

    // previous version
    RetrievalRequest requestExactVersion = objectApi
        .request(SampleCategory.class, RetrievalMode.EXACT_VERSION)
        .add(SampleCategory.CONTAINER_ITEMS, SampleContainerItem.DATASHEET);
    rootNode = requestExactVersion.load(rootUri);
    dataSheetNode = rootNode.list(SampleCategory.CONTAINER_ITEMS).get(0).get()
        .ref(SampleContainerItem.DATASHEET).get();
    assertNull(dataSheetNode, "DataSheet shouldn't exist in first version");

    RetrievalRequest requestNormal = objectApi
        .request(SampleCategory.class, RetrievalMode.NORMAL)
        .add(SampleCategory.CONTAINER_ITEMS, SampleContainerItem.DATASHEET);
    rootNode = requestNormal.load(rootUriAfterUpdate);
    dataSheetNode = rootNode.list(SampleCategory.CONTAINER_ITEMS).get(0).get()
        .ref(SampleContainerItem.DATASHEET).get();
    assertNotNull(dataSheetNode, "DataSheet must exist in first version");
    String dataSheetName = ((SampleDataSheet) dataSheetNode.getObject()).getName();
    assertEquals("data sheet testReferenceChangeUri", dataSheetName);

  }

  private URI constructSampleCategory(String referenceToItems) {
    URI branchUri = null;

    SampleCategory rootContainer = new SampleCategory().name("root");

    ApplyChangeRequest applyChangeRequest = applyChangeApi.request(branchUri);
    ObjectChangeRequest ocrContainer = applyChangeRequest.createAsNew(MY_SCHEME, rootContainer);
    ReferenceListChange rlcItems = ocrContainer.referenceList(referenceToItems);

    int itemCounter = 5;
    for (int i = 0; i < itemCounter; i++) {
      SampleContainerItem item = new SampleContainerItem().name("item " + i);
      ObjectChangeRequest ocrItem = applyChangeRequest.createAsNew(MY_SCHEME, item);

      ObjectChangeRequest ocrDatasheet =
          applyChangeRequest.createAsNew(MY_SCHEME, new SampleDataSheet().name("datasheet " + i));
      ReferenceValueChange referenceValue =
          ocrItem
              .referenceValue(SampleContainerItem.DATASHEET)
              .value(ocrDatasheet);

      rlcItems.add(ocrItem);
    }

    ReferenceListChange rlcSubCategories =
        ocrContainer.referenceList(SampleCategory.SUB_CATEGORIES);
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

    return result.getProcessedRequests().get(ocrContainer);
  }

}
