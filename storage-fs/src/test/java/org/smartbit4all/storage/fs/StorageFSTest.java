package org.smartbit4all.storage.fs;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.invocation.bean.InvocationParameterTemplate;
import org.smartbit4all.api.invocation.bean.InvocationRequestTemplate;
import org.smartbit4all.api.storage.bean.ObjectList;
import org.smartbit4all.api.storage.bean.ObjectReference;
import org.smartbit4all.api.storage.bean.StorageSettings;
import org.smartbit4all.core.io.TestFileUtil;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageLoadOption;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.smartbit4all.domain.data.storage.StorageObjectReferenceEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {StorageFSTestConfig.class})
class StorageFSTest {

  public static final String OBJECT_FILE_EXTENSION = "fs";

  public static class TestData {

    private URI uri;

    private String data;

    public URI getUri() {
      return uri;
    }

    public void setUri(URI uri) {
      this.uri = uri;
    }

    public String getData() {
      return data;
    }

    public void setData(String data) {
      this.data = data;
    }

  }

  public static class RefData {

    private URI uri;

    private String data;

    public URI getUri() {
      return uri;
    }

    public void setUri(URI uri) {
      this.uri = uri;
    }

    public String getData() {
      return data;
    }

    public void setData(String data) {
      this.data = data;
    }

  }

  @BeforeAll
  static void init() throws IOException {
    TestFileUtil.initTestDirectory();
  }

  @Autowired
  StorageApi storageApi;

  @Test
  void saveLoadDeleteTest() throws Exception {
    Storage storage = storageApi.get(StorageFSTestConfig.TESTSCHEME);

    saveAndCheckLoad(storage, "test string1");
    saveAndCheckLoad(storage, "test string2");

    // assertFalse(loaded.isPresent());
  }

  @Test
  void settiongsTest() throws Exception {
    Storage storage = storageApi.get(StorageFSTestConfig.TESTSCHEME);

    StorageObject<FSTestBean> storageObject = storage.instanceOf(FSTestBean.class);

    storageObject.setObject(new FSTestBean("referencesTest"));

    URI uri = storage.save(storageObject);

    Optional<StorageObject<FSTestBean>> optLoaded = storage.load(uri, FSTestBean.class);

    StorageObject<InvocationRequestTemplate> invocationReqObj =
        storage.instanceOf(InvocationRequestTemplate.class);

    invocationReqObj.setObject(new InvocationRequestTemplate().apiClass(StorageApi.class.getName())
        .executionApi("LOCAL").methodName("save")
        .addParametersItem(new InvocationParameterTemplate().defaultValueString("param1")));

    URI invocationUri = storage.save(invocationReqObj);

    StorageObject<StorageSettings> settings = storage.settings();

    String referenceName = "invocation";
    settings.setReference(referenceName, new ObjectReference().uri(invocationUri));

    StorageObject<ObjectList> soMyList = storage.instanceOf(ObjectList.class);

    soMyList.setObject(new ObjectList());

    storage.save(soMyList);

    soMyList.getObject().putUrisItem(invocationUri.toString(), invocationUri);
    soMyList.getObject().putUrisItem(invocationUri.toString() + "1", invocationUri);

    URI myListUri = storage.save(soMyList);

    settings.setReference("MyList", new ObjectReference().uri(myListUri));

    storage.save(settings);

    URI uriReloaded = storage.settings().getReference(referenceName).getReferenceData().getUri();

    URI myListUriLoaded = storage.settings().getReference("MyList").getReferenceData().getUri();

    assertEquals(myListUri, myListUriLoaded);

    ObjectList referenceList =
        storage.load(myListUriLoaded, ObjectList.class).get().getObject();

    assertEquals(invocationUri, uriReloaded);

  }


