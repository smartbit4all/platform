package org.smartbit4all.api.compobject;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import org.smartbit4all.api.compobject.bean.ComposeableObject;
import org.smartbit4all.domain.data.storage.Storage;

public class TestTreeObjectApi implements ComposeableObjectApi {

  public static final String SCHEME = "testtreeobject";

  public static final URI API_URI = URI.create("testtreeobject:/composeableobject/apiUri");

  private Storage storage;

  public TestTreeObjectApi(Storage storage) {
    this.storage = storage;
  }

  @Override
  public String getTitle(URI objectUri) throws Exception {
    return getUri(objectUri);
  }

  @Override
  public String getIcon(URI objectUri) throws Exception {
    return getUri(objectUri);
  }

  private String getUri(URI objectUri) throws Exception {
    Optional<TestTreeObject> testTreeObject = storage.read(objectUri, TestTreeObject.class);
    if (testTreeObject.isPresent()) {
      return testTreeObject.get().getUri().toString();
    }
    return null;
  }

  @Override
  public List<ComposeableObject> getChildren(URI parentObjectUri, URI definitionUri,
      Consumer<URI> nodeChangeListener)
      throws Exception {

    Optional<TestTreeObject> testTreeObject = storage.read(parentObjectUri, TestTreeObject.class);
    if (testTreeObject.isPresent()) {
      return createChildredComposeables(testTreeObject.get(), definitionUri);
    }

    return Collections.emptyList();
  }

  private List<ComposeableObject> createChildredComposeables(
      TestTreeObject testTreeObject,
      URI defUri) {

    List<URI> children = testTreeObject.getChildren();
    List<ComposeableObject> result = new ArrayList<>();

    for (URI childUri : children) {
      ComposeableObject newComposeableObject = new ComposeableObject()
          .defUri(defUri)
          .objectUri(childUri);

      result.add(newComposeableObject);
    }

    return result;
  }

  @Override
  public String getViewName(URI objectUri) throws Exception {
    return null;
  }

  @Override
  public URI getApiUri() {
    return API_URI;
  }

  @Override
  public Optional<Object> loadObject(URI objectUri) throws Exception {
    return Optional.ofNullable(storage.load(objectUri).orElse(null));
  }

}
