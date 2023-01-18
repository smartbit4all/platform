package org.smartbit4all.api.collection;

import static java.util.stream.Collectors.toList;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.sample.bean.SampleDataSheet;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.TableDatas;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.utility.crud.Crud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {
    CollectionTestConfig.class
})
class CollectionApiTest {

  private static final String LATE = "late";
  public static final String SCHEMA = "sample";
  public static final String FIRST = "first";
  public static final String MY_MAP = "myMap";
  public static final String MY_LIST = "myList";
  public static final String MY_REF = "myRef";
  public static final String MY_SEARCH = "mySearch";

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  private ObjectApi objectApi;

  private ExecutorService executor =
      new ThreadPoolExecutor(5, 5, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

  @Test
  void testMap() throws Exception {

    ObjectNode datasheet1 =
        objectApi.create(SCHEMA, new SampleDataSheet().name("datasheet " + 1));
    URI datasheet1Uri = objectApi.save(datasheet1);

    StoredMap map = collectionApi.map(SCHEMA, MY_MAP);

    Assertions.assertTrue(map.uris().isEmpty());

    map.put(FIRST, datasheet1Uri);

    Assertions.assertTrue(map.uris().size() == 1);

    Assertions.assertEquals(datasheet1Uri, map.uris().get(FIRST));

  }

  @Test
  void testObjectScopedMap() throws Exception {

    ObjectNode datasheet1 =
        objectApi.create(SCHEMA, new SampleDataSheet().name("datasheet " + 1));
    URI datasheetUri = objectApi.save(datasheet1);

    List<URI> resultUris = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      ObjectNode datasheet =
          objectApi.create(SCHEMA, new SampleDataSheet().name("datasheet " + 1));
      resultUris.add(objectApi.save(datasheet));
    }

    StoredMap map = collectionApi.map(datasheetUri, SCHEMA, MY_MAP);

    Assertions.assertTrue(map.uris().isEmpty());

    map.put(resultUris.stream().map(u -> new StoredMapEntry(u.toString(), u)));

    Map<String, URI> uris = map.uris();

    Assertions.assertEquals(uris.size(), resultUris.size());

    Assertions.assertEquals(
        resultUris.stream().filter(u -> uris.containsKey(u.toString())).count(), resultUris.size());

    Map<String, URI> updatedUris = map.update(uriMap -> {
      ObjectNode datasheet =
          objectApi.create(SCHEMA, new SampleDataSheet().name("datasheet lately added"));
      objectApi.save(datasheet);
      uriMap.put(LATE, objectApi.save(datasheet));
      return uriMap;
    });

    assertTrue(updatedUris.containsKey(LATE));

  }

  @Test
  void testList() throws Exception {

    ObjectNode datasheet1 =
        objectApi.create(SCHEMA, new SampleDataSheet().name("datasheet " + 1));
    URI datasheet1Uri = objectApi.save(datasheet1);

    StoredList list = collectionApi.list(SCHEMA, MY_LIST);

    Assertions.assertTrue(list.uris().isEmpty());

    list.add(datasheet1Uri);

    Assertions.assertEquals(list.uris().size(), 1);

    Assertions.assertEquals(datasheet1Uri, list.uris().get(0));

  }

  @Test
  void testObjectScopedList() throws Exception {

    ObjectNode datasheet1 =
        objectApi.create(SCHEMA, new SampleDataSheet().name("datasheet " + 1));
    URI datasheetUri = objectApi.save(datasheet1);

    List<URI> resultUris = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      ObjectNode datasheet =
          objectApi.create(SCHEMA, new SampleDataSheet().name("datasheet " + 1));
      resultUris.add(objectApi.save(datasheet));
    }

    StoredList list = collectionApi.list(datasheetUri, SCHEMA, MY_LIST);

    Assertions.assertTrue(list.uris().isEmpty());

    list.addAll(resultUris);

    List<URI> uris = list.uris();

    Assertions.assertEquals(uris.size(), resultUris.size());