  @Test
  void referencesTest() throws Exception {
    Storage storage = storageApi.get(StorageFSTestConfig.TESTSCHEME);

    StorageObject<FSTestBean> storageObject = storage.instanceOf(FSTestBean.class);

    storageObject.setObject(new FSTestBean("referencesTest"));

    URI uri = storage.save(storageObject);

    Optional<StorageObject<FSTestBean>> optLoaded = storage.load(uri, FSTestBean.class);

    StorageObject<InvocationRequestTemplate> invocationReqObj =
        storage.instanceOf(InvocationRequestTemplate.class);

    invocationReqObj.setObject(new InvocationRequestTemplate().apiClass(StorageApi.class.getName())
        .executionApi("LOCAL").methodName("save")
        .addParametersItem(new InvocationParameterTemplate().defaultValueString("param1")));

    URI invocationUri = storage.save(invocationReqObj);

    String refId = "001";
    String referenceName = "independentInvocation";
    optLoaded.get().setReference(referenceName,
        new ObjectReference().referenceId(refId).uri(invocationUri));

    // Update the uri.
    uri = storage.save(optLoaded.get());

    // Load again to check existing reference
    optLoaded = storage.load(uri, FSTestBean.class);

    StorageObjectReferenceEntry referenceEntry =
        optLoaded.get().getReference(referenceName);

    assertEquals(invocationUri, referenceEntry.getReferenceData().getUri());
    assertEquals(refId, referenceEntry.getReferenceData().getReferenceId());

  }

  @Test
  void collectionsTest() throws Exception {
    Storage storage = storageApi.get(StorageFSTestConfig.TESTSCHEME);

    StorageObject<FSTestBean> storageObject = storage.instanceOf(FSTestBean.class);

    storageObject.setObject(new FSTestBean("collectionsTest"));

    URI uri = storage.save(storageObject);

    Optional<StorageObject<FSTestBean>> optLoaded = storage.load(uri, FSTestBean.class);



    String collectionName = "independentInvocations";

    int count = 20;

    Map<String, URI> entriesByRefId = new HashMap<>();
    for (int i = 0; i < count; i++) {
      StorageObject<InvocationRequestTemplate> invocationReqObj =
          storage.instanceOf(InvocationRequestTemplate.class);

      invocationReqObj
          .setObject(new InvocationRequestTemplate().apiClass(StorageApi.class.getName())
              .executionApi("LOCAL").methodName("save")
              .addParametersItem(new InvocationParameterTemplate().defaultValueString("param1")));

      URI invocationUri = storage.save(invocationReqObj);

      String refId = "00" + i;
      optLoaded.get().addCollectionEntry(collectionName,
          new ObjectReference().referenceId(refId).uri(invocationUri));

      entriesByRefId.put(refId, invocationUri);
    }

    // Update the object with the collection.
    uri = storage.save(optLoaded.get());

    // Load again to check existing reference
    optLoaded = storage.load(uri, FSTestBean.class);

    {
      List<StorageObjectReferenceEntry> collection =
          optLoaded.get().getCollection(collectionName);


      assertEquals(count, collection.size());

      for (StorageObjectReferenceEntry entry : collection) {
        String referenceId = entry.getReferenceData().getReferenceId();
        URI storedUri = entriesByRefId.get(referenceId);
        assertEquals(storedUri, entry.getReferenceData().getUri());
      }
    }

    // Try to update the collection without creating a new version!
    // Load again to check existing reference
    optLoaded = storage.load(uri, FSTestBean.class, StorageLoadOption.skipData());

    Assertions.assertNull(optLoaded.get().getObject(),
        "The StorageLoadOption.skipData() was set but the object is still loaded.");

    {
      List<StorageObjectReferenceEntry> currentCollection =
          optLoaded.get().getCollection(collectionName);
      currentCollection.stream().forEach(e -> {
        e.setDelete(true);
      });
    }

    for (int i = 0; i < count; i++) {
      StorageObject<InvocationRequestTemplate> invocationReqObj =
          storage.instanceOf(InvocationRequestTemplate.class);

      invocationReqObj
          .setObject(new InvocationRequestTemplate().apiClass(StorageApi.class.getName())
              .executionApi("LOCAL").methodName("save")
              .addParametersItem(new InvocationParameterTemplate().defaultValueString("param1")));

      URI invocationUri = storage.save(invocationReqObj);

      String refId = "xx" + i;
      optLoaded.get().addCollectionEntry(collectionName,
          new ObjectReference().referenceId(refId).uri(invocationUri));
      entriesByRefId.put(refId, invocationUri);
    }

    // Update the object with the collection.
    uri = storage.save(optLoaded.get());

    // Load again to check existing reference
    optLoaded = storage.load(uri, FSTestBean.class, StorageLoadOption.skipData());

    {
      List<StorageObjectReferenceEntry> collection =
          optLoaded.get().getCollection(collectionName);


      assertEquals(count, collection.size());

      for (StorageObjectReferenceEntry entry : collection) {
        String referenceId = entry.getReferenceData().getReferenceId();
        URI storedUri = entriesByRefId.get(referenceId);
        assertEquals(storedUri, entry.getReferenceData().getUri());
      }
    }
  }

