package org.smartbit4all.api.collection;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.sample.bean.SampleDataSheet;
import org.smartbit4all.core.io.TestFileUtil;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {
    CollectionTestConfig.class
})
class CollectionApiTest {

  private static final String FIRST = "first";
  private static final String MY_MAP = "myMap";
  private static final String MY_LIST = "myList";
  private static final String SCHEMA = "collection";

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  private ObjectApi objectApi;

  @BeforeAll
  static void clearDirectory() throws IOException {
    TestFileUtil.clearTestDirectory();
  }

  @Test
  void testMap() throws Exception {

    ObjectNode datasheet1 =
        objectApi.create("sample", new SampleDataSheet().name("datasheet " + 1));
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
        objectApi.create("sample", new SampleDataSheet().name("datasheet " + 1));
    URI datasheetUri = objectApi.save(datasheet1);

    List<URI> resultUris = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      ObjectNode datasheet =
          objectApi.create("sample", new SampleDataSheet().name("datasheet " + 1));
      resultUris.add(objectApi.save(datasheet));
    }

    StoredMap map = collectionApi.map(datasheetUri, SCHEMA, MY_MAP);

    Assertions.assertTrue(map.uris().isEmpty());

    map.put(resultUris.stream().map(u -> new StoredMapEntry(u.toString(), u)));

    Map<String, URI> uris = map.uris();

    Assertions.assertEquals(uris.size(), resultUris.size());

    Assertions.assertEquals(
        resultUris.stream().filter(u -> uris.containsKey(u.toString())).count(), resultUris.size());

  }

  @Test
  void testList() throws Exception {

    ObjectNode datasheet1 =
        objectApi.create("sample", new SampleDataSheet().name("datasheet " + 1));
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
        objectApi.create("sample", new SampleDataSheet().name("datasheet " + 1));
    URI datasheetUri = objectApi.save(datasheet1);

    List<URI> resultUris = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      ObjectNode datasheet =
          objectApi.create("sample", new SampleDataSheet().name("datasheet " + 1));
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

}
