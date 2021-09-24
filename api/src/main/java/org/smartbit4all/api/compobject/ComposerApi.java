package org.smartbit4all.api.compobject;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import org.smartbit4all.api.compobject.bean.ComposeableObject;
import org.smartbit4all.api.compobject.bean.ComposeableObjectDef;
import org.smartbit4all.core.object.ApiObjectRef;

public interface ComposerApi {

  public List<ComposeableObject> getChildren(URI parentObjectUri, ComposeableObjectDef definition,
      Consumer<URI> nodeChangeListener) throws Exception;

  public String getTitle(URI objectUri, ComposeableObjectDef definition) throws Exception;

  public String getViewName(URI objectUri, ComposeableObjectDef definition) throws Exception;

  public String getIcon(URI objectUri, ComposeableObjectDef definition) throws Exception;

  public Optional<ApiObjectRef> loadObject(URI entryMetaUri, ComposeableObjectDef definition)
      throws Exception;

}
