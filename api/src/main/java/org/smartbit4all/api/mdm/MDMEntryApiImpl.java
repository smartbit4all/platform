package org.smartbit4all.api.mdm;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredMap;
import org.smartbit4all.api.collection.StoredReference;
import org.smartbit4all.api.object.BranchApi;
import org.smartbit4all.api.object.bean.BranchEntry;
import org.smartbit4all.api.object.bean.BranchOperation;
import org.smartbit4all.api.object.bean.MasterDataManagementEntry;
import org.smartbit4all.api.object.bean.MasterDataManagementInfo;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.utility.StringConstant;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * The base implementation of the master data management api.
 * 
 * @author Peter Boros
 *
 * @param <O>
 */
public class MDMEntryApiImpl<O extends Object> implements MDMEntryApi<O> {

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
   * The path of the unique identifier.
   */
  private String[] uniqueIdPath;

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  private BranchApi branchApi;

  /**
   * If it is true that the given api is using a local branch for the modification process. It means
   * that the publish will be local also.
   */
  private boolean useLocalBranch = true;

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

  public MDMEntryApiImpl(Class<O> clazz) {
    super();
    this.clazz = clazz;
    this.name = clazz.getSimpleName();
  }

  @Override
  public Class<O> clazz() {
    return clazz;
  }

  @Override
  public String getId(O object) {
    if (object == null) {
      return null;
    }
    if (uniqueIdPath != null) {
      return objectApi.getValueFromObject(String.class, object, uniqueIdPath);
    }
    return null;
  }

  public void setUniqueIdPath(String... path) {
    uniqueIdPath = path;
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
    URI uri = objectApi.saveAsNew(schema, object);
    StoredMap storedMap = collectionApi.map(schema, getPublishedMapName());
    storedMap.put(getId(object), uri);
    Map<String, URI> uris = storedMap.uris();
    if (publishedListName != null) {
      collectionApi.list(schema, publishedListName).update(l -> {
        return uris.values().stream().collect(toList());
      });
    }
    return uri;
  }

  @Override
  public URI saveAsDraft(O object) {
    if (object == null) {
      return null;
    }
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
    Map<URI, Supplier<URI>> map = Stream.of(publishedUri).collect(toMap(u -> u, u -> {
      return () -> objectApi.saveAsNew(schema, object);
    }));
    Map<URI, BranchOperation> branchedObjects =
        branchApi.initBranchedObjects(branchEntry.getUri(), map);
    return branchedObjects.get(publishedUri).getTargetUri();
  }

  @Override
  public void cancelDraft(URI draftUri) {
    // Simply remove the draft from the branch.
    URI branchEntryUri = getCurrentBranchEntryUri();
    if (branchEntryUri != null) {
      objectApi.loadLatest(branchEntryUri).modify(BranchEntry.class, b -> {
        b.setBranchedObjects(b.getBranchedObjects().entrySet().stream()
            .filter(e -> objectApi.equalsIgnoreVersion(draftUri,
                e.getValue().getBranchedObjectLatestUri()))
            .collect(toMap(Entry::getKey, Entry::getValue)));
        return b;
      });
    }
  }

  @Override
  public void discardCurrentModifications() {
    // Remove the branch from the entry.
    if (useLocalBranch) {
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
    if (!useLocalBranch) {
      return;
    }
    URI branchEntryUri = getCurrentBranchEntryUri();
    if (branchEntryUri != null) {
      StoredReference<MasterDataManagementInfo> reference = collectionApi
          .reference(MasterDataManagementApiImpl.SCHEMA,
              MasterDataManagementApiImpl.GLOBAL_INFO, MasterDataManagementInfo.class);
      reference.update(info -> {
        MasterDataManagementEntry entry = info.getObjects().get(getName());
        BranchEntry branchEntry =
            objectApi.read(objectApi.getLatestUri(branchEntryUri), BranchEntry.class);
        if (branchEntry != null) {
          Map<URI, ObjectNode> toSaveBySourceUri =
              branchEntry.getBranchedObjects().entrySet().stream().collect(
                  toMap(e -> URI.create(e.getKey()),
                      e -> objectApi.load(e.getValue().getBranchedObjectLatestUri())));
          Map<URI, URI> toReplace =
              toSaveBySourceUri.entrySet().stream().collect(toMap(Entry::getKey, e -> {
                ObjectNode objectNode = objectApi.loadLatest(e.getKey());
                objectNode.setValues(e.getValue().getObjectAsMap());
                return objectApi.save(objectNode);
              }));
          // Now set the new URIs in the published map and list.
          replaceInPublished(toReplace);
          entry.branch(null);
        }
        return info;
      });
    }
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
      return map;
    });
  }

  @Override
  public List<MDMDraftObjectURIEntry> getDraftUris() {
    URI branchEntryUri = getCurrentBranchEntryUri();
    if (branchEntryUri != null) {
      BranchEntry branchEntry = objectApi.read(branchEntryUri, BranchEntry.class);
      return branchEntry.getBranchedObjects().entrySet().stream()
          .map(e -> new MDMDraftObjectURIEntry(URI.create(e.getKey()),
              e.getValue().getBranchedObjectLatestUri()))
          .collect(toList());
    }
    return Collections.emptyList();
  }

  @Override
  public List<MDMObjectEntry<O>> getDraftObjects() {
    return getDraftUris().stream()
        .map(draft -> new MDMObjectEntry<>(objectApi.read(draft.getSourceURI(), clazz),
            objectApi.read(draft.getDraftURI(), clazz)))
        .collect(toList());
  }

  private BranchEntry getOrCreateBranchEntry() {
    // We check if we have the proper branch already or now we have to create a new one.
    if (useLocalBranch) {
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
    if (useLocalBranch) {
      MasterDataManagementEntry managementEntry = info.getObjects().get(getName());
      if (managementEntry != null) {
        return managementEntry.getBranch();
      }
    }
    return null;
  }

  @Override
  public List<MDMObjectEntry<O>> publishedAndDraftObjects() {
    List<MDMObjectEntry<O>> draftObjects = getDraftObjects();
    Map<URI, MDMObjectEntry<O>> draftsByPublishedURI = draftObjects.stream().collect(toMap(
        o -> objectApi.getLatestUri(objectApi.definition(clazz).getUri(o.getPublished())), o -> o));
    Map<String, URI> publishedMap = getPublishedMap();
    List<MDMObjectEntry<O>> result = publishedMap.entrySet().stream().map(e -> {
      MDMObjectEntry<O> draftObjectEntry = draftsByPublishedURI.remove(e.getValue());
      if (draftObjectEntry != null) {
        return draftObjectEntry;
      }
      // Else we dont have any draft so we create an empty
      return new MDMObjectEntry<>(objectApi.read(e.getValue(), clazz), null);
    }).collect(toList());
    // Add the remaining draft object. The new objects missing from the published.
    result.addAll(draftsByPublishedURI.values());
    return result;
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

  public final MasterDataManagementInfo getInfo() {
    return info;
  }

  final void setInfo(MasterDataManagementInfo info) {
    this.info = info;
  }

  public final boolean isUseLocalBranch() {
    return useLocalBranch;
  }

  public final void setUseLocalBranch(boolean useLocalBranch) {
    this.useLocalBranch = useLocalBranch;
  }

}
