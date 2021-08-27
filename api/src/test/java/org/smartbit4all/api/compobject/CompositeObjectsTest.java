package org.smartbit4all.api.compobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URI;
import org.junit.jupiter.api.Test;

public class CompositeObjectsTest {

  private static final URI COMPOSITE_OBJECT_TODO_COMPDEF_ASSOC_URI =
      URI.create(ComposeableObjectNavigation.SCHEME + ":/compositeobject/definition/assoc/testobject/definition");
  
  @Test
  void assocUriCreationTest() throws Exception {
    URI assocUri = CompositeObjects.createChildAssocUri(
        ComposeableObjectNavigationTest.COMPOSITE_OBJECT_COMPDEF_URI,
        ComposeableObjectNavigationTest.TODO_COMPDEF_URI);
    
    assertEquals(COMPOSITE_OBJECT_TODO_COMPDEF_ASSOC_URI, assocUri);
  }
  
}
