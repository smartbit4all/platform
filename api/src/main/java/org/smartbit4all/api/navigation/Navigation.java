/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.api.navigation;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.ApiItemChangeEvent;
import org.smartbit4all.api.ApiItemOperation;
import org.smartbit4all.api.navigation.bean.NavigationAssociation;
import org.smartbit4all.api.navigation.bean.NavigationAssociationMeta;
import org.smartbit4all.api.navigation.bean.NavigationEntry;
import org.smartbit4all.api.navigation.bean.NavigationEntryMeta;
import org.smartbit4all.api.navigation.bean.NavigationNode;
import org.smartbit4all.api.navigation.bean.NavigationReference;
import org.smartbit4all.api.navigation.bean.NavigationReferenceEntry;
import org.smartbit4all.api.navigation.bean.NavigationView;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.utility.StringConstant;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;

/**
 * This is the instance of a navigation. It has some configuration and it contains all the nodes and
 * edges (associations) we already navigated through.
 * 
 * @author Peter Boros
 *
 */
public class Navigation {

  private static final Logger log = LoggerFactory.getLogger(Navigation.class);

  /**
   * If null is returned while resolving values, the current {@link ResolvedValue}'s message will be
   * set to this.
   */
  private static final String VALUE_NOT_FOUND = "VALUE_NOT_FOUND";

  /**
   * If association is selected, the association URI passed in the UI parameters with this key.
   */
  public static final String ASSOC_URI_VIEW_PARAM_KEY = "assocUri";

  /**
   * The navigation root object URI passed in the UI parameters with this key.
   */
  public static final String ROOT_OBJECT_URI = "rootObjectUri";

  /**
   * The configuration of the given navigation.
   */
  protected NavigationConfig config;

  /**
   * All the node we already have in this navigation. Identified by their UUID as string.
   */
  protected Map<String, NavigationNode> nodes = new HashMap<>();

  /**
   * All the node we already have in this navigation. Identified by their URI. One URI can appear in
   * more then one position. So it's a list of references we have here.
   */
  protected Map<URI, List<WeakReference<NavigationNode>>> nodesByURI = new HashMap<>();

  /**
   * All the node we already have in this navigation. Identified by their URI. One URI can appear in
   * more then one position. So it's a list of references we have here.
   */
  protected Map<String, List<WeakReference<NavigationNode>>> parentNodesByNode =
      new HashMap<>();

  protected List<NavigationNode> roots = new ArrayList<>();

  /**
   * All the references we already have in this navigation. Identified by their UUID.
   */
  protected Map<String, NavigationReference> references = new HashMap<>();

  /**
   * All the associations we already have in this navigation. Identified by their UUID.
   */
  protected Map<String, NavigationAssociation> associations = new HashMap<>();

  /**
   * The API for the navigation.
   */
  protected NavigationApi api;

  /**
   * The node change publisher.
   */
  private PublishSubject<NavigationNode> nodeChangePublisher = PublishSubject.create();

  private PublishSubject<NavigationNode> rootNodeAddedPublisher = PublishSubject.create();

  private PublishSubject<NavigationNode> rootNodeRemovedPublisher = PublishSubject.create();

  /**
   * Constructs a new navigation that is the state, the session of the current navigation session.
   * 
   * @param config The configuration that describes the possible nodes {@link NavigationEntryMeta}
   *        and edges {@link NavigationAssociationMeta}.
   * @param api The api that is used for the navigation services.
   */
  public Navigation(NavigationConfig config, NavigationApi api) {
    super();
    this.config = config;
    this.api = api;
  }

  public int numberOfReferences(String associationId) {
    NavigationAssociation association = associations.get(associationId);
    return association.getReferences() == null ? 0 : association.getReferences().size();
  }

  public int numberOfChildren(String nodeId, boolean assumeVisibilityOfAssociations) {
    NavigationNode node = nodes.get(nodeId);
    if (node == null || node.getAssociations() == null || node.getAssociations().isEmpty()) {
      return 0;
    }
    int result = 0;
    for (NavigationAssociation assoc : node.getAssociations()) {
      List<NavigationReference> assocRefs = assoc.getReferences();
      if (!assumeVisibilityOfAssociations || assoc.getHidden()) {
        if (assocRefs != null) {
          result += assocRefs.size();
        }
      } else {
        // if (assocRefs != null && !assocRefs.isEmpty()) {
        result++;
        // }
      }
    }
    return result;
  }

