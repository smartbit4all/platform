package org.smartbit4all.api.mdm;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredList;
import org.smartbit4all.api.collection.StoredMap;
import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor.BranchStrategyEnum;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptorState;
import org.smartbit4all.api.object.BranchApi;
import org.smartbit4all.api.object.bean.BranchEntry;
import org.smartbit4all.api.object.bean.BranchOperation;
import org.smartbit4all.api.value.ValueSetApi;
import org.smartbit4all.api.value.bean.ValueSetDefinitionData;
import org.smartbit4all.api.value.bean.ValueSetDefinitionKind;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.utility.StringConstant;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

/**
 * The base implementation of the master data management api. The basic feature is that if we
 * configure an api like this into the spring context than it will be domain api for the given
 * object.
 *
 * @author Peter Boros
 *
 */
public class MDMEntryApiImpl implements MDMEntryApi {

  private static final String[] uriPath = {"uri"};

  private final MasterDataManagementApi api;

  private final MDMEntryDescriptor descriptor;

  private ObjectApi objectApi;

  private CollectionApi collectionApi;

  private InvocationApi invocationApi;

  private BranchApi branchApi;

  private ValueSetApi valueSetApi;

  /**
   * If the given MDM ap is managing a list of published values then this list forms a value set
   * definition by default. If it is true then the next access will try to refresh the value set
   * entry belongs to this api. The schema is the schema for the value set api but the storage
   * schema will be used to avoid name collisions.
   */
  private boolean refreshValueSetDefinition = true;

  public MDMEntryApiImpl(MasterDataManagementApi api, MDMEntryDescriptor descriptor,
      ObjectApi objectApi, CollectionApi collectionApi, InvocationApi invocationApi,
      BranchApi branchApi, ValueSetApi valueSetApi) {
    super();
    Objects.requireNonNull(descriptor, "Unable to initiate master data entry without descriptor.");
    this.api = api;
    this.descriptor = descriptor;
    this.objectApi = objectApi;
    this.collectionApi = collectionApi;
    this.invocationApi = invocationApi;
    this.branchApi = branchApi;
    this.valueSetApi = valueSetApi;
  }

  @Override
  public String getId(Object object) {
    if (object == null) {
      return null;
    }
    if (descriptor.getUniqueIdentifierPath() != null) {
      return objectApi.getValueFromObject(String.class, object,
          descriptor.getUniqueIdentifierPath().toArray(StringConstant.EMPTY_ARRAY));
    } else {
      URI uri = objectApi.getLatestUri(objectApi.getValueFromObject(URI.class, object, uriPath));
      if (uri != null) {
        return uri.toString();
      }
    }
    return null;
  }

  public String getIdFromNode(ObjectNode objectNode) {
    if (objectNode == null) {
      return null;
    }
    if (descriptor.getUniqueIdentifierPath() != null) {
      return objectNode.getValueAsString(
          descriptor.getUniqueIdentifierPath().toArray(StringConstant.EMPTY_ARRAY));
    } else {
      URI uri = objectApi.getLatestUri(objectNode.getValue(URI.class, uriPath));
      return uri.toString();
    }
  }

  @Override
  public Map<String, URI> getPublishedMap() {
    return collectionApi.map(descriptor.getSchema(), getPublishedMapName()).uris();
  }

  @Override
  public URI saveAsNewPublished(Object object) {
    if (object == null) {
      return null;
    }
    if (descriptor.getUriConstructor() != null) {
      ObjectDefinition<?> definition = objectApi.definition(object.getClass());
      if (definition.isExplicitUri()) {
        // uriConstructor.apply(object)
        try {
          definition.setUriToObj(object,
              (URI) invocationApi
                  .invoke(invocationApi.prepareByPosition(descriptor.getUriConstructor(), object))
                  .getValue());
        } catch (ApiNotFoundException e) {
          throw new IllegalArgumentException(
              "Unable to set the explicit uri for the " + object, e);
        }
      }
    }
    Object objectToSave = fireBeforeSave(object);
    URI uri = objectApi.saveAsNew(descriptor.getSchema(), objectToSave);
    StoredMap storedMap = collectionApi.map(descriptor.getSchema(), getPublishedMapName());
    storedMap.put(getId(objectToSave), uri);
    updatePublishedList(storedMap.uris());
    return uri;
  }

