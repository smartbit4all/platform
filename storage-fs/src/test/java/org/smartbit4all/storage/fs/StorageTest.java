package org.smartbit4all.storage.fs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataObject;
import org.smartbit4all.api.invocation.bean.InvocationParameterTemplate;
import org.smartbit4all.api.invocation.bean.InvocationRequestTemplate;
import org.smartbit4all.api.storage.bean.ObjectHistory;
import org.smartbit4all.api.storage.bean.ObjectHistoryEntry;
import org.smartbit4all.api.storage.bean.ObjectMap;
import org.smartbit4all.api.storage.bean.ObjectMapRequest;
import org.smartbit4all.api.storage.bean.ObjectReference;
import org.smartbit4all.api.storage.bean.StorageSettings;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectSummarySupplier;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.storage.ObjectModificationException;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageLoadOption;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.smartbit4all.domain.data.storage.StorageObjectLock;
import org.smartbit4all.domain.data.storage.StorageObjectReferenceEntry;
import org.smartbit4all.domain.data.storage.history.ObjectHistoryApi;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.io.ByteStreams;

@TestInstance(Lifecycle.PER_CLASS)
@Disabled
class StorageTest {

  private static final String MY_MAP = "MyMap";

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

  @Autowired
  StorageApi storageApi;

  @Autowired
  ObjectHistoryApi historyApi;

  @Autowired
  ObjectApi objectApi;

  @Autowired
  StorageTestApi testApi;

  protected URI collectionsTestUri;

  @Test
  void saveLoadDeleteTest() throws Exception {
    Storage storage = storageApi.get(StorageTestConfig.TESTSCHEME);

    saveAndCheckLoad(storage, "test string1");
    saveAndCheckLoad(storage, "test string2");

    testApi.saveAndLoad(storage, MY_MAP);

    // assertFalse(loaded.isPresent());
  }

  @Test
  void saveLoadBinaryDataTest() throws Exception {
    Storage storage = storageApi.get(StorageTestConfig.TESTSCHEME);

    File tempFile = File.createTempFile(getClass().getSimpleName(), "temp");
    FileWriter fw = new FileWriter(tempFile);
    fw.write("test");
    fw.close();

    BinaryData binaryData = new BinaryData(tempFile);

    StorageObject<BinaryDataObject> storageObject = storage.instanceOf(BinaryDataObject.class);
    storageObject.setObject(binaryData.asObject());
    URI save = storage.save(storageObject);

    BinaryDataObject bdResult = storage.read(save, BinaryDataObject.class);

    assertEquals(save, bdResult.getUri());

    ByteArrayOutputStream bdos = new ByteArrayOutputStream();
    ByteStreams.copy(bdResult.getBinaryData().inputStream(), bdos);
    bdos.close();

    assertEquals("test", new String(bdos.toByteArray()));

  }