  public Map<String, List<NavigationNode>> getChildrenNodes(String parentId,
      boolean assumeVisibilityOfAssociations) {
    NavigationNode parent = nodes.get(parentId);
    if (parent == null || parent.getAssociations() == null || parent.getAssociations().isEmpty()) {
      return new LinkedHashMap<>();
    }
    Map<String, List<NavigationNode>> nodesByAssocs = new LinkedHashMap<>();
    for (NavigationAssociation assoc : parent.getAssociations()) {
      // If the association is hidden then we get the references directly.
      if (!assumeVisibilityOfAssociations || assoc.getHidden()) {
        if (assoc.getReferences() != null) {
          List<NavigationNode> children = new ArrayList<>();
          assoc.getReferences().forEach(r -> children.add(r.getEndNode()));
          nodesByAssocs.put(assoc.getId(), children);
        }
      }
    }

    return nodesByAssocs;
  }

  /**
   * Get the children of a node
   * 
   * @param parentNode The parent of the children nodes.
   * @param assocName The name of the association.
   * @return
   */
  public List<NavigationNode> getChildrenNodes(NavigationNode parentNode,
      String assocName) {
    if (parentNode == null || parentNode.getAssociations() == null
        || parentNode.getAssociations().isEmpty()) {
      return Collections.emptyList();
    }
    URI uriByAssocName =
        config.findAssocMetaUriByAssocName(parentNode.getEntry().getMetaUri(), assocName);
    if (uriByAssocName == null) {
      return Collections.emptyList();
    }
    for (NavigationAssociation assoc : parentNode.getAssociations()) {
      // If the association is hidden then we get the references directly.
      if (uriByAssocName.equals(assoc.getMetaUri())) {
        if (assoc.getReferences() != null) {
          return assoc.getReferences().stream().map(r -> r.getEndNode())
              .collect(Collectors.toList());
        } else {
          return Collections.emptyList();
        }
      }
    }

    return Collections.emptyList();
  }

  /**
   * Set null the {@link NavigationAssociation#lastNavigation(Integer)} to force the refresh when
   * the children requested again.
   * 
   * @param associationId The unique UUID of the association.
   */
  public void forceRefreshAssociation(String associationId) {
    NavigationAssociation navigationAssociation = associations.get(associationId);
    if (navigationAssociation != null) {
      navigationAssociation.setLastNavigation(null);
    }
  }

  /**
   * Set null all the {@link NavigationAssociation#lastNavigation(Integer)} to force the refresh
   * when the children requested again.
   * 
   * @param nodeId The unique UUID of the association.
   */
  public void forceRefreshEntry(String nodeId) {
    NavigationNode node = nodes.get(nodeId);
    if (node.getAssociations() != null) {

      for (NavigationAssociation assoc : node.getAssociations()) {
        assoc.setLastNavigation(null);
      }
    }
  }

  public List<NavigationNode> getReferencedNodes(String associationId) {
    NavigationAssociation association = associations.get(associationId);
    if (association == null || association.getReferences() == null
        || association.getReferences().isEmpty()) {
      return new ArrayList<>();
    }
    List<NavigationNode> children = new ArrayList<>();
    association.getReferences().forEach(r -> children.add(r.getEndNode()));

    return children;
  }

  public boolean hasChildren(String nodeId) {
    NavigationNode node = nodes.get(nodeId);
    if (node == null || node.getAssociations() == null || node.getAssociations().isEmpty()) {
      return false;
    }
    for (NavigationAssociation assoc : node.getAssociations()) {
      if (assoc.getReferences() != null && !assoc.getReferences().isEmpty()) {
        return true;
      }
    }
    return false;
  }

  public NavigationNode getNode(String identifier) {
    return nodes.get(identifier);
  }


  /**
   * The expand all navigate the associations that hasn't been navigated yet.
   * 
   * @param node The starting node.
   * @return The list of newly created api items.
   */
  public List<ApiItemChangeEvent<NavigationReference>> expandAll(NavigationNode node) {
    return expandAll(node, false);
  }

