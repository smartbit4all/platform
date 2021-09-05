package org.smartbit4all.storage.fs;

import static org.smartbit4all.core.io.TestFileUtil.testFsRootFolder;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.storage.bean.ObjectReference;
import org.smartbit4all.core.io.TestFileUtil;
import org.smartbit4all.domain.annotation.property.Entity;
import org.smartbit4all.domain.annotation.property.Id;
import org.smartbit4all.domain.annotation.property.OwnProperty;
import org.smartbit4all.domain.annotation.property.Table;
import org.smartbit4all.domain.data.storage.ObjectReferenceRequest;
import org.smartbit4all.domain.data.storage.ObjectStorage;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.index.StorageIndex;
import org.smartbit4all.domain.data.storage.index.StorageIndexField;
import org.smartbit4all.domain.data.storage.index.StorageIndexer;
import org.smartbit4all.domain.meta.EntityConfiguration;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.MetaConfiguration;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.gson.GsonBinaryDataObjectSerializer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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

  @BeforeEach
  void init() throws IOException {
    TestFileUtil.initTestDirectory();
  }

  @Test
  void saveLoadDeleteTest() throws Exception {
    URI uri = URI.create("teststoragefs:/testfolder/testfile.fs");

    StorageFS<String> storageFS = new StorageFS<>(
        TestFileUtil.testFsRootFolder(),
        (text) -> URI.create(text),
        new GsonBinaryDataObjectSerializer(),
        String.class);

    saveAndCheckLoad(uri, storageFS, "test string1");
    saveAndCheckLoad(uri, storageFS, "test string2");

    storageFS.delete(uri);

    Optional<String> loaded = storageFS.load(uri);
    assertFalse(loaded.isPresent());
  }

  private void saveAndCheckLoad(
      URI uri,
      ObjectStorage<String> storage,
      String testText) throws Exception {

    storage.save(testText, uri);
    Optional<String> loaded = storage.load(uri);
    assertEquals(testText, loaded.get());
  }

  @Test
  void storageFsTest() throws Exception {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.register(MetaConfiguration.class);
    ctx.register(TestEntityDefConfig.class);
    ctx.refresh();

    TestSearchDef testSearchDef = ctx.getBean(TestSearchDef.class);

    String activeList = "active";
    String closedList = "closed";
    String undefinedList = "undefined";
    String notInAnyState = "notinanystate";

    TestData tdActive1 = new TestData();
    tdActive1.setUri(URI.create("teststoragefs:/td1/" + activeList + "." + OBJECT_FILE_EXTENSION));
    tdActive1.setData(activeList);

    TestData tdActive2 = new TestData();
    tdActive2.setUri(URI.create("teststoragefs:/td1/" + activeList + "2." + OBJECT_FILE_EXTENSION));
    tdActive2.setData(activeList);

    TestData tdClose = new TestData();
    tdClose.setUri(URI.create("teststoragefs:/td2/" + closedList + "." + OBJECT_FILE_EXTENSION));
    tdClose.setData(closedList);

    TestData tdUndefined = new TestData();
    tdUndefined
        .setUri(URI.create("teststoragefs:/td2/" + undefinedList + "." + OBJECT_FILE_EXTENSION));
    tdUndefined.setData(undefinedList);

    Function<TestData, URI> uriProvider = (testData) -> testData.getUri();

    StorageFS<TestData> storageFS = new StorageFS<>(
        testFsRootFolder(),
        uriProvider,
        new GsonBinaryDataObjectSerializer(),
        TestData.class,
        OBJECT_FILE_EXTENSION);

    StorageIndexSimpleFS<TestData> indexApi = new StorageIndexSimpleFS<>(
        testFsRootFolder(),
        storageFS);

    StorageIndexField<TestData, String> stateIndex =
        createStateIndexField(testSearchDef, activeList, closedList, indexApi);

    StorageIndexField<TestData, String> emptyStateIndex =
        createEmptyStateIndexField(testSearchDef, activeList, closedList, notInAnyState, indexApi);

    StorageIndexField<TestData, Boolean> isActiveIndex =
        createIsActiveIndexField(testSearchDef, activeList, indexApi);

    StorageIndex<TestData> storageIndex = new StorageIndex<>(
        testSearchDef,
        testSearchDef.key(),
        indexApi,
        Arrays.asList(isActiveIndex, stateIndex, emptyStateIndex),
        uriProvider);

    // Storage.of(TestData.class).save(tdActive1);
    // List<TestData> datas = Storage.of(TestData.class).search(searchDef).listDatas(expression);

    // Storage storage;

    // List<TestData> datas = storage.of(TestData.class).search(searchDef).listDatas(expression);

    // List<TestData> list = Crud.read(testSearchDef).where(null).list(TestData.class);

    // List<TestData> datas = objectStorage.search(searchDef).listDatas(expression);

    Storage<TestData> objectStorage = new Storage<>(TestData.class,
        storageFS,
        Arrays.asList(storageIndex));

    objectStorage.save(tdActive1);
    assertEquals(1, objectStorage.loadAll().size());

    objectStorage.save(tdClose);
    assertEquals(2, objectStorage.loadAll().size());

    objectStorage.save(tdUndefined);
    assertEquals(3, objectStorage.loadAll().size());

    Expression activeExpression = testSearchDef.state().eq(activeList);
    Expression closedExpression = testSearchDef.state().eq(closedList);
    Expression emptyStateExpression = testSearchDef.emptyState().eq(notInAnyState);
    Expression notActiveExpression = testSearchDef.isActive().eq(false);
    Expression oneMatchOtherEmptyExpression =
        testSearchDef.state().eq("cica").AND(testSearchDef.state().eq(activeList));
    Expression oneMatchOtherEmptyReversedExpression =
        testSearchDef.state().eq(activeList).AND(testSearchDef.state().eq("cica"));

    List<TestData> activeDatas = objectStorage.listDatas(activeExpression);
    assertEquals(1, activeDatas.size());

    List<TestData> closedDatas = objectStorage.listDatas(closedExpression);
    assertEquals(1, closedDatas.size());

    List<TestData> undefinedDatas = objectStorage.listDatas(emptyStateExpression);
    assertEquals(1, undefinedDatas.size());

    List<TestData> notActive = objectStorage.listDatas(notActiveExpression);
    assertEquals(2, notActive.size());

    objectStorage.save(tdActive2, uriProvider.apply(tdActive2));

    List<TestData> twoActiveDatas = objectStorage.listDatas(activeExpression);
    assertEquals(2, twoActiveDatas.size());

    assertEquals(1, objectStorage.listDatas(closedExpression).size());

    List<TestData> oneMatchOtherEmpty = objectStorage.listDatas(oneMatchOtherEmptyExpression);
    assertEquals(0, oneMatchOtherEmpty.size());

    List<TestData> oneMatchOtherEmptyReversed =
        objectStorage.listDatas(oneMatchOtherEmptyReversedExpression);
    assertEquals(0, oneMatchOtherEmptyReversed.size());

    List<TestData> twoMatchAndOneMatch = objectStorage
        .listDatas(testSearchDef.isActive().eq(false).AND(testSearchDef.state().eq(closedList)));
    assertEquals(1, twoMatchAndOneMatch.size());

    StorageReindexerFS reindexer =
        new StorageReindexerFS("teststoragefs", testFsRootFolder(), "fs");
    assertEquals(4, reindexer.listAllUris().size());

    ctx.close();
  }

  @Test
  void storageFsTestWithConstructedUris() throws Exception {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.register(MetaConfiguration.class);
    ctx.register(TestEntityDefConfig.class);
    ctx.refresh();

    Function<TestData, URI> uriProvider = (testData) -> testData.getUri();

    StorageFS<TestData> storageFS = new StorageFS<>(
        testFsRootFolder(),
        uriProvider,
        new GsonBinaryDataObjectSerializer(),
        TestData.class,
        OBJECT_FILE_EXTENSION);

    storageFS
        .setUriProvider(new ObjectUriProviderFSDefault<>("teststoragefs:/", OBJECT_FILE_EXTENSION));

    TestSearchDef testSearchDef = ctx.getBean(TestSearchDef.class);

    String activeList = "active";
    String closedList = "closed";
    String undefinedList = "undefined";
    String notInAnyState = "notinanystate";

    TestData tdActive1 = new TestData();
    tdActive1.setUri(storageFS.getUriProvider().constructUri(tdActive1));
    tdActive1.setData(activeList);

    URI uri = storageFS.save(tdActive1);

    Optional<TestData> loaded1 = storageFS.load(uri);

    Assertions.assertTrue(loaded1.isPresent());

    assertEquals(tdActive1.data, loaded1.get().data);
    assertEquals(tdActive1.uri, loaded1.get().uri);

    String myref = "myref";
    storageFS.saveReferences(new ObjectReferenceRequest(uri).create(myref));

    Set<ObjectReference> references = storageFS.loadReferences(uri);

    Assertions.assertTrue(references.stream().anyMatch(r -> r.getReference().equals(myref)));

    ctx.close();
  }

  private StorageIndexField<TestData, Boolean> createIsActiveIndexField(
      TestSearchDef testSearchDef,
      String activeList,
      StorageIndexer<TestData> indexApi) {

    Function<TestData, Optional<Boolean>> isActiveCalculator = (testData) -> {

      if (testData.getData().contains(activeList)) {
        return Optional.of(Boolean.TRUE);
      } else {
        return Optional.of(Boolean.FALSE);
      }

    };

    StorageIndexField<TestData, Boolean> isActiveIndex = new StorageIndexField<>(
        testSearchDef,
        testSearchDef.key(),
        testSearchDef.isActive(),
        isActiveCalculator,
        indexApi);

    return isActiveIndex;
  }

  private StorageIndexField<TestData, String> createEmptyStateIndexField(
      TestSearchDef testSearchDef,
      String activeList, String closedList, String notInAnyState,
      StorageIndexer<TestData> indexApi) {

    Function<TestData, Optional<String>> emptyStateCalculator = (testData) -> {

      if (!testData.getData().contains(activeList) && !testData.getData().contains(closedList)) {
        return Optional.of(notInAnyState);
      } else {
        return Optional.empty();
      }

    };

    StorageIndexField<TestData, String> emptyStateIndex = new StorageIndexField<>(
        testSearchDef,
        testSearchDef.key(),
        testSearchDef.emptyState(),
        emptyStateCalculator,
        indexApi);

    return emptyStateIndex;
  }

  private StorageIndexField<TestData, String> createStateIndexField(
      TestSearchDef testSearchDef,
      String activeList, String closedList,
      StorageIndexer<TestData> indexApi) {

    Function<TestData, Optional<String>> stateCalculator = (testData) -> {

      if (testData.getData().contains(activeList)) {
        return Optional.of(activeList);
      } else if (testData.getData().contains(closedList)) {
        return Optional.of(closedList);
      } else {
        return Optional.empty();
      }

    };

    StorageIndexField<TestData, String> stateIndex = new StorageIndexField<>(
        testSearchDef,
        testSearchDef.key(),
        testSearchDef.state(),
        stateCalculator,
        indexApi);

    return stateIndex;
  }

  @Entity("test")
  @Table("test")
  public interface TestSearchDef extends EntityDefinition {

    @Id
    @OwnProperty(name = "key", columnName = "key")
    Property<URI> key();

    @OwnProperty(name = "state", columnName = "state")
    Property<String> state();

    @OwnProperty(name = "emptyState", columnName = "emptyState")
    Property<String> emptyState();

    @OwnProperty(name = "isActive", columnName = "isActive")
    Property<Boolean> isActive();

  }

  public static class TestEntityDefConfig extends EntityConfiguration {

    @Bean("test")
    public TestSearchDef userAccountDef() {
      TestSearchDef userAccDef = createEntityProxy(TestSearchDef.class);
      return userAccDef;
    }

  }

}
