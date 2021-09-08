package org.smartbit4all.api.compobject;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.compobject.bean.ComposeableObject;
import org.smartbit4all.api.compobject.bean.ComposeableObjectDef;
import org.smartbit4all.api.navigation.Navigation;
import org.smartbit4all.api.navigation.NavigationCallBackApi;
import org.smartbit4all.api.navigation.NavigationImpl;
import org.smartbit4all.api.navigation.bean.NavigationAssociationMeta;
import org.smartbit4all.api.navigation.bean.NavigationEntry;
import org.smartbit4all.api.navigation.bean.NavigationEntryMeta;
import org.smartbit4all.api.navigation.bean.NavigationReferenceEntry;
import org.smartbit4all.api.navigation.bean.NavigationView;
import org.smartbit4all.domain.data.storage.Storage;
import com.google.common.base.Strings;

public class ComposeableObjectNavigation extends NavigationImpl {

  public static final Logger log = LoggerFactory.getLogger(ComposeableObjectNavigation.class);

  public static final String SCHEME = "compobj";

  private ComposerApi composeableObjectApi;

  private Storage<ComposeableObjectDef> compObjStorage;

  public ComposeableObjectNavigation(
      String name,
      ComposerApi composeableObjectApi,
      Storage<ComposeableObjectDef> compObjStorage) {

    super(name);
    this.composeableObjectApi = composeableObjectApi;
    this.compObjStorage = compObjStorage;
  }

  @Override
  public Map<URI, List<NavigationReferenceEntry>> navigate(
      URI objectUri,
      List<URI> associationMetaUris) {

    Map<URI, List<NavigationReferenceEntry>> result = new HashMap<>();
    for (URI associationMetaUri : associationMetaUris) {
      try {
        List<NavigationReferenceEntry> navReferenceEntries = createNavigationReferenceEntries(
            objectUri,
            getComposeableObjectDef(associationMetaUri));

        result.put(associationMetaUri, navReferenceEntries);
      } catch (Exception e) {
        log.error("Cannot load elements with associationMetaUris: " + associationMetaUri, e);
      }
    }

    return result;
  }

  @Override
  public NavigationEntry getEntry(URI entryMetaUri, URI objectUri) {
    try {
      ComposeableObjectDef compDef = getComposeableObjectDef(entryMetaUri);
      return createNavigationEntry(objectUri, compDef);
    } catch (Exception e) {
      throw new IllegalArgumentException(
          "Cannot get entry. EntryMeta URI: " + entryMetaUri + " Object URI: " + objectUri, e);
    }
  }

  private ComposeableObjectDef getComposeableObjectDef(URI entryMetaUri) {
    return loadComposeableObjectDef(entryMetaUri).orElseThrow(
        () -> new IllegalArgumentException(
            "ComposeableObjectDef not found with URI: " + entryMetaUri));
  }

  private Optional<ComposeableObjectDef> loadComposeableObjectDef(URI composeableObjectDefUri) {
    try {
      return compObjStorage.load(composeableObjectDefUri);
    } catch (Exception e) {
      throw new RuntimeException(
          "Error while loading ComposeableObjectDef from Storage with URI: "
              + composeableObjectDefUri,
          e);
    }
  }

  private List<NavigationReferenceEntry> createNavigationReferenceEntries(
      URI objectUri,
      ComposeableObjectDef compObjDef) throws Exception {

    List<ComposeableObject> children = composeableObjectApi.getChildren(
        objectUri,
        compObjDef);

    List<NavigationReferenceEntry> result = new ArrayList<>();

    for (ComposeableObject childItem : children) {
      result.add(createReferenceEntry(objectUri, compObjDef, childItem));
    }

    return result;
  }

  private NavigationReferenceEntry createReferenceEntry(
      URI objectUri,
      ComposeableObjectDef parentDef,
      ComposeableObject childItem) throws Exception {

    ComposeableObjectDef definition = getComposeableObjectDef(childItem.getDefUri());

    return Navigation.referenceEntry(
        objectUri,
        createNavigationEntry(childItem.getObjectUri(), definition),
        createAssocEntry(objectUri, parentDef, definition));
  }

  private NavigationEntry createAssocEntry(URI objectUri, ComposeableObjectDef parentDef,
      ComposeableObjectDef childDef) throws Exception {
    if (!Strings.isNullOrEmpty(parentDef.getViewName())) {
      NavigationEntry assocEntry = Navigation.entry(

          createEntryMeta(parentDef),
          objectUri,
          parentDef.getName(),
          parentDef.getIcon(),

          new NavigationView()
              .name(composeableObjectApi.getViewName(objectUri, parentDef))
              .putParametersItem(Navigation.ASSOC_URI_VIEW_PARAM_KEY, parentDef.getUri()));

      return assocEntry;
    }
    return null;
  }

  private NavigationEntry createNavigationEntry(URI objectUri, ComposeableObjectDef definition)
      throws Exception {

    String title = composeableObjectApi.getTitle(objectUri, definition);
    String icon = composeableObjectApi.getIcon(objectUri, definition);
    String viewName = composeableObjectApi.getViewName(objectUri, definition);

    NavigationEntry newEntry = Navigation.entry(
        createEntryMeta(definition),
        objectUri,
        title,
        icon);

    if (!Strings.isNullOrEmpty(viewName)) {
      NavigationView view = new NavigationView()
          .name(viewName);

      newEntry.addViewsItem(view);
    }

    return newEntry;
  }

  public static NavigationAssociationMeta createAssocMeta(ComposeableObjectDef compObjDef) {
    return createAssocMeta(compObjDef.getUri(), compObjDef.getUri(), compObjDef.getUri());
  }

  public static NavigationAssociationMeta createAssocMeta(
      URI assocUri,
      URI startEntryUri,
      URI endEntryUri) {

    return createAssocMeta(assocUri, startEntryUri, endEntryUri, null);
  }

  public static NavigationAssociationMeta createAssocMeta(
      URI assocUri,
      URI startEntryUri,
      URI endEntryUri,
      URI assocEntryUri) {

    return Navigation.assocMeta(
        assocUri,
        assocUri.getScheme(),
        createEntryMeta(startEntryUri),
        createEntryMeta(endEntryUri),
        assocEntryUri != null ? createEntryMeta(assocEntryUri) : null);
  }

  public static NavigationEntryMeta createEntryMeta(ComposeableObjectDef compObjDef) {
    return createEntryMeta(compObjDef.getUri());
  }

  public static NavigationEntryMeta createEntryMeta(URI compObjDefUri) {
    return Navigation.entryMeta(
        compObjDefUri,
        compObjDefUri.getScheme());
  }

  @Override
  public Map<URI, List<NavigationReferenceEntry>> navigate(URI objectUri,
      List<URI> associationMetaUris, NavigationCallBackApi callBack) {
    return navigate(objectUri, associationMetaUris);
  }

}
