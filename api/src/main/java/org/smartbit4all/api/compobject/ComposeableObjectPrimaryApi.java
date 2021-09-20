package org.smartbit4all.api.compobject;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.compobject.bean.ComposeableObject;
import org.smartbit4all.api.compobject.bean.ComposeableObjectDef;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import com.google.common.base.Strings;

/**
 * The Primary ComposeableObjectApi can wire multiple APIs and delegate the calls to the specific
 * API, based on the given ComposeableObjectDef apiUri.
 * 
 * @author Zoltan Szegedi
 *
 */
public class ComposeableObjectPrimaryApi implements ComposerApi, InitializingBean {

  public static final Logger log = LoggerFactory.getLogger(ComposeableObjectPrimaryApi.class);

  @Autowired(required = false)
  private List<ComposeableObjectApi> apis;

  private Map<URI, ComposeableObjectApi> apisByUri;

  public ComposeableObjectPrimaryApi() {
    this.apisByUri = new ConcurrentHashMap<>();
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (apis != null) {
      for (ComposeableObjectApi api : apis) {
        add(api);
      }
    }
  }

  @Override
  public List<ComposeableObject> getChildren(URI parentObjectUri, ComposeableObjectDef definition,
      Consumer<URI> nodeChangeListener) throws Exception {
    ComposeableObjectApi api = getApi(definition.getApiUri());
    return api.getChildren(parentObjectUri, definition.getUri(), nodeChangeListener);
  }

  @Override
  public String getTitle(URI objectUri, ComposeableObjectDef definition) throws Exception {
    ComposeableObjectApi api = getApi(definition.getApiUri());
    String title = api.getTitle(objectUri);
    return Strings.isNullOrEmpty(title) ? definition.getName() : title;
  }


  @Override
  public String getIcon(URI objectUri, ComposeableObjectDef definition) throws Exception {
    ComposeableObjectApi api = getApi(definition.getApiUri());
    String icon = api.getIcon(objectUri);
    return Strings.isNullOrEmpty(icon) ? definition.getIcon() : icon;
  }

  @Override
  public String getViewName(URI objectUri, ComposeableObjectDef definition) throws Exception {
    ComposeableObjectApi api = getApi(definition.getApiUri());
    String viewName = api.getViewName(objectUri);
    return Strings.isNullOrEmpty(viewName) ? definition.getViewName() : viewName;
  }

  private ComposeableObjectApi getApi(URI apiUri) {
    ComposeableObjectApi api = apisByUri.get(apiUri);

    Assert.notNull(
        api,
        "No API found with URI: " + apiUri);

    return api;
  }

  public void add(ComposeableObjectApi api) {
    this.apisByUri.put(api.getApiUri(), api);
  }

}
