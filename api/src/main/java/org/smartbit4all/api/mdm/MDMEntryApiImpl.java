package org.smartbit4all.api.mdm;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.ObjectLookup;
import org.smartbit4all.api.collection.StoredList;
import org.smartbit4all.api.collection.StoredList.OperationMode;
import org.smartbit4all.api.collection.StoredMap;
import org.smartbit4all.api.collection.VectorCollection;
import org.smartbit4all.api.collection.bean.ObjectLookupParameter;
import org.smartbit4all.api.collection.bean.ObjectLookupResult;
import org.smartbit4all.api.collection.bean.ObjectLookupResultItem;
import org.smartbit4all.api.collection.bean.VectorCollectionDescriptor;
import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.exception.BusinessLogicException;
import org.smartbit4all.api.mdm.bean.MDMBranchingStrategy;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMDefinitionState;
import org.smartbit4all.api.mdm.bean.MDMEntryConstraint;
import org.smartbit4all.api.mdm.bean.MDMEntryConstraint.KindEnum;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMModification;
import org.smartbit4all.api.object.BranchApi;
import org.smartbit4all.api.object.CompareApi;
import org.smartbit4all.api.object.bean.BranchedObject;
import org.smartbit4all.api.object.bean.BranchedObjectEntry;
import org.smartbit4all.api.object.bean.BranchedObjectEntry.BranchingStateEnum;
import org.smartbit4all.api.object.bean.LangString;
import org.smartbit4all.api.object.bean.ObjectNodeState;
import org.smartbit4all.api.object.bean.ObjectPropertyFormatter;
import org.smartbit4all.api.object.bean.ObjectPropertyValue;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.bean.UserActivityLog;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.value.ValueSetApi;
import org.smartbit4all.api.value.bean.GenericValue;
import org.smartbit4all.api.value.bean.ValueSetDefinitionData;
import org.smartbit4all.api.value.bean.ValueSetDefinitionKind;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectCacheEntry;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectPropertyResolver;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.storage.ObjectStorageImpl;
import org.springframework.util.ObjectUtils;

/**
 * The base implementation of the master data management entry api. The implementation is based on
 * the branching features of the {@link ObjectApi} and the {@link BranchApi}. The branching is
 * operated on a
 * {@link CollectionApi#list(org.smartbit4all.api.collection.bean.StoredCollectionDescriptor)} where
 * the branching is managed by this api. Most of the functionality is accessible also on the direct
 * apis.
 *
 * @author Peter Boros
 *
 */
public final class MDMEntryApiImpl implements MDMEntryApi {

  private static final String MERGED = "merged";

  private static final String UPDATED = "updated";

  private static final String CREATED = "created";

  /**
   * The postfix of the inactive list.
   */
  public static final String INACTIVE_POSTFIX = "-inactive";

  private static final Logger log = LoggerFactory.getLogger(MDMEntryApiImpl.class);

  public static final String[] uriPath = {"uri"};

  private final MasterDataManagementApi mdmApi;

  private final MDMDefinition definition;

  private final MDMEntryDescriptor descriptor;

  private ObjectApi objectApi;

  private CollectionApi collectionApi;

  private InvocationApi invocationApi;

  private BranchApi branchApi;

  private ValueSetApi valueSetApi;

  private LocaleSettingApi localeSettingApi;

  private SessionApi sessionApi;

  private CompareApi compareApi;

  /**
   * If the given MDM ap is managing a list of published values then this list forms a value set
   * definition by default. If it is true then the next access will try to refresh the value set
   * entry belongs to this api. The schema is the schema for the value set api but the storage
   * schema will be used to avoid name collisions.
   */
  private boolean refreshValueSetDefinition = true;

  /**
   * The cache entry to access the state of the definition.
   */
  private ObjectCacheEntry<MDMDefinitionState> definitionStateCache;

  public MDMEntryApiImpl(MasterDataManagementApi mdmApi, MDMDefinition definition,
      MDMEntryDescriptor descriptor,
      ObjectApi objectApi, CollectionApi collectionApi, InvocationApi invocationApi,
      BranchApi branchApi, ValueSetApi valueSetApi, LocaleSettingApi localeSettingApi,
      SessionApi sessionApi, CompareApi compareApi) {
    super();
    Objects.requireNonNull(descriptor, "Unable to initiate master data entry without descriptor.");
    this.mdmApi = mdmApi;
    this.definition = definition;

    definitionStateCache = objectApi.getCacheEntry(MDMDefinitionState.class);
    this.descriptor = descriptor;
    this.objectApi = objectApi;
    this.collectionApi = collectionApi;
    this.invocationApi = invocationApi;
    this.branchApi = branchApi;
    this.valueSetApi = valueSetApi;
    this.localeSettingApi = localeSettingApi;
    this.sessionApi = sessionApi;
    this.compareApi = compareApi;
  }

  @Override
  public List<URI> save(ObjectNode objectNode) {
    return save(Arrays.asList(objectNode));
  }

  @Override
  public List<URI> save(List<ObjectNode> objectNodes) {
    return save(objectNodes, MDMEntryOperation.SAVE, null);
  }

  public static class ConstraintEntry {

    StoredMap currentEditingMap;

    Map<String, URI> uniqueMap;

