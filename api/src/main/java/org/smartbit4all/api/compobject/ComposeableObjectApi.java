package org.smartbit4all.api.compobject;

import java.net.URI;
import java.util.List;
import org.smartbit4all.api.compobject.bean.ComposeableObject;

/**
 * ComposeableObjectApi can provide the children ComposeableObject of the given compositeObject.
 * The API should have a unique URI in the application.
 * 
 * @author Zoltan Szegedi
 * @deprecated InvocationApi will be used instead!
 */
@Deprecated
public interface ComposeableObjectApi {

  public List<ComposeableObject> getChildren(URI parentObjectUri, URI definitionUri) throws Exception;

  public String getTitle(URI objectUri) throws Exception;
  
  public String getIcon(URI objectUri) throws Exception;
  
  public String getViewName(URI objectUri) throws Exception;
  
  public URI getApiUri();
  
}
