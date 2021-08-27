package org.smartbit4all.api.compobject;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.compobject.bean.ComposeableObject;
import org.smartbit4all.api.compobject.bean.ComposeableObjectDef;
import org.smartbit4all.api.compobject.bean.CompositeObject;
import org.smartbit4all.api.compobject.bean.CompositeObjectAssociation;
import org.smartbit4all.api.compobject.bean.CompositeObjectDef;
import org.smartbit4all.api.navigation.NavigationConfig;
import org.smartbit4all.api.navigation.NavigationConfig.ConfigBuilder;
import org.smartbit4all.api.navigation.bean.NavigationAssociationMeta;
import org.smartbit4all.domain.data.storage.Storage;
import com.google.common.base.Strings;
import com.google.common.io.Files;

public class CompositeObjects {

  private static final String COMP_OBJECT_ASSOC_PATH_PREFIX = "assoc";

  private static final String COMPOBJ_URI_EXTENSION = "compobj";

  private static final String COMPOBJ_DEF_URI_EXTENSION = "compobj";

  public static URI createNewUri() {
    return URI.create(
        ComposeableObjectNavigation.SCHEME + ":/" + UUID.randomUUID() + "."
            + COMPOBJ_URI_EXTENSION);
  }

  public static URI createNewCompDefUri(String defName) {
    return URI.create(
        ComposeableObjectNavigation.SCHEME + ":/" + defName + "." + COMPOBJ_DEF_URI_EXTENSION);
  }

  public static CompositeObject addObject(
      CompositeObject compositeObject,
      URI objectUri,
      URI definitionUri) {

    compositeObject.addObjectsItem(
        new ComposeableObject()
            .defUri(definitionUri)
            .objectUri(objectUri));

    return compositeObject;
  }

  public static URI getChildDefUri(URI assocDefUri, CompositeObjectDef compositeDef) {
    for (CompositeObjectAssociation assoc : compositeDef.getAssociations()) {
      if (assoc.getAssocDefUri().equals(assocDefUri)) {
        return assoc.getChildDefUri();
      }
    }
    return null;
  }

  public static CompositeObjectAssociation getAssociactionWithDef(
      List<CompositeObjectAssociation> associations,
      URI definitionUri) {

    if (associations == null) {
      return null;
    }

    for (CompositeObjectAssociation assoc : associations) {
      if (assoc.getChildDefUri().equals(definitionUri)) {
        return assoc;
      }
    }
    return null;
  }

  public static ComposeableObjectDef addAssociationIfNotExists(
      CompositeObjectDef compositeObjectDef,
      URI childDefUri,
      URI childApiUri,
      boolean childRecursive,
      String icon,
      String viewName,
      boolean visible) {

    if (getAssociactionWithDef(compositeObjectDef.getAssociations(), childDefUri) == null) {
      return addAssociation(compositeObjectDef, childDefUri, childApiUri, childRecursive, icon,
          viewName, visible);
    }
    return null;
  }

  public static ComposeableObjectDef addAssociation(
      CompositeObjectDef compositeObject,
      URI childDefUri,
      URI childApiUri,
      boolean childRecursive,
      String icon,
      String viewName,
      boolean assocVisible) {

    URI childAssocUri = createChildAssocUri(compositeObject.getDefUri(), childDefUri);
    CompositeObjectAssociation newCompObjAssoc = new CompositeObjectAssociation()
        .assocDefUri(childAssocUri)
        .childDefUri(childDefUri)
        .childRecursive(childRecursive);

    compositeObject.addAssociationsItem(newCompObjAssoc);

    return createAssocDef(newCompObjAssoc, childApiUri, icon, viewName, assocVisible);
  }

  public static ComposeableObjectDef createAssocDef(
      CompositeObjectAssociation compObjAssoc,
      URI childApiUri,
      String icon,
      String viewName,
      boolean assocVisible) {

    return new ComposeableObjectDef()
        .uri(compObjAssoc.getAssocDefUri())
        .apiUri(childApiUri)
        .icon(icon)
        .viewName(viewName)
        .assocVisible(assocVisible);
  }

  public static URI createChildAssocUri(URI parentUri, URI childDefUri) {
    String uri = parentUri.toString();
    
    String fileExtension = Files.getFileExtension(uri);
    if(!Strings.isNullOrEmpty(fileExtension)) {
      String uriBeforeLastSlash = uri.substring(0, uri.lastIndexOf("/"));
      String nameWithoutExtension = Files.getNameWithoutExtension(uri);
      
      uri = uriBeforeLastSlash + "/" + nameWithoutExtension;
    }
    
    return URI
        .create(uri + "/" + COMP_OBJECT_ASSOC_PATH_PREFIX + childDefUri.getPath());
  }

  public static ConfigBuilder createNavigationConfig(
      URI compositeDefUri,
      Storage<CompositeObjectDef> compositeDefStorage,
      Storage<ComposeableObjectDef> compObjDefStorage) throws Exception {

    ConfigBuilder navigationConfig = NavigationConfig.builder();

    // TODO load CompositeObjects only with the given definition
    for (CompositeObjectDef compositeDef : compositeDefStorage.loadAll()) {
      for (CompositeObjectAssociation assoc : compositeDef.getAssociations()) {

        URI childDefUri = assoc.getChildDefUri();
        URI assocUri = assoc.getAssocDefUri();

        ComposeableObjectDef childDef = getDef(compObjDefStorage, childDefUri);
        ComposeableObjectDef assocDef = getDef(compObjDefStorage, assocUri);

        if (assoc.getChildRecursive()) {
          addChildDefinition(navigationConfig, childDef);
        }

        addAssocDefinition(
            navigationConfig,

            ComposeableObjectNavigation.createAssocMeta(
                assocUri,
                compositeDef.getDefUri(),
                childDefUri,
                assocUri),

            childDef,
            assocDef);
      }
    }

    return navigationConfig;
  }

  private static void addAssocDefinition(
      ConfigBuilder navigationConfig,
      NavigationAssociationMeta assocMeta,
      ComposeableObjectDef childDef,
      ComposeableObjectDef assocDef) {

    navigationConfig.addAssociationMeta(
        assocMeta,
        assocDef.getAssocVisible(),
        childDef.getName(),
        !Strings.isNullOrEmpty(assocDef.getIcon()) ? assocDef.getIcon() : childDef.getIcon());
  }

  private static void addChildDefinition(
      ConfigBuilder navigationConfig,
      ComposeableObjectDef childDef) {

    navigationConfig
        .addAssociationMeta(

            ComposeableObjectNavigation.createAssocMeta(
                childDef.getUri(),
                childDef.getUri(),
                childDef.getUri()),

            childDef.getAssocVisible(),
            childDef.getName(),
            childDef.getIcon());
  }

  private static ComposeableObjectDef getDef(
      Storage<ComposeableObjectDef> compObjDefStorage,
      URI defUri) throws Exception {

    ComposeableObjectDef def = compObjDefStorage
        .load(defUri)
        .orElseThrow(() -> new IllegalArgumentException(
            "CompDef URI does not exists: " + defUri));

    return def;
  }

}
