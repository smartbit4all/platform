package org.smartbit4all.api.navigation;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;
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
   * All the node we already have in this navigation. Identified by the URI of the entry.
   */
  protected Map<URI, NavigationNode> nodesByUri = new HashMap<>();

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
        for (NavigationReference reference : assoc.getReferences()) {
          result++;
        }
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
        api.navigate(node.getEntry(),
            new ArrayList<>(map.keySet()));
    List<ApiItemChangeEvent<NavigationReference>> result = new ArrayList<>();
    for (Entry<URI, List<NavigationReferenceEntry>> entry : navigation
        .entrySet()) {
      NavigationAssociation association = map.get(entry.getKey());
      List<NavigationReferenceEntry> references = entry.getValue();
      // Merge into
      result.addAll(merge(association, references));
    }
    return result;
  }

  public void setRoot(NavigationNode root) {
    registerNode(root);
  }

  /**
   * Merge the reference list of the given association with the reference list from the parameter.
   * 
   * @param association
   * @param references
   * @return
   */
  private final Collection<? extends ApiItemChangeEvent<NavigationReference>> merge(
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
        references.stream().map(r -> registerReferenceEntry(association, r))
            .collect(Collectors.toList());
    newReferences.stream().forEach(r -> this.references.put(r.getId(), r));
    result.addAll(newReferences.stream()
        .map(r -> new ApiItemChangeEvent<NavigationReference>(ApiItemOperation.NEW, r))
        .collect(Collectors.toList()));
    association.setReferences(newReferences);
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
  private NavigationReference registerReferenceEntry(NavigationAssociation association,
      NavigationReferenceEntry referenceEntry) {
    NavigationNode startNode = registerEntry(referenceEntry.getStartEntry());
    NavigationNode endNode = registerEntry(referenceEntry.getEndEntry());
    NavigationNode assocNode = referenceEntry.getAssociationEntry() == null ? null
        : registerEntry(referenceEntry.getAssociationEntry());
    return of(startNode, endNode, assocNode);
  }

  private NavigationNode registerEntry(NavigationEntry entry) {
    NavigationNode node = nodesByUri.get(entry.getUri());
    if (node == null) {
      node = of(entry);
    }
    registerNode(node);
    return node;
  }

  private final void registerNode(NavigationNode node) {
    nodes.put(node.getId(), node);
    nodesByUri.put(node.getEntry().getUri(), node);
  }

  public static NavigationNode of(NavigationEntry entry) {
    NavigationNode node;
    node = new NavigationNode();
    node.setEntry(entry);
    node.setId(UUID.randomUUID().toString());
    // Instantiate the associations by the meta.
    node.setAssociations(
        entry.getMeta().getAssociations().stream().map(a -> of(a)).collect(Collectors.toList()));
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

  public static NavigationAssociation of(NavigationAssociationMeta assocMeta) {
    NavigationAssociation result = new NavigationAssociation();
    result.setHidden(true); // TODO default by meta!
    result.setId(UUID.randomUUID().toString());
    result.setLastNavigation(null);
    result.setMeta(assocMeta);
    return result;
  }

  public static NavigationEntry of(NavigationEntryMeta meta, URI uri, String name) {
    NavigationEntry result = new NavigationEntry();
    result.setUri(uri);
    result.setName(name);
    result.setMeta(meta);
    return result;
  }

  public static NavigationReferenceEntry of(NavigationEntry startEntry, NavigationEntry endEntry,
      NavigationEntry associationEntry) {
    NavigationReferenceEntry result = new NavigationReferenceEntry();
    result.setId(startEntry.getUri()
        + (associationEntry == null ? " --> " : " -- " + associationEntry.getUri() + " -->")
        + endEntry.getUri());
    result.setStartEntry(startEntry);
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

}
