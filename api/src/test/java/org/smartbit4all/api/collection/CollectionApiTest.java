package org.smartbit4all.api.collection;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import org.smartbit4all.api.databasedefinition.bean.DatabaseKind;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionData;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOperandData;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOperation;
import org.smartbit4all.api.rdbms.DatabaseDefinitionApi;
import org.smartbit4all.api.rdbms.DatabaseRendition;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.api.sample.bean.SampleContainerItem;
import org.smartbit4all.api.sample.bean.SampleDataSheet;
import org.smartbit4all.api.sample.bean.SampleInlineObject;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectNodeList;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.TableDatas;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.utility.crud.Crud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static java.util.stream.Collectors.toList;

@SpringBootTest(classes = {
    CollectionTestConfig.class
})
class CollectionApiTest {

  private static final String LATE = "late";
  public static final String SCHEMA = "sample";
  public static final String FIRST = "first";
  public static final String MY_MAP = "myMap";
  public static final String MY_LIST = "myList";
  public static final String MY_LIST_RECENT = "myListRecent";
  public static final String MY_REF = "myRef";
  public static final String MY_SEARCH = "mySearch";
  public static final String MY_SEARCHDETAILVALUES = "mySearchDetailValues";
  public static final String SAMPLE_CATEGORY = "sampleCategory";

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private DatabaseDefinitionApi databaseDefinitionApi;

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
  void testListAsRecentItems() throws Exception {

    List<URI> uris = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      ObjectNode datasheet1 =
          objectApi.create(SCHEMA, new SampleDataSheet().name("datasheet " + 1));
      uris.add(objectApi.save(datasheet1));
    }

    StoredList list = collectionApi.list(SCHEMA, MY_LIST_RECENT);

    list.addOrMoveFirst(uris.get(0), 3, false);
    list.addOrMoveFirst(uris.get(1), 3, false);
    list.addOrMoveFirst(uris.get(2), 3, false);
    list.addOrMoveFirst(uris.get(3), 3, false);

    {
      List<URI> result = list.uris();
      assertEquals(3, result.size());
      assertEquals(uris.get(3), result.get(0));
      assertEquals(uris.get(2), result.get(1));
      assertEquals(uris.get(1), result.get(2));
    }

    list.addOrMoveFirst(uris.get(0), 3, true);
    list.addOrMoveFirst(uris.get(1), 3, true);
    list.addOrMoveFirst(uris.get(2), 3, true);
    list.addOrMoveFirst(uris.get(3), 3, true);

    {
      List<URI> result = list.uris();
      assertEquals(3, result.size());
      assertEquals(objectApi.getLatestUri(uris.get(3)), result.get(0));
      assertEquals(objectApi.getLatestUri(uris.get(2)), result.get(1));
      assertEquals(objectApi.getLatestUri(uris.get(1)), result.get(2));
    }
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

    ObjectDefinition<SampleInlineObject> definition =
        objectApi.definition(SampleInlineObject.class);

    System.out.println(definition.getQualifiedName());

    SearchIndexWithFilterBean<SampleDataSheet, TestFilter> searchIndex =
        collectionApi.searchIndex(SCHEMA,
            MY_SEARCH, SampleDataSheet.class, TestFilter.class);

    DatabaseRendition databaseRendition =
        databaseDefinitionApi
            .render(databaseDefinitionApi
                .definitionOf(Stream.of(searchIndex.getDefinition().definition).collect(toList()))
                .databaseKind(DatabaseKind.ORACLE));
    System.out.println(databaseRendition.getScript());

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

    TableData<?> tableDataByFilterExpression =
        searchIndex.executeSearch(new FilterExpressionList().addExpressionsItem(
            new FilterExpressionData().currentOperation(FilterExpressionOperation.EQUAL)
                .operand1(new FilterExpressionOperandData().isDataName(true)
                    .valueAsString(TestFilter.NAME))
                .operand2(
                    new FilterExpressionOperandData().isDataName(false).valueAsString("odd"))));

    assertEquals(TableDatas.toStringAdv(tableData),
        TableDatas.toStringAdv(tableDataByFilterExpression));

    TableData<?> tableDataByDerived =
        searchIndex.executeSearch(new TestFilter().isOdd(true).name("od").caption("odd.odd"));

    assertEquals(TableDatas.toStringAdv(tableData), TableDatas.toStringAdv(tableDataByDerived));
    assertEquals(TableDatas.contentOf(tableData).toString(),
        TableDatas.contentOf(tableDataByDerived).toString());

    TableData<?> tableDataByDerivedNoResult =
        searchIndex.executeSearch(new TestFilter().isOdd(true).name("od").caption("even.even"));

    FilterExpressionFieldList allFilterFields = searchIndex.allFilterFields();

    System.out.println(allFilterFields);

