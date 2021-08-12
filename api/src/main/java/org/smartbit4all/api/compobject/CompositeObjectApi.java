package org.smartbit4all.api.compobject;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.smartbit4all.api.compobject.bean.ComposeableObject;
import org.smartbit4all.api.compobject.bean.CompositeObject;
import org.smartbit4all.domain.data.storage.Storage;

public class CompositeObjectApi implements ComposeableObjectApi {

  public static final URI API_URI = URI.create("compobj:/composeableobject/apiUri");
  
  private Storage<CompositeObject> storage;
  
  public CompositeObjectApi(Storage<CompositeObject> storage) {
    this.storage = storage;
  }
  
  @Override
  public List<ComposeableObject> getChildren(ComposeableObject composeableObject) throws Exception {
    URI compositeObjectUri = composeableObject.getObjectUri();
    
    Optional<CompositeObject> loaded = storage.load(compositeObjectUri);
    if(loaded.isPresent()) {
      CompositeObject compositeObject = loaded.get();
      
      return compositeObject.getObjects();
    }
    
    return Collections.emptyList();
  }
  
  @Override
  public URI getApiUri() {
    return API_URI;
  }

}