    Assertions.assertEquals(
        resultUris.stream().filter(u -> uris.contains(u)).count(), resultUris.size());

  }

  @Test
  void testReference() throws Exception {

    SampleDataSheet sampleDataSheet = new SampleDataSheet().name("datasheet " + 1);

    StoredReference<SampleDataSheet> ref =
        collectionApi.reference(SCHEMA, MY_REF, SampleDataSheet.class);

    ref.set(sampleDataSheet);

    SampleDataSheet readDataSheet = ref.get();

    Assertions.assertEquals(sampleDataSheet.toString(), readDataSheet.toString());

  }

  @Test
  void testObjectScopedReference() throws Exception {

    ObjectNode datasheet1 =
        objectApi.create(SCHEMA, new SampleDataSheet().name("datasheet " + 1));
    URI datasheetUri = objectApi.save(datasheet1);

    SampleDataSheet sampleDataSheet = new SampleDataSheet().name("datasheet " + 1);

    StoredReference<SampleDataSheet> ref =
        collectionApi.reference(datasheetUri, SCHEMA, MY_REF, SampleDataSheet.class);

    ref.set(sampleDataSheet);

    SampleDataSheet readDataSheet = ref.get();

    Assertions.assertEquals(sampleDataSheet.toString(), readDataSheet.toString());

  }

  @Test
  void testSearchIndex() throws Exception {

    SearchIndexWithFilterBean<SampleDataSheet, TestFilter> searchIndex =
        collectionApi.searchIndex(SCHEMA,
            MY_SEARCH, SampleDataSheet.class, TestFilter.class);

    // Creating many object to search.

    boolean odd = false;
    int count = 10;
    for (int i = 0; i < count; i++) {
      ObjectNode datasheet1 =
          objectApi.create(SCHEMA, new SampleDataSheet().name(odd ? "odd" : "even"));
      objectApi.save(datasheet1);
      odd = !odd;
    }

    @SuppressWarnings("unchecked")
    Property<String> propertyName =
        (Property<String>) searchIndex.getDefinition().definition.getProperty(TestFilter.NAME);

    long start = System.currentTimeMillis();

    Crud.read(searchIndex.getDefinition().definition).selectAllProperties()
        .where(propertyName.eq("odd"));

    TableData<?> tableData = searchIndex.executeSearch(
        Crud.read(searchIndex.getDefinition().definition).selectAllProperties()
            .where(propertyName.eq("odd"))
            .getQuery());
    long end = System.currentTimeMillis();

    System.out.println("Search time: " + (end - start));

    Assertions.assertEquals(count / 2, tableData.size());

    TableData<?> tableDataByDerived =
        searchIndex.executeSearch(new TestFilter().isOdd(true).name("od").caption("odd.odd"));

    assertEquals(TableDatas.toStringAdv(tableData), TableDatas.toStringAdv(tableDataByDerived));

    List<TestFilter> resultList = tableDataByDerived.asList(TestFilter.class);

    System.out.println(resultList);

    TableData<?> tableDataByDerivedNoResult =
        searchIndex.executeSearch(new TestFilter().isOdd(true).name("od").caption("even.even"));

    assertTrue(tableDataByDerivedNoResult.rows().isEmpty());

  }

  @Test
  void testSequence() {
    StoredSequence sequence = collectionApi.sequence(SCHEMA, FIRST);

    List<Future<List<Long>>> results = new ArrayList<>();
    int count = 5;
    int nextCount = 15;

    for (int i = 0; i < count; i++) {
      results.add(executor.submit(() -> {
        List<Long> result = new ArrayList<>();
        for (int j = 0; j < nextCount; j++) {
          result.add(sequence.next());
        }
        return result;
      }));
    }

    List<Long> result = results.stream().flatMap(f -> {
      try {
        return f.get().stream();
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (ExecutionException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return Stream.empty();
    }).sorted(Long::compare).collect(toList());

    assertEquals(count * nextCount, result.size());

    List<Long> expectedResult = new ArrayList<>();
    for (int i = 1; i <= count * nextCount; i++) {
      expectedResult.add(Long.valueOf(i));
    }

    assertEquals(expectedResult.toString(), result.toString());

  }

}