  @Test
  void historyTest() throws Exception {
    long startCreationTime = System.currentTimeMillis();
    Storage storage = storageApi.get(StorageTestConfig.TESTSCHEME);

    StorageObject<FSTestBean> storageObject = storage.instanceOf(FSTestBean.class);

    storageObject.setObject(new FSTestBean("v0"));

    URI uri = storage.save(storageObject);

    int versionCount = 100;
    // Creating more versions.

    long saveTimes = 0;

    for (int i = 1; i < versionCount; i++) {

      StorageObject<FSTestBean> object = storage.load(uri, FSTestBean.class);
      object.getObject().setTitle("v" + i);
      long startSave = System.currentTimeMillis();
      URI savedUri = storage.save(object);
      assertEquals(savedUri, object.getUri());
      assertEquals(URI.create(savedUri + StringConstant.HASH + i), object.getVersionUri());
      saveTimes += System.currentTimeMillis() - startSave;
    }

    long endCreationTime = System.currentTimeMillis();

    List<ObjectHistoryEntry> loadHistory = storage.loadHistory(uri);

    int versionSerial = 0;
    for (ObjectHistoryEntry objectHistoryEntry : loadHistory) {
      String title = "v" + versionSerial++;
      assertEquals(title + " (" + uri + ")", objectHistoryEntry.getSummary());
    }

    long endTime = System.currentTimeMillis();

    long totalCreationTime = endCreationTime - startCreationTime;

    System.out.println("Total creation time: " + (totalCreationTime) + ", Total save time: "
        + saveTimes + " , creationOneObject: "
        + (totalCreationTime / versionCount) + ", save one object: " + (saveTimes / versionCount)
        + ", history retrieval time: "
        + (endTime - endCreationTime));

    Assertions.assertTrue(totalCreationTime < 1000);

    assertEquals(versionCount, loadHistory.size());



    // Load a specific version

    long startLoad = System.currentTimeMillis();

    ObjectDefinition<FSTestBean> objectDefinition = objectApi.definition(FSTestBean.class);
    ObjectSummarySupplier<FSTestBean> summarySupplier = objectDefinition.getSummarySupplier();

    for (int i = 0; i < versionCount; i++) {

      URI versionUri = URI.create(uri.toString() + StringConstant.HASH + i);
      StorageObject<FSTestBean> object = storage
          .load(versionUri, FSTestBean.class);
      String title = object.getObject().getTitle();
      assertEquals("v" + i, title);
      assertEquals(object.getUri(), uri);
      assertEquals(object.getObject().getUri(), versionUri);
      assertEquals(title + " (" + object.getObject().getUri() + ")",
          summarySupplier.apply(object.getObject()));

    }

    long endLoad = System.currentTimeMillis();

    System.out.println("Total load time: " + (endLoad - startLoad) + ", load one object: "
        + (endLoad - startLoad) / versionCount);
    // assertFalse(loaded.isPresent());
  }

  @Test
  void objectHistoryTest() throws Exception {
    Storage storage = storageApi.get(StorageTestConfig.TESTSCHEME);
    StorageObject<FSTestBean> storageObject = storage.instanceOf(FSTestBean.class);

    storageObject.setObject(new FSTestBean("v0"));

    URI uri = storage.save(storageObject);

    StorageObject<FSTestBean> object = storage.load(uri, FSTestBean.class);
    object.getObject().setTitle("v" + 1);
    storage.save(object);

    ObjectHistory objectHistory = historyApi.getObjectHistory(uri, StorageTestConfig.TESTSCHEME);

    assertEquals(2, objectHistory.getObjectHistoryEntries().size());

    URI v0Uri = objectHistory.getObjectHistoryEntries().get(0).getVersionUri();
    assertEquals(URI.create(uri.toString() + StringConstant.HASH + 0), v0Uri);

    StorageObject<FSTestBean> v0obj = storage.load(v0Uri, FSTestBean.class);
    FSTestBean v0bean = v0obj.getObject();

    assertEquals("v0", v0bean.getTitle());

    URI v1Uri = objectHistory.getObjectHistoryEntries().get(1).getVersionUri();
    assertEquals(URI.create(uri.toString() + StringConstant.HASH + 1), v1Uri);

    StorageObject<FSTestBean> v1obj = storage.load(v1Uri, FSTestBean.class);;
    FSTestBean v1bean = v1obj.getObject();

    assertEquals("v1", v1bean.getTitle());
  }

  @Test
  void optimisticLockTest() throws Exception {
    Storage storage = storageApi.get(StorageTestConfig.TESTSCHEME);

    URI uri;
    {
      StorageObject<FSTestBean> storageObject = storage.instanceOf(FSTestBean.class);

      storageObject.setObject(new FSTestBean("LockObject"));

      uri = storage.save(storageObject);
    }

    // Load the same version and modify the first one.
    StorageObject<FSTestBean> storageObject1 = storage.load(uri, FSTestBean.class);

    StorageObject<FSTestBean> storageObject2 = storage.load(uri, FSTestBean.class);

    storageObject1.getObject().setTitle("LockObject-modified");

    storage.save(storageObject1);

    // Now try to modify the object 2 and set the strict version control.

    storageObject2.getObject().setTitle("LockObject-paralell-modified");
    storageObject2.setStrictVersionCheck(true);

    Assertions.assertThrows(ObjectModificationException.class, () -> {
      storage.save(storageObject2);
    });

  }