  public void nodeChanged(URI nodeUri) {
    List<WeakReference<NavigationNode>> list = nodesByURI.get(nodeUri);
    if (list != null) {
      list.removeIf(ref -> ref.get() == null);
      for (WeakReference<NavigationNode> ref : list) {
        NavigationNode navigationNode = ref.get();
        refreshNavigationEntry(navigationNode);
        navigationNode.getAssociations().forEach(a -> a.setLastNavigation(null));
        expandAll(navigationNode);
        nodeChangePublisher.onNext(navigationNode);
        // List<WeakReference<NavigationNode>> parents =
        // parentNodesByNode.get(navigationNode.getId());
        // parents.removeIf(parentRef -> parentRef.get() == null);
        // for (WeakReference<NavigationNode> parentRef : parents) {
        // NavigationNode parentNode = parentRef.get();
        // if (parentNode != null) {
        // nodeChangePublisher.onNext(parentNode);
        // }
        // }
      }
    }
  }

  public void refreshNavigationEntry(NavigationNode navigationNode) {
    NavigationEntry currentEntry = navigationNode.getEntry();
    NavigationEntry newEntry =
        api.getEntry(currentEntry.getMetaUri(), currentEntry.getObjectUri());
    navigationNode.setEntry(newEntry);
  }

  /**
   * This function expand all the nodes in a given navigation and returns all the children nodes.
   * 
   * @param node The starting node in the navigation.
   * @return All the children nodes under the given node.
   */
  public List<NavigationNode> expandRecursiveAndRetrieve(NavigationNode node, int depth) {
    if (node == null || depth == 0) {
      return Collections.emptyList();
    }
    expandAll(node);
    Map<String, List<NavigationNode>> childrenNodes = getChildrenNodes(node.getId(), false);
    List<NavigationNode> allChildren =
        childrenNodes.values().stream().flatMap(list -> list.stream()).collect(Collectors.toList());
    List<NavigationNode> result = new ArrayList<>(allChildren);
    for (NavigationNode childNode : allChildren) {
      result.addAll(expandRecursiveAndRetrieve(childNode, depth - 1));
    }
    return result;
  }

  /**
   * The expand all navigate the associations that hasn't been navigated yet.
   * 
   * @param node The starting node.
   * @param force If true then we skip the last navigation time and navigate.
   * @return The list of newly created api items.
   */
  public List<ApiItemChangeEvent<NavigationReference>> expandAll(NavigationNode node,
      boolean force) {
    Map<URI, NavigationAssociation> naviAssocByMetaUri = node.getAssociations()
        .stream()
        .filter(a -> a.getLastNavigation() == null || force)
        .collect(Collectors.toMap(NavigationAssociation::getMetaUri,
            a -> a));

    return expandAssociations(node, naviAssocByMetaUri);
  }

  public List<ApiItemChangeEvent<NavigationReference>> expandAll(NavigationAssociation assoc,
      boolean force) {

    if (force || assoc.getLastNavigation() == null) {
      Map<URI, NavigationAssociation> naviAssocByMetaUri = new HashMap<>();
      naviAssocByMetaUri.put(assoc.getMetaUri(), assoc);
      NavigationNode parentNode = getNode(assoc.getNodeId());
      return expandAssociations(parentNode, naviAssocByMetaUri);
    }

    return Collections.emptyList();
  }

  /**
   * This function expands the given associations of a node regardless the last refreshment time.
   * 
   * @param node The note to expand
   * @param naviAssocByMetaUri The associations identified by the meta uri of the association to
   *        pass it for the navigation.
   * @return The result is the change list.
   */
  private List<ApiItemChangeEvent<NavigationReference>> expandAssociations(NavigationNode node,
      Map<URI, NavigationAssociation> naviAssocByMetaUri) {
    URI currentObjectUri = node.getEntry().getObjectUri();
    ArrayList<URI> assocMetaUris = new ArrayList<>(naviAssocByMetaUri.keySet());

    Map<URI, List<NavigationReferenceEntry>> navigation =
        api.navigate(currentObjectUri, assocMetaUris, this::nodeChanged);

    List<ApiItemChangeEvent<NavigationReference>> result = new ArrayList<>();
    for (Entry<URI, List<NavigationReferenceEntry>> entry : navigation
        .entrySet()) {

      NavigationAssociation association = naviAssocByMetaUri.get(entry.getKey());
      List<NavigationReferenceEntry> referenceEntries =
          entry.getValue() != null
              ? entry.getValue()
              : Collections.emptyList();

      // Merge into
      result.addAll(merge(node, association, referenceEntries));
    }
    return result;
  }

