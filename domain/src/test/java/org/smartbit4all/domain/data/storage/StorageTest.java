package org.smartbit4all.domain.data.storage;

import static org.junit.jupiter.api.Assertions.*;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class StorageTest {

  @Test
  void intersectUriListsTest() {
    List<URI> uris = Arrays.asList(
        URI.create("test://valami1"),
        URI.create("test://valami2"));

    List<URI> toIntersect = Arrays.asList(
        URI.create("test://valami1"),
        URI.create("test://valami3"));

    uris = Storage.intersectUriLists(uris, toIntersect);

    assertEquals(1, uris.size());
    assertEquals(URI.create("test://valami1"), uris.get(0));
  }

}
