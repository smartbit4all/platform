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
import org.smartbit4all.api.navigation.NavigationImpl;
import org.smartbit4all.api.navigation.bean.NavigationAssociationMeta;
import org.smartbit4all.api.navigation.bean.NavigationEntry;
import org.smartbit4all.api.navigation.bean.NavigationEntryMeta;
import org.smartbit4all.api.navigation.bean.NavigationReferenceEntry;
import org.smartbit4all.api.navigation.bean.NavigationView;
import org.smartbit4all.domain.data.storage.Storage;

public class ComposeableObjectNavigation extends NavigationImpl {

  public static final Logger log = LoggerFactory.getLogger(ComposeableObjectNavigation.class);

  public static final String SCHEME = "compobj";

  private ComposeableObjectApi composeableObjectApi;

  private Storage<ComposeableObjectDef> compObjStorage;

  public ComposeableObjectNavigation(
      String name,
      ComposeableObjectApi composeableObjectApi,
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
    NavigationEntry navigationEntry = Navigation.entry(
        createEntryMeta(getComposeableObjectDef(entryMetaUri)),
        objectUri,
        "Test name",
        "Test icon");

    return navigationEntry;
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

    List<NavigationReferenceEntry> result = new ArrayList<>();

    ComposeableObject currentComposeableObject = new ComposeableObject()
        .definition(compObjDef)
        .objectUri(objectUri);

    List<ComposeableObject> children = composeableObjectApi.getChildren(currentComposeableObject);
    for (ComposeableObject childItem : children) {
      result.add(createReferenceEntry(objectUri, childItem));
    }

    return result;
  }

  private NavigationReferenceEntry createReferenceEntry(
      URI objectUri,
      ComposeableObject childItem) {

    ComposeableObjectDef definition = childItem.getDefinition();
    NavigationEntry newEntry = Navigation.entry(
        createEntryMeta(definition),
        childItem.getObjectUri(),
        definition.getName(),
        definition.getIcon());

    NavigationView view = new NavigationView()
        .name(definition.getViewName());
    
    newEntry.addViewsItem(view);

    return Navigation.referenceEntry(objectUri, newEntry, null);
  }

  public static NavigationAssociationMeta createAssocMeta(ComposeableObjectDef compObjDef) {
    URI compObjDefUri = compObjDef.getUri();

    return Navigation.assocMeta(
        compObjDefUri,
        compObjDefUri.getScheme(),
        createEntryMeta(compObjDef),
        createEntryMeta(compObjDef),
        null);
  }

  public static NavigationEntryMeta createEntryMeta(ComposeableObjectDef compObjDef) {
    URI compObjDefUri = compObjDef.getUri();

    return Navigation.entryMeta(
        compObjDefUri,
        compObjDefUri.getScheme());
  }

}