  /**
   * This call will add the given entries by the URI of the navigation entry meta. It's necessary to
   * identify the {@link NavigationApi} that is responsible for the given kind of entry. And we need
   * the object URI that identifies the api object on the specific api. The navigation will retrieve
   * the entry from the api and add it to the navigation state but won't examine if it's connected
   * with the currently existing nodes! So be careful and use this only for adding root nodes for
   * the navigation.
   * 
   * @param entryMeta An existing meta descriptor for the navigation we have.
   * @param objectUri The URI of the Api object exposed by the original API.
   * 
   * @return
   */
  public NavigationNode addRootNode(URI entryMetaUri, URI objectUri) {

    NavigationEntry entry = api.getEntry(entryMetaUri, objectUri);
    NavigationNode node = node(entry, config);
    registerNode(null, node);
    roots.add(node);

    rootNodeAddedPublisher.onNext(node);

    return node;
  }

  /**
   * Merge the reference list of the given association with the reference list from the parameter.
   * 
   * @param association
   * @param references
   * @return
   */
  private final Collection<? extends ApiItemChangeEvent<NavigationReference>> merge(
      NavigationNode startNode,
      NavigationAssociation association, List<NavigationReferenceEntry> references) {

    List<ApiItemChangeEvent<NavigationReference>> result = new ArrayList<>();

    Map<URI, NavigationReference> oldReferences =
        association.getReferences() == null ? Collections.emptyMap()
            : association.getReferences().stream()
                .collect(Collectors.toMap(r -> r.getEndNode().getEntry().getObjectUri(), r -> r));

    // We create a new list based on the references. If a
    List<NavigationReference> newReferenceList = new ArrayList<>();

    for (NavigationReferenceEntry navigationReferenceEntry : references) {
      NavigationReference navigationReference =
          oldReferences.remove(navigationReferenceEntry.getEndEntry().getObjectUri());
      if (navigationReference != null) {
        NavigationEntry entryOld = navigationReference.getEndNode().getEntry();
        NavigationEntry entryNew = navigationReferenceEntry.getEndEntry();
        if (copyNavigationEntry(entryOld, entryNew)) {
          result.add(new ApiItemChangeEvent<>(ApiItemOperation.CHANGED, navigationReference));
        }
        newReferenceList.add(navigationReference);
      } else {
        NavigationReference newReferenceEntry =
            registerReferenceEntry(startNode, association, navigationReferenceEntry);
        newReferenceList
            .add(newReferenceEntry);
        result.add(new ApiItemChangeEvent<>(ApiItemOperation.NEW, newReferenceEntry));
      }
    }

    for (NavigationReference deletedReference : oldReferences.values()) {
      result.add(new ApiItemChangeEvent<>(ApiItemOperation.DELETED, deletedReference));
    }

    association.setReferences(newReferenceList);
    association.setLastNavigation(Integer.valueOf((int) System.currentTimeMillis()));
    return result;
  }

  private boolean copyNavigationEntry(NavigationEntry entryOld, NavigationEntry entryNew) {
    boolean changed = !entryNew.getName().equals(entryOld.getName());
    entryOld.actions(entryNew.getActions()).icon(entryNew.getIcon()).name(entryNew.getName())
        .styles(entryNew.getStyles()).views(entryNew.getViews());
    return changed;
  }

  /**
   * Constructs the {@link NavigationReference} based on the entry. If we find all the
   * {@link NavigationEntry}s as {@link NavigationNode} in the registry then we attach to the
   * existing ones. Else we add the {@link NavigationEntry} as node and we refer the newly added
   * node.
   * 
   * @param referenceEntry
   * @return
   */
  private NavigationReference registerReferenceEntry(NavigationNode startNode,
      NavigationAssociation association,
      NavigationReferenceEntry referenceEntry) {
    NavigationNode endNode = registerEntry(startNode, referenceEntry.getEndEntry());
    NavigationNode assocNode = referenceEntry.getAssociationEntry() == null ? null
        : registerEntry(startNode, referenceEntry.getAssociationEntry());
    return reference(startNode, endNode, assocNode);
  }

