package org.smartbit4all.api.object;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.object.bean.RetrievalMode;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.api.sample.bean.SampleCategory.ColorEnum;
import org.smartbit4all.api.sample.bean.SampleCategoryType;
import org.smartbit4all.api.sample.bean.SampleContainerItem;
import org.smartbit4all.api.sample.bean.SampleDataSheet;
import org.smartbit4all.api.sample.bean.SampleGenericContainer;
import org.smartbit4all.api.sample.bean.SampleInlineObject;
import org.smartbit4all.api.sample.bean.SampleLinkObject;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectNodeList;
import org.smartbit4all.core.object.ObjectNodeReference;
import org.smartbit4all.core.object.ObjectNodes;
import org.smartbit4all.core.object.ObjectProperties;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.storage.ObjectStorageImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.google.common.base.Objects;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

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

    // SampleDataSheet dataSheet = new SampleDataSheet().name("data sheet testReferenceChangeUri");
    URI itemUri = objectApi.saveAsNew(MY_SCHEME,
        new SampleContainerItem()
            .name("container item"));
    URI item2Uri = objectApi.saveAsNew(MY_SCHEME,
        new SampleContainerItem()
            .name("container item2"));

    rootNode = objectApi.load(rootUri);
    ObjectNodeList linkList = rootNode.list(SampleCategory.LINKS);
    assertNotNull(linkList);
    assertTrue(linkList.isEmpty());
    linkList.addNewObject(new SampleLinkObject()
        .linkName("link")
        .category(rootUri)
        .item(itemUri));
    rootNode.ref(SampleCategory.SINGLE_LINK).setNewObject(
        new SampleLinkObject()
            .linkName("link2")
            .category(rootUri)
            .item(item2Uri));
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

    String itemName = rootNode.getValueAsString(SampleCategory.LINKS, "0", SampleLinkObject.ITEM,
        SampleContainerItem.NAME);
    assertEquals("container item", itemName);

    Object item2NameObject = rootNode.getValue(SampleCategory.SINGLE_LINK, SampleLinkObject.ITEM,
        SampleContainerItem.NAME);
    assertEquals("container item2", item2NameObject);

    String item2Name = rootNode.getValueAsString(SampleCategory.SINGLE_LINK, SampleLinkObject.ITEM,
        SampleContainerItem.NAME);
    assertEquals("container item2", item2Name);

  }

  @Test
  void testUpdateWithExistingLoadedReference() {
    SampleDataSheet dataSheet = new SampleDataSheet().name("data sheet created before");
    URI dataSheetUri = objectApi.saveAsNew(MY_SCHEME, dataSheet);

    SampleContainerItem item = new SampleContainerItem().name("container");
    URI itemUri = objectApi.saveAsNew(MY_SCHEME, item);

    // set(URI) and .get()
    ObjectNode itemNode = objectApi.load(itemUri);
    itemNode.ref(SampleContainerItem.DATASHEET).set(dataSheetUri);
    itemNode.ref(SampleContainerItem.DATASHEET).get();
    itemUri = objectApi.save(itemNode);
    itemNode = objectApi.load(itemUri);
    assertEquals(dataSheetUri, itemNode.ref(SampleContainerItem.DATASHEET).getObjectUri());

    // set(ObjectNode) as NEW
    ObjectNode dataSheetNode =
        objectApi.create(MY_SCHEME, new SampleDataSheet().name("other datasheet"));
    itemNode.ref(SampleContainerItem.DATASHEET).set(dataSheetNode);
    itemUri = objectApi.save(itemNode);
    itemNode = objectApi.load(itemUri);
    assertEquals("other datasheet", itemNode.getValueAsString(
        SampleContainerItem.DATASHEET, SampleDataSheet.NAME));
    assertEquals(dataSheetNode.getResultUri(),
        itemNode.ref(SampleContainerItem.DATASHEET).getObjectUri());

    // set(ObjectNode) as NOP
    ObjectNode dataSheetNode2 =
        objectApi.create(MY_SCHEME, new SampleDataSheet().name("other datasheet2"));
    URI dataSheet2Uri = objectApi.save(dataSheetNode2);
    dataSheetNode2 = objectApi.load(dataSheet2Uri);
    itemNode.ref(SampleContainerItem.DATASHEET).set(dataSheetNode2);
    itemUri = objectApi.save(itemNode);
    itemNode = objectApi.load(itemUri);
    assertEquals("other datasheet2", itemNode.getValueAsString(
        SampleContainerItem.DATASHEET, SampleDataSheet.NAME));
    assertEquals(dataSheetNode2.getResultUri(),
        itemNode.ref(SampleContainerItem.DATASHEET).getObjectUri());

    // set(ObjectNode) as NOP and update it
    ObjectNode dataSheetNode3 =
        objectApi.create(MY_SCHEME, new SampleDataSheet().name("other datasheet3"));
    URI dataSheet3Uri = objectApi.save(dataSheetNode3);
    dataSheetNode3 = objectApi.load(dataSheet3Uri);
    itemNode.ref(SampleContainerItem.DATASHEET).set(dataSheetNode3);
    itemNode.ref(SampleContainerItem.DATASHEET).get().setValue("other datasheet3_modified",
        SampleDataSheet.NAME);
    itemUri = objectApi.save(itemNode);
    itemNode = objectApi.load(itemUri);
    assertEquals("other datasheet3_modified", itemNode.getValueAsString(
        SampleContainerItem.DATASHEET, SampleDataSheet.NAME));
    assertEquals(dataSheetNode3.getResultUri(),
        itemNode.ref(SampleContainerItem.DATASHEET).getObjectUri());

    dataSheetNode3 = itemNode.ref(SampleContainerItem.DATASHEET).get();
    dataSheetNode3.setValue("other datasheet3_modified_further", SampleDataSheet.NAME);
    itemUri = objectApi.save(itemNode);
    itemNode = objectApi.load(itemUri);
    assertEquals("other datasheet3_modified_further", itemNode.getValueAsString(
        SampleContainerItem.DATASHEET, SampleDataSheet.NAME));
    assertEquals(dataSheetNode3.getResultUri(),
        itemNode.ref(SampleContainerItem.DATASHEET).getObjectUri());

  }

  @Test
  void testObjectApiEqualsIgnoreVersion() {
    SampleCategory category = new SampleCategory().name("category");
    ObjectNode node = objectApi.create(MY_SCHEME, category);
    URI uri1_0 = objectApi.save(node);

    node = objectApi.load(uri1_0);
    node.setValue(SampleCategory.NAME, "category modified");
    URI uri1_1 = objectApi.save(node);

    SampleCategory category2 = new SampleCategory().name("category");
    ObjectNode node2 = objectApi.create(MY_SCHEME, category2);
    URI uri2_0 = objectApi.save(node2);

    assertTrue(objectApi.equalsIgnoreVersion(null, null));
    assertFalse(objectApi.equalsIgnoreVersion(uri1_0, null));
    assertFalse(objectApi.equalsIgnoreVersion(null, uri1_0));
    assertTrue(objectApi.equalsIgnoreVersion(uri1_0, uri1_1));
    assertFalse(Objects.equal(uri1_0, uri1_1));
    assertFalse(objectApi.equalsIgnoreVersion(uri1_0, uri2_0));

  }


  @Test
  void testObjectApiObjectHistory() {

    // init test objects: one SampleCategory object with 10 versions, counting in the name
    String name = "category";
    SampleCategory category = new SampleCategory().name(name + "0");
    URI fistUri = objectApi.saveAsNew(MY_SCHEME, category);
    URI uri = fistUri;
    for (int i = 1; i <= 10; i++) {
      ObjectNode node = objectApi.loadLatest(uri);
      node.setValue(name + i, SampleCategory.NAME);
      uri = objectApi.save(node);
    }

    // test objectApi.objectHistory
    Iterator<ObjectNode> historyIter = objectApi.objectHistory(uri);
    int counter = 0;
    while (historyIter.hasNext()) {
      ObjectNode currentNode = historyIter.next();
      assertEquals(name + counter++, currentNode.getValue(SampleCategory.NAME));
    }
    assertEquals(11, counter);

    // test objectApi.objectHistoryReverse
    Iterator<ObjectNode> historyIterRev = objectApi.objectHistoryReverse(uri);
    while (historyIterRev.hasNext()) {
      ObjectNode currentNode = historyIterRev.next();
      assertEquals(name + --counter, currentNode.getValue(SampleCategory.NAME));
    }
    assertEquals(0, counter);


    // check with an uri from a middle version: it still iterates from the beginning to the end
    uri = ObjectStorageImpl.getUriWithVersion(uri, 5);
    historyIter = objectApi.objectHistory(uri);
    while (historyIter.hasNext()) {
      ObjectNode currentNode = historyIter.next();
      assertEquals(name + counter++, currentNode.getValue(SampleCategory.NAME));
    }
    assertEquals(11, counter);

  }



  @Test
  void testSetValueWithMap() {
    SampleContainerItem item = new SampleContainerItem().cost(Long.valueOf(1))
        .inlineObject(new SampleInlineObject().name("inline name").categoryType(
            objectApi.saveAsNew(MY_SCHEME, new SampleCategory().name("Inline referred type"))));
    ObjectNode node = objectApi.create(MY_SCHEME, item);
    URI uri1_0 = objectApi.save(node);

    node = objectApi.load(uri1_0);
    Map<String, Object> extensionValues = new HashMap<>();
    extensionValues.put("favoriteColor", ColorEnum.BLACK);
    extensionValues.put("pet", "cat");
    extensionValues.put("price", Double.valueOf(1000.0));
    node.setValue(extensionValues, SampleContainerItem.INLINE_OBJECT);
    URI uri1_1 = objectApi.save(node);

    ObjectNode objectNode = objectApi.loadLatest(uri1_1);

    assertEquals("inline name",
        objectNode.getValueAsString(SampleContainerItem.INLINE_OBJECT, SampleInlineObject.NAME));
    assertEquals(ColorEnum.BLACK,
        objectNode.getValue(ColorEnum.class, SampleContainerItem.INLINE_OBJECT, "favoriteColor"));
    assertEquals("cat",
        objectNode.getValueAsString(SampleContainerItem.INLINE_OBJECT, "pet"));
    assertEquals(Double.valueOf(1000.0),
        objectNode.getValue(Double.class, SampleContainerItem.INLINE_OBJECT, "price"));
    assertEquals("Inline referred type",
        objectNode.getValueAsString(SampleContainerItem.INLINE_OBJECT,
            SampleInlineObject.CATEGORY_TYPE, SampleCategoryType.NAME));

  }

  private URI constructSampleCategory(String referenceToItems) {
    URI branchUri = null;

    SampleCategory rootContainer = new SampleCategory().name("root");

    ApplyChangeRequest applyChangeRequest = applyChangeApi.request(null);
    ObjectChangeRequest ocrContainer =
        applyChangeRequest.createAsNew(objectApi.create(MY_SCHEME, rootContainer));
    ReferenceListChange rlcItems = ocrContainer.referenceList(referenceToItems);

    int itemCounter = 5;
    for (int i = 0; i < itemCounter; i++) {
      SampleContainerItem item = new SampleContainerItem().name("item " + i);
      ObjectChangeRequest ocrItem =
          applyChangeRequest.createAsNew(objectApi.create(MY_SCHEME, item));

      ObjectChangeRequest ocrDatasheet =
          applyChangeRequest.createAsNew(
              objectApi.create(MY_SCHEME, new SampleDataSheet().name("datasheet " + i)));
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
      ObjectChangeRequest ocrSubCategory =
          applyChangeRequest.createAsNew(objectApi.create(MY_SCHEME, subCategory));
      rlcSubCategories.add(ocrSubCategory);
    }

    ApplyChangeResult result =
        applyChangeApi
            .save(applyChangeRequest);

    Assertions.assertEquals(itemCounter * 2 + 4, result.getProcessedRequests().size());

    return (URI) result.getProcessedRequests().get(ocrContainer);
  }

  @Test
  void inlineRefListCanBeSetAsObjectNodeList_canBeLoadedAsNodeList() {
    // given:
    ObjectNode root = objectApi.create(MY_SCHEME, new SampleCategory());

    // when:
    ObjectNodeList list = root.list(SampleCategory.LINKS);
    IntStream.range(0, 3)
        .mapToObj(i -> new SampleLinkObject().linkName(String.valueOf(i)))
        .map(o -> objectApi.create(MY_SCHEME, o))
        .forEach(list::add);
    final URI rootUri = objectApi.save(root);

    root = objectApi.loadLatest(rootUri);

    // then:
    list = root.list(SampleCategory.LINKS);
    assertThat(list).isNotNull();
    assertThat(list.stream(SampleLinkObject.class).map(SampleLinkObject::getLinkName))
        .containsExactly(StringConstant.ZERO, StringConstant.ONE, "2");

    final Object listObj = root.getValue(SampleCategory.LINKS);
    assertThat(listObj)
        .isNotNull()
        .isInstanceOf(ObjectNodeList.class)
        .isEqualTo(list);
  }

  @Test
  void inlineRefListCanBeSetAsObjectProperty_canBeLoadedAsNodeList() {
    // given:
    ObjectNode root = objectApi.create(MY_SCHEME, new SampleCategory());
    List<SampleLinkObject> links = IntStream.range(0, 3)
        .mapToObj(i -> new SampleLinkObject()
            .linkName(String.valueOf(i)))
        .collect(toList());

    // when:
    root.modify(SampleCategory.class, rootObject -> rootObject.links(links));
    final URI rootUri = objectApi.save(root);

    root = objectApi.loadLatest(rootUri);

    // then:
    final ObjectNodeList list = root.list(SampleCategory.LINKS);
    assertThat(list).isNotNull();
    assertThat(list.stream(SampleLinkObject.class).map(SampleLinkObject::getLinkName))
        .containsExactly(StringConstant.ZERO, StringConstant.ONE, "2");

    final Object listObj = root.getValue(SampleCategory.LINKS);
    assertThat(listObj)
        .isNotNull()
        .isInstanceOf(ObjectNodeList.class)
        .isEqualTo(list);
  }

  @Test
  @Disabled
  void inlineRefListCanBePresentAsObjectProperty_beforeNodeCreation_canBeLoadedAsObjectNodeList() {
    // given:
    final SampleCategory category = new SampleCategory();
    List<SampleLinkObject> links = IntStream.range(0, 3)
        .mapToObj(i -> new SampleLinkObject()
            .linkName(String.valueOf(i)))
        .collect(toList());
    category.setLinks(links);

    // when:
    // TODO: Saved data is incomplete! List is saved as [null, null, null]
    final URI rootUri = objectApi.saveAsNew(MY_SCHEME, category);
    assertThat(rootUri).isNotNull(); // This is OK.
    // TODO: Loading fails with NPE at ObjectNode#102
    final ObjectNode root = objectApi.loadLatest(rootUri);

    // then:
    final ObjectNodeList list = root.list(SampleCategory.LINKS);
    assertThat(list).isNotNull();
    assertThat(list.stream(SampleLinkObject.class).map(SampleLinkObject::getLinkName))
        .containsExactly(StringConstant.ZERO, StringConstant.ONE, "2");

    final Object listObj = root.getValue(SampleCategory.LINKS);
    assertThat(listObj)
        .isNotNull()
        .isInstanceOf(ObjectNodeList.class)
        .isEqualTo(list);
  }

  @Test
  void inlineRefListCanBeSetAsValues_canBeLoadedAsObjectNodeList() {
    // given:
    ObjectNode root = objectApi.create(MY_SCHEME, new SampleCategory());
    {
      List<SampleLinkObject> links = IntStream.range(0, 3)
          .mapToObj(i -> new SampleLinkObject()
              .linkName(String.valueOf(i)))
          .collect(toList());

      final Map<String, Object> valuesToSet = new HashMap<>();
      valuesToSet.put(SampleCategory.LINKS, links);

      // when:
      root.setValues(valuesToSet);
      final URI rootUri = objectApi.save(root);

      root = objectApi.loadLatest(rootUri);

      // then:
      final ObjectNodeList list = root.list(SampleCategory.LINKS);
      assertThat(list).isNotNull();
      assertThat(list.stream(SampleLinkObject.class).map(SampleLinkObject::getLinkName))
          .containsExactly(StringConstant.ZERO, StringConstant.ONE, "2");

      final Object listObj = root.getValue(SampleCategory.LINKS);
      assertThat(listObj)
          .isNotNull()
          .isInstanceOf(ObjectNodeList.class)
          .isEqualTo(list);
    }

    {
      List<SampleLinkObject> links = IntStream.range(1, 6)
          .mapToObj(i -> new SampleLinkObject()
              .linkName(String.valueOf(i)))
          .collect(toList());

      final Map<String, Object> valuesToSet = new HashMap<>();
      valuesToSet.put(SampleCategory.LINKS, links);

      // when:
      root.setValuesWithReference(valuesToSet);
      final URI rootUri = objectApi.save(root);

      root = objectApi.loadLatest(rootUri);

      // then:
      final ObjectNodeList list = root.list(SampleCategory.LINKS);
      assertThat(list).isNotNull();
      assertThat(list.stream(SampleLinkObject.class).map(SampleLinkObject::getLinkName))
          .containsExactly("1", "2", "3", "4", "5");

      final Object listObj = root.getValue(SampleCategory.LINKS);
      assertThat(listObj)
          .isNotNull()
          .isInstanceOf(ObjectNodeList.class)
          .isEqualTo(list);
    }

  }

  @Test
  void inlineRefListCanBeSetAsValues_inRefOfAggregateRoot_whenImmediateParentIsAlreadyPersisted() {
    // @formatter:off
    // +------------------------+              *----------------+             +-----------------+
    // | SampleGenericContainer | --content--> | SampleCategory | --links--*> | SampleLinkObject|
    // +------------------------+              *----------------+             +-----------------+
    //            root                               child                           links
    // @formatter:on

    // given:
    ObjectNode root = objectApi.create(MY_SCHEME, new SampleGenericContainer());
    ObjectNode child = objectApi.create(MY_SCHEME, new SampleCategory());

    List<SampleLinkObject> links = IntStream.range(0, 3)
        .mapToObj(i -> new SampleLinkObject()
            .linkName(String.valueOf(i)))
        .collect(toList());

    final Map<String, Object> valuesToSet = new HashMap<>();
    valuesToSet.put(SampleCategory.LINKS, links);

    // when:
    // an object already exists as saved:
    URI childUri = objectApi.save(child);
    // it is set as a reference in URI form:
    root.ref(SampleGenericContainer.CONTENT).set(childUri);
    // the reference is accessed through the aggregate root and values are set in it:
    root.ref(SampleGenericContainer.CONTENT).get().setValues(valuesToSet);

    final URI rootUri = objectApi.save(root);
    assertThat(rootUri).isNotNull(); // TODO: THIS ASSERTION FAILS! (save failure)

    root = objectApi.loadLatest(rootUri);

    // then:
    final ObjectNodeList list = root.list(SampleGenericContainer.CONTENT, SampleCategory.LINKS);
    assertThat(list).isNotNull();
    // TODO: Failure incoming because each list element has NULL node (and of course no objectUri):
    assertThat(list.stream(SampleLinkObject.class).map(SampleLinkObject::getLinkName))
        .containsExactly(StringConstant.ZERO, StringConstant.ONE, "2");

    final Object listObj = root.getValue(SampleGenericContainer.CONTENT, SampleCategory.LINKS);
    assertThat(listObj)
        .isNotNull()
        .isInstanceOf(ObjectNodeList.class)
        .isEqualTo(list);
  }

  @Test
  void inlineRefListCanBeSetAsValues_inRefOfAggregateRoot_whenImmediateParentIsHasNotYetBeenPersisted() {
    // @formatter:off
    // +------------------------+              *----------------+             +-----------------+
    // | SampleGenericContainer | --content--> | SampleCategory | --links--*> | SampleLinkObject|
    // +------------------------+              *----------------+             +-----------------+
    //            root                               child                           links
    // @formatter:on

    // given:
    ObjectNode root = objectApi.create(MY_SCHEME, new SampleGenericContainer());
    ObjectNode child = objectApi.create(MY_SCHEME, new SampleCategory());

    List<SampleLinkObject> links = IntStream.range(0, 3)
        .mapToObj(i -> new SampleLinkObject()
            .linkName(String.valueOf(i)))
        .collect(toList());

    final Map<String, Object> valuesToSet = new HashMap<>();
    valuesToSet.put(SampleCategory.LINKS, links);

    // when:
    // a reference is set:
    root.ref(SampleGenericContainer.CONTENT).set(child);
    // the reference is accessed later (without interim storage access) through the aggregate root
    // and values are set in it:
    root.ref(SampleGenericContainer.CONTENT).get().setValues(valuesToSet);
    final URI rootUri = objectApi.save(root);
    assertThat(rootUri).isNotNull(); // TODO: THIS ASSERTION FAILS! (save failure)

    root = objectApi.loadLatest(rootUri);

    // then:
    final ObjectNodeList list = root.list(SampleGenericContainer.CONTENT, SampleCategory.LINKS);
    assertThat(list).isNotNull();
    // TODO: Failure incoming because each list element has NULL node (and of course no objectUri):
    assertThat(list.stream(SampleLinkObject.class).map(SampleLinkObject::getLinkName))
        .containsExactly(StringConstant.ZERO, StringConstant.ONE, "2");

    final Object listObj = root.getValue(SampleGenericContainer.CONTENT, SampleCategory.LINKS);
    assertThat(listObj)
        .isNotNull()
        .isInstanceOf(ObjectNodeList.class)
        .isEqualTo(list);
  }
}
