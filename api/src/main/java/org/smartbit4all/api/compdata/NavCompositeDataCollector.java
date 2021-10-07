package org.smartbit4all.api.compdata;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.compdata.bean.CompositeData;
import org.smartbit4all.api.compdata.bean.CompositeDataCollection;
import org.smartbit4all.api.compdata.bean.CompositeDataItem;
import org.smartbit4all.api.compobject.CompositeObjects;
import org.smartbit4all.api.compobject.bean.CompositeObjectDef;
import org.smartbit4all.api.navigation.Navigation;
import org.smartbit4all.api.navigation.NavigationApi;
import org.smartbit4all.api.navigation.NavigationConfig;
import org.smartbit4all.api.navigation.NavigationConfig.ConfigBuilder;
import org.smartbit4all.api.navigation.bean.NavigationNode;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageObject;

/**
 * TODO delete it soon
 * 
 * @deprecated
 */
@Deprecated
public class NavCompositeDataCollector implements CompositeDataCollector {

  private static final Logger log =
      LoggerFactory.getLogger(NavCompositeDataCollector.class);

  private Storage compositeStorage;

  private NavigationApi navigationApi;

  public NavCompositeDataCollector(
      Storage compositeStorage,
      NavigationApi navigationApi) {

    this.compositeStorage = compositeStorage;
    this.navigationApi = navigationApi;
  }

  @Override
  public Map<String, Object> collect(
      Collection<String> qualifiedNames,
      CompositeDataCollection compositeDataCollection) {

    Map<String, Object> result = new HashMap<>();

    for (CompositeData compositeData : compositeDataCollection.getCompositeDatas()) {
      URI compositeDefConfigUri = compositeData.getConfigUri();
      Optional<StorageObject<CompositeObjectDef>> compositeDefConfigLoaded =
          compositeStorage.load(compositeDefConfigUri, CompositeObjectDef.class);

      if (compositeDefConfigLoaded.isPresent()) {
        CompositeObjectDef compositeObjectDef = compositeDefConfigLoaded.get().getObject();

        Map<String, Object> resolvedValues = getResolvedValues(
            qualifiedNames,
            compositeData,
            compositeObjectDef);

        addResolvedValues(result, resolvedValues);
      }
    }

    return result;
  }

  private Map<String, Object> getResolvedValues(
      Collection<String> qualifiedNames,
      CompositeData compositeData,
      CompositeObjectDef compositeObjectDef) {

    Map<String, Object> result = new HashMap<>();

    ConfigBuilder navigationConfig = NavigationConfig.builder();
    CompositeObjects.addCompositeObjectDef(
        compositeStorage,
        navigationConfig,
        compositeObjectDef);

    Navigation navigation = new Navigation(navigationConfig.build(), navigationApi);

    List<NavigationNode> rootNodes = createRootNodes(compositeData, navigation);

    for (NavigationNode rootNode : rootNodes) {
      Map<String, Object> resolvedValues = navigation.resolveValues(rootNode, qualifiedNames);
      addResolvedValues(result, resolvedValues);
    }

    return result;
  }

  private List<NavigationNode> createRootNodes(CompositeData compositeData, Navigation navigation) {
    List<NavigationNode> rootNodes = new ArrayList<>();
    for (CompositeDataItem compositeDataItem : compositeData.getRoots()) {
      NavigationNode rootNode = navigation.addRootNode(
          compositeDataItem.getAssocUri(),
          compositeDataItem.getObjectUri());

      rootNodes.add(rootNode);
    }
    return rootNodes;
  }

  private void addResolvedValues(Map<String, Object> result, Map<String, Object> resolvedValues) {
    for (Map.Entry<String, Object> entry : resolvedValues.entrySet()) {
      String identifier = entry.getKey();
      if (containsIdentifier(result, identifier)) {
        log.warn("More than one value found for the field, identifier: " + identifier);
      }
      result.put(identifier, entry.getValue());
    }
  }

  private boolean containsIdentifier(Map<String, Object> fields, String identifier) {
    for (Map.Entry<String, Object> entry : fields.entrySet()) {
      if (entry.getKey().equals(identifier)) {
        return true;
      }
    }
    return false;
  }

}