  private NavigationNode registerEntry(NavigationNode parentNode, NavigationEntry entry) {
    NavigationNode node = node(entry, config);
    registerNode(parentNode, node);
    return node;
  }

  private final void registerNode(NavigationNode parentNode, NavigationNode node) {
    nodes.put(node.getId(), node);
    List<WeakReference<NavigationNode>> list =
        nodesByURI.computeIfAbsent(node.getEntry().getObjectUri(), uri -> new ArrayList<>());
    list.removeIf(ref -> ref.get() == null);
    list.add(new WeakReference<>(node));

    if (parentNode != null) {
      List<WeakReference<NavigationNode>> parentNodes =
          parentNodesByNode.computeIfAbsent(node.getId(), n -> new ArrayList<>());
      parentNodes.removeIf(ref -> ref.get() == null);
      parentNodes.add(new WeakReference<NavigationNode>(parentNode));
    }

    // We can add the association to the cache because they are constants by the configuration.
    if (node.getAssociations() != null) {
      for (NavigationAssociation association : node.getAssociations()) {
        associations.put(association.getId(), association);
      }
    }
  }

  /**
   * Creates a new {@link NavigationNode} with the given {@link NavigationEntry} and the
   * {@link NavigationAssociation}-s provided by the corresponding meta of the given
   * {@link NavigationConfig}.
   * 
   * @param entry The entry of the new node.
   * @param config The configuration that provides the possible associations from the node.
   * @return A new {@link NavigationNode} instance.
   */
  public static NavigationNode node(NavigationEntry entry, NavigationConfig config) {
    NavigationNode node = new NavigationNode();
    node.setEntry(entry);
    node.setId(UUID.randomUUID().toString());
    // Instantiate the associations by the meta.
    /*
     * It's important that the meta held by the NavigationEntry here is the meta served by the
     * NavigationApi and it's associations are most likely different from the currently configured
     * associations.
     */
    List<URI> assocMetaUris = config.getAssocMetaUris(entry.getMetaUri());
    if (assocMetaUris == null || assocMetaUris.isEmpty()) {
      node.setAssociations(Collections.emptyList());
    } else {
      List<NavigationAssociation> navigationAssociations = assocMetaUris.stream()
          .map(assocMetaUri -> association(assocMetaUri, node, config))
          .collect(Collectors.toList());
      node.setAssociations(navigationAssociations);
    }


    return node;
  }


  public static NavigationReference reference(NavigationNode startNode, NavigationNode endNode,
      NavigationNode associationNode) {
    NavigationReference result = new NavigationReference();
    result.setId(UUID.randomUUID().toString());
    result.setEndNode(endNode);
    result.setAssociationNode(associationNode);
    return result;
  }

  public static NavigationAssociation association(
      URI assocMetaUri,
      NavigationNode node,
      NavigationConfig config) {

    NavigationAssociation result = new NavigationAssociation()
        .hidden(!config.isAssocVisible(assocMetaUri))
        .id(UUID.randomUUID().toString())
        .lastNavigation(null)
        .metaUri(assocMetaUri)
        .nodeId(node.getId())
        .caption(config.getAssocLabel(assocMetaUri))
        .icon(config.getAssocIconKey(assocMetaUri));

    return result;
  }

  public static NavigationEntry entry(NavigationEntryMeta meta, URI objectUri, String name,
      String icon, NavigationView... views) {
    NavigationEntry result = new NavigationEntry();
    result.setObjectUri(objectUri);
    result.setName(name);
    result.setMetaUri(meta.getUri());
    result.setIcon(icon);
    if (views != null) {
      for (int i = 0; i < views.length; i++) {
        result.addViewsItem(views[i]);
      }
    }
    return result;
  }

