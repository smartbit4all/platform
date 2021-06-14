package org.smartbit4all.sql.storage;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.smartbit4all.domain.data.storage.ObjectStorage;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.index.StorageIndex;
import org.smartbit4all.domain.data.storage.index.StorageIndexField;
import org.smartbit4all.domain.data.storage.index.StorageIndexer;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.gson.GsonBinaryDataObjectSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(classes = {
    StorageSQLTestConfig.class,
})
@Sql({"/storage/storage_test_schema.sql"})
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class StorageSQLTest {

  @Autowired
  private TestSearchDef testSearchDef;

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

  @Test
  void saveLoadDeleteTest() throws Exception {
    URI uri = URI.create("teststoragefs:/testfolder/testfile.fs");

    StorageSQL<String> storageFS = new StorageSQL<>(
        testSearchDef,
        testSearchDef.key(),
        testSearchDef.content(),
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
  void storageSqlTest() throws Exception {
    String activeList = "active";
    String closedList = "closed";
    String undefinedList = "undefined";
    String notInAnyState = "notinanystate";

    TestData tdActive1 = new TestData();
    tdActive1.setUri(URI.create("teststoragefs:/td1/" + activeList + ".fs"));
    tdActive1.setData(activeList);

    TestData tdActive2 = new TestData();
    tdActive2.setUri(URI.create("teststoragefs:/td1/" + activeList + "2.fs"));
    tdActive2.setData(activeList);

    TestData tdClose = new TestData();
    tdClose.setUri(URI.create("teststoragefs:/td2/" + closedList + ".fs"));
    tdClose.setData(closedList);

    TestData tdUndefined = new TestData();
    tdUndefined.setUri(URI.create("teststoragefs:/td2/" + undefinedList + ".fs"));
    tdUndefined.setData(undefinedList);

    Function<TestData, URI> uriProvider = (testData) -> testData.getUri();

    StorageSQL<TestData> storageSql = new StorageSQL<StorageSQLTest.TestData>(
        testSearchDef,
        testSearchDef.key(),
        testSearchDef.content(),
        uriProvider,
        new GsonBinaryDataObjectSerializer(),
        TestData.class);

    StorageIndexSQL indexApi = new StorageIndexSQL();

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
        storageSql,
        Arrays.asList(storageIndex));

    objectStorage.save(tdActive1);
    objectStorage.save(tdClose);
    objectStorage.save(tdUndefined);

    Expression activeExpression = testSearchDef.state().eq(activeList);
    Expression closedExpression = testSearchDef.state().eq(closedList);
    Expression emptyStateExpression = testSearchDef.emptyState().eq(notInAnyState);
    Expression notActiveExpression = testSearchDef.isActive().eq(false);

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

    StorageReindexerSQL reindexer = new StorageReindexerSQL(testSearchDef, TestSearchDef.KEY);
    assertEquals(4, reindexer.listAllUris().size());
  }

  private StorageIndexField<TestData, Boolean> createIsActiveIndexField(
      TestSearchDef testSearchDef,
      String activeList,
      StorageIndexer indexApi) {

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
      StorageIndexer indexApi) {

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
      StorageIndexer indexApi) {

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

}
