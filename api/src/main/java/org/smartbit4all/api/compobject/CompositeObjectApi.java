package org.smartbit4all.api.compobject;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.smartbit4all.api.compobject.bean.ComposeableObject;
import org.smartbit4all.api.compobject.bean.ComposeableObjectDef;
import org.smartbit4all.api.compobject.bean.CompositeObject;
import org.smartbit4all.api.compobject.bean.CompositeObjectAssociation;
import org.smartbit4all.api.compobject.bean.CompositeObjectDef;
import org.smartbit4all.domain.data.storage.Storage;
import com.google.common.base.Strings;
import com.google.common.collect.MoreCollectors;

public class CompositeObjectApi implements ComposeableObjectApi {

  public static final URI API_URI = URI.create("compobj:/composeableobject/apiUri");

  private Storage<CompositeObject> compositeStorage;

  private Storage<CompositeObjectDef> compositeDefStorage;

  private Storage<ComposeableObjectDef> composeableDefStorage;

  public CompositeObjectApi(
      Storage<CompositeObject> compositeStorage,
      Storage<CompositeObjectDef> compositeDefStorage,
      Storage<ComposeableObjectDef> composeableDefStorage) {

    this.compositeStorage = compositeStorage;
    this.compositeDefStorage = compositeDefStorage;
    this.composeableDefStorage = composeableDefStorage;
  }

  @Override
  public List<ComposeableObject> getChildren(URI parentObjectUri, URI definitionUri,
      Consumer<URI> nodeChangeListener) throws Exception {

    Optional<CompositeObject> loaded = compositeStorage.load(parentObjectUri);
    if (loaded.isPresent()) {
      CompositeObject compositeObject = loaded.get();
      CompositeObjectDef def = loadCompositeObjectDef(loaded.get());

      Optional<CompositeObjectAssociation> association = def.getAssociations()
          .stream()
          .filter(assoc -> assoc.getAssocDefUri().equals(definitionUri))
          .collect(MoreCollectors.toOptional());

      return createChildren(compositeObject, association.get());
    }
    return Collections.emptyList();
  }

  private List<ComposeableObject> createChildren(
      CompositeObject compositeObject,
      CompositeObjectAssociation assoc) {

    if (compositeObject.getObjects() == null) {
      return Collections.emptyList();
    }

    return compositeObject
        .getObjects()
        .stream()
        .filter(object -> object.getDefUri().equals(assoc.getChildDefUri()))
        .collect(Collectors.toList());
  }

  @Override
  public String getTitle(URI objectUri) throws Exception {
    Optional<CompositeObject> loaded = compositeStorage.load(objectUri);
    if (loaded.isPresent()) {
      CompositeObject compositeObject = loaded.get();
      if (!Strings.isNullOrEmpty(compositeObject.getCaption())) {
        return compositeObject.getCaption();
      }

      CompositeObjectDef compositeDef = loadCompositeObjectDef(compositeObject);
      ComposeableObjectDef composeableDef = load(compositeDef);

      return composeableDef.getName();
    }
    return null;
  }


  @Override
  public String getIcon(URI objectUri) throws Exception {
    Optional<CompositeObject> compositeObject = compositeStorage.load(objectUri);
    if (compositeObject.isPresent()) {
      CompositeObjectDef compositeDef = loadCompositeObjectDef(compositeObject.get());
      ComposeableObjectDef composeableDef = load(compositeDef);

      return composeableDef.getIcon();
    }
    return null;
  }

  @Override
  public String getViewName(URI objectUri) throws Exception {
    return null;
  }

  @Override
  public URI getApiUri() {
    return API_URI;
  }

  private CompositeObjectDef loadCompositeObjectDef(CompositeObject compositeObject)
      throws Exception {

    return compositeDefStorage.load(
        compositeObject.getCompositeDefUri())
        .orElseThrow(() -> new IllegalArgumentException(
            "CompositeObjectDef does not exist with URI: " + compositeObject.getUri()));
  }

  private ComposeableObjectDef load(CompositeObjectDef def)
      throws Exception {

    return composeableDefStorage.load(
        def.getComposeableDefUri())
        .orElseThrow(() -> new IllegalArgumentException(
            "CompositeObjectAssociation does not exist with URI: " + def.getComposeableDefUri()));
  }

}