  public static NavigationReferenceEntry referenceEntry(URI startEntryUri, NavigationEntry endEntry,
      NavigationEntry associationEntry) {
    NavigationReferenceEntry result = new NavigationReferenceEntry();
    result.setId(startEntryUri
        + (associationEntry == null ? " --> " : " -- " + associationEntry.getObjectUri() + " -->")
        + endEntry.getObjectUri());
    result.setStartEntryUri(startEntryUri);
    result.setEndEntry(endEntry);
    result.setAssociationEntry(associationEntry);
    return result;
  }

  public static NavigationAssociationMeta assocMeta(
      URI uri,
      String name,
      NavigationEntryMeta startEntry,
      NavigationEntryMeta endEntry,
      NavigationEntryMeta associationEntry) {

    NavigationAssociationMeta result = new NavigationAssociationMeta();

    result.setUri(uri);
    result.setName(name);
    result.setStartEntry(startEntry);
    result.setEndEntry(endEntry);
    result.setAssociationEntry(associationEntry);

    return result;
  }

  public static NavigationEntryMeta entryMeta(URI uri, String name) {
    NavigationEntryMeta result = new NavigationEntryMeta();
    result.setUri(uri);
    result.setName(name);
    return result;
  }

  public static URI uriOf(NavigationEntryMeta meta, URI uri) throws URISyntaxException {
    return new URI(meta.getUri().getScheme(), null, meta.getUri().getPath(),
        uri != null ? uri.toString() : null);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (NavigationNode rootNode : roots) {
      appendToString(sb, rootNode, StringConstant.EMPTY);
    }
    return sb.toString();
  }

  private final void appendToString(StringBuilder sb, NavigationNode node, String indent) {
    sb.append(indent).append(node.getEntry().getName());
    List<NavigationAssociation> assocList =
        node.getAssociations() != null ? node.getAssociations() : Collections.emptyList();
    for (NavigationAssociation association : assocList) {
      sb.append(StringConstant.NEW_LINE).append(indent).append(StringConstant.SPACE)
          .append(StringConstant.ARROW).append(StringConstant.SPACE)
          .append(association.getMetaUri())
          .append(StringConstant.NEW_LINE);
      List<NavigationReference> refList = association.getReferences();
      for (NavigationReference ref : refList) {
        appendToString(sb, ref.getEndNode(), indent + StringConstant.SPACE + StringConstant.SPACE);
      }
    }
  }

  public NavigationAssociation getAssociation(String identifier) {
    return associations.get(identifier);
  }

  public Disposable subscribeForNodeRefresh(Consumer<NavigationNode> listener) {
    return nodeChangePublisher.subscribe(node -> listener.accept(node));
  }

  public Disposable subscribeForRootNodeAdded(Consumer<NavigationNode> listener) {
    Disposable subscription = rootNodeAddedPublisher.subscribe(node -> listener.accept(node));
    Collection<NavigationNode> rootsToNotify = Collections.unmodifiableCollection(roots);
    for (NavigationNode root : rootsToNotify) {
      listener.accept(root);
    }
    return subscription;
  }

  public Disposable subscribeForRootNodeRemoved(Consumer<NavigationNode> listener) {
    return rootNodeRemovedPublisher.subscribe(node -> listener.accept(node));
  }