    public ConstraintEntry(StoredMap currentEditingMap, Map<String, URI> uniqueMap) {
      super();
      this.currentEditingMap = currentEditingMap;
      this.uniqueMap = uniqueMap;
    }

  }

  @Override
  public List<URI> save(List<ObjectNode> objectNodes, MDMEntryOperation operation,
      BiFunction<ObjectNode, ObjectNode, Boolean> isEqual) {
    if (operation != MDMEntryOperation.SAVE) {
      Objects.requireNonNull(isEqual);
    }
    URI branchUri = getBranchUri();
    StoredList list = getList();
    list.branch(branchUri);
    List<URI> results = new ArrayList<>();

    list.update(l -> {
      Map<URI, URI> savedUriByOriginal = new HashMap<>();

      Map<MDMEntryConstraint, ConstraintEntry> uniqueMapsByConstraints =
          getUniqueMapsByconstraints();

      List<ObjectNode> finalSaveList = mergeNodesToSave(objectNodes, operation, isEqual);

      if (!uniqueMapsByConstraints.isEmpty()) {
        checkIfUniquePropertyUsed(finalSaveList, uniqueMapsByConstraints);
      }

      for (ObjectNode objectNode : finalSaveList) {
        // Save the object node
        if (objectNode.getState() == ObjectNodeState.NEW) {
          objectNode
              .setValues(fireBeforeSaveNew(objectApi.definition(descriptor.getTypeQualifiedName()),
                  objectNode.getObjectAsMap(), descriptor));
          updatePropertyWithUserActiviyLog(objectNode, Props.CREATED);
        } else {
          updatePropertyWithUserActiviyLog(objectNode, Props.UPDATED);
        }
        objectApi.save(objectNode, branchUri);
        if (descriptor.getSelfContainedRefList() != null && objectNode.getDefinition()
            .getOutgoingReference(descriptor.getSelfContainedRefList()) != null) {
          results.addAll(
              getResultsUrisBySelfContainedList(objectNode, descriptor.getSelfContainedRefList(),
                  savedUriByOriginal)
                      .collect(toList()));
        } else {
          boolean savedAndOriginalEquals =
              Objects.equals(objectNode.getObjectUri(), objectNode.getResultUri());
          if (!savedAndOriginalEquals) {
            results.add(objectNode.getResultUri());
            if (objectNode.getObjectUri() != null) {
              savedUriByOriginal.put(objectNode.getObjectUri(), objectNode.getResultUri());
            }
          }
        }
      }

      // save new unique property values to StoredMaps
      if (uniqueMapsByConstraints != null) {
        maintainUniqueMapsOnSave(finalSaveList, uniqueMapsByConstraints);
      }

      Map<URI, URI> savedUrisByLatest =
          results.stream().collect(toMap(u -> objectApi.getLatestUri(u), u -> u, (u1, u2) -> {
            Long uv1 = ObjectStorageImpl.getUriVersion(u1);
            Long uv2 = ObjectStorageImpl.getUriVersion(u2);
            if (uv1 == null) {
              return u2;
            } else if (uv2 == null) {
              return u1;
            } else if (uv1 > uv2) {
              return u1;
            } else {
              return u2;
            }
          }));

      // Merge the existing ones
      List<URI> merged = l.stream().map(u -> {
        URI latestUri = objectApi.getLatestUri(u);
        Map<URI, URI> uris;
        if (Objects.equals(latestUri, u)) {
          // latestUri was in collection, deal with it
          uris = savedUriByOriginal.entrySet().stream()
              .collect(toMap(
                  e -> objectApi.getLatestUri(e.getKey()),
                  Entry::getValue,
                  (v1, v2) -> v1));
        } else {
          uris = savedUriByOriginal;
        }
        URI uri = uris.get(u);
        if (uri == null) {
          uri = u;
        }
        URI savedUri = savedUrisByLatest.remove(objectApi.getLatestUri(uri));
        return savedUri != null ? savedUri : u;
      }).collect(toList());
      // Add the newly saved ones.
      merged.addAll(savedUrisByLatest.values());
      return merged;
    });
    return results;
  }

  private List<ObjectNode> mergeNodesToSave(List<ObjectNode> objectNodes,
      MDMEntryOperation operation, BiFunction<ObjectNode, ObjectNode, Boolean> isEqual) {
    List<ObjectNode> finalSaveList = null;

    if (operation != MDMEntryOperation.SAVE) {
      // We need the merged list of ObjectNode to save
      StoredMap uniqueMap = getUniqueMap(getPrimaryId());
      // TODO Add cache to map!
      Map<String, ObjectNode> nodesByUniqueValue = uniqueMap.uris().entrySet().stream().collect(
          toMap(e -> e.getKey(), e -> objectApi.loadLatest(e.getValue(), getBranchUri())));
      finalSaveList = new ArrayList<>();
      for (ObjectNode node : objectNodes) {
        ObjectNode existingNode =
            nodesByUniqueValue.remove(node.getValueAsString(getPrimaryId()));
        if (existingNode != null) {
          if (!isEqual.apply(existingNode, node)) {
            node.overwriteObject(existingNode.getObjectUri());
            finalSaveList.add(node);
          }
        } else {
          // We save as new the given node.
          finalSaveList.add(node);
        }
      }
    } else {
      finalSaveList = objectNodes;
    }
    return finalSaveList;
  }

