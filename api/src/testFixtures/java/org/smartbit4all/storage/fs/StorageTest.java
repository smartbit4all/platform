package org.smartbit4all.storage.fs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataObject;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredList;
import org.smartbit4all.api.collection.StoredMap;
import org.smartbit4all.api.collection.bean.StoredCollectionDescriptor;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.AsyncInvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.object.AccessControlInternalApi;
import org.smartbit4all.api.object.bean.ObjectReferenceById;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.api.sample.bean.SampleInlineObject;
import org.smartbit4all.api.sample.bean.SampleTimeBasedData;
import org.smartbit4all.api.storage.bean.ObjectAspect;
import org.smartbit4all.api.storage.bean.ObjectMap;
import org.smartbit4all.api.storage.bean.ObjectMapRequest;
import org.smartbit4all.api.storage.bean.ObjectReference;
import org.smartbit4all.api.storage.bean.StorageArchiveProcessConfig;
import org.smartbit4all.api.storage.bean.StorageArchiveProcessConfig.ModeEnum;
import org.smartbit4all.api.storage.bean.StorageSettings;
import org.smartbit4all.api.view.bean.SmartLinkData;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.storage.ObjectModificationException;
import org.smartbit4all.domain.data.storage.ObjectNotFoundException;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageArchiveApi;
import org.smartbit4all.domain.data.storage.StorageLoadOption;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.smartbit4all.domain.data.storage.StorageObjectLock;
import org.smartbit4all.domain.data.storage.StorageObjectReferenceEntry;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.io.ByteStreams;

@TestInstance(Lifecycle.PER_CLASS)
@Disabled
public class StorageTest {

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
  protected StorageApi storageApi;

  @Autowired
  protected ObjectApi objectApi;

  @Autowired
  protected StorageTestApi testApi;

  @Autowired
  protected StorageArchiveApi archiveApi;

  @Autowired
  protected CollectionApi collectionApi;

  @Autowired
  private InvocationApi invocationApi;

  protected URI collectionsTestUri;

  @Test
  void setTest() throws Exception {
    Storage storage = storageApi.get(StorageTestConfig.TESTSCHEME);

    URI uri = storage.saveAsNew(new FSTestBean("SucceedTest"));

    Storage storageSingle = storageApi.get(StorageTestConfig.TESTSCHEMESINGLE);

    URI refUri = storageSingle.addToSet(MY_MAP, uri);

    List<FSTestBean> collect = storageSingle.readAllReferenceFromSet(MY_MAP, FSTestBean.class);

    assertEquals(1, collect.size());

    assertEquals(uri, collect.get(0).getUri());

    storageSingle.moveToSet(refUri, "archive");

    collect = storageSingle.readAllReferenceFromSet(MY_MAP, FSTestBean.class);

    assertEquals(0, collect.size());

  }

  @Test
  void writeOnceReadManyTimes() throws Exception {
    Storage storage = storageApi.get(StorageTestConfig.TESTSCHEME);

    URI uri1 = saveAndCheckLoad(storage, "test string1");
    URI uri2 = saveAndCheckLoad(storage, "test string2");

    for (int i = 0; i < 10; i++) {
      FSTestBean read1 = storage.read(uri1, FSTestBean.class);
      FSTestBean read2 = storage.read(uri2, FSTestBean.class);
      assertEquals("test string1", read1.getTitle());
      assertEquals("test string2", read2.getTitle());
    }

  }

  @Test
  void saveLoadDeleteTest() throws Exception {
    Storage storage = storageApi.get(StorageTestConfig.TESTSCHEME);

    saveAndCheckLoad(storage, "test string1");
    saveAndCheckLoad(storage, "test string2");

    testApi.saveAndLoad(storage, MY_MAP);

    // assertFalse(loaded.isPresent());
  }

