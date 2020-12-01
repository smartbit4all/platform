package org.smartbit4all.api.navigation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.smartbit4all.api.ApiItemChangeEvent;
import org.smartbit4all.api.ApiItemOperation;
import org.smartbit4all.api.navigation.bean.NavigationAssociation;
import org.smartbit4all.api.navigation.bean.NavigationAssociationMeta;
import org.smartbit4all.api.navigation.bean.NavigationConfig;
import org.smartbit4all.api.navigation.bean.NavigationEntry;
import org.smartbit4all.api.navigation.bean.NavigationEntryMeta;
import org.smartbit4all.api.navigation.bean.NavigationNode;
import org.smartbit4all.api.navigation.bean.NavigationReference;
import org.smartbit4all.api.navigation.bean.NavigationReferenceEntry;
import org.smartbit4all.api.navigation.bean.NavigationView;

/**
 * This is the instance of a navigation. It has some configuration and it contains all the nodes and
 * edges (associations) we already navigated through.
 * 
 * @author Peter Boros
 *
 */
public class Navigation {

  /**
   * The configuration of the given navigation.
   */
  protected NavigationConfig config;

  /**
   * All the node we already have in this navigation. Identified by their UUID as string.
   */
  protected Map<String, NavigationNode> nodes = new HashMap<>();

  /**
   * All the references we already have in this navigation. Identified by their UUID.
   */
  protected Map<String, NavigationReference> references = new HashMap<>();

  /**
   * The API for the navigation.
   */
  protected NavigationApi api;

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

  public int numberOfChildren(String nodeId) {
    NavigationNode node = nodes.get(nodeId);
    if (node == null || node.getAssociations() == null || node.getAssociations().isEmpty()) {
      return 0;
    }
    int result = 0;
    for (NavigationAssociation assoc : node.getAssociations()) {
      if (assoc.getReferences() != null) {
        result += assoc.getReferences().size();
      }
    }
    return result;
  }

  public List<NavigationNode> getCildrens(String parentId) {
    NavigationNode parent = nodes.get(parentId);
    if (parent == null || parent.getAssociations() == null || parent.getAssociations().isEmpty()) {
      return new ArrayList<>();
    }
    List<NavigationNode> childrens = new ArrayList<>();
    for (NavigationAssociation assoc : parent.getAssociations()) {
      if (assoc.getReferences() != null) {
        assoc.getReferences().forEach(r -> childrens.add(r.getEndNode()));
      }
    }

    return childrens;
  }