  protected void maintainUniqueMapsOnSave(List<ObjectNode> objectNodes,
      Map<MDMEntryConstraint, ConstraintEntry> uniqueMapsByConstraints) {
    uniqueMapsByConstraints.entrySet().forEach(e -> {
      StoredMap uniqueMap = e.getValue().currentEditingMap;

      String[] pathArr = e.getKey().getPath().stream().toArray(String[]::new);
      List<ObjectNode> objectNodesWithUniqueValue =
          objectNodes.stream().filter(n -> n.getValue(pathArr) != null).collect(toList());

      // remove the unused values from unique map
      List<URI> uniqueValueUri = objectNodesWithUniqueValue.stream()
          .map(ObjectNode::getObjectUri).collect(toList());
      List<String> keysToRemove = uniqueMap.uris().entrySet().stream()
          .filter(es -> uniqueValueUri.contains(es.getValue()))
          .map(Entry::getKey).collect(toList());
      uniqueMap.remove(keysToRemove);

      // add the new values to the unique map
      addNewValuesToUniqueMap(uniqueMap, pathArr, objectNodesWithUniqueValue);
    });
  }

  protected void maintainUniqueMapsOnRestore(List<ObjectNode> objectNodes,
      Map<MDMEntryConstraint, ConstraintEntry> uniqueMapsByConstraints) {
    uniqueMapsByConstraints.entrySet().forEach(e -> {
      StoredMap uniqueMap = e.getValue().currentEditingMap;

      String[] pathArr = e.getKey().getPath().stream().toArray(String[]::new);
      List<ObjectNode> objectNodesWithUniqueValue =
          objectNodes.stream().filter(n -> n.getValue(pathArr) != null).collect(toList());

      // add the new values to the unique map
      addNewValuesToUniqueMap(uniqueMap, pathArr, objectNodesWithUniqueValue);
    });
  }

  protected void addNewValuesToUniqueMap(StoredMap uniqueMap, String[] pathArr,
      List<ObjectNode> objectNodesWithUniqueValue) {
    Map<String, URI> updateUniqueMap = objectNodesWithUniqueValue.stream()
        .collect(toMap(n -> n.getValue(pathArr).toString(),
            n -> n.getResultUri() != null ? n.getResultUri() : n.getObjectUri()));
    uniqueMap.putAll(updateUniqueMap);
  }

  private void updatePropertyWithUserActiviyLog(ObjectNode objectNode, String property) {
    if (sessionApi != null) {
      property = getMappedPropertyPath(property);
      objectNode.setValue(sessionApi.createActivityLog(), property);

      // RESTORED and REMOVED properties can not be set at the same time.
      if (MDMEntryApi.Props.RESTORED.equals(property)) {
        String removedProperty = getMappedPropertyPath(MDMEntryApi.Props.REMOVED);
        objectNode.setValue(null, removedProperty);
      } else if (MDMEntryApi.Props.REMOVED.equals(property)) {
        String restoredProperty = getMappedPropertyPath(MDMEntryApi.Props.RESTORED);
        objectNode.setValue(null, restoredProperty);
      }
    }
  }

  private String getMappedPropertyPath(String property) {
    if (descriptor.getPropertyMappings() != null
        && descriptor.getPropertyMappings().containsKey(property)) {
      property = descriptor.getPropertyMappings().get(property);
    }
    return property;
  }

  protected void checkIfUniquePropertyUsed(List<ObjectNode> objectNodes,
      Map<MDMEntryConstraint, ConstraintEntry> uniqueMapsByConstraints) {
    Map<ObjectNode, URI> objectNodeAndUriToCheckPairs = new HashMap<>();
    objectNodes.forEach(n -> objectNodeAndUriToCheckPairs.put(n, n.getObjectUri()));

    checkIfUniquePropertyUsed(objectNodeAndUriToCheckPairs, uniqueMapsByConstraints);
  }

  protected void checkIfUniquePropertyUsed(Map<ObjectNode, URI> objectNodeAndUriToCheckPairs,
      Map<MDMEntryConstraint, ConstraintEntry> uniqueMapsByConstraints) {

    uniqueMapsByConstraints.entrySet().forEach(e -> {
      MDMEntryConstraint constraint = e.getKey();
      boolean caseInsensitive = constraint.getKind() == KindEnum.UNIQUECASEINSENSITIVE;
      Map<String, URI> uniqueMap = e.getValue().uniqueMap;
      if (caseInsensitive) {
        // map the stored map keys (values) case insensitive
        uniqueMap = uniqueMap.entrySet().stream()
            .collect(toMap(uniqueE -> uniqueE.getKey().toLowerCase(),
                Entry::getValue, (objectUriForUniqueValue1, objectUriForUniqueValue2) -> {
                  log.warn(
                      "There is a duplicated key in [{}] unique constraint map, because same unique values"
                          + " were saved with different cases before the constraint becamed uniqueCaseInsensitive.",
                      getUniqueMapName(constraint.getPath()));
                  return objectUriForUniqueValue1;
                }));
      }

      // we store the new values to keep uniqueness between the new objects
      List<String> uniqueValues = new ArrayList<>();

      for (Entry<ObjectNode, URI> objectNodeAndUriToCheck : objectNodeAndUriToCheckPairs
          .entrySet()) {
        String[] path = constraint.getPath().stream().toArray(String[]::new);
        Object uniqueValue = objectNodeAndUriToCheck.getKey().getValue(path);

        if (uniqueValue != null) {
          String uniqueValueStr = caseInsensitive
              ? uniqueValue.toString().toLowerCase()
              : uniqueValue.toString();
          URI uriToUniqueValue = uniqueMap.get(uniqueValueStr);

          if ((uriToUniqueValue != null
              && !objectApi.equalsIgnoreVersion(uriToUniqueValue,
                  objectNodeAndUriToCheck.getValue()))
              || uniqueValues.contains(uniqueValueStr)) {
            throw new BusinessLogicException(
                localeSettingApi.get("mdm.notunique")
                    + StringConstant.SPACE
                    + localeSettingApi.get(String.join(".", path)));
          } else {
            uniqueValues.add(uniqueValueStr);
          }
        }
      }
    });
  }

