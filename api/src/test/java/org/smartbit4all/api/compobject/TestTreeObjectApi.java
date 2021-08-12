package org.smartbit4all.api.compobject;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.smartbit4all.api.compobject.bean.ComposeableObject;
import org.smartbit4all.domain.data.storage.Storage;

public class TestTreeObjectApi implements ComposeableObjectApi {

  public static final URI API_URI = URI.create("testtreeobject:/composeableobject/apiUri");

  private Storage<TestTreeObject> storage;
  
  public TestTreeObjectApi(Storage<TestTreeObject> storage) {
    this.storage = storage;
  }
  
  @Override
  public List<ComposeableObject> getChildren(ComposeableObject composeableObject) throws Exception {
    URI testTreeObjectUri = composeableObject.getObjectUri();
    
    Optional<TestTreeObject> loaded = storage.load(testTreeObjectUri);
    if(loaded.isPresent()) {
      TestTreeObject testTreeObject = loaded.get();
      
      List<URI> children = testTreeObject.getChildren();
      if(children.size() > 0) {     
        List<ComposeableObject> result = new ArrayList<>();
        
        for(URI childUri : children) {
          ComposeableObject newComposeableObject = new ComposeableObject();
          
          newComposeableObject.setObjectUri(childUri);
          
          // TODO not necessarily the same definition should be passed
          newComposeableObject.setDefinition(composeableObject.getDefinition());
          
          result.add(newComposeableObject);
        }
        
        return result;
      }
    }
    
    return Collections.emptyList();
  }

  @Override
  public URI getApiUri() {
    return API_URI;
  }

}