  @Test
  void saveLoadDeleteTestForSingleVersion() throws Exception {
    Storage storage = storageApi.get(StorageTestConfig.TESTSCHEMESINGLE);

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

    StorageObject<AsyncInvocationRequest> invocationReqObj =
        storage.instanceOf(AsyncInvocationRequest.class);

    invocationReqObj.setObject(new AsyncInvocationRequest()
        .request(new InvocationRequest().interfaceClass(StorageApi.class.getName())
            .name("LOCAL").methodName("save")
            .addParametersItem(new InvocationParameter().name("param1"))));

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

    StorageObject<AsyncInvocationRequest> invocationReqObj =
        storage.instanceOf(AsyncInvocationRequest.class);

    invocationReqObj.setObject(new AsyncInvocationRequest()
        .request(new InvocationRequest().interfaceClass(StorageApi.class.getName())
            .name("LOCAL").methodName("save")
            .addParametersItem(new InvocationParameter().name("param1"))));

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
  @Disabled
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
        StorageObject<AsyncInvocationRequest> invocationReqObj =
            storage.instanceOf(AsyncInvocationRequest.class);

        invocationReqObj.setObject(new AsyncInvocationRequest()
            .request(new InvocationRequest().interfaceClass(StorageApi.class.getName())
                .name("LOCAL").methodName("save")
                .addParametersItem(new InvocationParameter().name("param1"))));

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
        StorageObject<AsyncInvocationRequest> invocationReqObj =
            storage.instanceOf(AsyncInvocationRequest.class);

        invocationReqObj.setObject(new AsyncInvocationRequest()
            .request(new InvocationRequest().interfaceClass(StorageApi.class.getName())
                .name("LOCAL").methodName("save")
                .addParametersItem(new InvocationParameter().name("param1"))));

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
      lock.unlock();
    }
  }

  @Test
  void lockParalelTest() throws Exception {
    Storage storage = storageApi.get(StorageTestConfig.TESTSCHEME);
    StorageObject<SampleCategory> so = storage.saveAsNewObject(new SampleCategory().name(MY_MAP));
    ExecutorService pool = Executors.newFixedThreadPool(5);
    List<Future<?>> futures = new ArrayList<>();
    AtomicInteger finalizedThreads = new AtomicInteger(0);
    for (int i = 0; i < 5; i++) {
      Future<?> submit = pool.submit(() -> {
        for (int j = 0; j < 100; j++) {
          StorageObjectLock lock = storage.getLock(so.getUri());
          lock.lock();
          try {
            Thread.sleep(2);
          } catch (InterruptedException e) {
            e.printStackTrace();
          } finally {
            lock.unlock();
          }
        }
        finalizedThreads.incrementAndGet();
      });
      futures.add(submit);
    }
    for (Future<?> future : futures) {
      future.get(10, TimeUnit.SECONDS);
    }
    assertEquals(futures.size(), finalizedThreads.get());
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
      assertEquals("SucceedTest", ((FSTestBean) e.getOldVersion()).getTitle());
      assertEquals("SucceedTest-modified", ((FSTestBean) e.getNewVersion()).getTitle());
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
    URI version0Uri = URI.create(simpleUri.toString() + ".v0");

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
    URI version1Uri = URI.create(simpleUri.toString() + ".v1");

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

  @Test
  void aspectsTest() throws Exception {
    Storage storage = storageApi.get(StorageTestConfig.TESTSCHEME);

    URI uri = storage.saveAsNew(new FSTestBean("SucceedTest"));

    StorageObject<?> storageObject = storage.load(uri);

    ObjectDefinition<SampleInlineObject> sampleTypeDefinition =
        objectApi.definition(SampleInlineObject.class);
    storageObject.getOrCreateAspects().put(AccessControlInternalApi.ACL_ASPECT,
        new ObjectAspect().typeQualifiedName(SampleInlineObject.class.getName())
            .objectAsMap(sampleTypeDefinition
                .toMap(new SampleInlineObject().name("apple"))));

    uri = storage.save(storageObject);

    storageObject = storage.load(uri);

    assertEquals(1, storageObject.getAspects().size());

    Assertions.assertNotNull(storageObject.getAspects());
    Assertions.assertNotNull(storageObject.getAspects().get(AccessControlInternalApi.ACL_ASPECT));

    storageObject.getAspects().values().stream()
        .map(a -> sampleTypeDefinition.fromMap(a.getObjectAsMap()))
        .forEach(s -> Assertions.assertEquals("apple", s.getName()));
  }

  @Test
  void removalTest() throws Exception {
    List<URI> toDelete = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      toDelete.add(objectApi.saveAsNew(StorageTestConfig.TESTSCHEME + "-removal",
          new FSTestBean("RemovalTest" + i)));
    }

    Storage storage = storageApi.get(StorageTestConfig.TESTSCHEME + "-removal");

    storage.remove(toDelete);

    Assertions.assertThrows(ObjectNotFoundException.class, () -> objectApi.loadBatch(toDelete));

  }

  @Test
  void removalTestById() throws Exception {
    List<URI> toDelete = new ArrayList<>();
    List<String> ids = new ArrayList<>();
    for (int i = 1000; i < 1010; i++) {
      URI uri = objectApi.saveAsNew(StorageTestConfig.TESTSCHEME,
          new SmartLinkData().uuid(UUID.randomUUID()));
      String id = Integer.toString(i);
      ids.add(id);
      toDelete.add(objectApi.saveAsNew(StorageTestConfig.TESTSCHEME + "-removalbyid",
          new ObjectReferenceById().id(id).uri(uri)));
    }

    Storage storage = storageApi.get(StorageTestConfig.TESTSCHEME + "-removalbyid");

    storage.remove(toDelete);

    Assertions.assertThrows(ObjectNotFoundException.class, () -> objectApi.loadBatch(toDelete));
    ObjectDefinition<ObjectReferenceById> definition =
        objectApi.definition(ObjectReferenceById.class);
    for (String id : ids) {
      Assertions.assertThrows(ObjectNotFoundException.class,
          () -> objectApi.load(StorageTestConfig.TESTSCHEME + "-removalbyid", definition, id));
    }

  }

  @Test
  void findOldestObjectsMinutes() throws Exception {
    List<URI> oldests = new ArrayList<>();
    OffsetDateTime now = OffsetDateTime.now();
    for (int i = 1; i <= 10; i++) {
      OffsetDateTime minusSeconds = now.minusMinutes(i);
      for (int j = 0; j < 5; j++) {
        URI uri = objectApi.saveAsNew(StorageTestConfig.TESTSCHEME + "-findMinutes",
            new SampleTimeBasedData().name("object-" + i + StringConstant.MINUS_SIGN + j)
                .timeOf(minusSeconds));
        if (i == 10) {
          oldests.add(objectApi.getLatestUri(uri));
        }
      }
    }

    Storage storage = storageApi.get(StorageTestConfig.TESTSCHEME + "-findMinutes");
    List<URI> currentOldests = storage.readOldests(null, SampleTimeBasedData.class.getName());
    org.assertj.core.api.Assertions.assertThat(currentOldests)
        .containsExactlyInAnyOrderElementsOf(oldests);
  }

  @Test
  void findOldestObjectsDays() throws Exception {
    List<URI> oldests = new ArrayList<>();
    OffsetDateTime now = OffsetDateTime.now();
    for (int i = 1; i <= 10; i++) {
      OffsetDateTime minusSeconds = now.minusDays(i);
      for (int j = 0; j < 5; j++) {
        URI uri = objectApi.saveAsNew(StorageTestConfig.TESTSCHEME + "-findDays",
            new SampleTimeBasedData().name("object-" + i + StringConstant.MINUS_SIGN + j)
                .timeOf(minusSeconds));
        if (i == 10) {
          oldests.add(objectApi.getLatestUri(uri));
        }
      }
    }

    Storage storage = storageApi.get(StorageTestConfig.TESTSCHEME + "-findDays");
    List<URI> currentOldests = storage.readOldests(null, SampleTimeBasedData.class.getName());
    org.assertj.core.api.Assertions.assertThat(currentOldests)
        .containsExactlyInAnyOrderElementsOf(oldests);
  }

  @Test
  void archiveOldestObjectsDays() throws Exception {
    List<URI> oldests = new ArrayList<>();
    OffsetDateTime now = OffsetDateTime.now();
    String storageScheme = StorageTestConfig.TESTSCHEME + "-archiveDays";
    for (int i = 1; i <= 10; i++) {
      OffsetDateTime minusSeconds = now.minusDays(i);
      for (int j = 0; j < 5; j++) {
        URI uri = objectApi.saveAsNew(storageScheme,
            new SampleTimeBasedData().name("object-" + i + StringConstant.MINUS_SIGN + j)
                .timeOf(minusSeconds));
        if (i == 10) {
          oldests.add(objectApi.getLatestUri(uri));
        }
      }
    }
    StorageArchiveProcessConfig config = new StorageArchiveProcessConfig().storage(storageScheme)
        .addTypeClassNamesItem(SampleTimeBasedData.class.getName())
        .beforeDurationInMillis(Long.valueOf(1000 * 60 * 60))
        .cronExpression("0 0 0 * * *");
    URI configUri = objectApi.saveAsNew(StorageArchiveApi.SCHEMA_ARCHIVAL, config);

    Storage storage = storageApi.get(storageScheme);
    {
      List<SampleTimeBasedData> all = storage.readAll(SampleTimeBasedData.class);
      org.assertj.core.api.Assertions.assertThat(all)
          .hasSize(50);
    }

    int executeArchive = archiveApi.executeArchive(configUri);
    Assertions.assertEquals(50, executeArchive);

    {
      List<SampleTimeBasedData> all = storage.readAll(SampleTimeBasedData.class);
      org.assertj.core.api.Assertions.assertThat(all)
          .isEmpty();
    }
  }

  @Test
  void archiveObjectsFromCollectionList() throws Exception {
    List<URI> uris = new ArrayList<>();
    OffsetDateTime now = OffsetDateTime.now();
    String storageScheme = StorageTestConfig.TESTSCHEME + "-archiveDays";
    for (int i = 1; i <= 10; i++) {
      OffsetDateTime minusSeconds = now.minusDays(i);
      for (int j = 0; j < 5; j++) {
        uris.add(objectApi.saveAsNew(storageScheme,
            new SampleTimeBasedData().name("object-" + i + StringConstant.MINUS_SIGN + j)
                .timeOf(minusSeconds)));
      }
    }
    StoredList testList = collectionApi.list(storageScheme, "testList");
    testList.addAll(uris);
    StoredCollectionDescriptor descriptor = testList.getDescriptor();
    descriptor.schema(storageScheme);
    StorageArchiveProcessConfig config = new StorageArchiveProcessConfig().storage(storageScheme)
        .addTypeClassNamesItem(SampleTimeBasedData.class.getName())
        .mode(ModeEnum.COLLECTION_BY_PREDICATE)
        .objectPredicate(invocationApi.builder(StorageTestApi.class)
            .build(api -> api.getRemovableItems(null, null)))
        .addCollectionsItem(descriptor)
        .cronExpression("0 0 0 * * *");
    URI configUri = objectApi.saveAsNew(StorageArchiveApi.SCHEMA_ARCHIVAL, config);

    int executeArchive = archiveApi.executeArchive(configUri);
    Assertions.assertEquals(50, executeArchive);
    StoredList list = collectionApi.list(storageScheme, "testList");
    assertEquals(0, list.uris().size());

    StorageArchiveProcessConfig newConfig = new StorageArchiveProcessConfig().storage(storageScheme)
        .addTypeClassNamesItem(SampleTimeBasedData.class.getName())
        .beforeDurationInMillis(Long.valueOf(1000 * 60 * 60))
        .cronExpression("0 0 0 * * *");
    URI newConfigUri = objectApi.saveAsNew(StorageArchiveApi.SCHEMA_ARCHIVAL, newConfig);
    archiveApi.executeArchive(newConfigUri);
  }


  @Test
  void archiveObjectsFromCollectionMap() throws Exception {
    Map<String, URI> uriMapByCounter = new HashMap<>();
    OffsetDateTime now = OffsetDateTime.now();
    String storageScheme = StorageTestConfig.TESTSCHEME + "-archiveDays";
    for (int i = 1; i <= 10; i++) {
      OffsetDateTime minusSeconds = now.minusDays(i);
      for (int j = 0; j < 5; j++) {
        uriMapByCounter.put("" + i + "" + j, objectApi.saveAsNew(storageScheme,
            new SampleTimeBasedData().name("object-" + i + StringConstant.MINUS_SIGN + j)
                .timeOf(minusSeconds)));
      }
    }
    StoredMap map = collectionApi.map(storageScheme, "testMap");
    map.putAll(uriMapByCounter);
    StoredCollectionDescriptor descriptor = map.getDescriptor();
    descriptor.schema(storageScheme);
    StorageArchiveProcessConfig config = new StorageArchiveProcessConfig().storage(storageScheme)
        .addTypeClassNamesItem(SampleTimeBasedData.class.getName())
        .mode(ModeEnum.COLLECTION_BY_PREDICATE)
        .objectPredicate(invocationApi.builder(StorageTestApi.class)
            .build(api -> api.getRemovableItems(null, null)))
        .addCollectionsItem(descriptor)
        .cronExpression("0 0 0 * * *");
    URI configUri = objectApi.saveAsNew(StorageArchiveApi.SCHEMA_ARCHIVAL, config);

    int executeArchive = archiveApi.executeArchive(configUri);
    Assertions.assertEquals(50, executeArchive);
    StoredMap updatedMap = collectionApi.map(storageScheme, "testMap");
    assertEquals(0, updatedMap.uris().size());

    StorageArchiveProcessConfig newConfig = new StorageArchiveProcessConfig().storage(storageScheme)
        .addTypeClassNamesItem(SampleTimeBasedData.class.getName())
        .beforeDurationInMillis(Long.valueOf(1000 * 60 * 60))
        .cronExpression("0 0 0 * * *");
    URI newConfigUri = objectApi.saveAsNew(StorageArchiveApi.SCHEMA_ARCHIVAL, newConfig);
    archiveApi.executeArchive(newConfigUri);
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

  private URI saveAndCheckLoad(
      Storage storage,
      String testText) throws Exception {

    StorageObject<FSTestBean> storageObject = storage.instanceOf(FSTestBean.class);

    storageObject.setObject(new FSTestBean(testText));

    URI uri = storage.save(storageObject);

    StorageObject<FSTestBean> optLoaded = storage.load(uri, FSTestBean.class);
    assertEquals(testText, optLoaded.getObject().getTitle());
    return uri;
  }

}