  private final Stream<URI> getResultsUrisBySelfContainedList(ObjectNode objectNode, String list,
      Map<URI, URI> originalByResultUri) {
    boolean savedAndOriginalEquals =
        Objects.equals(objectNode.getObjectUri(), objectNode.getResultUri());

    if (objectNode.getObjectUri() != null && !savedAndOriginalEquals) {
      originalByResultUri.put(objectNode.getObjectUri(), objectNode.getResultUri());
    }
    if (savedAndOriginalEquals) {
      return objectNode.list(list).stream().filter(ref -> ref.isLoaded())
          .flatMap(
              ref -> getResultsUrisBySelfContainedList(ref.get(), list, originalByResultUri));
    } else {
      return Stream.concat(Stream.of(objectNode.getResultUri()),
          objectNode.list(list).stream().filter(ref -> ref.isLoaded())
              .flatMap(
                  ref -> getResultsUrisBySelfContainedList(ref.get(), list, originalByResultUri)));
    }
  }

  @Override
  public final URI getBranchUri() {
    MDMBranchingStrategy branchingStrategy = descriptor.getBranchingStrategy();
    if (branchingStrategy == null) {
      branchingStrategy = definition.getBranchingStrategy();
    }
    if (branchingStrategy == MDMBranchingStrategy.NONE) {
      return null;
    }
    MDMDefinitionState mdmDefinitionState = definitionStateCache.get(definition.getState());
    if (mdmDefinitionState != null && sessionApi != null) {
      User user = sessionApi.getUser();
      if (branchingStrategy == MDMBranchingStrategy.STRICT_PARALLEL) {
        // By default we are looking for the first modification where the current user is editor..
        Optional<MDMModification> firstModification =
            mdmDefinitionState.getActiveModifications().stream()
                .filter(m -> m.getCurrentEditors().contains(objectApi.getLatestUri(user.getUri())))
                .findFirst();
        return firstModification.isPresent() ? firstModification.get().getBranchUri() : null;
      }
      if (branchingStrategy == MDMBranchingStrategy.GLOBAL) {
        MDMModification modification = mdmDefinitionState.getGlobalModification();
        // TODO modify the to get the branch if and only if the current user is editor in the
        // modification.
        return modification == null ? null : modification.getBranchUri();
      }
    }
    return null;
  }

  /**
   * Constructs a {@link ConstraintEntry} with the currently edited {@link StoredMap} and the map of
   * all the unique values.
   *
   * @param c The constraint to create the entry for.
   * @return
   */
  private final ConstraintEntry getConstraintEntry(MDMEntryConstraint c) {
    MDMBranchingStrategy branchingStrategy = descriptor.getBranchingStrategy();
    if (branchingStrategy == null) {
      branchingStrategy = definition.getBranchingStrategy();
    }
    MDMDefinitionState mdmDefinitionState = definitionStateCache.get(definition.getState());
    URI editorBranchUri = null;
    List<URI> editorBranchUris = new ArrayList<>();
    if (mdmDefinitionState != null && sessionApi != null) {
      User user = sessionApi.getUser();
      if (branchingStrategy == MDMBranchingStrategy.STRICT_PARALLEL) {
        // By default we are looking for the first modification where the current user is editor..
        for (MDMModification m : mdmDefinitionState.getActiveModifications()) {
          if (editorBranchUri == null
              && m.getCurrentEditors().contains(objectApi.getLatestUri(user.getUri()))) {
            editorBranchUri = m.getBranchUri();
          } else {
            editorBranchUris.add(m.getBranchUri());
          }
        }
      }
      if (branchingStrategy == MDMBranchingStrategy.GLOBAL) {
        MDMModification modification = mdmDefinitionState.getGlobalModification();
        // TODO modify the to get the branch if and only if the current user is editor in the
        // modification.
        editorBranchUri = modification == null ? null : modification.getBranchUri();
      }
    }
    StoredMap currentEditingMap = collectionApi.map(descriptor.getSchema(),
        getUniqueMapName(c.getPath()));
    currentEditingMap.branch(editorBranchUri);
    // Initiate the basic unique map from the current editor and then append all other editor
    // branches.
    Map<String, URI> uniqueMap = new HashMap<>(currentEditingMap.uris());
    for (URI branchUri : editorBranchUris) {
      StoredMap editorMap = collectionApi.map(descriptor.getSchema(),
          getUniqueMapName(c.getPath()));
      editorMap.branch(branchUri);
      uniqueMap.putAll(editorMap.uris());
    }
    // Now append every other editor branches.

    return new ConstraintEntry(currentEditingMap, uniqueMap);
  }

