package org.smartbit4all.api.object;

import java.io.IOException;
import java.net.URI;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.object.bean.ObjectNodeState;
import org.smartbit4all.api.object.bean.SnapshotEntry;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.api.sample.bean.SampleContainerItem;
import org.smartbit4all.api.sample.bean.SampleDataSheet;
import org.smartbit4all.core.io.TestFileUtil;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectNodeList;
import org.smartbit4all.core.object.ObjectNodeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {ApplyChangeTestConfig.class})
public class SnapshotTest {

  private static final String MY_SCHEME = "myScheme";

  @Autowired
  private ObjectApi objectApi;

  @BeforeAll
  static void clearDirectory() throws IOException {
    TestFileUtil.clearTestDirectory();
  }

  @Test
  void snapshotSimpleObject() {
    ObjectNode node = objectApi.create(
        MY_SCHEME,
        new SampleCategory().name("root"));
    URI uri = objectApi.save(node);
    ObjectNode loaded = objectApi.load(uri);
    ObjectNode loadedFromSnapshot = saveAndLoadSnapshot(loaded, true);
    assertNotNull(loadedFromSnapshot);
    assertEquals(loaded, loadedFromSnapshot);
  }

  @Test
  void snapshotUnAvailable() {
    ObjectNode node = objectApi.create(
        MY_SCHEME,
        new SampleCategory().name("root"));
    URI uri = objectApi.save(node);
    ObjectNode loaded = objectApi.load(uri);
    loaded.setValue("modified", SampleCategory.NAME);
    assertNotEquals(ObjectNodeState.NOP, loaded.getState());
    assertThrows(IllegalStateException.class, () -> loaded.snapshot());
  }

  @Test
  void snapshotWithReference() {
    ObjectNode node = objectApi.create(
        MY_SCHEME,
        new SampleContainerItem().name("item"));
    node.ref(SampleContainerItem.DATASHEET).setNewObject(
        new SampleDataSheet().name("datasheet"));
    URI uri = objectApi.save(node);

    // not loaded
    {
      ObjectNode loaded = objectApi.load(uri);
      ObjectNodeReference ref = loaded.ref(SampleContainerItem.DATASHEET);
      assertFalse(ref.isLoaded());
      ObjectNode loadedFromSnapshot = saveAndLoadSnapshot(loaded, true);
      ref = loadedFromSnapshot.ref(SampleContainerItem.DATASHEET);
      assertFalse(ref.isLoaded());
      assertEquals(loaded, loadedFromSnapshot);
    }

    // loaded
    {
      ObjectNode loaded = objectApi.load(uri);
      ObjectNodeReference ref = loaded.ref(SampleContainerItem.DATASHEET);
      assertFalse(ref.isLoaded());
      ref.get();
      assertTrue(ref.isLoaded());
      ObjectNode loadedFromSnapshot = saveAndLoadSnapshot(loaded, true);
      ref = loadedFromSnapshot.ref(SampleContainerItem.DATASHEET);
      assertTrue(ref.isLoaded());
      assertEquals(loaded, loadedFromSnapshot);
    }
  }

  private ObjectNode saveAndLoadSnapshot(ObjectNode loaded, boolean includeData) {
    // create
    SnapshotEntry snapshot = new SnapshotEntry()
        .data(loaded.snapshot(includeData));
    // save
    URI snapshotUri = objectApi.saveAsNew(MY_SCHEME, snapshot);
    // load
    snapshot = objectApi.read(snapshotUri, SnapshotEntry.class);
    // recreate ObjectNode
    return objectApi.loadSnapshot(snapshot.getData());
  }

