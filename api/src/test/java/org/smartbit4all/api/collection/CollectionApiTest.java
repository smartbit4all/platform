package org.smartbit4all.api.collection;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.object.ApplyChangeApi;
import org.smartbit4all.api.object.ApplyChangeRequest;
import org.smartbit4all.api.object.ObjectChangeRequest;
import org.smartbit4all.api.sample.bean.SampleDataSheet;
import org.smartbit4all.core.io.TestFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {
    CollectionTestConfig.class
})
class CollectionApiTest {

  private static final String FIRST = "first";
  private static final String MY_MAP = "myMap";
  private static final String SCHEMA = "collection";

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  private ApplyChangeApi applyChangeApi;

  @BeforeAll
  static void clearDirectory() throws IOException {
    TestFileUtil.clearTestDirectory();
  }

  @Test
  void testMap() throws Exception {

    ApplyChangeRequest applyChangeRequest = applyChangeApi.request();
    ObjectChangeRequest ocrDatasheet1 =
        applyChangeRequest.createAsNew("sample", new SampleDataSheet().name("datasheet " + 1));
    URI datasheet1Uri =
        applyChangeApi.save(applyChangeRequest).getProcessedRequests().get(ocrDatasheet1);

    StoredMap map = collectionApi.map(SCHEMA, MY_MAP);

    Assertions.assertTrue(map.uris().isEmpty());

    map.put(FIRST, datasheet1Uri);

    Assertions.assertTrue(map.uris().size() == 1);

    Assertions.assertEquals(datasheet1Uri, map.uris().get(FIRST));

  }

  @Test
  void testObjectScopedMap() throws Exception {

    ApplyChangeRequest applyChangeRequest = applyChangeApi.request();
    ObjectChangeRequest ocrDatasheet =
        applyChangeRequest.createAsNew("sample", new SampleDataSheet().name("datasheet"));


    List<ObjectChangeRequest> requests = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      requests.add(
          applyChangeRequest.createAsNew("sample", new SampleDataSheet().name("datasheet " + i)));
    }

    Map<ObjectChangeRequest, URI> processedRequests =
        applyChangeApi.save(applyChangeRequest).getProcessedRequests();

    URI datasheetUri = processedRequests.get(ocrDatasheet);

    List<URI> resultUris =
        requests.stream().map(o -> processedRequests.get(o)).collect(Collectors.toList());

    StoredMap map = collectionApi.map(datasheetUri, SCHEMA, MY_MAP);

    Assertions.assertTrue(map.uris().isEmpty());

    map.put(resultUris.stream().map(u -> new StoredMapEntry(u.toString(), u)));

    Map<String, URI> uris = map.uris();

    Assertions.assertEquals(uris.size(), resultUris.size());

    Assertions.assertEquals(
        resultUris.stream().filter(u -> uris.containsKey(u.toString())).count(), resultUris.size());

  }

}
