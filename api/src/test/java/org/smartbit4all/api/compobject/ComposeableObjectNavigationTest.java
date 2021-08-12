package org.smartbit4all.api.compobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.ApiItemChangeEvent;
import org.smartbit4all.api.ApiItemOperation;
import org.smartbit4all.api.compobject.bean.ComposeableObject;
import org.smartbit4all.api.compobject.bean.ComposeableObjectDef;
import org.smartbit4all.api.compobject.bean.CompositeObject;
import org.smartbit4all.api.navigation.Navigation;
import org.smartbit4all.api.navigation.NavigationConfig;
import org.smartbit4all.api.navigation.NavigationPrimary;
import org.smartbit4all.api.navigation.bean.NavigationNode;
import org.smartbit4all.api.navigation.bean.NavigationReference;
import org.smartbit4all.domain.data.storage.ObjectStorageInMemory;
import org.smartbit4all.domain.data.storage.Storage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ComposeableObjectNavigationTest {

  private static final URI COMPOSITE_OBJECT_COMPDEF_URI = URI.create(ComposeableObjectNavigation.SCHEME + ":/compositeobject/definition");

  @Test
  void simpleCompositeObjectNavigationTest() throws Exception {
    Storage<ComposeableObjectDef> composeableObjStorage = createInMemoryStorage(
        testobj -> testobj.getUri(),
        ComposeableObjectDef.class);
    
    Storage<CompositeObject> objCompStorage = createInMemoryStorage(
        compobj -> compobj.getUri(),
        CompositeObject.class);

    Storage<TestTreeObject> testObjectStorage = createInMemoryStorage(
        testobj -> testobj.getUri(),
        TestTreeObject.class);

    CompositeObjectApi compositeObjectApi = new CompositeObjectApi(objCompStorage);
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

    ComposeableObjectDef compositeObjectCompDef = createCompositeObjectCompObjectDef(compositeObjectApi, composeableObjStorage);
    ComposeableObjectDef testObjectCompDef = createTestCompObjectDef(testTreeObjectApi, composeableObjStorage);
    
    URI compObj1 = URI.create("testnavschema:/compositeObject1.compobj");
    CompositeObject rootCompositeObject = new CompositeObject()
        .uri(compObj1)
        .objects(new ArrayList<>());
    objCompStorage.save(rootCompositeObject);

    NavigationConfig navigationConfig = NavigationConfig.builder()
        .addAssociationMeta(ComposeableObjectNavigation.createAssocMeta(compositeObjectCompDef))
        .addAssociationMeta(ComposeableObjectNavigation.createAssocMeta(testObjectCompDef))
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

    ComposeableObject testCompObject = new ComposeableObject()
        .definition(testObjectCompDef)
        .objectUri(testTreeObject.getUri());

    rootCompositeObject.addObjectsItem(testCompObject);
    objCompStorage.save(rootCompositeObject);

    assertEquals(1, navigation.expandAll(rootNode, true).size());

    addTreeObject(testObjectStorage, "second.tto", testTreeObject);

    List<ApiItemChangeEvent<NavigationReference>> expandEvents = navigation.expandAll(rootNode, true);
    checkNewSize(expandEvents, 1);
    
    NavigationNode firstExpandedNode = expandEvents.get(0).item().getEndNode();
    checkNewSize(navigation.expandAll(firstExpandedNode, true), 1);

    addTreeObject(testObjectStorage, "third.tto", testTreeObject);
    checkNewSize(navigation.expandAll(firstExpandedNode, true), 2);
    
    ctx.close();
  }

  private ComposeableObjectDef createCompositeObjectCompObjectDef(
      CompositeObjectApi compositeObjectApi,
      Storage<ComposeableObjectDef> compDefObjStorage) throws Exception {
    
    ComposeableObjectDef compositeObjectCompDef = new ComposeableObjectDef()
        .apiUri(CompositeObjectApi.API_URI)
        .uri(COMPOSITE_OBJECT_COMPDEF_URI);

    compDefObjStorage.save(compositeObjectCompDef);

    return compositeObjectCompDef;
  }

  private ComposeableObjectDef createTestCompObjectDef(
      TestTreeObjectApi testTreeObjectApi,
      Storage<ComposeableObjectDef> compDefObjStorage) throws Exception {
    
    URI testObjectCompDefUri = URI.create(ComposeableObjectNavigation.SCHEME + ":/testobject/definition");
    ComposeableObjectDef testObjectCompDef = new ComposeableObjectDef()
        .apiUri(TestTreeObjectApi.API_URI)
        .uri(testObjectCompDefUri);

    compDefObjStorage.save(testObjectCompDef);

    return testObjectCompDef;
  }

  private void addTreeObject(
      Storage<TestTreeObject> testObjectStorage,
      String uriPath,
      TestTreeObject parent) throws Exception {
    
    TestTreeObject testTreeObject2 = new TestTreeObject();
    testTreeObject2.setUri(URI.create("testobject:/" + uriPath));
    testObjectStorage.save(testTreeObject2);

    parent.addChild(testTreeObject2);
    testObjectStorage.save(parent);
  }

  private void checkNewSize(List<ApiItemChangeEvent<NavigationReference>> expandEvents, int expected) {
    int counter = 0;
    for(ApiItemChangeEvent<NavigationReference> expandEvent : expandEvents) {
      if(expandEvent.operation() == ApiItemOperation.NEW) {
        counter++;
      }
    }
    assertEquals(expected, counter);
  }

  private <T> Storage<T> createInMemoryStorage(Function<T, URI> uriProvider, Class<T> clazz) {
    ObjectStorageInMemory<T> objectStorageInMemory = new ObjectStorageInMemory<T>(uriProvider);
    return new Storage<>(clazz, objectStorageInMemory, Collections.emptyList());
  }

}