  /**
   * The navigation will fetch for the nodes in the navigation and
   * {@link #expandAll(NavigationNode)} all the necessary nodes to reach them.
   * 
   * @param navigationPath The format is the following:
   * 
   *        <ul>
   *        <li>/ --> This is the root itself.</li>
   *        <li>/ASSOC1 --> The nodes available through the ASSOC1 navigation association of the
   *        root.</li>
   *        <li>/ASSOC1/ASSOC2/ASSOC3 --> The nodes available through the ASSOC1-ASSOC2-ASSOC3
   *        navigation association starting from the root.</li>
   *        </ul>
   * 
   * @return
   */
  public List<NavigationNode> resolveNodes(NavigationNode rootNode, String navigationPath) {
    if (rootNode == null) {
      return Collections.emptyList();
    }
    List<String> pathElements = getPathElements(navigationPath);
    // Ensure to have all the nodes by expanding the path elements.
    List<NavigationNode> actualNodes = new ArrayList<>();
    actualNodes.add(rootNode);
    StringBuilder track = new StringBuilder();
    for (String actualAssociation : pathElements) {
      // When we arrive to the given association we have an actual list of nodes. We must expand all
      // the nodes with the associations we need and update the actualNodes list with the newly
      // accessed nodes.
      List<NavigationNode> nextRoundNodes = new ArrayList<>();
      URI metaUriByAssocName = null;
      for (NavigationNode node : actualNodes) {
        if (metaUriByAssocName == null) {
          track.append(node.getEntry().getMetaUri()).append(StringConstant.ARROW)
              .append(actualAssociation).append(StringConstant.EQUAL);
          metaUriByAssocName = findAssocMetaUri(node, actualAssociation);
          if (metaUriByAssocName == null) {
            track.append(StringConstant.QUESTIONMARK);
            throw new IllegalArgumentException(
                "The navigation path (" + track
                    + ") is not available in the given navigation (" + config.toString()
                    + ")");
          }
          track.append(metaUriByAssocName);
        }
        URI actualAssocMetaUri = metaUriByAssocName;
        if (node.getAssociations() == null) {
          // We must stop because the path doesn't exist in this navigation!
          throw new IllegalStateException("The navigation node (" + node
              + ") on " + track
              + " track is not consistent the association is missing from the instance ("
              + actualAssocMetaUri
              + ")");
        }
        Optional<NavigationAssociation> associationOpt = node.getAssociations().stream()
            .filter(a -> a.getMetaUri().equals(actualAssocMetaUri)).findFirst();
        NavigationAssociation association = associationOpt
            .orElseThrow(() -> new IllegalStateException("The navigation node (" + node
                + ")  on " + track
                + " track is not consistent the association is missing from the instance ("
                + actualAssocMetaUri
                + ")"));

        Map<URI, NavigationAssociation> associationsByMetaUri = new HashMap<>();
        associationsByMetaUri.put(metaUriByAssocName, association);
        expandAssociations(node, associationsByMetaUri);
        nextRoundNodes.addAll(getChildrenNodes(node, actualAssociation));
      }
      track.append(StringConstant.ARROW);
      actualNodes = nextRoundNodes;
    }
    return actualNodes;
  }

  /**
   * We can identify meta uri of the association by the association name and an actual node.
   * 
   * @param actualAssociation
   * @param node
   * @return
   */
  public URI findAssocMetaUri(NavigationNode node, String actualAssociation) {
    URI metaUriByAssocName;
    metaUriByAssocName =
        config.findAssocMetaUriByAssocName(node.getEntry().getMetaUri(), actualAssociation);
    return metaUriByAssocName;
  }

  private static class ResolvedPropertyEntry {

    public ResolvedPropertyEntry(String qualifiedName, String name) {
      super();
      this.qualifiedName = qualifiedName;
      this.name = name;
    }

    String qualifiedName;

    String name;

  }

