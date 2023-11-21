package org.smartbit4all.api.mdm;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredList;
import org.smartbit4all.api.collection.StoredList.OperationMode;
import org.smartbit4all.api.collection.StoredMap;
import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.mdm.bean.MDMBranchingStrategy;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMDefinitionState;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMModification;
import org.smartbit4all.api.object.BranchApi;
import org.smartbit4all.api.object.bean.BranchedObject;
import org.smartbit4all.api.object.bean.BranchedObjectEntry;
import org.smartbit4all.api.object.bean.BranchedObjectEntry.BranchingStateEnum;
import org.smartbit4all.api.object.bean.LangString;
import org.smartbit4all.api.object.bean.ObjectNodeState;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.value.ValueSetApi;
import org.smartbit4all.api.value.bean.ValueSetDefinitionData;
import org.smartbit4all.api.value.bean.ValueSetDefinitionKind;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectCacheEntry;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectNode;

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
public class MDMEntryApiImpl implements MDMEntryApi {

  /**
   * The postfix of the inactive list.
   */
  public static final String INACTIVE_POSTFIX = "-inactive";

  private static final Logger log = LoggerFactory.getLogger(MDMEntryApiImpl.class);

  public static final String[] uriPath = {"uri"};

  private final MasterDataManagementApi api;

  private final MDMDefinition definition;

  private final MDMEntryDescriptor descriptor;

  private ObjectApi objectApi;

  private CollectionApi collectionApi;

  private InvocationApi invocationApi;

  private BranchApi branchApi;

  private ValueSetApi valueSetApi;

  private LocaleSettingApi localeSettingApi;

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

  public MDMEntryApiImpl(MasterDataManagementApi api, MDMDefinition definition,
      MDMEntryDescriptor descriptor,
      ObjectApi objectApi, CollectionApi collectionApi, InvocationApi invocationApi,
      BranchApi branchApi, ValueSetApi valueSetApi, LocaleSettingApi localeSettingApi) {
    super();
    Objects.requireNonNull(descriptor, "Unable to initiate master data entry without descriptor.");
    this.api = api;
    this.definition = definition;

    definitionStateCache = objectApi.getCacheEntry(MDMDefinitionState.class);
    this.descriptor = descriptor;
    this.objectApi = objectApi;
    this.collectionApi = collectionApi;
    this.invocationApi = invocationApi;
    this.branchApi = branchApi;
    this.valueSetApi = valueSetApi;
    this.localeSettingApi = localeSettingApi;
  }

  @Override
  public List<URI> save(ObjectNode objectNode) {
    return save(Arrays.asList(objectNode));
  }