  @Override
  public URI saveAsDraft(Object object) {
    if (object == null) {
      return null;
    }
    // TODO Lock the entry to ensure mutual exclusion.
    @SuppressWarnings("unchecked")
    ObjectDefinition<?> objectDefinition =
        objectApi.definition(object.getClass());
    if (!objectDefinition.hasUri()) {
      throw new IllegalArgumentException(
          "Unable to use master data management api based on stored map if the " + object.getClass()
              + " doesn't have URI property.");
    }
    // We check if we have the proper branch already or now we have to create a new one.
    BranchEntry branchEntry = getOrCreateBranchEntry();
    if (branchEntry == null) {
      throw new IllegalStateException(
          "Unable to save draft because the branch can not be identify.");
    }
    URI publishedUri = getPublishedMap().get(getId(object));
    Object objectToSave = fireBeforeSave(object);
    if (publishedUri != null) {
      Map<URI, Supplier<URI>> map = Stream.of(publishedUri).collect(toMap(u -> u, u -> {
        return () -> objectApi.saveAsNew(descriptor.getSchema(), objectToSave);
      }));
      Map<URI, BranchOperation> branchedObjects =
          branchApi.initBranchedObjects(branchEntry.getUri(), map);
      return branchedObjects.get(publishedUri).getTargetUri();
    } else {
      // Construct a new object on the branch
      URI newDraftUri = objectApi.saveAsNew(descriptor.getSchema(), objectToSave);
      branchApi.addNewBranchedObjects(branchEntry.getUri(),
          Arrays.asList(newDraftUri));
      return newDraftUri;
    }
  }

  @Override
  public void cancelDraft(URI draftUri) {
    if (draftUri == null) {
      return;
    }
    // Simply remove the draft from the branch.
    URI branchEntryUri = getCurrentBranchEntryUri();
    if (branchEntryUri != null) {
      URI latestUri = objectApi.getLatestUri(draftUri);
      // Remove the draft from the modified or new objects of the branch.
      objectApi.loadLatest(branchEntryUri).modify(BranchEntry.class, b -> {
        b.setBranchedObjects(b.getBranchedObjects().entrySet().stream()
            .filter(e -> e.getValue().getBranchedObjectLatestUri().equals(latestUri))
            .collect(toMap(Entry::getKey, Entry::getValue)));
        b.getNewObjects().remove(latestUri.toString());
        return b;
      });
    }
  }

  @Override
  public void discardCurrentModifications() {
    // Remove the branch from the entry.
    if (descriptor.getBranchStrategy() == BranchStrategyEnum.ENTRYLEVEL) {
      Lock lockState = objectApi.getLock(descriptor.getState());
      lockState.lock();
      try {
        ObjectNode objectNode = objectApi.loadLatest(descriptor.getState());
        objectNode.modify(MDMEntryDescriptorState.class, s -> s.branch(null));
        objectApi.save(objectNode);
      } finally {
        lockState.unlock();
      }
    }
  }