  /**
   * Here we assume that the navigation is already started and the given context node is available
   * already. For the first time it can be the root object added to the navigation to start from. We
   * can resolve the values by fully qualified names. These are the path of the given property
   * inside the navigation tree and the Object belongs to the given node. We must separate the
   * navigation path elements and the property access path with slash. The two segments are
   * separated with hash sign. (It's the path and the fragment section of an URI)
   * 
   * @param qualifiedNames An example: /assoc1/assoc2#refProperty1/myPropery in this case we lookup
   *        for the assoc1 and the assoc2 node inside the
   * @return The resolved values identified by the qualified names.
   */
  public Map<String, ResolvedValue> resolveValues(NavigationNode contextNode,
      Collection<String> qualifiedNames) {
    if (qualifiedNames == null || qualifiedNames.isEmpty()) {
      return Collections.emptyMap();
    }
    // This will be the resolved navigation nodes by the
    Map<String, Optional<NavigationNode>> nodesByPath = new HashMap<>();
    Map<String, List<ResolvedPropertyEntry>> propertiesToResolveByNode = new HashMap<>();
    for (String qualifiedName : qualifiedNames) {
      String[] parts = qualifiedName.split(StringConstant.HASH);
      // We must have a proper qualified name!
      if (parts.length != 2 || (parts.length == 2 && (parts[0].isEmpty() || parts[1].isEmpty()))) {
        throw new IllegalArgumentException("Invalid qualified name " + qualifiedName);
      }
      String navigationPath = parts[0];
      String propertyPath = parts[1];
      List<ResolvedPropertyEntry> propertyPathList = null;
      Optional<NavigationNode> navigationNodeOpt = nodesByPath.get(navigationPath);
      // We enter only if we haven't tried before to avoid resolving again and again!
      if (navigationNodeOpt == null) {
        List<NavigationNode> resolveNodes = resolveNodes(contextNode, navigationPath);
        // We must have exactly one node on the given path to be able to resolve the values.
        if (resolveNodes.size() > 1) {
          throw new IllegalArgumentException("The nodes on the " + navigationPath
              + " are ambigious, more then one node found (" + resolveNodes + ")");
        }
        Iterator<NavigationNode> nodeIter = resolveNodes.iterator();
        if (!nodeIter.hasNext()) {
          // We add the empty optional to show that we have already tried.
          nodesByPath.put(navigationPath, Optional.empty());
        } else {
          // We add the given node and setup the property list for the property paths to resolve.
          NavigationNode node = nodeIter.next();
          nodesByPath.put(navigationPath, Optional.of(node));
          propertyPathList = new ArrayList<>();
          propertiesToResolveByNode.put(node.getId(), propertyPathList);
        }
      } else if (navigationNodeOpt.isPresent()) {
        // If we already have the proper node.
        propertyPathList = propertiesToResolveByNode.get(navigationNodeOpt.get().getId());
      }
      // If we found the propertyPathList we add the current path else we give a debug log.
      if (propertyPathList != null) {
        propertyPathList.add(new ResolvedPropertyEntry(qualifiedName, propertyPath));
      } else {
        log.debug("Unable to retrive {} property because the navigation node was not found.",
            qualifiedName);
      }
    }
    Map<String, ResolvedValue> result = new HashMap<>();
    for (Entry<String, List<ResolvedPropertyEntry>> entry : propertiesToResolveByNode
        .entrySet()) {
      NavigationNode node = getNode(entry.getKey());
      List<ResolvedPropertyEntry> properties = entry.getValue();
      ApiObjectRef objectRef =
          api.loadObject(node.getEntry().getMetaUri(), node.getEntry().getObjectUri())
              .orElseThrow(() -> new IllegalArgumentException(
                  "Unable to load the " + node.getEntry() + " object."));
      for (ResolvedPropertyEntry resolvedPropertyEntry : properties) {
        Object value = objectRef.getValueByPath(resolvedPropertyEntry.name);
        ResolvedValue resolvedValue = new ResolvedValue();
        if (value == null) {
          resolvedValue.setMessage(VALUE_NOT_FOUND);
        } else {
          result.put(resolvedPropertyEntry.qualifiedName, new ResolvedValue(value));
        }
      }
    }

    return result;
  }

  /**
   * Parse the path elements by splitting the original path
   * 
   * @param path
   * @return
   */
  public static List<String> getPathElements(String path) {
    if (path == null || path.isEmpty()) {
      return Collections.emptyList();
    }
    String[] split = path.split(StringConstant.SLASH);
    if (split == null || split.length == 0) {
      return Collections.emptyList();
    }
    List<String> result = new ArrayList<>();
    for (String element : split) {
      if (element.length() > 0) {
        result.add(element);
      }
    }
    return result;
  }

  public NavigationNode getRootNode(NavigationNode node) {
    return getRootNode(node, new HashSet<>());
  }

  private NavigationNode getRootNode(NavigationNode node, Set<String> alreadyVisitedId) {
    String id = node.getId();

    if (alreadyVisitedId.contains(id)) {
      return null;
    }

    alreadyVisitedId.add(id);

    List<WeakReference<NavigationNode>> parentNodes = parentNodesByNode.get(id);

    if (parentNodes != null) {
      for (WeakReference<NavigationNode> parentNodeRef : parentNodes) {
        NavigationNode parentNode = parentNodeRef.get();
        if (parentNode != null && roots.contains(parentNode)) {
          return parentNode;
        } else {
          return getRootNode(parentNode, alreadyVisitedId);
        }
      }
    }

    return null;
  }

  public List<NavigationNode> getRootNodes() {
    return Collections.unmodifiableList(roots);
  }

}