  @Override
  public boolean cancel(URI draftUri) {
    if (draftUri == null) {
      return false;
    }
    return cancelInner(Arrays.asList(draftUri));
  }

  @Override
  public void cancelAll() {
    cancelInner(null);
  }

  private final boolean cancelInner(List<URI> toCancel) {
    URI branchUri = getBranchUri();
    StoredList list = getList();
    list.branch(branchUri);
    Map<MDMEntryConstraint, ConstraintEntry> uniqueMaps = getUniqueMapsByconstraints();

    List<URI> result = new ArrayList<>();
    List<BranchedObjectEntry> compareWithBranch = list.compareWithBranch(branchUri);
    Set<URI> urisToCancel = toCancel == null ? null
        : toCancel.stream().flatMap(u -> Stream.of(u, objectApi.getLatestUri(u))).collect(toSet());

    compareWithBranch.stream()
        .filter(boe -> urisToCancel == null || urisToCancel.contains(boe.getBranchUri()))
        .forEach(boe -> {

          if (boe.getBranchingState() == BranchingStateEnum.NEW) {
            // Simply remove from branch
            list.update(
                l -> l.stream().filter(u -> !objectApi.equalsIgnoreVersion(u, boe.getBranchUri()))
                    .collect(toList()));
            result.add(boe.getBranchUri());

            if (!uniqueMaps.isEmpty()) {
              // remove unique values from StoredMaps
              removeValueFromUniqueMaps(uniqueMaps, objectApi.load(boe.getBranchUri()));
            }

          } else if (boe.getBranchingState() == BranchingStateEnum.MODIFIED) {
            if (!uniqueMaps.isEmpty()) {
              ObjectNode originalObjectNode = objectApi.load(boe.getOriginalUri());
              Map<ObjectNode, URI> objNodeToCheckWithBranchedUri = new HashMap<>();
              objNodeToCheckWithBranchedUri.put(originalObjectNode, boe.getBranchUri());

              checkIfUniquePropertyUsed(objNodeToCheckWithBranchedUri, uniqueMaps);
              removeValueFromUniqueMaps(uniqueMaps, objectApi.load(boe.getBranchUri()));
              maintainUniqueMapsOnRestore(Arrays.asList(originalObjectNode), uniqueMaps);
            }

            // Replace with the original uri
            list.update(l -> l.stream().map(u -> {
              if (objectApi.equalsIgnoreVersion(u, boe.getBranchUri())) {
                // TODO Here we need the last init / rebase operation source uri.
                return boe.getOriginalUri();
              } else {
                return u;
              }
            }).collect(toList()));
            result.add(boe.getBranchUri());

          } else if (boe.getBranchingState() == BranchingStateEnum.DELETED) {
            if (!uniqueMaps.isEmpty()) {
              ObjectNode originalObjectNode = objectApi.load(boe.getOriginalUri());
              Map<ObjectNode, URI> objNodeToCheckWithBranchedUri = new HashMap<>();
              objNodeToCheckWithBranchedUri.put(originalObjectNode, boe.getBranchUri());

              checkIfUniquePropertyUsed(objNodeToCheckWithBranchedUri, uniqueMaps);
              maintainUniqueMapsOnRestore(Arrays.asList(originalObjectNode), uniqueMaps);
            }

            // insert the original uri again. (undelete)
            list.add(boe.getOriginalUri());
            result.add(boe.getBranchUri());
          }
        });
    if (toCancel != null) {
      toCancel.stream()
          .forEach(uri -> branchApi.removeBranchedObject(branchUri, uri));
    }

    return !result.isEmpty();
  }

  protected void removeValueFromUniqueMaps(Map<MDMEntryConstraint, ConstraintEntry> uniqueMaps,
      ObjectNode objectNode) {
    uniqueMaps.entrySet().forEach(e -> {
      Object uniqueValue =
          objectNode.getValue(e.getKey().getPath().stream().toArray(String[]::new));
      if (uniqueValue != null) {
        StoredMap uniqueMap = e.getValue().currentEditingMap;
        uniqueMap.remove(uniqueValue.toString());
      }
    });
  }

  @Override
  public boolean remove(URI objectUri) {
    URI branchUri = getBranchUri();
    StoredList list = getList();
    list.branch(branchUri);
    boolean remove = list.remove(objectUri);
    if (!remove) {
      return false;
    }
    ObjectNode objectNode = objectApi.load(objectUri);
    Map<MDMEntryConstraint, ConstraintEntry> uniqueMaps = getUniqueMapsByconstraints();
    if (!uniqueMaps.isEmpty()) {
      // remove unique values from StoredMaps
      removeValueFromUniqueMaps(uniqueMaps, objectNode);
    }
    BranchedObject removeNewBranchedObjects =
        branchApi.removeNewBranchedObjects(branchUri, objectUri);
    if (removeNewBranchedObjects != null) {
      fireEntryRemoved(objectUri);
      return true;
    }

    updatePropertyWithUserActiviyLog(objectNode, Props.REMOVED);
    objectUri = objectApi.save(objectNode, branchUri);

    StoredList inactiveList = getInactiveList();
    if (inactiveList != null) {
      inactiveList.branch(branchUri);
      inactiveList.operationMode(OperationMode.UNIQUE_ON_LATEST);
      inactiveList.add(objectUri);
    }
    fireEntryInactivated(objectUri);
    return true;
  }