  private void saveAndCheckLoad(
      Storage storage,
      String testText) throws Exception {

    StorageObject<FSTestBean> storageObject = storage.instanceOf(FSTestBean.class);

    storageObject.setObject(new FSTestBean(testText));

    URI uri = storage.save(storageObject);

    Optional<StorageObject<FSTestBean>> optLoaded = storage.load(uri, FSTestBean.class);
    assertEquals(testText, optLoaded.get().getObject().getTitle());
  }

  // @Test
  // void storageFsTest() throws Exception {
  // AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
  // ctx.register(MetaConfiguration.class);
  // ctx.register(TestEntityDefConfig.class);
  // ctx.refresh();
  //
  // TestSearchDef testSearchDef = ctx.getBean(TestSearchDef.class);
  //
  // String activeList = "active";
  // String closedList = "closed";
  // String undefinedList = "undefined";
  // String notInAnyState = "notinanystate";
  //
  // TestData tdActive1 = new TestData();
  // tdActive1.setUri(URI.create("teststoragefs:/td1/" + activeList + "." + OBJECT_FILE_EXTENSION));
  // tdActive1.setData(activeList);
  //
  // TestData tdActive2 = new TestData();
  // tdActive2.setUri(URI.create("teststoragefs:/td1/" + activeList + "2." +
  // OBJECT_FILE_EXTENSION));
  // tdActive2.setData(activeList);
  //
  // TestData tdClose = new TestData();
  // tdClose.setUri(URI.create("teststoragefs:/td2/" + closedList + "." + OBJECT_FILE_EXTENSION));
  // tdClose.setData(closedList);
  //
  // TestData tdUndefined = new TestData();
  // tdUndefined
  // .setUri(URI.create("teststoragefs:/td2/" + undefinedList + "." + OBJECT_FILE_EXTENSION));
  // tdUndefined.setData(undefinedList);
  //
  // Function<TestData, URI> uriProvider = (testData) -> testData.getUri();
  //
  // StorageFS<TestData> storageFS = new StorageFS<>(
  // testFsRootFolder(),
  // uriProvider,
  // new GsonBinaryDataObjectSerializer(),
  // TestData.class,
  // OBJECT_FILE_EXTENSION);
  //
  // StorageIndexSimpleFS<TestData> indexApi = new StorageIndexSimpleFS<>(
  // testFsRootFolder(),
  // storageFS);
  //
  // StorageIndexField<TestData, String> stateIndex =
  // createStateIndexField(testSearchDef, activeList, closedList, indexApi);
  //
  // StorageIndexField<TestData, String> emptyStateIndex =
  // createEmptyStateIndexField(testSearchDef, activeList, closedList, notInAnyState, indexApi);
  //
  // StorageIndexField<TestData, Boolean> isActiveIndex =
  // createIsActiveIndexField(testSearchDef, activeList, indexApi);
  //
  // StorageIndex<TestData> storageIndex = new StorageIndex<>(
  // testSearchDef,
  // testSearchDef.key(),
  // indexApi,
  // Arrays.asList(isActiveIndex, stateIndex, emptyStateIndex),
  // uriProvider);
  //
  // // Storage.of(TestData.class).save(tdActive1);
  // // List<TestData> datas = Storage.of(TestData.class).search(searchDef).listDatas(expression);
  //
  // // Storage storage;
  //
  // // List<TestData> datas = storage.of(TestData.class).search(searchDef).listDatas(expression);
  //
  // // List<TestData> list = Crud.read(testSearchDef).where(null).list(TestData.class);
  //
  // // List<TestData> datas = objectStorage.search(searchDef).listDatas(expression);
  //
  // Storage<TestData> objectStorage = new Storage<>(TestData.class,
  // storageFS,
  // Arrays.asList(storageIndex));
  //
  // objectStorage.save(tdActive1);
  // assertEquals(1, objectStorage.loadAll().size());
  //
  // objectStorage.save(tdClose);
  // assertEquals(2, objectStorage.loadAll().size());
  //
  // objectStorage.save(tdUndefined);
  // assertEquals(3, objectStorage.loadAll().size());
  //
  // Expression activeExpression = testSearchDef.state().eq(activeList);
  // Expression closedExpression = testSearchDef.state().eq(closedList);
  // Expression emptyStateExpression = testSearchDef.emptyState().eq(notInAnyState);
  // Expression notActiveExpression = testSearchDef.isActive().eq(false);
  // Expression oneMatchOtherEmptyExpression =
  // testSearchDef.state().eq("cica").AND(testSearchDef.state().eq(activeList));
  // Expression oneMatchOtherEmptyReversedExpression =
  // testSearchDef.state().eq(activeList).AND(testSearchDef.state().eq("cica"));
  //
  // List<TestData> activeDatas = objectStorage.listDatas(activeExpression);
  // assertEquals(1, activeDatas.size());
  //
  // List<TestData> closedDatas = objectStorage.listDatas(closedExpression);
  // assertEquals(1, closedDatas.size());
  //
  // List<TestData> undefinedDatas = objectStorage.listDatas(emptyStateExpression);
  // assertEquals(1, undefinedDatas.size());
  //
  // List<TestData> notActive = objectStorage.listDatas(notActiveExpression);
  // assertEquals(2, notActive.size());
  //
  // objectStorage.save(tdActive2, uriProvider.apply(tdActive2));
  //
  // List<TestData> twoActiveDatas = objectStorage.listDatas(activeExpression);
  // assertEquals(2, twoActiveDatas.size());
  //
  // assertEquals(1, objectStorage.listDatas(closedExpression).size());
  //
  // List<TestData> oneMatchOtherEmpty = objectStorage.listDatas(oneMatchOtherEmptyExpression);
  // assertEquals(0, oneMatchOtherEmpty.size());
  //
  // List<TestData> oneMatchOtherEmptyReversed =
  // objectStorage.listDatas(oneMatchOtherEmptyReversedExpression);
  // assertEquals(0, oneMatchOtherEmptyReversed.size());
  //
  // List<TestData> twoMatchAndOneMatch = objectStorage
  // .listDatas(testSearchDef.isActive().eq(false).AND(testSearchDef.state().eq(closedList)));
  // assertEquals(1, twoMatchAndOneMatch.size());
  //
  // StorageReindexerFS reindexer =
  // new StorageReindexerFS("teststoragefs", testFsRootFolder(), "fs");
  // assertEquals(4, reindexer.listAllUris().size());
  //
  // ctx.close();
  // }
  //
  // @Test
  // void storageFsTestWithConstructedUris() throws Exception {
  // AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
  // ctx.register(MetaConfiguration.class);
  // ctx.register(TestEntityDefConfig.class);
  // ctx.refresh();
  //
  // Function<TestData, URI> uriProvider = (testData) -> testData.getUri();
  //
  // StorageFS<TestData> storageFS = new StorageFS<>(
  // testFsRootFolder(),
  // uriProvider,
  // new GsonBinaryDataObjectSerializer(),
  // TestData.class,
  // OBJECT_FILE_EXTENSION);
  //
  // storageFS
  // .setUriProvider(new ObjectUriProviderFSDefault<>("teststoragefs:/", OBJECT_FILE_EXTENSION));
  //
  // TestSearchDef testSearchDef = ctx.getBean(TestSearchDef.class);
  //
  // String activeList = "active";
  // String closedList = "closed";
  // String undefinedList = "undefined";
  // String notInAnyState = "notinanystate";
  //
  // TestData tdActive1 = new TestData();
  // tdActive1.setUri(storageFS.getUriProvider().constructUri(tdActive1));
  // tdActive1.setData(activeList);
  //
  // URI uri = storageFS.save(tdActive1);
  //
  // Optional<TestData> loaded1 = storageFS.load(uri);
  //
  // Assertions.assertTrue(loaded1.isPresent());
  //
  // assertEquals(tdActive1.data, loaded1.get().data);
  // assertEquals(tdActive1.uri, loaded1.get().uri);
  //
  // String myref = "myref";
  // storageFS
  // .saveReferences(new ObjectReferenceRequest(uri, RefData.class).add(myref));
  //
  // Optional<ObjectReferenceList> references =
  // storageFS.loadReferences(uri, RefData.class.getName());
  //
  // Assertions.assertTrue(
  // references.isPresent());
  // Assertions.assertTrue(
  // references.get().getReferences().stream().anyMatch(r -> r.getReferenceId().equals(myref)));
  //
  // ctx.close();
  // }
  //
  // private StorageIndexField<TestData, Boolean> createIsActiveIndexField(
  // TestSearchDef testSearchDef,
  // String activeList,
  // StorageIndexer<TestData> indexApi) {
  //
  // Function<TestData, Optional<Boolean>> isActiveCalculator = (testData) -> {
  //
  // if (testData.getData().contains(activeList)) {
  // return Optional.of(Boolean.TRUE);
  // } else {
  // return Optional.of(Boolean.FALSE);
  // }
  //
  // };
  //
  // StorageIndexField<TestData, Boolean> isActiveIndex = new StorageIndexField<>(
  // testSearchDef,
  // testSearchDef.key(),
  // testSearchDef.isActive(),
  // isActiveCalculator,
  // indexApi);
  //
  // return isActiveIndex;
  // }
  //
  // private StorageIndexField<TestData, String> createEmptyStateIndexField(
  // TestSearchDef testSearchDef,
  // String activeList, String closedList, String notInAnyState,
  // StorageIndexer<TestData> indexApi) {
  //
  // Function<TestData, Optional<String>> emptyStateCalculator = (testData) -> {
  //
  // if (!testData.getData().contains(activeList) && !testData.getData().contains(closedList)) {
  // return Optional.of(notInAnyState);
  // } else {
  // return Optional.empty();
  // }
  //
  // };
  //
  // StorageIndexField<TestData, String> emptyStateIndex = new StorageIndexField<>(
  // testSearchDef,
  // testSearchDef.key(),
  // testSearchDef.emptyState(),
  // emptyStateCalculator,
  // indexApi);
  //
  // return emptyStateIndex;
  // }
  //
  // private StorageIndexField<TestData, String> createStateIndexField(
  // TestSearchDef testSearchDef,
  // String activeList, String closedList,
  // StorageIndexer<TestData> indexApi) {
  //
  // Function<TestData, Optional<String>> stateCalculator = (testData) -> {
  //
  // if (testData.getData().contains(activeList)) {
  // return Optional.of(activeList);
  // } else if (testData.getData().contains(closedList)) {
  // return Optional.of(closedList);
  // } else {
  // return Optional.empty();
  // }
  //
  // };
  //
  // StorageIndexField<TestData, String> stateIndex = new StorageIndexField<>(
  // testSearchDef,
  // testSearchDef.key(),
  // testSearchDef.state(),
  // stateCalculator,
  // indexApi);
  //
  // return stateIndex;
  // }
  //
  // @Entity("test")
  // @Table("test")
  // public interface TestSearchDef extends EntityDefinition {
  //
  // @Id
  // @OwnProperty(name = "key", columnName = "key")
  // Property<URI> key();
  //
  // @OwnProperty(name = "state", columnName = "state")
  // Property<String> state();
  //
  // @OwnProperty(name = "emptyState", columnName = "emptyState")
  // Property<String> emptyState();
  //
  // @OwnProperty(name = "isActive", columnName = "isActive")
  // Property<Boolean> isActive();
  //
  // }
  //
  // public static class TestEntityDefConfig extends EntityConfiguration {
  //
  // @Bean("test")
  // public TestSearchDef userAccountDef() {
  // TestSearchDef userAccDef = createEntityProxy(TestSearchDef.class);
  // return userAccDef;
  // }
  //
  // }

}
