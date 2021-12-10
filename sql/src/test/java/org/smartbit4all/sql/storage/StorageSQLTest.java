package org.smartbit4all.sql.storage;

import java.net.URI;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(classes = {
    StorageSQLTestConfig.class,
})
@Sql({"/storage/storage_test_schema.sql"})
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class StorageSQLTest {

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

  // @Test
  // void saveLoadDeleteTest() throws Exception {
  // URI uri = URI.create("teststoragefs:/testfolder/testfile.fs");
  //
  // StorageSQL<String> storageSQL = new StorageSQL<>(
  // testSearchDef,
  // testSearchDef.key(),
  // testSearchDef.content(),
  // (text) -> URI.create(text),
  // new GsonBinaryDataObjectSerializer(),
  // String.class);
  //
  // saveAndCheckLoad(uri, storageSQL, "test string1");
  // assertEquals(1, storageSQL.loadAll().size());
  //
  // saveAndCheckLoad(uri, storageSQL, "test string2");
  // assertEquals(1, storageSQL.loadAll().size());
  //
  // storageSQL.delete(uri);
  // assertEquals(0, storageSQL.loadAll().size());
  //
  // Optional<String> loaded = storageSQL.load(uri);
  // assertFalse(loaded.isPresent());
  // }
  //
  // private void saveAndCheckLoad(
  // URI uri,
  // ObjectStorage<String> storage,
  // String testText) throws Exception {
  //
  // storage.save(testText, uri);
  // Optional<String> loaded = storage.load(uri);
  // assertEquals(testText, loaded.get());
  // }
  //
  // @Test
  // void storageSqlTest() throws Exception {
  // String activeList = "active";
  // String closedList = "closed";
  // String undefinedList = "undefined";
  // String notInAnyState = "notinanystate";
  //
  // TestData tdActive1 = new TestData();
  // tdActive1.setUri(URI.create("teststoragefs:/td1/" + activeList + ".fs"));
  // tdActive1.setData(activeList);
  //
  // TestData tdActive2 = new TestData();
  // tdActive2.setUri(URI.create("teststoragefs:/td1/" + activeList + "2.fs"));
  // tdActive2.setData(activeList);
  //
  // TestData tdClose = new TestData();
  // tdClose.setUri(URI.create("teststoragefs:/td2/" + closedList + ".fs"));
  // tdClose.setData(closedList);
  //
  // TestData tdUndefined = new TestData();
  // tdUndefined.setUri(URI.create("teststoragefs:/td2/" + undefinedList + ".fs"));
  // tdUndefined.setData(undefinedList);
  //
  // Function<TestData, URI> uriProvider = (testData) -> testData.getUri();
  //
  // StorageSQL<TestData> storageSql = new StorageSQL<StorageSQLTest.TestData>(
  // testSearchDef,
  // testSearchDef.key(),
  // testSearchDef.content(),
  // uriProvider,
  // new GsonBinaryDataObjectSerializer(),
  // TestData.class);
  //
  // StorageIndexSQL<TestData> indexApi = new StorageIndexSQL<>();
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
  // storageSql,
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
  // StorageReindexerSQL reindexer = new StorageReindexerSQL(testSearchDef, TestSearchDef.KEY);
  // assertEquals(4, reindexer.listAllUris().size());
  // }

}