  private void fireEntryRemoved(URI objectUri) {
    invocationApi
        .publisher(
            MasterDataManagementApi.class,
            MDMSubscriberApi.class,
            REMOVED)
        .publish(api -> api.entryRemoved(definition.getUri(), descriptor.getName(), objectUri,
            getBranchUri()));
  }

  private void fireEntryInactivated(URI objectUri) {
    invocationApi
        .publisher(
            MasterDataManagementApi.class,
            MDMSubscriberApi.class,
            INACTIVATED)
        .publish(api -> api.entryInactivated(definition.getUri(), descriptor.getName(), objectUri,
            getBranchUri()));
  }

  @Override
  public boolean restore(URI objectUri) {
    Map<MDMEntryConstraint, ConstraintEntry> uniqueMaps = getUniqueMapsByconstraints();

    ObjectNode originalObjectNode = objectApi.load(objectUri);
    if (!uniqueMaps.isEmpty()) {
      Map<ObjectNode, URI> objNodeToCheckWithBranchedUri = new HashMap<>();
      objNodeToCheckWithBranchedUri.put(originalObjectNode, objectUri);

      checkIfUniquePropertyUsed(objNodeToCheckWithBranchedUri, uniqueMaps);
      maintainUniqueMapsOnRestore(Arrays.asList(originalObjectNode), uniqueMaps);
    }

    URI branchUri = getBranchUri();
    StoredList inactiveList = getInactiveList();
    if (inactiveList != null) {
      inactiveList.branch(branchUri);
      if (inactiveList.remove(objectUri)) {
        updatePropertyWithUserActiviyLog(originalObjectNode, Props.RESTORED);
        objectUri = objectApi.save(originalObjectNode, branchUri);

        StoredList list = getList();
        list.branch(branchUri);
        list.add(objectUri);
        return true;
      }
    }
    return false;
  }

  /**
   * Run all the event handlers on the given object.
   *
   * @param object
   * @return
   */
  private final Map<String, Object> fireBeforeSaveNew(ObjectDefinition<?> objectDefinition,
      Map<String, Object> object, MDMEntryDescriptor descrtiptor) {
    if (descriptor.getEventHandlersBeforeSave() == null) {
      return object;
    }
    Map<String, Object> result = object;
    for (InvocationRequest handler : descriptor.getEventHandlersBeforeSave()) {
      if (handler != null && handler.getParameters() != null
          && handler.getParameters().size() == 1) {
        try {
          Object invocationResult =
              invocationApi
                  .invoke(invocationApi.prepareByPosition(handler,
                      object))
                  .getValue();
          if (invocationResult != null) {
            Map<String, Object> invocationResultMap = objectDefinition.toMap(invocationResult);
            result.putAll(invocationResultMap);
          }
        } catch (ApiNotFoundException e) {
          throw new IllegalArgumentException("Unable to run the " + handler
              + " before save event handler on the " + descriptor + " master data entry.", e);
        }
      } else {
        log.error("Unable to call the {} event handler for {} entry.", handler,
            descrtiptor);
      }
    }
    return result;
  }

  @Override
  public String getName() {
    return descriptor.getName();
  }

  public final String getListName() {
    return MasterDataManagementApiImpl.getPublishedListName(descriptor);
  }

  @Override
  public StoredList getList() {
    return collectionApi.list(descriptor.getSchema(), getListName());
  }

  @Override
  public StoredMap getUniqueMap(String... path) {
    if (path == null || path.length == 0) {
      return null;
    }
    return collectionApi.map(descriptor.getSchema(), getUniqueMapName(Arrays.asList(path)));
  }

  private Map<MDMEntryConstraint, ConstraintEntry> getUniqueMapsByconstraints() {
    List<MDMEntryConstraint> uniqueConstraints = Collections.emptyList();

    if (!ObjectUtils.isEmpty(descriptor.getConstraints())) {
      uniqueConstraints = descriptor.getConstraints().stream()
          .filter(
              c -> c.getKind() == KindEnum.UNIQUE || c.getKind() == KindEnum.UNIQUECASEINSENSITIVE)
          .collect(toList());
    } else if (!ObjectUtils.isEmpty(descriptor.getUniquePropertyPaths().isEmpty())) {
      uniqueConstraints = descriptor.getUniquePropertyPaths().stream()
          .map(path -> new MDMEntryConstraint().path(path).kind(KindEnum.UNIQUE))
          .collect(toList());
    }

    if (ObjectUtils.isEmpty(descriptor.getConstraints())
        && !ObjectUtils.isEmpty(descriptor.getUniquePropertyPaths()) && log.isWarnEnabled()) {
      log.warn(
          "Constraints and uniquePropertyPaths defined in the [{}] entry descriptor. The uniqueness will be calculated by the constraints property only.",
          descriptor.getName());
    }

    return uniqueConstraints.stream().collect(toMap(Function.identity(), this::getConstraintEntry));
  }

  private String getUniqueMapName(List<String> path) {
    return descriptor.getName() + "-" + path.stream().collect(Collectors.joining("."));
  }

