package org.smartbit4all.api.compobject;

import java.net.URI;
import java.util.List;
import org.smartbit4all.api.compobject.bean.ComposeableObject;

/**
 * ComposeableObjectApi can provide the children ComposeableObject of the given compositeObject.
 * The API should have a unique URI in the application.
 * 
 * @author Zoltan Szegedi
 *
 */
public interface ComposeableObjectApi {

  public List<ComposeableObject> getChildren(ComposeableObject compositeObject) throws Exception;

  public URI getApiUri();
  
}
