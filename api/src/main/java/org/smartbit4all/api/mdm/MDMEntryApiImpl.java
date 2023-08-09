package org.smartbit4all.api.mdm;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredList;
import org.smartbit4all.api.collection.StoredMap;
import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMDefinitionState;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.object.BranchApi;
import org.smartbit4all.api.object.bean.BranchedObjectEntry;
import org.smartbit4all.api.object.bean.BranchedObjectEntry.BranchingStateEnum;
import org.smartbit4all.api.object.bean.ObjectNodeState;
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
      BranchApi branchApi, ValueSetApi valueSetApi) {
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
  }

  @Override
  public String getId(Object object) {
    if (object == null) {
      return null;
    }
    if (descriptor.getUniqueIdentifierPath() != null
        && !descriptor.getUniqueIdentifierPath().isEmpty()) {
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
    if (descriptor.getUniqueIdentifierPath() != null
        && !descriptor.getUniqueIdentifierPath().isEmpty()) {
      return objectNode.getValueAsString(
          descriptor.getUniqueIdentifierPath().toArray(StringConstant.EMPTY_ARRAY));
    } else {
      URI uri = objectApi.getLatestUri(objectNode.getValue(URI.class, uriPath));
      return uri.toString();
    }
  }

  @Override
  public URI save(ObjectNode objectNode) {
    URI branchUri = getBranchUri();
    StoredList list = getList();
    list.branch(branchUri);

    if (objectNode.getState() == ObjectNodeState.NEW) {
      objectNode
          .setValues(fireBeforeSaveNew(objectApi.definition(descriptor.getTypeQualifiedName()),
              objectNode.getObjectAsMap(), descriptor));
      URI uri = objectApi.save(objectNode);
      list.add(uri);
      return uri;
    } else if (objectNode.getState() == ObjectNodeState.MODIFIED) {
      URI uri = objectNode.getObjectUri();
      List<URI> resultUri = new ArrayList<>(1);
      list.update(l -> {
        return l.stream().map(u -> {
          if (objectApi.equalsIgnoreVersion(uri, u)) {
            // Save a modified version and replace the original uri.
            URI savedUri = objectApi.save(objectNode, branchUri);
            resultUri.add(savedUri);
            return savedUri;
          } else {
            return u;
          }
        }).collect(toList());
      });
      return resultUri.isEmpty() ? null : resultUri.iterator().next();
    }
    return null;
  }

  private final URI getBranchUri() {
    MDMDefinitionState mdmDefinitionState = definitionStateCache.get(definition.getState());
    URI branchUri = null;
    if (mdmDefinitionState != null) {
      branchUri = mdmDefinitionState.getBranchForEntries().get(descriptor.getName());
      if (branchUri == null) {
        branchUri = mdmDefinitionState.getGlobalBranch();
      }
    }
    return branchUri;
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
    toCancel.stream()
        .forEach(uri -> branchApi.removeBranchedObject(branchUri, uri));

    return !result.isEmpty();
  }

  @Override
  public boolean remove(URI objectUri) {
    URI branchUri = getBranchUri();
    StoredList list = getList();
    list.branch(branchUri);
    list.remove(objectUri);
    return true;
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

  private StoredMap getPublishedStoredMap() {
    return collectionApi.map(descriptor.getSchema(), getPublishedMapName());
  }

  @Override
  public String getName() {
    return descriptor.getName();
  }

  private String getPublishedMapName() {
    return descriptor.getPublishedMapName() != null
        ? descriptor.getPublishedMapName()
        : descriptor.getName() + "Map";
  }

  public final String getListName() {
    return descriptor.getPublishedListName() != null
        ? descriptor.getPublishedListName()
        : descriptor.getName() + "List";
  }

  @Override
  public StoredList getList() {
    return collectionApi.list(descriptor.getSchema(), getListName());
  }

  public final void refreshValueSetDefinition() {
    if (refreshValueSetDefinition) {
      refreshValueSetDefinition = false;
      ObjectDefinition<?> definition = objectApi.definition(descriptor.getTypeQualifiedName());
      String publishedListName = getListName();
      valueSetApi.save(descriptor.getSchema(),
          new ValueSetDefinitionData().kind(ValueSetDefinitionKind.LIST)
              .storageSchema(descriptor.getSchema()).containerName(publishedListName)
              .objectDefinition(ObjectDefinition.uriOf(descriptor.getTypeQualifiedName()))
              .qualifiedName(publishedListName));
    }
  }

  @Override
  public MDMEntryDescriptor getDescriptor() {
    return descriptor;
  }

  @Override
  public StoredMap getMap() {
    return getPublishedStoredMap();
  }

  @Override
  public List<BranchedObjectEntry> getBranchingList() {
    URI branchUri = getBranchUri();
    StoredList list = getList();
    return list.compareWithBranch(branchUri);
  }

  @Override
  public boolean hasActiveBranch() {
    return getBranchUri() != null;
  }

}
