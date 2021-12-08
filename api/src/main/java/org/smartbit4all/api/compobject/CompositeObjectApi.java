package org.smartbit4all.api.compobject;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

  public static final String SCHEME = "compositeobj";

  public static final String API_SCHEME = "compobj";

  public static final URI API_URI = URI.create(API_SCHEME + ":/composeableobject/apiUri");

  private Storage compositeStorage;

  public CompositeObjectApi(Storage compositeStorage) {
    this.compositeStorage = compositeStorage;
  }

  @Override
  public List<ComposeableObject> getChildren(
      URI parentObjectUri,
      URI definitionUri) throws Exception {

    if (compositeStorage.exists(parentObjectUri)) {
      CompositeObject compositeObject = compositeStorage.read(
          parentObjectUri,
          CompositeObject.class);
      CompositeObjectDef def = loadCompositeObjectDef(compositeStorage.read(
          parentObjectUri,
          CompositeObject.class));

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
    if (compositeStorage.exists(objectUri)) {
      CompositeObject compositeObject = compositeStorage.read(objectUri, CompositeObject.class);
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
    if (compositeStorage.exists(objectUri)) {
      CompositeObjectDef compositeDef =
          loadCompositeObjectDef(compositeStorage.read(objectUri, CompositeObject.class));
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
    return compositeStorage.read(
        compositeObject.getCompositeDefUri(), CompositeObjectDef.class);
  }

  private ComposeableObjectDef load(CompositeObjectDef def)
      throws Exception {
    return compositeStorage.read(
        def.getComposeableDefUri(), ComposeableObjectDef.class);
  }

}
