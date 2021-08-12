package org.smartbit4all.api.compobject;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.compobject.bean.ComposeableObject;
import org.smartbit4all.api.compobject.bean.ComposeableObjectDef;
import org.springframework.util.Assert;

/**
 * The Primary ComposeableObjectApi can wire multiple APIs and delegate the calls to the specific API,
 * based on the given ComposeableObjectDef apiUri.
 * 
 * @author Zoltan Szegedi
 *
 */
public class ComposeableObjectPrimaryApi implements ComposeableObjectApi {

  public static final Logger log = LoggerFactory.getLogger(ComposeableObjectPrimaryApi.class);

  public static final URI API_URI = URI.create("composeableprimary:/composeableobject/apiUri");

  private Map<URI, ComposeableObjectApi> apisByUri;

  public ComposeableObjectPrimaryApi() {
    this.apisByUri = new ConcurrentHashMap<>();
  }

  @Override
  public List<ComposeableObject> getChildren(ComposeableObject composeableObject) throws Exception {
    ComposeableObjectDef definition = composeableObject.getDefinition();
    ComposeableObjectApi api = apisByUri.get(definition.getApiUri());

    Assert.notNull(api, "No API found for ComposeableObjectDef with URI: " + definition.getUri()
        + " API URI: " + definition.getApiUri());

    return api.getChildren(composeableObject);
  }

  @Override
  public URI getApiUri() {
    return API_URI;
  }
  
  public void add(ComposeableObjectApi api) {
    this.apisByUri.put(api.getApiUri(), api);
  }

  public ComposeableObjectApi getApi(URI apiUri) {
    return this.apisByUri.get(apiUri);
  }

}