  @Test
  void snapshotWithList() {
    ObjectNode node = objectApi.create(
        MY_SCHEME,
        new SampleCategory().name("root"));
    ObjectNodeList subCategories = node.list(SampleCategory.SUB_CATEGORIES);
    subCategories.addNewObject(new SampleCategory().name("sub1"));
    subCategories.addNewObject(new SampleCategory().name("sub2"));
    URI uri = objectApi.save(node);

    // not loaded
    {
      ObjectNode loaded = objectApi.load(uri);
      ObjectNodeList list = loaded.list(SampleCategory.SUB_CATEGORIES);
      assertEquals(2, list.size());
      assertFalse(list.get(0).isLoaded());
      assertFalse(list.get(1).isLoaded());
      ObjectNode loadedFromSnapshot = saveAndLoadSnapshot(loaded, true);
      assertNotNull(loadedFromSnapshot);
      assertEquals(loaded, loadedFromSnapshot);
    }

    // fully loaded
    {
      ObjectNode loaded = objectApi
          .request(SampleCategory.class)
          .add(SampleCategory.SUB_CATEGORIES)
          .load(uri);
      ObjectNodeList list = loaded.list(SampleCategory.SUB_CATEGORIES);
      assertEquals(2, list.size());
      assertTrue(list.get(0).isLoaded());
      assertTrue(list.get(1).isLoaded());

      ObjectNode loadedFromSnapshot = saveAndLoadSnapshot(loaded, true);
      assertNotNull(loadedFromSnapshot);
      assertEquals(loaded, loadedFromSnapshot);
    }

    // partially loaded
    {
      ObjectNode loaded = objectApi.load(uri);
      ObjectNodeList list = loaded.list(SampleCategory.SUB_CATEGORIES);
      assertEquals(2, list.size());
      assertFalse(list.get(0).isLoaded());
      assertFalse(list.get(1).isLoaded());
      list.get(0).get();
      assertTrue(list.get(0).isLoaded());
      assertFalse(list.get(1).isLoaded());

      ObjectNode loadedFromSnapshot = saveAndLoadSnapshot(loaded, true);
      assertNotNull(loadedFromSnapshot);
      assertEquals(loaded, loadedFromSnapshot);
    }
  }

  void snapshotWithMap() {
    // implement later
  }

  @Test
  void snapshotComplexObject() {
    ObjectNode node = objectApi.create(
        MY_SCHEME,
        new SampleCategory().name("root testObjectNodeOnly"));
    ObjectNodeList itemList = node.list(SampleCategory.CONTAINER_ITEMS);
    ObjectNodeList subCategoriesList = node.list(SampleCategory.SUB_CATEGORIES);
    IntStream.range(0, 5).forEach(i -> itemList
        .addNewObject(new SampleContainerItem().name("item " + i))
        .ref(SampleContainerItem.DATASHEET)
        .setNewObject(new SampleDataSheet().name("datasheet " + i)));
    IntStream.range(0, 3).forEach(i -> {
      ObjectNode subCategory =
          subCategoriesList.addNewObject(new SampleCategory().name("subcat " + i));
      constructCategory(i, subCategory);
      IntStream.range(0, 3).forEach(j -> {
        ObjectNodeList subSubCategories = subCategory.list(SampleCategory.SUB_CATEGORIES);
        ObjectNode subSubCategory =
            subSubCategories.addNewObject(new SampleCategory().name("subcat " + i + "." + j));
        constructCategory(10 * i + j, subSubCategory);
      });
    });

    URI uri = objectApi.save(node);
    {
      ObjectNode loaded = objectApi.request(SampleCategory.class)
          .recursiveStartLabel(SampleCategory.class.getSimpleName())
          .add(SampleCategory.CONTAINER_ITEMS, SampleContainerItem.DATASHEET)
          .addContinueAt(SampleCategory.class.getSimpleName(), SampleCategory.SUB_CATEGORIES)
          .add(SampleCategory.SUB_CATEGORIES, SampleCategory.CONTAINER_ITEMS,
              SampleContainerItem.DATASHEET)
          .load(uri);
      ObjectNode loadedFromSnapshot = saveAndLoadSnapshot(loaded, true);
      assertEquals(loaded, loadedFromSnapshot);
    }

    {
      ObjectNode loaded = objectApi.request(SampleCategory.class)
          .recursiveStartLabel(SampleCategory.class.getSimpleName())
          .add(SampleCategory.CONTAINER_ITEMS, SampleContainerItem.DATASHEET)
          .addContinueAt(SampleCategory.class.getSimpleName(), SampleCategory.SUB_CATEGORIES)
          .add(SampleCategory.SUB_CATEGORIES, SampleCategory.CONTAINER_ITEMS,
              SampleContainerItem.DATASHEET)
          .load(uri);
      ObjectNode loadedFromSnapshot = saveAndLoadSnapshot(loaded, false);
      assertEquals(loaded, loadedFromSnapshot);
    }
    // add maps later
  }

  private void constructCategory(int i, ObjectNode subCategory) {
    subCategory
        .list(SampleCategory.CONTAINER_ITEMS)
        .addNewObject(new SampleContainerItem().name("subcat " + i + " item"))
        .ref(SampleContainerItem.DATASHEET)
        .setNewObject(new SampleDataSheet().name("subcat " + i + " item datasheet "));
  }

}
