package org.smartbit4all.api.compobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.ApiItemChangeEvent;
import org.smartbit4all.api.ApiItemOperation;
import org.smartbit4all.api.compdata.CompositeDataCollector;
import org.smartbit4all.api.compdata.NavCompositeDataCollector;
import org.smartbit4all.api.compdata.bean.CompositeData;
import org.smartbit4all.api.compdata.bean.CompositeDataCollection;
import org.smartbit4all.api.compdata.bean.CompositeDataItem;
import org.smartbit4all.api.compobject.bean.ComposeableObjectDef;
import org.smartbit4all.api.compobject.bean.CompositeObject;
import org.smartbit4all.api.compobject.bean.CompositeObjectDef;
import org.smartbit4all.api.navigation.Navigation;
import org.smartbit4all.api.navigation.NavigationConfig;
import org.smartbit4all.api.navigation.NavigationPrimary;
import org.smartbit4all.api.navigation.bean.NavigationNode;
import org.smartbit4all.api.navigation.bean.NavigationReference;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.domain.data.storage.ObjectStorageInMemory;
import org.smartbit4all.domain.data.storage.Storage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ComposeableObjectNavigationTest {

  public static final URI TODO_COMPDEF_URI =
      URI.create(ComposeableObjectNavigation.SCHEME + ":/testobject/definition");

  public static final URI COMPOSITE_OBJECT_COMPDEF_URI =
      URI.create(ComposeableObjectNavigation.SCHEME + ":/compositeobject/definition");

  public static final URI COMPOSITE_OBJECT_TODO_COMPDEF_ASSOC_URI =
      URI.create(ComposeableObjectNavigation.SCHEME + ":/compositeobject/definition/assoc/todo");

  @Test
  void dataCollectorWithNavigationTest() throws Exception {
    Storage<ComposeableObjectDef> composeableObjStorage = createInMemoryStorage(
        testobj -> testobj.getUri(),
        ComposeableObjectDef.class);

    Storage<CompositeObject> objCompStorage = createInMemoryStorage(
        compobj -> compobj.getUri(),
        CompositeObject.class);

    Storage<TestTreeObject> testObjectStorage = createInMemoryStorage(
        testobj -> testobj.getUri(),
        TestTreeObject.class);

    Storage<CompositeObjectDef> compositeDefStorage = createInMemoryStorage(
        compobj -> compobj.getUri(),
        CompositeObjectDef.class);

    CompositeObjectApi compositeObjectApi = new CompositeObjectApi(
        objCompStorage,
        compositeDefStorage,
        composeableObjStorage);

    TestTreeObjectApi testTreeObjectApi = new TestTreeObjectApi(testObjectStorage);

    ComposeableObjectPrimaryApi composeablePrimary = new ComposeableObjectPrimaryApi();
    composeablePrimary.add(compositeObjectApi);
    composeablePrimary.add(testTreeObjectApi);

    Set<Class<?>> beans = new HashSet<>();
    beans.add(TestTreeObject.class);
    composeablePrimary.addDescriptor(ApiBeanDescriptor.of(beans));

    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.register(NavigationPrimary.class);
    ctx.registerBean(
        ComposeableObjectNavigation.class,
        () -> new ComposeableObjectNavigation(
            ComposeableObjectNavigation.SCHEME,
            composeablePrimary,
            composeableObjStorage));
    ctx.refresh();

    ComposeableObjectDef compositeObjectCompDef = createDef(
        composeableObjStorage,
        COMPOSITE_OBJECT_COMPDEF_URI,
        CompositeObjectApi.API_URI);

    ComposeableObjectDef testObjectCompDef = createDef(
        composeableObjStorage,
        TODO_COMPDEF_URI,
        TestTreeObjectApi.API_URI);

    URI compObjDef = URI.create("testnavschema:/compositeObjectDef.compobj");
    CompositeObjectDef compositeDef = new CompositeObjectDef()
        .uri(compObjDef)
        .composeableDefUri(compositeObjectCompDef.getUri());
    compositeDefStorage.save(compositeDef);

    URI compObj1 = URI.create("testnavschema:/compositeObject1.compobj");
    CompositeObject rootCompositeObject = new CompositeObject()
        .uri(compObj1)
        .compositeDefUri(compositeDef.getUri());

    String assocName = "todo";

    ComposeableObjectDef compositeToTodoDef = CompositeObjects.addAssociation(
        assocName,
        compositeDef,
        TODO_COMPDEF_URI,
        compositeObjectCompDef.getApiUri(),
        true,
        null,
        "tesztviewname",
        false);
    composeableObjStorage.save(compositeToTodoDef);

    objCompStorage.save(rootCompositeObject);

    NavigationPrimary primaryNavigationApi = ctx.getBean(NavigationPrimary.class);

    CompositeDataCollector dataCollector = new NavCompositeDataCollector(compositeDefStorage,
        composeableObjStorage, primaryNavigationApi);

    CompositeData data = new CompositeData().configUri(compObjDef);
    CompositeDataItem item =
        new CompositeDataItem().assocUri(COMPOSITE_OBJECT_COMPDEF_URI).objectUri(compObj1);
    data.addRootsItem(item);

    CompositeDataCollection collection = new CompositeDataCollection();
    collection.addCompositeDatasItem(data);

    String path = "/" + assocName + "#" + "uri";
    Map<String, Object> collectedData = dataCollector.collect(Arrays.asList(path), collection);
    assertTrue(collectedData.isEmpty());

    TestTreeObject testTreeObject = new TestTreeObject();
    testTreeObject.setUri(URI.create("testobject:/first.tto"));
    testObjectStorage.save(testTreeObject);

    CompositeObjects.addObject(
        rootCompositeObject,
        testTreeObject.getUri(),
        testObjectCompDef.getUri());

    objCompStorage.save(rootCompositeObject);

    assertEquals(
        testTreeObject.getUri(),
        dataCollector.collect(Arrays.asList(path), collection).values().iterator().next());

    String path2 = "/" + assocName + "/" + assocName + "#" + "uri";
    assertTrue(dataCollector.collect(Arrays.asList(path2), collection).isEmpty());

    URI childUri = addTreeObject(testObjectStorage, "second.tto", testTreeObject);
    assertEquals(
        childUri,
        dataCollector.collect(Arrays.asList(path2), collection).values().iterator().next());
  }

  @Test
  void simpleCompositeObjectNavigationTest() throws Exception {
    Storage<ComposeableObjectDef> composeableObjStorage = createInMemoryStorage(
        testobj -> testobj.getUri(),
        ComposeableObjectDef.class);

    Storage<CompositeObject> objCompStorage = createInMemoryStorage(
        compobj -> compobj.getUri(),
        CompositeObject.class);

    Storage<CompositeObjectDef> compositeDefStorage = createInMemoryStorage(
        compobj -> compobj.getUri(),
        CompositeObjectDef.class);

    Storage<TestTreeObject> testObjectStorage = createInMemoryStorage(
        testobj -> testobj.getUri(),
        TestTreeObject.class);

    CompositeObjectApi compositeObjectApi = new CompositeObjectApi(
        objCompStorage,
        compositeDefStorage,
        composeableObjStorage);

    TestTreeObjectApi testTreeObjectApi = new TestTreeObjectApi(testObjectStorage);

    ComposeableObjectPrimaryApi composeablePrimary = new ComposeableObjectPrimaryApi();
    composeablePrimary.add(compositeObjectApi);
    composeablePrimary.add(testTreeObjectApi);

    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();

    ctx.register(NavigationPrimary.class);
    ctx.registerBean(
        ComposeableObjectNavigation.class,
        () -> new ComposeableObjectNavigation(
            ComposeableObjectNavigation.SCHEME,
            composeablePrimary,
            composeableObjStorage));

    ctx.refresh();

    ComposeableObjectDef compositeObjectCompDef = createDef(
        composeableObjStorage,
        COMPOSITE_OBJECT_COMPDEF_URI,
        CompositeObjectApi.API_URI);

    ComposeableObjectDef testObjectCompDef = createDef(
        composeableObjStorage,
        TODO_COMPDEF_URI,
        TestTreeObjectApi.API_URI);

    URI compObjDef = URI.create("testnavschema:/compositeObjectDef.compobj");
    CompositeObjectDef compositeDef = new CompositeObjectDef()
        .uri(compObjDef)
        .composeableDefUri(compositeObjectCompDef.getUri());
    compositeDefStorage.save(compositeDef);

    URI compObj1 = URI.create("testnavschema:/compositeObject1.compobj");
    CompositeObject rootCompositeObject = new CompositeObject()
        .uri(compObj1)
        .compositeDefUri(compositeDef.getUri());

    ComposeableObjectDef compositeToTodoDef = CompositeObjects.addAssociation(
        "todo",
        compositeDef,
        TODO_COMPDEF_URI,
        compositeObjectCompDef.getApiUri(),
        true,
        null,
        "tesztviewname",
        false);
    composeableObjStorage.save(compositeToTodoDef);

    objCompStorage.save(rootCompositeObject);

    NavigationConfig navigationConfig = NavigationConfig.builder()
        .addAssociationMeta(

            ComposeableObjectNavigation.createAssocMeta(
                "testAssocName",
                compositeToTodoDef.getUri(),
                COMPOSITE_OBJECT_COMPDEF_URI,
                TODO_COMPDEF_URI,
                null))

        .addAssociationMeta(

            ComposeableObjectNavigation.createAssocMeta(
                "testAssocName",
                testObjectCompDef))

        .build();

    NavigationPrimary primaryNavigationApi = ctx.getBean(NavigationPrimary.class);

    Navigation navigation = new Navigation(navigationConfig, primaryNavigationApi);
    assertNotNull(navigation);

    NavigationNode rootNode = navigation.addRootNode(
        COMPOSITE_OBJECT_COMPDEF_URI,
        compObj1);

    assertEquals(0, navigation.expandAll(rootNode, true).size());

    TestTreeObject testTreeObject = new TestTreeObject();
    testTreeObject.setUri(URI.create("testobject:/first.tto"));
    testObjectStorage.save(testTreeObject);

    CompositeObjects.addObject(
        rootCompositeObject,
        testTreeObject.getUri(),
        testObjectCompDef.getUri());

    objCompStorage.save(rootCompositeObject);

    List<ApiItemChangeEvent<NavigationReference>> expanded = navigation.expandAll(rootNode, true);
    assertEquals(1, expanded.size());

    addTreeObject(testObjectStorage, "second.tto", testTreeObject);

    checkNewSize(navigation.expandAll(rootNode, true), 0);

    NavigationNode firstExpandedNode = expanded.get(0).item().getEndNode();
    checkNewSize(navigation.expandAll(firstExpandedNode, true), 1);

    checkNewSize(navigation.expandAll(firstExpandedNode, true), 0);
    addTreeObject(testObjectStorage, "third.tto", testTreeObject);
    checkNewSize(navigation.expandAll(firstExpandedNode, true), 1);

    ctx.close();
  }

  private ComposeableObjectDef createDef(
      Storage<ComposeableObjectDef> compDefObjStorage,
      URI uri,
      URI apiUri) throws Exception {

    ComposeableObjectDef compositeObjectCompDef = new ComposeableObjectDef()
        .uri(uri)
        .apiUri(apiUri);

    compDefObjStorage.save(compositeObjectCompDef);

    return compositeObjectCompDef;
  }

  private URI addTreeObject(
      Storage<TestTreeObject> testObjectStorage,
      String uriPath,
      TestTreeObject parent) throws Exception {

    URI newUri = URI.create("testobject:/" + uriPath);

    TestTreeObject testTreeObject2 = new TestTreeObject();
    testTreeObject2.setUri(newUri);
    testObjectStorage.save(testTreeObject2);

    parent.addChild(testTreeObject2);
    testObjectStorage.save(parent);

    return newUri;
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

  private <T> Storage<T> createInMemoryStorage(Function<T, URI> uriAccessor, Class<T> clazz) {
    ObjectStorageInMemory<T> objectStorageInMemory =
        new ObjectStorageInMemory<T>(uriAccessor, null);
    return new Storage<>(clazz, objectStorageInMemory, Collections.emptyList());
  }

}