  @Test
  void settingsTest() throws Exception {
    Storage storage = storageApi.get(StorageTestConfig.TESTSCHEME);

    StorageObject<FSTestBean> storageObject = storage.instanceOf(FSTestBean.class);

    storageObject.setObject(new FSTestBean("referencesTest"));

    storage.save(storageObject);

    StorageObject<InvocationRequestTemplate> invocationReqObj =
        storage.instanceOf(InvocationRequestTemplate.class);

    invocationReqObj.setObject(new InvocationRequestTemplate().apiClass(StorageApi.class.getName())
        .executionApi("LOCAL").methodName("save")
        .addParametersItem(new InvocationParameterTemplate().defaultValueString("param1")));

    URI invocationUri = storage.save(invocationReqObj);

    StorageObject<StorageSettings> settings = storage.settings();

    String referenceName = "invocation";
    settings.setReference(referenceName, new ObjectReference().uri(invocationUri));

    StorageObject<ObjectMap> soMyList = storage.instanceOf(ObjectMap.class);

    soMyList.setObject(new ObjectMap());

    storage.save(soMyList);

    soMyList.getObject().putUrisItem(invocationUri.toString(), invocationUri);
    soMyList.getObject().putUrisItem(invocationUri.toString() + "1", invocationUri);

    URI myListUri = storage.save(soMyList);

    settings.setReference("MyList", new ObjectReference().uri(myListUri));

    storage.save(settings);

    URI uriReloaded = storage.settings().getReference(referenceName).getReferenceData().getUri();

    URI myListUriLoaded = storage.settings().getReference("MyList").getReferenceData().getUri();

    assertEquals(myListUri, myListUriLoaded);

    ObjectMap referenceList =
        storage.load(myListUriLoaded, ObjectMap.class).getObject();

    assertEquals(soMyList.getObject().getUris().size(), referenceList.getUris().size());

    assertEquals(invocationUri, uriReloaded);
  }

  @Test
  void referencesTest() throws Exception {
    Storage storage = storageApi.get(StorageTestConfig.TESTSCHEME);

    StorageObject<FSTestBean> storageObject = storage.instanceOf(FSTestBean.class);

    storageObject.setObject(new FSTestBean("referencesTest"));

    URI uri = storage.save(storageObject);

    StorageObject<FSTestBean> optLoaded = storage.load(uri, FSTestBean.class);

    StorageObject<InvocationRequestTemplate> invocationReqObj =
        storage.instanceOf(InvocationRequestTemplate.class);

    invocationReqObj.setObject(new InvocationRequestTemplate().apiClass(StorageApi.class.getName())
        .executionApi("LOCAL").methodName("save")
        .addParametersItem(new InvocationParameterTemplate().defaultValueString("param1")));

    URI invocationUri = storage.save(invocationReqObj);

    String refId = "001";
    String referenceName = "independentInvocation";
    optLoaded.setReference(referenceName,
        new ObjectReference().referenceId(refId).uri(invocationUri));

    // Update the uri.
    uri = storage.save(optLoaded);

    // Load again to check existing reference
    optLoaded = storage.load(uri, FSTestBean.class);

    StorageObjectReferenceEntry referenceEntry =
        optLoaded.getReference(referenceName);

    assertEquals(invocationUri, referenceEntry.getReferenceData().getUri());
    assertEquals(refId, referenceEntry.getReferenceData().getReferenceId());

  }

