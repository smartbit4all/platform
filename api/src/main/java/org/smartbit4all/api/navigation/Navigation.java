/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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

  public List<NavigationNode> getChildren(String parentId) {
    NavigationNode parent = nodes.get(parentId);
    if (parent == null || parent.getAssociations() == null || parent.getAssociations().isEmpty()) {
      return new ArrayList<>();
    }
    List<NavigationNode> children = new ArrayList<>();
    for (NavigationAssociation assoc : parent.getAssociations()) {
      if (assoc.getReferences() != null) {
        assoc.getReferences().forEach(r -> children.add(r.getEndNode()));
      }
    }

    return children;
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
    Map<URI, NavigationAssociation> naviAssocByMetaUri = node.getAssociations()
          .stream()
          .filter(a -> a.getLastNavigation() == null)
          .collect(Collectors.toMap(a -> a.getMetaUri(),
                                    a -> a));
    
    URI currentObjectUri = node.getEntry().getObjectUri();
    ArrayList<URI> assocMetaUris = new ArrayList<>(naviAssocByMetaUri.keySet());
    
    Map<URI, List<NavigationReferenceEntry>> navigation =
        api.navigate(currentObjectUri, assocMetaUris);
    
    List<ApiItemChangeEvent<NavigationReference>> result = new ArrayList<>();
    for (Entry<URI, List<NavigationReferenceEntry>> entry : navigation
        .entrySet()) {
      NavigationAssociation association = naviAssocByMetaUri.get(entry.getKey());
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
  public NavigationNode addRootNode(URI entryMetaUri, URI objectUri) {
    
    NavigationEntry entry = api.getEntry(entryMetaUri, objectUri);
    NavigationNode node = node(entry, config);
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
   * existing ones. Else we add the {@link NavigationEntry} as node and we refer the newly added
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
    return reference(startNode, endNode, assocNode);
  }

  private NavigationNode registerEntry(NavigationEntry entry) {
    NavigationNode node = node(entry, config);
    registerNode(node);
    return node;
  }

  private final void registerNode(NavigationNode node) {
    nodes.put(node.getId(), node);
  }

  /**
   * Creates a new {@link NavigationNode} with the given {@link NavigationEntry} and the
   * {@link NavigationAssociation}-s provided by the corresponding meta of the given 
   * {@link NavigationConfig}.
   * 
   * @param entry
   *            The entry of the new node.
   * @param config
   *            The configuration that provides the possible associations from the node.
   * @return A new {@link NavigationNode} instance.
   */
  public static NavigationNode node(NavigationEntry entry, NavigationConfig config) {
    NavigationNode node = new NavigationNode();
    node.setEntry(entry);
    node.setId(UUID.randomUUID().toString());
    // Instantiate the associations by the meta.
    /* It's important that the meta held by the NavigationEntry here is the meta served by the NavigationApi and it's 
       associations are most likely different from the currently configured associations.*/
    List<URI> assocMetaUris = config.getAssocMetaUris(entry.getMetaUri());
    node.setAssociations(assocMetaUris == null || assocMetaUris.isEmpty() ? Collections.emptyList()
        : assocMetaUris.stream()
            .map(assocMetaUri -> association(assocMetaUri, node, config))
            .collect(Collectors.toList()));
//    List<NavigationAssociationMeta> assocMetas = new ArrayList<>();
//    config.getEntries()
//        .stream()
//        .filter(e -> e.getName().equals(entry.getMeta().getName()))
//        .findFirst()
//        .ifPresent(e -> assocMetas.addAll(
//            e.getAssociations() != null ? e.getAssociations() : Collections.emptyList()));
//    node.setAssociations(assocMetas.isEmpty() ? Collections.emptyList()
//        : assocMetas.stream()
//            .map(assocMeta -> association(assocMeta.getUri(), node))
//            .collect(Collectors.toList()));
    
    return node;
  }
  

  public static NavigationReference reference(NavigationNode startNode, NavigationNode endNode,
      NavigationNode associationNode) {
    NavigationReference result = new NavigationReference();
    result.setId(UUID.randomUUID().toString());
    result.setStartNode(startNode);
    result.setEndNode(endNode);
    result.setAssociationNode(associationNode);
    return result;
  }

  public static NavigationAssociation association(URI assocMetaUri, NavigationNode node, NavigationConfig config) {
    NavigationAssociation result = new NavigationAssociation();
    result.setHidden(config.isAssocVisible(assocMetaUri));
    result.setId(UUID.randomUUID().toString());
    result.setLastNavigation(null);
    result.setMetaUri(assocMetaUri);
    result.setNode(node);
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

  public static NavigationAssociationMeta assocMeta(URI uri, String name, NavigationEntryMeta startEntry,
      NavigationEntryMeta endEntry, NavigationEntryMeta associationEntry) {
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

}
