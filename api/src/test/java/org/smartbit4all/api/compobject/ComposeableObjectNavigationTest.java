package org.smartbit4all.api.compobject;

import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.ApiItemChangeEvent;
import org.smartbit4all.api.ApiItemOperation;
import org.smartbit4all.api.compobject.bean.ComposeableObjectDef;
import org.smartbit4all.api.compobject.bean.CompositeObject;
import org.smartbit4all.api.compobject.bean.CompositeObjectDef;
import org.smartbit4all.api.navigation.Navigation;
import org.smartbit4all.api.navigation.NavigationApi;
import org.smartbit4all.api.navigation.NavigationConfig;
import org.smartbit4all.api.navigation.bean.NavigationNode;
import org.smartbit4all.api.navigation.bean.NavigationReference;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {ComposeableObjectNavigationTestConfig.class})
public class ComposeableObjectNavigationTest {

  public static final URI TODO_COMPDEF_URI =
      URI.create(ComposeableObjectNavigation.SCHEME + ":/testobject/definition");

  public static final URI COMPOSITE_OBJECT_COMPDEF_URI =
      URI.create(ComposeableObjectNavigation.SCHEME + ":/compositeobject/definition");

  public static final URI COMPOSITE_OBJECT_TODO_COMPDEF_ASSOC_URI =
      URI.create(ComposeableObjectNavigation.SCHEME + ":/compositeobject/definition/assoc/todo");

  @Autowired
  private StorageApi storageApi;

  @Autowired
  private NavigationApi navigationApi;

  @Test
  void simpleCompositeObjectNavigationTest() throws Exception {
    ComposeableObjectDef compositeObjectCompDef = createDef(CompositeObjectApi.API_URI);
    ComposeableObjectDef testObjectCompDef = createDef(TestTreeObjectApi.API_URI);

    Storage compositeDefStorage = storageApi.get(CompositeObjectApi.API_SCHEME);
    Storage compositeStorage = storageApi.get(CompositeObjectApi.SCHEME);
    Storage composableStorage = storageApi.get(ComposeableObjectApi.SCHEME);

    StorageObject<CompositeObjectDef> newCompositeObjDef = compositeDefStorage
        .instanceOf(CompositeObjectDef.class);
    CompositeObjectDef compositeDef = new CompositeObjectDef()
        .composeableDefUri(compositeObjectCompDef.getUri());
    newCompositeObjDef.setObject(compositeDef);

    StorageObject<CompositeObject> newCompsoiteObj =
        compositeStorage.instanceOf(CompositeObject.class);
    CompositeObject rootCompositeObject = new CompositeObject()
        .compositeDefUri(compositeDef.getUri());
    newCompsoiteObj.setObject(rootCompositeObject);
    compositeStorage.save(newCompsoiteObj);

    String assocName = "todo";

    ComposeableObjectDef compositeToTodoDef = CompositeObjects.addAssociation(
        storageApi.get(ComposeableObjectApi.SCHEME),
        compositeDef,
        assocName,
        testObjectCompDef.getUri(),
        compositeObjectCompDef.getApiUri(),
        true,
        null,
        "tesztviewname",
        false);

    compositeDefStorage.save(newCompositeObjDef);
    compositeStorage.save(newCompsoiteObj);

    NavigationConfig navigationConfig = NavigationConfig.builder()
        .addAssociationMeta(

            ComposeableObjectNavigation.createAssocMeta(
                "testAssocName",
                compositeToTodoDef.getUri(),
                compositeToTodoDef.getUri(),
                TODO_COMPDEF_URI,
                null))

        .addAssociationMeta(

            ComposeableObjectNavigation.createAssocMeta(
                "testAssocName",
                testObjectCompDef))

        .build();

    Navigation navigation = new Navigation(navigationConfig, navigationApi);
    assertNotNull(navigation);

    NavigationNode rootNode = navigation.addRootNode(
        compositeToTodoDef.getUri(),
        rootCompositeObject.getUri());

    assertEquals(0, navigation.expandAll(rootNode, true).size());

    Storage testTreeStorage = storageApi.get(TestTreeObjectApi.SCHEME);
    StorageObject<TestTreeObject> newTestTreeObject = testTreeStorage
        .instanceOf(TestTreeObject.class);

    TestTreeObject testTreeObject = new TestTreeObject();
    newTestTreeObject.setObject(testTreeObject);

    testTreeStorage.save(newTestTreeObject);

    CompositeObjects.addObject(
        rootCompositeObject,
        testTreeObject.getUri(),
        testObjectCompDef.getUri());

    compositeStorage.save(newCompsoiteObj);

    List<ApiItemChangeEvent<NavigationReference>> expanded = navigation.expandAll(rootNode, true);
    assertEquals(1, expanded.size());

    addTreeObject("second.tto", testTreeObject.getUri());

    checkNewSize(navigation.expandAll(rootNode, true), 0);

    NavigationNode firstExpandedNode = expanded.get(0).item().getEndNode();
    checkNewSize(navigation.expandAll(firstExpandedNode, true), 1);

    checkNewSize(navigation.expandAll(firstExpandedNode, true), 0);
    addTreeObject("third.tto", testTreeObject.getUri());
    checkNewSize(navigation.expandAll(firstExpandedNode, true), 1);
  }

  private ComposeableObjectDef createDef(URI apiUri) throws Exception {
    Storage storage = storageApi.get(ComposeableObjectApi.SCHEME);

    StorageObject<ComposeableObjectDef> newCompObjectDefSo =
        storage.instanceOf(ComposeableObjectDef.class);

    ComposeableObjectDef compositeObjectCompDef = new ComposeableObjectDef()
        .apiUri(apiUri);

    newCompObjectDefSo.setObject(compositeObjectCompDef);

    storage.save(newCompObjectDefSo);

    return compositeObjectCompDef;
  }

  private URI addTreeObject(String uriPath, URI parentUri) throws Exception {
    Storage storage = storageApi.get(TestTreeObjectApi.SCHEME);

    StorageObject<TestTreeObject> newTestTreeObject = storage.instanceOf(TestTreeObject.class);
    TestTreeObject testTreeObject2 = new TestTreeObject();
    newTestTreeObject.setObject(testTreeObject2);
    storage.save(newTestTreeObject);

    StorageObject<TestTreeObject> parentStorageObject = storage
        .load(parentUri, TestTreeObject.class);

    TestTreeObject parent = parentStorageObject.getObject();
    parent.addChild(testTreeObject2);

    storage.save(parentStorageObject);

    return testTreeObject2.getUri();
  }

  private void checkNewSize(
      List<ApiItemChangeEvent<NavigationReference>> expandEvents,
      int expected) {

    int counter = 0;
    for (ApiItemChangeEvent<NavigationReference> expandEvent : expandEvents) {
      if (expandEvent.operation() == ApiItemOperation.NEW) {
        counter++;
      }
    }
    assertEquals(expected, counter);
  }

}
