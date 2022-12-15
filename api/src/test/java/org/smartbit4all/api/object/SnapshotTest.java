package org.smartbit4all.api.object;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.net.URI;
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
    ObjectNode loadedFromSnapshot = saveAndLoadSnapshot(loaded);
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
      ObjectNode loadedFromSnapshot = saveAndLoadSnapshot(loaded);
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
      ObjectNode loadedFromSnapshot = saveAndLoadSnapshot(loaded);
      ref = loadedFromSnapshot.ref(SampleContainerItem.DATASHEET);
      assertTrue(ref.isLoaded());
      assertEquals(loaded, loadedFromSnapshot);
    }
  }

  private ObjectNode saveAndLoadSnapshot(ObjectNode loaded) {
    // create
    SnapshotEntry snapshot = new SnapshotEntry()
        .data(loaded.snapshot());
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
      ObjectNode loadedFromSnapshot = saveAndLoadSnapshot(loaded);
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

      ObjectNode loadedFromSnapshot = saveAndLoadSnapshot(loaded);
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

      ObjectNode loadedFromSnapshot = saveAndLoadSnapshot(loaded);
      assertNotNull(loadedFromSnapshot);
      assertEquals(loaded, loadedFromSnapshot);
    }
  }

  void snapshotWithMap() {
    // implement later
  }

  @Test
  void snapshotComplexObject() {
    // implement later with refs, lists and maps
  }

}