  @Override
  public void publishCurrentModifications() {
    if (descriptor.getBranchStrategy() != BranchStrategyEnum.ENTRYLEVEL) {
      throw new IllegalStateException("Unable to publish the modifications of " + getName()
          + " master data if the branch strategy is not local.");
    }
    Lock lockState = objectApi.getLock(descriptor.getState());
    lockState.lock();
    try {
      ObjectNode stateNode = objectApi.loadLatest(descriptor.getState());
      URI branchUri = stateNode.getValue(URI.class, MDMEntryDescriptorState.BRANCH);
      if (branchUri != null) {
        BranchEntry branchEntry =
            objectApi.read(objectApi.getLatestUri(branchUri), BranchEntry.class);
        if (branchEntry != null) {
          // Check the uniqueness of the objects.
          Map<URI, ObjectNode> toSaveBySourceUri =
              branchEntry.getBranchedObjects().entrySet().stream().collect(
                  toMap(e -> URI.create(e.getKey()),
                      e -> objectApi.load(e.getValue().getBranchedObjectLatestUri())));
          // Now set the new URIs in the published map and list.
          replaceInPublished(
              toSaveBySourceUri.entrySet().stream().collect(toMap(Entry::getKey, e -> {
                ObjectNode objectNode = objectApi.loadLatest(e.getKey());
                objectNode.setValues(e.getValue().getObjectAsMap());
                return objectApi.save(objectNode);
              })));
          // The new objects coming from the draft can be registered directly.
          addNewToPublished(branchEntry.getNewObjects().values().stream()
              .map(bo -> objectApi.load(bo.getBranchedObjectLatestUri()))
              .collect(toMap(this::getIdFromNode, ObjectNode::getObjectUri)));
          stateNode.modify(MDMEntryDescriptorState.class, s -> s.branch(null));
          objectApi.save(stateNode);
        }

      }
    } finally {
      lockState.unlock();
    }
  }

  /**
   * Run all the event handlers on the given object.
   *
   * @param object
   * @return
   */
  private final Object fireBeforeSave(Object object) {
    if (descriptor.getEventHandlersBeforeSave() == null) {
      return object;
    }
    Object result = object;
    for (InvocationRequest handler : descriptor.getEventHandlersBeforeSave()) {
      try {
        result = invocationApi.invoke(invocationApi.prepareByPosition(handler, result)).getValue();
      } catch (ApiNotFoundException e) {
        throw new IllegalArgumentException("Unable to run the " + handler
            + " before save event handler on the " + descriptor + " master data entry.", e);
      }
    }
    return result;
  }

  private final void replaceInPublished(Map<URI, URI> toPublishByLatest) {
    if (toPublishByLatest == null || toPublishByLatest.isEmpty()) {
      return;
    }
    collectionApi.map(descriptor.getSchema(), getPublishedMapName()).update(map -> {
      map.putAll(map.entrySet().stream()
          .filter(e -> toPublishByLatest.containsKey(objectApi.getLatestUri(e.getValue())))
          .collect(toMap(Entry::getKey,
              e -> toPublishByLatest.get(objectApi.getLatestUri(e.getValue())))));
      updatePublishedList(map);
      return map;
    });
  }

  private final void addNewToPublished(Map<String, URI> toPublish) {
    if (toPublish == null || toPublish.isEmpty()) {
      return;
    }
    collectionApi.map(descriptor.getSchema(), getPublishedMapName()).update(map -> {
      map.putAll(toPublish);
      updatePublishedList(map);
      return map;
    });
  }

  @Override
  public List<MDMEntry> getDraftEntries() {
    URI branchEntryUri = getCurrentBranchEntryUri();
    if (branchEntryUri != null) {
      BranchEntry branchEntry = objectApi.read(branchEntryUri, BranchEntry.class);
      return Stream.concat(branchEntry.getBranchedObjects().entrySet().stream()
          .map(e -> new MDMEntry(URI.create(e.getKey()),
              e.getValue().getBranchedObjectLatestUri())),
          branchEntry.getNewObjects().values().stream().map(bo -> new MDMEntry(null,
              bo.getBranchedObjectLatestUri())))
          .collect(toList());
    }
    return Collections.emptyList();
  }

  private BranchEntry getOrCreateBranchEntry() {
    // We check if we have the proper branch already or now we have to create a new one.
    if (descriptor.getBranchStrategy() == BranchStrategyEnum.ENTRYLEVEL) {

      Lock lockState = objectApi.getLock(descriptor.getState());
      lockState.lock();
      try {
        ObjectNode objectNode = objectApi.loadLatest(descriptor.getState());
        URI branchUri = objectNode.getValue(URI.class, MDMEntryDescriptorState.BRANCH);
        if (branchUri == null) {
          BranchEntry branch = branchApi.makeBranch(getName());
          objectNode.modify(MDMEntryDescriptorState.class,
              s -> s.branch(objectApi.getLatestUri(branch.getUri())));
          objectApi.save(objectNode);
          return branch;
        }
        return objectApi.read(branchUri, BranchEntry.class);
      } finally {
        lockState.unlock();
      }
    }
    return null;
  }

