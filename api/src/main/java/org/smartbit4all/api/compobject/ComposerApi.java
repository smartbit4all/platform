package org.smartbit4all.api.compobject;

import java.net.URI;
import java.util.List;
import org.smartbit4all.api.compobject.bean.ComposeableObject;
import org.smartbit4all.api.compobject.bean.ComposeableObjectDef;

public interface ComposerApi {
  
  public List<ComposeableObject> getChildren(URI parentObjectUri, ComposeableObjectDef definition) throws Exception;

  public String getTitle(URI objectUri, ComposeableObjectDef definition) throws Exception;
  
  public String getViewName(URI objectUri, ComposeableObjectDef definition) throws Exception;
  
  public String getIcon(URI objectUri, ComposeableObjectDef definition) throws Exception;
  
}
