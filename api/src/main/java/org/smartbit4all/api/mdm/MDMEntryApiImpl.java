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
import java.util.Optional;
import java.util.Set;
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
import org.smartbit4all.api.collection.bean.VectorCollectionDescriptor;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.ServiceConnection;
import org.smartbit4all.api.mdm.bean.MDMBranchingStrategy;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMDefinitionState;
import org.smartbit4all.api.mdm.bean.MDMEntryConstraint;
import org.smartbit4all.api.mdm.bean.MDMEntryConstraint.KindEnum;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMModification;
import org.smartbit4all.api.object.BranchApi;
import org.smartbit4all.api.object.bean.BranchedObject;
import org.smartbit4all.api.object.bean.BranchedObjectEntry;
import org.smartbit4all.api.object.bean.BranchedObjectEntry.BranchingStateEnum;
import org.smartbit4all.api.object.bean.LangString;
import org.smartbit4all.api.object.bean.ObjectNodeState;
import org.smartbit4all.api.object.bean.ObjectPropertyValue;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.value.ValueSetApi;
import org.smartbit4all.api.value.bean.ValueSetDefinitionData;
import org.smartbit4all.api.value.bean.ValueSetDefinitionKind;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectCacheEntry;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.utility.StringConstant;

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

      Map<MDMEntryConstraint, StoredMap> uniqueMapsByConstraints = getUniqueMapsByconstraints();

      if (!uniqueMapsByConstraints.isEmpty()) {
        checkIfUniquePropertyUsed(objectNodes, uniqueMapsByConstraints);
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
      if (uniqueMapsByConstraints != null) {
        uniqueMapsByConstraints.entrySet().forEach(e -> {
          String[] pathArr = e.getKey().getPath().stream().toArray(String[]::new);
          Map<String, URI> updateUniqueMap =
              objectNodes.stream().filter(n -> n.getValue(pathArr) != null)
                  .collect(
                      toMap(n -> n.getValue(pathArr).toString(), ObjectNode::getResultUri));

          StoredMap uniqueMap = uniqueMapsByConstraints.get(e.getKey());
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
      Map<MDMEntryConstraint, StoredMap> uniqueMapsByConstraints) {

    uniqueMapsByConstraints.entrySet().forEach(e -> {
      MDMEntryConstraint constraint = e.getKey();
      boolean caseInsensitive = constraint.getKind() == KindEnum.UNIQUECASEINSENSITIVE;
      Map<String, URI> uniqueMap = e.getValue().uris();
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

      for (ObjectNode nodeToSave : objectNodes) {
        String[] path = constraint.getPath().stream().toArray(String[]::new);
        Object uniqueValue = nodeToSave.getValue(path);

        if (uniqueValue != null) {
          String uniqueValueStr = caseInsensitive
              ? uniqueValue.toString().toLowerCase()
              : uniqueValue.toString();
          URI uriToUniqueValue = uniqueMap.get(uniqueValueStr);

          if ((uriToUniqueValue != null && !uriToUniqueValue.equals(nodeToSave.getObjectUri()))
              || uniqueValues.contains(uniqueValueStr)) {
            throw new IllegalArgumentException(
                localeSettingApi.get("mdm", descriptor.getName(), "notunique",
                    String.join(".", path)));
          } else {
            uniqueValues.add(uniqueValueStr);
          }
        }
      }
    });
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
      Map<MDMEntryConstraint, StoredMap> uniqueMaps = getUniqueMapsByconstraints();
      if (!uniqueMaps.isEmpty()) {
        // remove unique values from StoredMaps
        ObjectNode objectNode = objectApi.load(objectUri);
        uniqueMaps.entrySet().forEach(e -> {
          Object uniqueValue =
              objectNode.getValue(e.getKey().getPath().stream().toArray(String[]::new));
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

  @Override
  public StoredMap getUniqueMap(String... path) {
    if (path == null || path.length == 0) {
      return null;
    }
    return collectionApi.map(descriptor.getSchema(), getUniqueMapName(Arrays.asList(path)));
  }

  private Map<MDMEntryConstraint, StoredMap> getUniqueMapsByconstraints() {
    List<MDMEntryConstraint> uniqueConstraints = Collections.emptyList();

    if (descriptor.getConstraints() != null) {
      uniqueConstraints = descriptor.getConstraints().stream()
          .filter(
              c -> c.getKind() == KindEnum.UNIQUE || c.getKind() == KindEnum.UNIQUECASEINSENSITIVE)
          .collect(toList());
    } else if (descriptor.getUniquePropertyPaths() != null) {
      uniqueConstraints = descriptor.getUniquePropertyPaths().stream()
          .map(path -> new MDMEntryConstraint().path(path).kind(KindEnum.UNIQUE))
          .collect(toList());
    }

    if (descriptor.getConstraints() != null && descriptor.getUniquePropertyPaths() != null) {
      log.warn(
          "Constraints and uniquePropertyPaths defined in the [{}] entry descriptor. The uniqueness will be calculated by the constraints property only.",
          descriptor.getName());
    }

    return uniqueConstraints.stream().collect(toMap(Function.identity(), c -> {
      StoredMap uniqueMap = collectionApi.map(descriptor.getSchema(),
          getUniqueMapName(c.getPath()));
      uniqueMap.branch(getBranchUri());
      return uniqueMap;
    }));
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

  /**
   * Updates the denoted {@link VectorCollection} of the given entry if any.
   * 
   * @param toUpdate The list of object nodes to update in the {@link VectorCollection}.
   * 
   * 
   *        TODO Later on we need some update result with the success code and the problematic
   *        records.
   */
  private final void updateVectorCollection(List<String> idPath, List<ObjectNode> toUpdate) {
    VectorCollectionDescriptor vectorCollectionDescriptor = descriptor.getVectorCollection();
    if (vectorCollectionDescriptor == null) {
      return;
    }
    // Resolve the service connections defined in the MDM_DEFINITION_SYSTEM_INTEGRATION definition.
    VectorCollection vectorCollection = getVectorCollection(vectorCollectionDescriptor);
    for (ObjectNode objectNode : toUpdate) {
      vectorCollection.addObject(idPath, objectNode);
    }
  }

  private String[] getPrimaryId() {
    Map<MDMEntryConstraint, StoredMap> uniqueMapsByconstraints = getUniqueMapsByconstraints();
    if (uniqueMapsByconstraints.isEmpty()) {
      return uriPath;
    }
    Optional<String[]> first = uniqueMapsByconstraints.keySet().stream()
        .filter(c -> c.getKind() == KindEnum.UNIQUE).map(c -> StringConstant.toArray(c.getPath()))
        .findFirst();
    return first.orElse(uriPath);
  }

  @Override
  public void updateAllIndices(List<String> idPath) {
    VectorCollectionDescriptor vectorCollectionDescriptor = descriptor.getVectorCollection();
    if (vectorCollectionDescriptor != null) {
      // Remove the whole collection and fill again with all the object.
      // String[] primaryId = getPrimaryId();
      VectorCollection vectorCollection = getVectorCollection(vectorCollectionDescriptor);
      if (vectorCollection == null) {
        throw new IllegalArgumentException(
            "A frissítéshez érvényes vektoradatbázis kapcsolat és érvényes beágyazó kapcsolat szükséges.");
      }
      vectorCollection.clear();
      getList().nodesFromCache().forEach(n -> {
        vectorCollection.addObject(idPath, n.getObjectAsMap());
      });
    }
  }

  @Override
  public ObjectLookup lookup() {
    return new ObjectLookupMDMEntry(objectApi);
  }

  private final class ObjectLookupMDMEntry extends ObjectLookup {

    ObjectLookupMDMEntry(ObjectApi objectApi) {
      super(objectApi);
      // TODO Auto-generated constructor stub
    }

    @Override
    public ObjectLookupResult lookup(Object valueObject, ObjectLookupParameter parameter) {
      // TODO implement later on - If we have a map and a value inside that is
      return null;
    }

    @Override
    public Map<String, Object> findByUnique(ObjectPropertyValue value) {
      if (value == null || value.getPath() == null || value.getPath().isEmpty()) {
        return Collections.emptyMap();
      }
      // If we have the proper unique id
      StoredMap uniqueMap = getUniqueMap(StringConstant.toArray(value.getPath()));
      if (uniqueMap == null) {
        return Collections.emptyMap();
      }
      return objectApi.loadLatest(uniqueMap.uris().get(value.getValue())).getObjectAsMap();
    }

  }

  private final VectorCollection getVectorCollection(
      VectorCollectionDescriptor vectorCollectionDescriptor) {
    MDMEntryApi vectorDBEntryApi =
        api.getApi(MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION,
            PlatformApiConfig.VECTOR_DB_CONNECTIONS);
    ServiceConnection vectorDBConnection =
        objectApi.asType(ServiceConnection.class,
            vectorDBEntryApi.lookup().findByUnique(new ObjectPropertyValue()
                .addPathItem(ServiceConnection.NAME)
                .value(vectorCollectionDescriptor.getVectorDBConnection())));
    MDMEntryApi embeddingEntryApi =
        api.getApi(MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION,
            PlatformApiConfig.EMBEDDING_CONNECTIONS);
    ServiceConnection embeddingConnection = objectApi.asType(ServiceConnection.class,
        embeddingEntryApi.lookup().findByUnique(new ObjectPropertyValue()
            .addPathItem(ServiceConnection.NAME)
            .value(vectorCollectionDescriptor.getEmbeddingConnection())));
    if (vectorDBConnection == null || embeddingConnection == null) {
      return null;
    }
    return collectionApi.vectorCollection(vectorCollectionDescriptor.getVectorCollectionName(),
        vectorDBConnection, embeddingConnection);
  }

}