  @Override
  public StoredList getInactiveList() {
    return Boolean.TRUE.equals(descriptor.getInactiveMgmt())
        ? collectionApi.list(descriptor.getSchema(), getListName() + INACTIVE_POSTFIX)
        : null;
  }

  public final void refreshValueSetDefinition() {
    if (refreshValueSetDefinition) {
      refreshValueSetDefinition = false;
      String publishedListName = getListName();
      valueSetApi.save(definition.getName(),
          new ValueSetDefinitionData()
              .qualifiedName(descriptor.getName())
              .kind(ValueSetDefinitionKind.LIST)
              .storageSchema(descriptor.getSchema())
              .containerName(publishedListName)
              .objectDefinition(ObjectDefinition.uriOf(descriptor.getTypeQualifiedName())));
    }
  }

  @Override
  public MDMEntryDescriptor getDescriptor() {
    return descriptor;
  }

  @Override
  public List<BranchedObjectEntry> getBranchingList() {
    URI branchUri = getBranchUri();
    StoredList list = getList();
    return list.compareWithBranch(branchUri);
  }

  @Override
  public boolean hasBranch() {
    return getBranchUri() != null;
  }

  @Override
  public String getDisplayNameList() {
    LangString displayName = descriptor.getDisplayNameList();
    return displayName != null ? localeSettingApi.get(displayName) : descriptor.getName();
  }

  @Override
  public String getDisplayNameForm() {
    LangString displayName = descriptor.getDisplayNameForm();
    return displayName != null ? localeSettingApi.get(displayName) : descriptor.getName();
  }

  @Override
  public URI getApprover() {
    // TODO handle branching strategy
    MDMDefinitionState mdmDefinitionState = definitionStateCache.get(definition.getState());
    if (mdmDefinitionState != null) {
      MDMModification modification = mdmDefinitionState.getGlobalModification();
      return modification == null ? null : modification.getApprover();
    }
    return null;
  }

  private String[] getPrimaryId() {
    Map<MDMEntryConstraint, ConstraintEntry> uniqueMapsByconstraints = getUniqueMapsByconstraints();
    if (uniqueMapsByconstraints.isEmpty()) {
      return uriPath;
    }
    Optional<String[]> first = uniqueMapsByconstraints.keySet().stream()
        .filter(c -> c.getKind() == KindEnum.UNIQUE).map(c -> StringConstant.toArray(c.getPath()))
        .findFirst();
    return first.orElse(uriPath);
  }

  private static Set<String> excludedProperties = new HashSet<>(Arrays.asList(GenericValue.ICON,
      GenericValue.URI, GenericValue.INACTIVE, CREATED, UPDATED, MERGED));

  private static Set<String> excludedPropertiesFromEqual =
      new HashSet<>(Arrays.asList(GenericValue.ICON,
          GenericValue.URI, GenericValue.INACTIVE,
          createPath(CREATED, UserActivityLog.NAME), createPath(CREATED, UserActivityLog.ROLE),
          createPath(CREATED, UserActivityLog.TIMESTAMP),
          createPath(CREATED, UserActivityLog.USER_NAME),
          createPath(CREATED, UserActivityLog.USER_URI),
          createPath(UPDATED, UserActivityLog.NAME), createPath(UPDATED, UserActivityLog.ROLE),
          createPath(UPDATED, UserActivityLog.TIMESTAMP),
          createPath(UPDATED, UserActivityLog.USER_NAME),
          createPath(UPDATED, UserActivityLog.USER_URI),
          MERGED,
          createPath(MERGED, UserActivityLog.NAME), createPath(MERGED, UserActivityLog.ROLE),
          createPath(MERGED, UserActivityLog.TIMESTAMP),
          createPath(MERGED, UserActivityLog.USER_NAME),
          createPath(MERGED, UserActivityLog.USER_URI)));

  private static final String createPath(String... paths) {
    return Stream.of(paths).collect(joining(StringConstant.SLASH));
  }

  @Override
  public void updateAllIndices() {
    VectorCollectionDescriptor vectorCollectionDescriptor = descriptor.getVectorCollection();
    if (vectorCollectionDescriptor != null) {
      // Remove the whole collection and fill again with all the object.
      // String[] primaryId = getPrimaryId();
      VectorCollection vectorCollection = mdmApi.getVectorCollection(vectorCollectionDescriptor);
      if (vectorCollection == null) {
        throw new IllegalArgumentException(
            "A frissítéshez érvényes vektoradatbázis kapcsolat és érvényes beágyazó kapcsolat szükséges.");
      }
      if (vectorCollection.exists()) {
        vectorCollection.clear();
      } else {
        vectorCollection.ensureExist();
      }
      ObjectPropertyFormatter formatter = vectorCollectionDescriptor.getFormatter();
      getList().nodesFromCache().parallel().forEach(n -> {
        ObjectPropertyResolver resolver = objectApi.resolver();
        resolver.addContextObject("object", n);
        String formattedString = resolver.resolve(formatter);
        vectorCollection.add(formattedString, n.getObjectAsMap());
      });
    }
  }

  /**
   * This implementation searches exact matches in the MDM (Master Data Management) based on given
   * key value pairs.
   */
  @Override
  public ObjectLookup lookup() {
    return new ObjectLookupMDMEntry(objectApi, this);
  }

