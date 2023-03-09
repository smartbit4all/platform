package org.smartbit4all.api.object;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.object.bean.RetrievalMode;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.api.sample.bean.SampleContainerItem;
import org.smartbit4all.api.sample.bean.SampleDataSheet;
import org.smartbit4all.api.sample.bean.SampleLinkObject;
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

  public static final String MY_SCHEME = "myScheme";

  @Autowired
  private ApplyChangeApi applyChangeApi;

  @Autowired
  private ObjectApi objectApi;

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
    subCatNode.setValue("modified sub category", SampleCategory.NAME);
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
      containerItemNode.setValue(
          containerItemNode.getObjectAsMap().get(SampleContainerItem.NAME) + "-modified",
          SampleContainerItem.NAME);
      if (i % 2 == 0) {
        ObjectNodeReference datasheetNodeRef =
            containerItemNode.ref(SampleContainerItem.DATASHEET);
        assertTrue(datasheetNodeRef.isLoaded());
        ObjectNode datasheetNode = datasheetNodeRef.get();
        datasheetNode.setValue("modified", SampleDataSheet.NAME);

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
    List<ObjectNode> itemNodes = new ArrayList<>();
    for (int i = 0; i < itemCounter; i++) {
      ObjectNode itemNode = items.addNewObject(new SampleContainerItem().name("item " + i));
      itemNode
          .ref(SampleContainerItem.DATASHEET)
          .setNewObject(new SampleDataSheet().name("datasheet " + i));
      itemNodes.add(itemNode);
    }
    URI rootCategoryUri = objectApi.save(rootNode);
    assertNotNull(rootCategoryUri);
    assertEquals(rootCategoryUri, rootNode.getResultUri());
    assertNotNull(itemNodes.get(0).getResultUri());

    ObjectNode rootNodeLoaded = objectApi.request(SampleCategory.class)
        .add(SampleCategory.CONTAINER_ITEMS, SampleContainerItem.DATASHEET)
        .load(rootCategoryUri);

    URI item1nodeUriLoaded =
        rootNodeLoaded.list(SampleCategory.CONTAINER_ITEMS).get(1).getObjectUri();
    URI item1nodeUriResult = rootNode.list(SampleCategory.CONTAINER_ITEMS).get(1).getResultUri();
    URI item1nodeUriSaved = itemNodes.get(1).getResultUri();

    assertEquals(item1nodeUriLoaded, item1nodeUriResult);
    assertEquals(item1nodeUriLoaded, item1nodeUriSaved);


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

    // load and update 3rd item
    ObjectNode item3datasheetNode =
        rootNodeOnly.list(SampleCategory.CONTAINER_ITEMS).get(3).get()
            .ref(SampleContainerItem.DATASHEET).get();
    item3datasheetNode.setValue("datasheet 3 modified", SampleDataSheet.NAME);
    URI rootUriUpdated = objectApi.save(rootNodeOnly);

    rootNodeOnly = objectApi.load(rootUriUpdated);
    Object item3datasheetName = rootNodeOnly.getValue(
        SampleCategory.CONTAINER_ITEMS, "3",
        SampleContainerItem.DATASHEET,
        SampleDataSheet.NAME);
    assertEquals("datasheet 3 modified", item3datasheetName);
    assertEquals(5, rootNodeOnly.list(SampleCategory.CONTAINER_ITEMS).size());

    ObjectNode nodeByReq2 = objectApi.request(SampleCategory.class)
        .add(SampleCategory.CONTAINER_ITEMS, SampleContainerItem.DATASHEET)
        .load(rootUriUpdated);

    ObjectNode nodeByReq = objectApi.request(SampleCategory.class)
        .add(SampleCategory.CONTAINER_ITEMS, SampleContainerItem.DATASHEET)
        .load(rootUriUpdated);

    assertEquals(nodeByReq2, nodeByReq);

    ObjectNode nodeByNav = objectApi.load(rootUriUpdated);
    nodeByNav.list(SampleCategory.CONTAINER_ITEMS).stream()
        .map(ref -> ref.get())
        .map(node -> node.ref(SampleContainerItem.DATASHEET).get())
        .collect(toList());

    assertEquals(nodeByReq, nodeByNav);



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

    // add new SampleContainerItem by URI
    SampleContainerItem itemToAdd = new SampleContainerItem()
        .name("added container item");
    ObjectNode itemToAddNode = objectApi.create(MY_SCHEME, itemToAdd);
    itemToAddNode.ref(SampleContainerItem.DATASHEET).setNewObject(
        new SampleDataSheet().name("added datasheet"));
    URI itemToAddUri = objectApi.save(itemToAddNode);
    rootNode.list(SampleCategory.CONTAINER_ITEMS).add(itemToAddUri);
    rootUriAfterUpdate = objectApi.save(rootNode);

    rootNode = requestNormal.load(rootUriAfterUpdate);
    assertEquals(2, rootNode.list(SampleCategory.CONTAINER_ITEMS).size());
    ObjectNode itemAddedNode = rootNode.list(SampleCategory.CONTAINER_ITEMS).get(1).get();
    assertEquals("added container item", itemAddedNode.getValue(SampleContainerItem.NAME));
    ObjectNode datasheetAddedNode = itemAddedNode.ref(SampleContainerItem.DATASHEET).get();
    assertEquals("added datasheet", datasheetAddedNode.getValue(SampleDataSheet.NAME));

    // remove from ref. list, isLoaded = true
    assertEquals(2, rootNode.list(SampleCategory.CONTAINER_ITEMS).size());
    assertTrue(rootNode.list(SampleCategory.CONTAINER_ITEMS).get(0).isLoaded());
    rootNode.list(SampleCategory.CONTAINER_ITEMS).get(0).clear();
    rootUriAfterUpdate = objectApi.save(rootNode);

    rootNode = objectApi.load(rootUriAfterUpdate);
    assertEquals(1, rootNode.list(SampleCategory.CONTAINER_ITEMS).size());
    itemAddedNode = rootNode.list(SampleCategory.CONTAINER_ITEMS).get(0).get();
    assertEquals("added container item", itemAddedNode.getValue(SampleContainerItem.NAME));

    // remove from ref. list, isLoaded = false
    rootNode = objectApi.load(rootUriAfterUpdate);
    assertFalse(rootNode.list(SampleCategory.CONTAINER_ITEMS).get(0).isLoaded());
    rootNode.list(SampleCategory.CONTAINER_ITEMS).get(0).clear();
    rootUriAfterUpdate = objectApi.save(rootNode);

    rootNode = objectApi.load(rootUriAfterUpdate);
    assertEquals(0, rootNode.list(SampleCategory.CONTAINER_ITEMS).size());

  }

  @Test
  void testReferenceHandling() {
    SampleContainerItem item1 = new SampleContainerItem()
        .name("item1");
    URI item1uri = objectApi.saveAsNew(MY_SCHEME, item1);
    ObjectNode item1Node = objectApi.load(item1uri);
    assertFalse(item1Node.ref(SampleContainerItem.DATASHEET).isPresent());

    SampleDataSheet datasheet1 = new SampleDataSheet()
        .name("datasheet1");
    item1Node.ref(SampleContainerItem.DATASHEET).setNewObject(datasheet1);
    item1uri = objectApi.save(item1Node);
    item1Node = objectApi.load(item1uri);
    assertTrue(item1Node.ref(SampleContainerItem.DATASHEET).isPresent());
    assertEquals("datasheet1",
        item1Node.getValueAsString(SampleContainerItem.DATASHEET, SampleDataSheet.NAME));

    item1Node = objectApi.load(item1uri);
    SampleDataSheet datasheet2 = new SampleDataSheet()
        .name("datasheet2");
    URI datasheet2uri = objectApi.saveAsNew(MY_SCHEME, datasheet2);
    item1Node.ref(SampleContainerItem.DATASHEET).set(datasheet2uri);
    item1uri = objectApi.save(item1Node);
    item1Node = objectApi.load(item1uri);
    assertTrue(item1Node.ref(SampleContainerItem.DATASHEET).isPresent());
    assertEquals("datasheet2",
        item1Node.getValueAsString(SampleContainerItem.DATASHEET, SampleDataSheet.NAME));

    item1Node = objectApi.load(item1uri);
    SampleDataSheet datasheet3 = new SampleDataSheet()
        .name("datasheet3");
    item1Node.ref(SampleContainerItem.DATASHEET).setNewObject(datasheet3);
    item1uri = objectApi.save(item1Node);
    item1Node = objectApi.load(item1uri);
    assertTrue(item1Node.ref(SampleContainerItem.DATASHEET).isPresent());
    assertEquals("datasheet3",
        item1Node.getValueAsString(SampleContainerItem.DATASHEET, SampleDataSheet.NAME));

    // use clear to clear reference
    item1Node.ref(SampleContainerItem.DATASHEET).clear();
    item1uri = objectApi.save(item1Node);
    item1Node = objectApi.load(item1uri);
    assertFalse(item1Node.ref(SampleContainerItem.DATASHEET).isPresent());

    SampleDataSheet datasheet4 = new SampleDataSheet()
        .name("datasheet4");
    item1Node.ref(SampleContainerItem.DATASHEET).setNewObject(datasheet4);
    item1uri = objectApi.save(item1Node);
    item1Node = objectApi.load(item1uri);
    assertTrue(item1Node.ref(SampleContainerItem.DATASHEET).isPresent());
    assertEquals("datasheet4",
        item1Node.getValueAsString(SampleContainerItem.DATASHEET, SampleDataSheet.NAME));

    // use set(null) to clear reference. have to load object, because ref is already loaded, so
    // setting uri directly is forbidden
    item1Node = objectApi.load(item1uri);
    item1Node.ref(SampleContainerItem.DATASHEET).set((URI) null);
    item1uri = objectApi.save(item1Node);
    item1Node = objectApi.load(item1uri);
    assertFalse(item1Node.ref(SampleContainerItem.DATASHEET).isPresent());

  }

  @Test
  void testGetValueFromObjectMap() {
    Map<String, Object> root = new HashMap<>();

    root.put("first", "value");
    Object firstValue = objectApi.getValueFromObjectMap(root, "first");
    assertEquals("value", firstValue);

    Map<String, Object> second = new HashMap<>();
    second.put("value", 22);
    root.put("second", second);
    Object secondValue = objectApi.getValueFromObjectMap(root, "second", "value");
    assertEquals(22, secondValue);

    List<Object> list = new ArrayList<>();
    Map<String, Object> mapInList = new HashMap<>();
    mapInList.put("value", "mapInList");
    mapInList.put("object", new SampleCategory().name("sample"));
    list.add(mapInList);
    root.put("third", list);

    Object thirdValue = objectApi.getValueFromObjectMap(root, "third", "0", "value");
    assertEquals("mapInList", thirdValue);

    Object name = objectApi.getValueFromObjectMap(root, "third", "0", "object", "name");
    assertEquals(name, "sample");
  }

  @Test
  void testLinkObject() {
    SampleCategory root = new SampleCategory().name("root linkObject");
    ObjectNode rootNode = objectApi.create(MY_SCHEME, root);
    URI rootUri = objectApi.save(rootNode);

    SampleContainerItem item = new SampleContainerItem().name("container item");
    // SampleDataSheet dataSheet = new SampleDataSheet().name("data sheet testReferenceChangeUri");
    URI itemUri = objectApi.saveAsNew(MY_SCHEME, item);

    rootNode = objectApi.load(rootUri);
    ObjectNodeList linkList = rootNode.list(SampleCategory.LINKS);
    assertNotNull(linkList);
    assertTrue(linkList.isEmpty());
    linkList.addNewObject(new SampleLinkObject()
        .linkName("link")
        .category(rootUri)
        .item(itemUri));
    rootUri = objectApi.save(rootNode);

    rootNode = objectApi.load(rootUri);
    linkList = rootNode.list(SampleCategory.LINKS);
    assertNotNull(linkList);
    assertFalse(linkList.isEmpty());

    ObjectNode linkNode = linkList.get(0).get();
    SampleLinkObject linkObject = linkNode.getObject(SampleLinkObject.class);
    assertEquals("link", linkObject.getLinkName());

    ObjectNode itemNode = linkNode.ref(SampleLinkObject.ITEM).get();
    assertEquals("container item", itemNode.getValueAsString(SampleContainerItem.NAME));

  }

  @Test
  void testUpdateWithExistingLoadedReference() {
    SampleDataSheet dataSheet = new SampleDataSheet().name("data sheet created before");
    URI dataSheetUri = objectApi.saveAsNew(MY_SCHEME, dataSheet);

    SampleContainerItem item = new SampleContainerItem().name("container");
    URI itemUri = objectApi.saveAsNew(MY_SCHEME, item);

    ObjectNode itemNode = objectApi.load(itemUri);
    itemNode.ref(SampleContainerItem.DATASHEET).set(dataSheetUri);
    // uncomment this to fail test
    // itemNode.ref(SampleContainerItem.DATASHEET).get();
    itemUri = objectApi.save(itemNode);

    itemNode = objectApi.load(itemUri);
    assertEquals(dataSheetUri, itemNode.ref(SampleContainerItem.DATASHEET).getObjectUri());
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

    return (URI) result.getProcessedRequests().get(ocrContainer);
  }

}