    assertTrue(tableDataByDerivedNoResult.rows().isEmpty());

  }

  @Test
  void testSearchIndexExists() throws Exception {

    ObjectDefinition<SampleInlineObject> definition =
        objectApi.definition(SampleInlineObject.class);

    System.out.println(definition.getQualifiedName());

    int count = 25;
    Map<String, URI> urisByCategoryName = new HashMap<>();
    for (int i = 0; i < count; i++) {
      String name = "category " + i;
      ObjectNode node =
          objectApi.create(SCHEMA, new SampleCategory().name(name)
              .keyWords(Arrays.asList("key " + i, "key " + (i + 1))));
      ObjectNodeList list = node.list(SampleCategory.CONTAINER_ITEMS);
      for (int j = i; j < i + 3; j++) {
        list
            .addNewObject(new SampleContainerItem().name("sub " + j))
            .setValue(new SampleInlineObject().name("inline " + j),
                SampleContainerItem.INLINE_OBJECT)
            .ref(SampleContainerItem.DATASHEET)
            .setNewObject(new SampleDataSheet().name("datasheet " + j));
      }
      urisByCategoryName.put(name, objectApi.save(node));
    }

    SearchIndex<SampleCategory> searchIndex =
        collectionApi.searchIndex(SCHEMA, CollectionApiTest.SAMPLE_CATEGORY, SampleCategory.class);
    {
      FilterExpressionList filterExpressions = new FilterExpressionList()
          .addExpressionsItem(
              new FilterExpressionData().currentOperation(FilterExpressionOperation.LIKE)
                  .operand1(new FilterExpressionOperandData().isDataName(true)
                      .valueAsString(SampleCategory.NAME))
                  .operand2(
                      new FilterExpressionOperandData().isDataName(false)
                          .valueAsString("category %2%")))
          .addExpressionsItem(
              new FilterExpressionData().currentOperation(FilterExpressionOperation.EXISTS)
                  .operand1(new FilterExpressionOperandData().isDataName(true)
                      .valueAsString(SampleCategory.CONTAINER_ITEMS))
                  .subExpression(
                      new FilterExpressionList().addExpressionsItem(
                          new FilterExpressionData()
                              .currentOperation(FilterExpressionOperation.LIKE)
                              .operand1(new FilterExpressionOperandData().isDataName(true)
                                  .valueAsString(SampleContainerItem.DATASHEET))
                              .operand2(new FilterExpressionOperandData().isDataName(false)
                                  .valueAsString("data%3")))));
      {
        TableData<?> tableData = searchIndex.executeSearch(filterExpressions);
        assertEquals(5, tableData.size());

        List<String> categoryNammeWithDetail =
            Arrays.asList("category 2", "category 12", "category 21", "category 22", "category 23");
        DataColumn<?> colName =
            tableData.getColumn(tableData.entity().getProperty(SampleCategory.NAME));
        List<?> nameValues = tableData.values(colName);
        assertTrue(categoryNammeWithDetail.containsAll(nameValues));
        assertTrue(nameValues.containsAll(categoryNammeWithDetail));
      }

      {
        List<String> categoryNammeWithDetail =
            Arrays.asList("category 2", "category 21", "category 22");
        TableData<?> tableData = searchIndex.executeSearchOn(urisByCategoryName.entrySet().stream()
            .filter(e -> categoryNammeWithDetail.contains(e.getKey())).map(e -> e.getValue()),
            filterExpressions);
        assertEquals(3, tableData.size());

        DataColumn<?> colName =
            tableData.getColumn(tableData.entity().getProperty(SampleCategory.NAME));
        List<?> nameValues = tableData.values(colName);
        assertTrue(categoryNammeWithDetail.containsAll(nameValues));
        assertTrue(nameValues.containsAll(categoryNammeWithDetail));
      }
    }

    {
      SearchIndexWithFilterBean<SampleCategory, TestCategoryFilter> detailValueSearch =
          (SearchIndexWithFilterBean<SampleCategory, TestCategoryFilter>) collectionApi
              .searchIndex(SCHEMA, CollectionApiTest.MY_SEARCHDETAILVALUES, SampleCategory.class);
      TableData<?> tableData = detailValueSearch.executeSearch(
          new TestCategoryFilter().setName("category %2%")
              .setKeyWords(Arrays.asList("key 1", "key 2", "key 3", "key 13")));

      org.assertj.core.api.Assertions
          .assertThat((List<String>) tableData
              .values(tableData.getColumn(tableData.entity().getProperty(SampleCategory.NAME))))
          .containsExactlyInAnyOrderElementsOf(Arrays.asList("category 2", "category 12"));
    }

    {
      SearchIndexWithFilterBean<SampleCategory, TestCategoryFilter> detailValueSearch =
          (SearchIndexWithFilterBean<SampleCategory, TestCategoryFilter>) collectionApi
              .searchIndex(SCHEMA, CollectionApiTest.MY_SEARCHDETAILVALUES, SampleCategory.class);
      TableData<?> tableData = detailValueSearch.executeSearch(
          new TestCategoryFilter()
              .setKeyWords(Arrays.asList("%3%")));

      org.assertj.core.api.Assertions
          .assertThat((List<String>) tableData
              .values(tableData.getColumn(tableData.entity().getProperty(SampleCategory.NAME))))
          .containsExactlyInAnyOrderElementsOf(Arrays.asList("category 2", "category 3",
              "category 12", "category 13", "category 22", "category 23"));
    }

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