  private final class ObjectLookupMDMEntry extends ObjectLookup {

    private MDMEntryApi entryApi;

    ObjectLookupMDMEntry(ObjectApi objectApi) {
      super(objectApi);
    }

    ObjectLookupMDMEntry(ObjectApi objectApi, MDMEntryApi entryApi) {
      super(objectApi);
      this.entryApi = entryApi;
    }

    @Override
    public List<Map<String, Object>> transformData(ObjectLookupResult result) {
      List<Map<String, Object>> maps = result.getItems().stream()
          .map(ObjectLookupResultItem::getObjectAsMap)
          .collect(Collectors.toList());
      return maps;
    }

    @Override
    public ObjectLookupResult lookup(Object valueObject, ObjectLookupParameter parameter) {
      Map<String, Object> searchMap =
          switch (valueObject) {
            case ObjectNode node -> node.getObjectAsMap();
            case Map<?, ?> m -> (Map<String, Object>) m;
            default -> objectApi.toMapObject(valueObject);
          };

      ObjectLookupResult collect = getList().nodesFromCache()
          .filter(node -> {
            return searchMap.entrySet().stream()
                .anyMatch(e -> Objects.equals(e.getValue(), node.getValue(e.getKey())));
          })
          .map(node -> new ObjectLookupResultItem().objectAsMap(node.getObjectAsMap()))
          .collect(
              Collectors.collectingAndThen(Collectors.toList(), new ObjectLookupResult()::items));
      return collect;
    }

    @Override
    public ObjectLookupResult lookupWithMultipleKeys(Object valueObject,
        ObjectLookupParameter parameter) {
      Map<String, List<Object>> searchMap = (Map<String, List<Object>>) valueObject;

      List<ObjectNode> matchedNodes = getList().nodesFromCache()
          .filter(node -> {
            return searchMap.entrySet().stream()
                .anyMatch(e -> e.getValue().contains(node.getValue(e.getKey())));
          }).collect(Collectors.toList());

      ObjectLookupResult result = matchedNodes.stream()
          .map(node -> new ObjectLookupResultItem().objectAsMap(node.getObjectAsMap()))
          .collect(Collectors.collectingAndThen(Collectors.toList(),
              list -> new ObjectLookupResult().items(list)));
      return result;
    }

    @Override
    public Map<String, Object> findByUnique(ObjectPropertyValue value) {
      ObjectNode objectNode = findByUniqueInternal(value);
      if (objectNode == null) {
        return Collections.emptyMap();
      }
      return objectNode.getObjectAsMap();
    }

    @Override
    public <T> T findByUnique(ObjectPropertyValue value, Class<T> clazz) {
      ObjectNode objectNode = findByUniqueInternal(value);
      if (objectNode == null) {
        return null;
      }
      return objectNode.getObject(clazz);
    }

    protected ObjectNode findByUniqueInternal(ObjectPropertyValue value) {
      if (value == null || value.getPath() == null || value.getPath().isEmpty()) {
        return null;
      }

      ObjectNode node = null;
      if (entryApi.getDescriptor() != null
          && !ObjectUtils.isEmpty(entryApi.getDescriptor().getSelfContainedRefList())) {
        node = entryApi.getList().nodes()
            .filter(n -> value.getValue().equals(n.getValueAsString(value.getPath().get(0))))
            .findFirst().orElse(null);
      } else {
        // If we have the proper unique id
        StoredMap uniqueMap = getUniqueMap(StringConstant.toArray(value.getPath()));
        if (uniqueMap == null) {
          return null;
        }

        URI uri = uniqueMap.uris().get(value.getValue());
        if (uri != null) {
          node = objectApi.loadLatest(uri);
        }
      }
      return node;
    }

  }

  @Override
  public void setBranchedEntriesMerged(UserActivityLog merged) {
    getBranchingList().stream()
        .filter(e -> e.getBranchingState() != BranchingStateEnum.NOP
            && e.getBranchingState() != BranchingStateEnum.DELETED)
        .forEach(branchedEntry -> {
          ObjectNode branchedNode = objectApi.loadLatest(branchedEntry.getBranchUri());
          branchedNode.setValue(merged, Props.MERGED);
          objectApi.save(branchedNode);
        });
  }

  @Override
  public boolean setList(List<Object> requiredObjects) {
    return false;
  }

  @Override
  public List<URI> updateList(String schema, List<Object> objects) {
    Objects.requireNonNull(objects);
    ObjectDefinition<?> objectDefinition = getObjectDefinition();
    String finalSchema = schema != null ? schema : descriptor.getSchema();
    List<ObjectNode> nodesToSave = objects.stream().map(o -> {
      if (o instanceof ObjectNode) {
        return (ObjectNode) o;
      } else if (o instanceof Map) {
        return objectApi.create(finalSchema, objectDefinition, (Map<String, Object>) o);
      } else if (o != null) {
        return objectApi.create(finalSchema, o);
      }
      return null;
    }).filter(Objects::nonNull).collect(toList());
    return save(nodesToSave, MDMEntryOperation.UPDATE,
        (n1, n2) -> compareApi.isEquals(n1, n2, excludedPropertiesFromEqual));
  }

  @Override
  public ObjectDefinition<?> getObjectDefinition() {
    return objectApi.definition(descriptor.getTypeQualifiedName());
  }

}


