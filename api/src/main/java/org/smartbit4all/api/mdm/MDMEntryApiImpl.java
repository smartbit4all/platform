package org.smartbit4all.api.mdm;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredList;
import org.smartbit4all.api.collection.StoredMap;
import org.smartbit4all.api.collection.StoredReference;
import org.smartbit4all.api.object.BranchApi;
import org.smartbit4all.api.object.bean.BranchEntry;
import org.smartbit4all.api.object.bean.BranchOperation;
import org.smartbit4all.api.object.bean.MasterDataManagementEntry;
import org.smartbit4all.api.object.bean.MasterDataManagementInfo;
import org.smartbit4all.api.value.ValueSetApi;
import org.smartbit4all.api.value.bean.ValueSetDefinitionData;
import org.smartbit4all.api.value.bean.ValueSetDefinitionKind;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.utility.StringConstant;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @param <O>
 */
public class MDMEntryApiImpl<O extends Object> implements MDMEntryApi<O> {

  private static final String[] uriPath = {"uri"};

  /**
   * The storage schema of the domain object and the collections.
   */
  private String schema;

  /**
   * The name of the given api object.
   */
  private String name;

  /**
   * The class of the object managed by the given entry.
   */
  private Class<O> clazz;

  /**
   * The path of the unique identifier. If there is no unique id then the identity is based on the
   * URI. The latest URI value will be the key in this situation.
   */
  private String[] uniqueIdPath;

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  private BranchApi branchApi;

  @Autowired
  private ValueSetApi valueSetApi;

  public enum BranchStrategy {
    GLOBAL, LOCAL
  }

  /**
   * If it is true that the given api is using a local branch for the modification process. It means
   * that the publish will be local also.
   */
  private BranchStrategy branchStrategy = BranchStrategy.LOCAL;

  /**
   * The given master data entry is managed inside an information entry. It is usually global for
   * the application but it is set by the {@link MasterDataManagementApi} when the given entry api
   * is registered.
   */
  private MasterDataManagementInfo info;

  /**
   * If it is set then the result of the publishing is just the map but also a list. It brings in
   * the backward compatibility to be able to use this even on older solutions where the only
   * collection was a list.
   */
  private String publishedListName;

  /**
   * If the given MDM ap is managing a list of published values then this list forms a value set
   * definition by default. If it is true then the next access will try to refresh the value set
   * entry belongs to this api. The schema is the schema for the value set api but the storage
   * schema will be used to avoid name collisions.
   */
  private boolean refreshValueSetDefinition = true;

  /**
   * If the given object has implicit URI then the storage won't generate a new uri for the new
   * object. In this case we can set a {@link Function} to generate the URI from the object.
   */
  private Function<O, URI> uriConstructor;

  /**
   * These are the before save event handlers.
   * 
   * TODO create an event handler class to be able to identify them and modify their order.
   */
  private final List<UnaryOperator<O>> beforeSaveEventHandlers = new ArrayList<>();

  public MDMEntryApiImpl(Class<O> clazz) {
    super();
    this.clazz = clazz;
    this.name = getApiName(clazz);
  }

  /**
   * Constructs the name of the api from the Java class.
   * 
   * @param clazz
   * @return
   */
  public static final String getApiName(Class<?> clazz) {
    return clazz.getName().replace(StringConstant.DOT, StringConstant.UNDERLINE);
  }

  @Override
  public Class<O> getClazz() {
    return clazz;
  }