  @Override
  public List<URI> save(List<ObjectNode> objectNodes) {
    URI branchUri = getBranchUri();
    StoredList list = getList();
    list.branch(branchUri);
    List<URI> results = new ArrayList<>();

    list.update(l -> {
      Map<URI, URI> savedUriByOriginal = new HashMap<>();

      Map<List<String>, StoredMap> uniqueMaps = getUniqueMapsByProperties();
      Map<List<String>, String[]> uniquePaths = !uniqueMaps.isEmpty()
          ? getUniquePathArrayByPathList()
          : null;

      if (!uniqueMaps.isEmpty()) {
        checkIfUniquePropertyUsed(objectNodes, uniqueMaps, uniquePaths);
      }

      for (ObjectNode objectNode : objectNodes) {
        // Save the object node
        if (objectNode.getState() == ObjectNodeState.NEW) {
          objectNode
              .setValues(fireBeforeSaveNew(objectApi.definition(descriptor.getTypeQualifiedName()),
                  objectNode.getObjectAsMap(), descriptor));
        }
        objectApi.save(objectNode, branchUri);
        if (descriptor.getSelfContainedRefList() != null && objectNode.getDefinition()
            .getOutgoingReference(descriptor.getSelfContainedRefList()) != null) {
          results.addAll(
              getResultsUrisBySelfContainedList(objectNode, descriptor.getSelfContainedRefList(),
                  savedUriByOriginal)
                      .collect(toList()));
        } else {
          results.add(objectNode.getResultUri());
          if (objectNode.getObjectUri() != null) {
            savedUriByOriginal.put(objectNode.getObjectUri(), objectNode.getResultUri());
          }
        }


      }

      // save new unique property values to StoredMaps
      if (uniquePaths != null) {
        uniquePaths.entrySet().forEach(e -> {
          Map<String, URI> updateUniqueMap =
              objectNodes.stream().filter(n -> n.getValue(e.getValue()) != null)
                  .collect(
                      toMap(n -> n.getValue(e.getValue()).toString(), ObjectNode::getResultUri));

          StoredMap uniqueMap = uniqueMaps.get(e.getKey());
          uniqueMap.putAll(updateUniqueMap);
        });
      }

      Map<URI, URI> savedUrisByLatest =
          results.stream().collect(toMap(u -> objectApi.getLatestUri(u), u -> u));

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

  protected void checkIfUniquePropertyUsed(List<ObjectNode> objectNodes,
      Map<List<String>, StoredMap> uniqueMaps, Map<List<String>, String[]> uniquePaths) {
    for (ObjectNode nodeToSave : objectNodes) {
      descriptor.getUniquePropertyPaths().forEach(uniquePath -> {
        String[] path = uniquePaths.get(uniquePath);
        Object uniqueValue = nodeToSave.getValue(path);

        if (uniqueValue != null) {
          StoredMap storedMap = uniqueMaps.get(uniquePath);
          URI uriToUniqueValue = storedMap.uris().get(uniqueValue);

          boolean newNodesHasTheSameValue = objectNodes.stream()
              .anyMatch(n -> n.getValue(path).equals(uniqueValue) && n.getObjectUri() != null
                  && !n.getObjectUri().equals(nodeToSave.getObjectUri()));

          if ((uriToUniqueValue != null && !uriToUniqueValue.equals(nodeToSave.getObjectUri()))
              || (newNodesHasTheSameValue)) {
            throw new IllegalArgumentException(
                localeSettingApi.get("mdm", descriptor.getName(), "notunique",
                    String.join(".", path)));
          }
        }
      });
    }
  }

  private final Stream<URI> getResultsUrisBySelfContainedList(ObjectNode objectNode, String list,
      Map<URI, URI> originalByResultUri) {
    if (objectNode.getObjectUri() != null) {
      originalByResultUri.put(objectNode.getObjectUri(), objectNode.getResultUri());
    }
    return Stream.concat(Stream.of(objectNode.getResultUri()),
        objectNode.list(list).stream().filter(ref -> ref.isLoaded())
            .flatMap(
                ref -> getResultsUrisBySelfContainedList(ref.get(), list, originalByResultUri)));
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
    if (mdmDefinitionState != null) {
      if (branchingStrategy == MDMBranchingStrategy.ENTRY) {
        MDMModification modification = mdmDefinitionState.getModificationsForEntries()
            .get(descriptor.getName());
        return modification == null ? null : modification.getBranchUri();
      }
      if (branchingStrategy == MDMBranchingStrategy.GLOBAL) {
        MDMModification modification = mdmDefinitionState.getGlobalModification();
        return modification == null ? null : modification.getBranchUri();
      }
      // TODO handle MDMBranchingStrategy.GROUP
    }
    return null;
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
          } else if (boe.getBranchingState() == BranchingStateEnum.MODIFIED) {
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

  @Override
  public boolean remove(URI objectUri) {
    URI branchUri = getBranchUri();
    StoredList list = getList();
    list.branch(branchUri);
    boolean remove = list.remove(objectUri);
    if (!remove) {
      return false;
    } else {
      Map<List<String>, StoredMap> uniqueMaps = getUniqueMapsByProperties();
      if (!uniqueMaps.isEmpty()) {
        // remove unique values from StoredMaps
        Map<List<String>, String[]> uniquePaths = getUniquePathArrayByPathList();
        ObjectNode objectNode = objectApi.load(objectUri);
        uniquePaths.entrySet().forEach(e -> {
          Object uniqueValue = objectNode.getValue(e.getValue());
          if (uniqueValue != null) {
            StoredMap uniqueMap = uniqueMaps.get(e.getKey());
            uniqueMap.remove(uniqueValue.toString());
          }
        });
      }
    }
    BranchedObject removeNewBranchedObjects =
        branchApi.removeNewBranchedObjects(branchUri, objectUri);
    if (removeNewBranchedObjects != null) {
      return true;
    }
    StoredList inactiveList = getInactiveList();
    if (inactiveList != null) {
      inactiveList.branch(branchUri);
      inactiveList.operationMode(OperationMode.UNIQUE_ON_LATEST);
      inactiveList.add(objectUri);
    }
    return true;
  }

  protected Map<List<String>, String[]> getUniquePathArrayByPathList() {
    return descriptor.getUniquePropertyPaths().stream()
        .collect(toMap(Function.identity(), path -> path.stream().toArray(String[]::new)));
  }

  @Override
  public boolean restore(URI objectUri) {
    URI branchUri = getBranchUri();
    StoredList inactiveList = getInactiveList();
    if (inactiveList != null) {
      inactiveList.branch(branchUri);
      if (inactiveList.remove(objectUri)) {
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

  private Map<List<String>, StoredMap> getUniqueMapsByProperties() {
    if (descriptor.getUniquePropertyPaths() == null) {
      return Collections.emptyMap();
    } else {
      return descriptor
          .getUniquePropertyPaths().stream()
          .collect(toMap(Function.identity(), path -> collectionApi.map(descriptor.getSchema(),
              getUniqueMapName(path))));
    }
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
}