  @RepeatedTest(5)
  void collectionsTest() throws Exception {
    ExecutorService pool = Executors.newFixedThreadPool(5);
    List<Future<?>> futures = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      Future<?> submit = pool.submit(() -> {
        collectionsTestInner(true);
      });
      futures.add(submit);
    }
    for (Future<?> future : futures) {
      future.get();
    }
  }

  void collectionsTestInner(boolean assumeSingleThread) {
    Storage storage = storageApi.get(StorageTestConfig.TESTSCHEME);

    // StorageObject<FSTestBean> storageObject = storage.instanceOf(FSTestBean.class);
    // storageObject.setObject(new FSTestBean("collectionsTest"));
    // URI uri = storage.save(storageObject);
    StorageObjectLock lock = storage.getLock(collectionsTestUri);

    lock.lock();
    try {

      StorageObject<FSTestBean> optLoaded =
          storage.load(collectionsTestUri, FSTestBean.class);



      // String collectionName = "independentInvocations - " + Thread.currentThread().getName();
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
        optLoaded.addCollectionEntry(collectionName,
            new ObjectReference().referenceId(refId).uri(invocationUri));

        entriesByRefId.put(refId, invocationUri);
      }

      // Update the object with the collection.
      URI uriAfterSave = storage.save(optLoaded);
      assertEquals(collectionsTestUri, uriAfterSave);

      // Load again to check existing reference
      optLoaded = storage.load(collectionsTestUri, FSTestBean.class);

      {
        List<StorageObjectReferenceEntry> collection =
            optLoaded.getCollection(collectionName);


        assertEquals(count, collection.size());

        if (assumeSingleThread) {
          for (StorageObjectReferenceEntry entry : collection) {
            String referenceId = entry.getReferenceData().getReferenceId();
            URI storedUri = entriesByRefId.get(referenceId);
            assertEquals(storedUri, entry.getReferenceData().getUri());
          }
        }
      }

      // Try to update the collection without creating a new version!
      // Load again to check existing reference
      optLoaded = storage.load(collectionsTestUri, FSTestBean.class, StorageLoadOption.skipData());

      Assertions.assertNull(optLoaded.getObject(),
          "The StorageLoadOption.skipData() was set but the object is still loaded.");

      {
        List<StorageObjectReferenceEntry> currentCollection =
            optLoaded.getCollection(collectionName);
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
        optLoaded.addCollectionEntry(collectionName,
            new ObjectReference().referenceId(refId).uri(invocationUri));
        entriesByRefId.put(refId, invocationUri);
      }

      // Update the object with the collection.
      uriAfterSave = storage.save(optLoaded);
      assertEquals(collectionsTestUri, uriAfterSave);

      // Load again to check existing reference
      optLoaded = storage.load(collectionsTestUri, FSTestBean.class);

      {
        List<StorageObjectReferenceEntry> collection =
            optLoaded.getCollection(collectionName);

        Assertions.assertNotNull(optLoaded.getObject());

        assertEquals(count, collection.size());

        if (assumeSingleThread) {
          for (StorageObjectReferenceEntry entry : collection) {
            String referenceId = entry.getReferenceData().getReferenceId();
            URI storedUri = entriesByRefId.get(referenceId);
            assertEquals(storedUri, entry.getReferenceData().getUri());
          }
        }
      }
    } finally {
      lock.unlockAndRelease();
    }
  }

  @Test
  void onSucceedTest() throws Exception {
    Storage storage = storageApi.get(StorageTestConfig.TESTSCHEME);

    URI uri;
    {
      StorageObject<FSTestBean> storageObject = storage.instanceOf(FSTestBean.class);

      storageObject.setObject(new FSTestBean("SucceedTest"));

      uri = storage.save(storageObject);
    }

    // Load the same version and modify the first one.
    StorageObject<FSTestBean> storageObject1 = storage.load(uri, FSTestBean.class);

    storageObject1.getObject().setTitle("SucceedTest-modified");

    storageObject1.onSucceed(e -> {
      assertEquals("SucceedTest", e.getOldVersion().getTitle());
      assertEquals("SucceedTest-modified", e.getNewVersion().getTitle());
    });

    storage.save(storageObject1);

  }

  @Test
  void attachedMapTest() throws Exception {
    Storage storage = storageApi.get(StorageTestConfig.TESTSCHEME);

    URI uri;
    {
      StorageObject<FSTestBean> storageObject = storage.instanceOf(FSTestBean.class);

      storageObject.setObject(new FSTestBean("SucceedTest"));

      uri = storage.save(storageObject);
    }

    List<Object> collect = attachAndLoadMap(storage, uri);

    // Read the list directly
    List<FSTestBean> readAttachedMap = storage.readAttachedMap(uri, MY_MAP, FSTestBean.class);

    assertEquals(1, collect.size());

    assertEquals(1, readAttachedMap.size());

    assertEquals(uri, ((FSTestBean) collect.get(0)).getUri());

    assertEquals(uri, readAttachedMap.get(0).getUri());

    StorageObject<StorageSettings> settings = storage.settings();

    attachAndLoadMap(storage, settings.getUri());

  }

  @Test
  void uriVersionOptionTest() {
    Storage storage = storageApi.get(StorageTestConfig.TESTSCHEME);

    StorageObject<FSTestBean> storageObject = storage.instanceOf(FSTestBean.class);

    storageObject.setObject(new FSTestBean("SucceedTest"));

    URI simpleUri = storage.save(storageObject);
    URI version0Uri = URI.create(simpleUri.toString() + "#0");

    StorageObject<FSTestBean> beanSo = storage.load(simpleUri, FSTestBean.class);
    assertEquals(simpleUri, beanSo.getObject().getUri());
    assertEquals(version0Uri, beanSo.getVersionUri());

    StorageObject<FSTestBean> beanSo2 =
        storage.load(simpleUri, FSTestBean.class, StorageLoadOption.uriWithVersion(true));
    assertEquals(version0Uri, beanSo2.getObject().getUri());
    assertEquals(version0Uri, beanSo2.getVersionUri());

    StorageObject<FSTestBean> beanSo3 =
        storage.load(version0Uri, FSTestBean.class, StorageLoadOption.uriWithVersion(true));
    assertEquals(version0Uri, beanSo3.getObject().getUri());
    assertEquals(version0Uri, beanSo3.getVersionUri());

    StorageObject<FSTestBean> beanSo4 =
        storage.load(version0Uri, FSTestBean.class, StorageLoadOption.uriWithVersion(false));
    assertEquals(simpleUri, beanSo4.getObject().getUri());
    assertEquals(version0Uri, beanSo4.getVersionUri());

    StorageObject<FSTestBean> beanSo5 = storage.load(version0Uri, FSTestBean.class);
    assertEquals(version0Uri, beanSo5.getObject().getUri());
    assertEquals(version0Uri, beanSo5.getVersionUri());


    beanSo.getObject().setTitle("SucceedTest_MODIFIED");
    URI modifiedUri = storage.save(storageObject);
    assertEquals(simpleUri, modifiedUri);
    URI version1Uri = URI.create(simpleUri.toString() + "#1");

    StorageObject<FSTestBean> beanSo6 = storage.load(simpleUri, FSTestBean.class);
    assertEquals(simpleUri, beanSo6.getObject().getUri());
    assertEquals(version1Uri, beanSo6.getVersionUri());

    StorageObject<FSTestBean> beanSo7 = storage.load(version1Uri, FSTestBean.class);
    assertEquals(version1Uri, beanSo7.getObject().getUri());
    assertEquals(version1Uri, beanSo7.getVersionUri());

    StorageObject<FSTestBean> beanSo8 =
        storage.load(version1Uri, FSTestBean.class, StorageLoadOption.uriWithVersion(true));
    assertEquals(version1Uri, beanSo8.getObject().getUri());
    assertEquals(version1Uri, beanSo8.getVersionUri());

    StorageObject<FSTestBean> beanSo9 =
        storage.load(version1Uri, FSTestBean.class, StorageLoadOption.uriWithVersion(false));
    assertEquals(simpleUri, beanSo9.getObject().getUri());
    assertEquals(version1Uri, beanSo9.getVersionUri());

  }

  private List<Object> attachAndLoadMap(Storage storage, URI uri) {
    ObjectMap attachedMap = storage.getAttachedMap(uri, MY_MAP);

    Assertions.assertNotNull(attachedMap);

    String keyA = "A";
    storage.updateAttachedMap(uri,
        new ObjectMapRequest().mapName(MY_MAP).putUrisToAddItem(keyA, uri));

    attachedMap = storage.getAttachedMap(uri, MY_MAP);

    assertEquals(uri, attachedMap.getUris().get(keyA));

    List<Object> collect = attachedMap.getUris().values().stream().map(u -> storage.read(u))
        .collect(Collectors.toList());

    return collect;
  }

  private void saveAndCheckLoad(
      Storage storage,
      String testText) throws Exception {

    StorageObject<FSTestBean> storageObject = storage.instanceOf(FSTestBean.class);

    storageObject.setObject(new FSTestBean(testText));

    URI uri = storage.save(storageObject);

    StorageObject<FSTestBean> optLoaded = storage.load(uri, FSTestBean.class);
    assertEquals(testText, optLoaded.getObject().getTitle());
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