  @Override
  public String getId(O object) {
    if (object == null) {
      return null;
    }
    if (uniqueIdPath != null) {
      return objectApi.getValueFromObject(String.class, object, uniqueIdPath);
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
    if (uniqueIdPath != null) {
      return objectNode.getValueAsString(uniqueIdPath);
    } else {
      URI uri = objectApi.getLatestUri(objectNode.getValue(URI.class, uriPath));
      return uri.toString();
    }
  }

  public void setUniqueIdPath(String... path) {
    uniqueIdPath = path;
  }

  public MDMEntryApiImpl<O> uniqueIdPath(String... path) {
    uniqueIdPath = path;
    return this;
  }

  @Override
  public Map<String, URI> getPublishedMap() {
    return collectionApi.map(schema, getPublishedMapName()).uris();
  }

  @Override
  public Map<String, O> getPublishedObjects() {
    return getPublishedMap().entrySet().stream()
        .collect(toMap(Entry::getKey, e -> objectApi.read(e.getValue(), clazz)));
  }

  @Override
  public URI saveAsNewPublished(O object) {
    if (uriConstructor != null) {
      ObjectDefinition<O> definition = objectApi.definition(clazz);
      if (definition.isExplicitUri()) {
        definition.setUri(object, uriConstructor.apply(object));
      }
    }
    URI uri = objectApi.saveAsNew(schema, fireBeforeSave(object));
    StoredMap storedMap = collectionApi.map(schema, getPublishedMapName());
    storedMap.put(getId(object), uri);
    updatePublishedList(storedMap.uris());
    return uri;
  }

  @Override
  public URI saveAsDraft(O object) {
    if (object == null) {
      return null;
    }
    // TODO Lock the entry to ensure mutual exclusion.
    @SuppressWarnings("unchecked")
    ObjectDefinition<O> objectDefinition =
        (ObjectDefinition<O>) objectApi.definition(object.getClass());
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
    O objectToSave = fireBeforeSave(object);
    if (publishedUri != null) {
      Map<URI, Supplier<URI>> map = Stream.of(publishedUri).collect(toMap(u -> u, u -> {
        return () -> objectApi.saveAsNew(schema, objectToSave);
      }));
      Map<URI, BranchOperation> branchedObjects =
          branchApi.initBranchedObjects(branchEntry.getUri(), map);
      return branchedObjects.get(publishedUri).getTargetUri();
    } else {
      // Construct a new object on the branch
      URI newDraftUri = objectApi.saveAsNew(schema, objectToSave);
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
    if (branchStrategy == BranchStrategy.LOCAL) {
      StoredReference<MasterDataManagementInfo> reference = collectionApi
          .reference(MasterDataManagementApiImpl.SCHEMA,
              MasterDataManagementApiImpl.GLOBAL_INFO, MasterDataManagementInfo.class);
      reference.update(info -> {
        MasterDataManagementEntry entry = info.getObjects().get(getName());
        entry.branch(null);
        return info;
      });
    }
  }

  @Override
  public void publishCurrentModifications() {
    if (branchStrategy != BranchStrategy.LOCAL) {
      throw new IllegalStateException("Unable to publish the modifications of " + getName()
          + " master data if the branch startegy is not local.");
    }
    URI branchEntryUri = getCurrentBranchEntryUri();
    if (branchEntryUri != null) {
      StoredReference<MasterDataManagementInfo> reference = getMasterInfoReference();
      reference.update(info -> {
        MasterDataManagementEntry entry = info.getObjects().get(getName());
        BranchEntry branchEntry =
            objectApi.read(objectApi.getLatestUri(branchEntryUri), BranchEntry.class);
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
          entry.branch(null);
        }
        return info;
      });
    }
  }

  /**
   * Run all the event handlers on the given object.
   * 
   * @param object
   * @return
   */
  private final O fireBeforeSave(O object) {
    O result = object;
    for (UnaryOperator<O> handler : beforeSaveEventHandlers) {
      result = handler.apply(result);
    }
    return result;
  }

  private StoredReference<MasterDataManagementInfo> getMasterInfoReference() {
    return collectionApi
        .reference(MasterDataManagementApiImpl.SCHEMA,
            MasterDataManagementApiImpl.GLOBAL_INFO, MasterDataManagementInfo.class);
  }

  private final void replaceInPublished(Map<URI, URI> toPublishByLatest) {
    if (toPublishByLatest == null || toPublishByLatest.isEmpty()) {
      return;
    }
    collectionApi.map(schema, getPublishedMapName()).update(map -> {
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
    collectionApi.map(schema, getPublishedMapName()).update(map -> {
      map.putAll(toPublish);
      updatePublishedList(map);
      return map;
    });
  }

  @Override
  public List<MDMDraftObjectURIEntry> getDraftUris() {
    URI branchEntryUri = getCurrentBranchEntryUri();
    if (branchEntryUri != null) {
      BranchEntry branchEntry = objectApi.read(branchEntryUri, BranchEntry.class);
      return Stream.concat(branchEntry.getBranchedObjects().entrySet().stream()
          .map(e -> new MDMDraftObjectURIEntry(URI.create(e.getKey()),
              e.getValue().getBranchedObjectLatestUri())),
          branchEntry.getNewObjects().values().stream().map(bo -> new MDMDraftObjectURIEntry(null,
              bo.getBranchedObjectLatestUri())))
          .collect(toList());
    }
    return Collections.emptyList();
  }

  @Override
  public List<MDMObjectEntry<O>> getDraftObjects() {
    return getDraftUris().stream()
        .map(draft -> new MDMObjectEntry<>(
            draft.getSourceURI() != null ? objectApi.read(draft.getSourceURI(), clazz) : null,
            objectApi.read(draft.getDraftURI(), clazz)))
        .collect(toList());
  }

  private BranchEntry getOrCreateBranchEntry() {
    // We check if we have the proper branch already or now we have to create a new one.
    if (branchStrategy == BranchStrategy.LOCAL) {
      MasterDataManagementEntry managementEntry = info.getObjects().get(getName());
      if (managementEntry != null) {
        if (managementEntry.getBranch() == null) {
          BranchEntry branch = branchApi.makeBranch(getName());
          StoredReference<MasterDataManagementInfo> reference = collectionApi
              .reference(MasterDataManagementApiImpl.SCHEMA,
                  MasterDataManagementApiImpl.GLOBAL_INFO, MasterDataManagementInfo.class);
          reference.update(info -> {
            MasterDataManagementEntry entry = info.getObjects().get(getName());
            entry.branch(branch.getUri());
            return info;
          });
          info = reference.get();
          return branch;
        }
        return objectApi.read(managementEntry.getBranch(), BranchEntry.class);
      }
    }
    return null;
  }

  private URI getCurrentBranchEntryUri() {
    // We check if we have the proper branch already or now we have to create a new one.
    if (branchStrategy == BranchStrategy.LOCAL) {
      MasterDataManagementEntry managementEntry = info.getObjects().get(getName());
      if (managementEntry != null) {
        return objectApi.getLatestUri(managementEntry.getBranch());
      }
    }
    return null;
  }

  @Override
  public List<MDMObjectEntry<O>> getPublishedAndDraftObjects() {
    List<MDMObjectEntry<O>> results = getDraftObjects();
    // In the draft objects we have all the published objects that have draft version. And also the
    // brand new object are included.
    Set<URI> publishedWithDraft = results.stream().filter(oe -> oe.getPublished() != null)
        .map(oe -> {
          return objectApi.getLatestUri(objectApi.definition(clazz).getUri(oe.getPublished()));
        }).collect(toSet());
    Map<String, URI> publishedMap =
        getPublishedMap().entrySet().stream()
            .filter(e -> !publishedWithDraft.contains(objectApi.getLatestUri(e.getValue())))
            .collect(toMap(Entry::getKey, Entry::getValue));

    List<MDMObjectEntry<O>> publishedList =
        publishedMap.values().stream().map(u -> objectApi.read(u, clazz))
            .map(o -> new MDMObjectEntry<>(o, null)).collect(toList());

    // Add the remaining draft object. The new objects missing from the published.
    results.addAll(publishedList);
    return results;
  }

  @Override
  public String getName() {
    return name;
  }

  private String getPublishedMapName() {
    return name + StringConstant.HYPHEN + "published";
  }

  public final String getSchema() {
    return schema;
  }

  public final void setSchema(String schema) {
    this.schema = schema;
  }

  public final MDMEntryApiImpl<O> schema(String schema) {
    this.schema = schema;
    return this;
  }

  public final MasterDataManagementInfo getInfo() {
    return info;
  }

  final void setInfo(MasterDataManagementInfo info) {
    this.info = info;
  }

  @Override
  public final BranchStrategy getBranchStrategy() {
    return branchStrategy;
  }

  final void setBranchStrategy(BranchStrategy branchStrategy) {
    this.branchStrategy = branchStrategy;
  }

  private final void updatePublishedList(Map<String, URI> uris) {
    if (publishedListName != null) {
      collectionApi.list(schema, publishedListName).update(l -> {
        return uris.values().stream().collect(toList());
      });
    }
  }

  public final String getPublishedListName() {
    return publishedListName;
  }

  public final void setPublishedListName(String publishedListName) {
    this.publishedListName = publishedListName;
  }

  public final MDMEntryApiImpl<O> publishedListName(String publishedListName) {
    this.publishedListName = publishedListName;
    return this;
  }

  public final Function<O, URI> getUriConstructor() {
    return uriConstructor;
  }

  public final void setUriConstructor(Function<O, URI> uriConstructor) {
    this.uriConstructor = uriConstructor;
  }

  public final MDMEntryApiImpl<O> uriConstructor(Function<O, URI> uriConstructor) {
    this.uriConstructor = uriConstructor;
    return this;
  }

  public final MDMEntryApiImpl<O> addBeforeSaveHandler(UnaryOperator<O> beforeSave) {
    this.beforeSaveEventHandlers.add(beforeSave);
    return this;
  }

  @Override
  public StoredList getPublishedList() {
    if (publishedListName != null) {
      return collectionApi.list(schema, publishedListName);
    }
    return null;
  }

  public final void refreshValueSetDefinition() {
    if (refreshValueSetDefinition) {
      refreshValueSetDefinition = false;
      if (publishedListName != null) {
        ObjectDefinition<O> definition = objectApi.definition(clazz);
        valueSetApi.save(schema, new ValueSetDefinitionData().kind(ValueSetDefinitionKind.LIST)
            .storageSchema(schema).containerName(publishedListName)
            .objectDefinition(ObjectDefinition.uriOf(definition.getQualifiedName()))
            .qualifiedName(publishedListName));
      } else {
        ObjectDefinition<O> definition = objectApi.definition(clazz);
        valueSetApi.save(schema, new ValueSetDefinitionData().kind(ValueSetDefinitionKind.MAP)
            .storageSchema(schema).containerName(getPublishedMapName())
            .objectDefinition(ObjectDefinition.uriOf(definition.getQualifiedName()))
            .qualifiedName(getPublishedMapName()));
      }
    }
  }

}