  public boolean hasChildren(String nodeId) {
    NavigationNode node = nodes.get(nodeId);
    if (node == null || node.getAssociations() == null || node.getAssociations().isEmpty()) {
      return false;
    }
    for (NavigationAssociation assoc : node.getAssociations()) {
      if (assoc.getReferences() != null && assoc.getReferences().size() > 0) {
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
    Map<URI, NavigationAssociation> map =
        node.getAssociations().stream().filter(a -> a.getLastNavigation() == null)
            .collect(Collectors.toMap(a -> a.getMeta().getUri(), a -> a));
    Map<URI, List<NavigationReferenceEntry>> navigation =
        api.navigate(node.getEntry().getObjectUri(),
            new ArrayList<>(map.keySet()));
    List<ApiItemChangeEvent<NavigationReference>> result = new ArrayList<>();
    for (Entry<URI, List<NavigationReferenceEntry>> entry : navigation
        .entrySet()) {
      NavigationAssociation association = map.get(entry.getKey());
      List<NavigationReferenceEntry> references = entry.getValue();
      // Merge into
      result.addAll(merge(node, association, references));
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
  public NavigationNode addRootNode(NavigationEntryMeta entryMeta, URI objectUri) {
    NavigationEntry entry = api.getEntry(entryMeta.getUri(), objectUri);
    NavigationNode node = of(entry);
    registerNode(node);
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
    // TODO implement merge!
    // Naive impl: By default we clear the current references.
    List<ApiItemChangeEvent<NavigationReference>> result = null;
    if (association.getReferences() != null && !association.getReferences().isEmpty()) {
      association.getReferences().forEach(r -> this.references.remove(r.getId()));
      result = association.getReferences().stream()
          .map(r -> new ApiItemChangeEvent<NavigationReference>(ApiItemOperation.DELETED, r))
          .collect(Collectors.toList());
    } else {
      result = new ArrayList<>();
    }
    // We add all the references as new.
    List<NavigationReference> newReferences =
        references.stream().map(r -> registerReferenceEntry(startNode, association, r))
            .collect(Collectors.toList());
    newReferences.stream().forEach(r -> this.references.put(r.getId(), r));
    result.addAll(newReferences.stream()
        .map(r -> new ApiItemChangeEvent<NavigationReference>(ApiItemOperation.NEW, r))
        .collect(Collectors.toList()));
    association.setReferences(newReferences);
    association.setLastNavigation(Integer.valueOf((int) System.currentTimeMillis()));
    return result;
  }

  /**
   * Constructs the {@link NavigationReference} based on the entry. If we find all the
   * {@link NavigationEntry}s as {@link NavigationNode} in the registry then we attach to the
   * existing ones. Else we add the {@link NavigationEntry} as node and we refers the newly added
   * node.
   * 
   * @param referenceEntry
   * @return
   */
  private NavigationReference registerReferenceEntry(NavigationNode startNode,
      NavigationAssociation association,
      NavigationReferenceEntry referenceEntry) {
    NavigationNode endNode = registerEntry(referenceEntry.getEndEntry());
    NavigationNode assocNode = referenceEntry.getAssociationEntry() == null ? null
        : registerEntry(referenceEntry.getAssociationEntry());
    return of(startNode, endNode, assocNode);
  }

  private NavigationNode registerEntry(NavigationEntry entry) {
    NavigationNode node = of(entry);
    registerNode(node);
    return node;
  }

  private final void registerNode(NavigationNode node) {
    nodes.put(node.getId(), node);
  }

  public static NavigationNode of(NavigationEntry entry) {
    NavigationNode node;
    node = new NavigationNode();
    node.setEntry(entry);
    node.setId(UUID.randomUUID().toString());
    // Instantiate the associations by the meta.
    @Valid
    List<NavigationAssociationMeta> associations = entry.getMeta().getAssociations();
    node.setAssociations(associations == null || associations.isEmpty() ? Collections.emptyList()
        : associations.stream().map(a -> of(a, node))
            .collect(Collectors.toList()));
    return node;
  }

  public static NavigationReference of(NavigationNode startNode, NavigationNode endNode,
      NavigationNode associationNode) {
    NavigationReference result = new NavigationReference();
    result.setId(UUID.randomUUID().toString());
    result.setStartNode(startNode);
    result.setEndNode(endNode);
    result.setAssociationNode(associationNode);
    return result;
  }

  public static NavigationAssociation of(NavigationAssociationMeta assocMeta, NavigationNode node) {
    NavigationAssociation result = new NavigationAssociation();
    result.setHidden(true); // TODO default by meta!
    result.setId(UUID.randomUUID().toString());
    result.setLastNavigation(null);
    result.setMeta(assocMeta);
    result.setNode(node);
    return result;
  }

  public static NavigationEntry of(NavigationEntryMeta meta, URI objectUri, String name,
      NavigationView... views) {
    NavigationEntry result = new NavigationEntry();
    result.setObjectUri(objectUri);
    result.setName(name);
    result.setMeta(meta);
    if (views != null) {
      for (int i = 0; i < views.length; i++) {
        result.addViewsItem(views[i]);
      }
    }
    return result;
  }

  public static NavigationReferenceEntry of(URI startEntryUri, NavigationEntry endEntry,
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

  public static NavigationAssociationMeta of(URI uri, String name, NavigationEntryMeta startEntry,
      NavigationEntryMeta endEntry, NavigationEntryMeta associationEntry) {
    NavigationAssociationMeta result = new NavigationAssociationMeta();
    result.setUri(uri);
    result.setName(name);
    result.setStartEntry(startEntry);
    result.setEndEntry(endEntry);
    result.setAssociationEntry(associationEntry);
    return result;
  }

  public static NavigationEntryMeta of(URI uri, String name) {
    NavigationEntryMeta result = new NavigationEntryMeta();
    result.setUri(uri);
    result.setName(name);
    return result;
  }

  public static URI uriOf(NavigationEntryMeta meta, URI uri) throws URISyntaxException {
    return new URI(meta.getUri().getScheme(), null, meta.getUri().getPath(),
        uri != null ? uri.toString() : null);
  }

}