  private URI getCurrentBranchEntryUri() {
    // We check if we have the proper branch already or now we have to create a new one.
    if (descriptor.getBranchStrategy() == BranchStrategyEnum.ENTRYLEVEL) {
      return objectApi.loadLatest(descriptor.getState()).getValue(URI.class,
          MDMEntryDescriptorState.BRANCH);
    }
    return null;
  }

  @Override
  public List<MDMEntry> getAllEntries() {
    List<MDMEntry> results = getDraftEntries();
    // In the draft objects we have all the published objects that have draft version. And also the
    // brand new object are included.
    Set<URI> publishedWithDraft = results.stream()
        .map(MDMEntry::getPublished)
        .filter(Objects::nonNull)
        .collect(toSet());
    Map<String, URI> publishedMap =
        getPublishedMap().entrySet().stream()
            .filter(e -> !publishedWithDraft.contains(objectApi.getLatestUri(e.getValue())))
            .collect(toMap(Entry::getKey, Entry::getValue));

    List<MDMEntry> publishedList =
        publishedMap.values().stream()
            .map(o -> new MDMEntry(o, null))
            .collect(toList());

    // Add the remaining draft object. The new objects missing from the published.
    results.addAll(publishedList);
    return results;
  }

  @Override
  public String getName() {
    return descriptor.getName();
  }

  private String getPublishedMapName() {
    return descriptor.getName() + "Map";
  }

  public final String getPublishedListName() {
    return descriptor.getName() + "List";
  }

  private final void updatePublishedList(Map<String, URI> uris) {
    if (Boolean.TRUE.equals(descriptor.getPublishInList())) {
      collectionApi.list(descriptor.getSchema(), getPublishedListName()).update(l -> {
        return uris.values().stream().collect(toList());
      });
    }
  }

  @Override
  public StoredList getPublishedList() {
    if (Boolean.TRUE.equals(descriptor.getPublishInList())) {
      return collectionApi.list(descriptor.getSchema(), getPublishedListName());
    }
    return null;
  }

  public final void refreshValueSetDefinition() {
    if (refreshValueSetDefinition) {
      refreshValueSetDefinition = false;
      ObjectDefinition<?> definition = objectApi.definition(descriptor.getTypeQualifiedName());
      if (Boolean.TRUE.equals(descriptor.getPublishInList())) {
        String publishedListName = getPublishedListName();
        valueSetApi.save(descriptor.getSchema(),
            new ValueSetDefinitionData().kind(ValueSetDefinitionKind.LIST)
                .storageSchema(descriptor.getSchema()).containerName(publishedListName)
                .objectDefinition(ObjectDefinition.uriOf(descriptor.getTypeQualifiedName()))
                .qualifiedName(publishedListName));
      } else {
        String publishedMapName = getPublishedMapName();
        valueSetApi.save(descriptor.getSchema(),
            new ValueSetDefinitionData().kind(ValueSetDefinitionKind.MAP)
                .storageSchema(descriptor.getSchema()).containerName(publishedMapName)
                .objectDefinition(ObjectDefinition.uriOf(descriptor.getTypeQualifiedName()))
                .qualifiedName(publishedMapName));
      }
    }
  }

  @Override
  public MDMEntryDescriptor getDescriptor() {
    return descriptor;
  }

  @Override
  public Map<String, ObjectNode> getPublishedObjects() {
    return getPublishedMap().entrySet().stream()
        .collect(toMap(Entry::getKey, e -> objectApi.load(e.getValue())));
  }

  @Override
  public BranchStrategyEnum getBranchStrategy() {
    return descriptor.getBranchStrategy();
  }

}
