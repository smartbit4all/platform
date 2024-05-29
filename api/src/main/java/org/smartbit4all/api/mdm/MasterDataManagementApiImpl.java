package org.smartbit4all.api.mdm;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.net.URI;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.DefaultComparatorProvider;
import org.smartbit4all.api.collection.FilterExpressionApi;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.collection.SearchIndexImpl;
import org.smartbit4all.api.collection.StoredMap;
import org.smartbit4all.api.collection.VectorCollection;
import org.smartbit4all.api.collection.bean.VectorCollectionDescriptor;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.ServiceConnection;
import org.smartbit4all.api.mdm.bean.MDMBranchingStrategy;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMDefinitionState;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMErrorLog;
import org.smartbit4all.api.mdm.bean.MDMErrorLogData;
import org.smartbit4all.api.mdm.bean.MDMModification;
import org.smartbit4all.api.mdm.bean.MDMModificationArchive;
import org.smartbit4all.api.mdm.bean.MDMModificationRequest;
import org.smartbit4all.api.mdm.bean.MDMModificationState;
import org.smartbit4all.api.mdm.bean.MDMTableColumnDescriptor;
import org.smartbit4all.api.object.BranchApi;
import org.smartbit4all.api.object.CompareApi;
import org.smartbit4all.api.object.bean.AggregationKind;
import org.smartbit4all.api.object.bean.BranchedObjectEntry;
import org.smartbit4all.api.object.bean.BranchedObjectEntry.BranchingStateEnum;
import org.smartbit4all.api.object.bean.ObjectPropertyValue;
import org.smartbit4all.api.object.bean.ReferenceDefinitionData;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.bean.UserActivityLog;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.storage.bean.ObjectAspect;
import org.smartbit4all.api.value.ValueSetApi;
import org.smartbit4all.api.value.bean.ValueSetDefinitionData;
import org.smartbit4all.api.value.bean.ValueSetDefinitionKind;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectCacheEntry;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.core.object.ObjectHistoryIterator;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ReferenceDefinition;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.service.CrudApi;
import org.smartbit4all.domain.service.dataset.TableDataApi;
import org.smartbit4all.domain.service.entity.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ObjectUtils;
import com.google.common.base.Strings;

public class MasterDataManagementApiImpl implements MasterDataManagementApi {

  private static final String LIST = "List";

  public static final String MAP_DEFINITIONS = "definitions";

  private static final Logger log = LoggerFactory.getLogger(MasterDataManagementApiImpl.class);

  @Autowired(required = false)
  private List<MDMDefinitionOption> options;

  @Autowired(required = false)
  private List<MDMEntrySetup> setups;

  private boolean optionsSaved = false;

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private ObjectDefinitionApi objectDefinitionApi;

  @Autowired
  private StorageApi storageApi;

  @Autowired
  private CrudApi crudApi;

  @Autowired
  private TableDataApi tableDataApi;

  @Autowired
  private ApplicationContext ctx;

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private LocaleSettingApi localeSettingApi;

  @Autowired
  private FilterExpressionApi filterExpressionApi;

  @Autowired
  private InvocationApi invocationApi;

  @Autowired
  private BranchApi branchApi;

  @Autowired
  private ValueSetApi valueSetApi;

  @Autowired(required = false)
  private OrgApi orgApi;

  @Autowired
  private MasterDataManagementApi self;

  @Autowired
  private DefaultComparatorProvider comparatorProvider;

  @Autowired(required = false)
  private SessionApi sessionApi;

  @Autowired(required = false)
  private CompareApi compareApi;

  @Override
  public MDMEntryApi getApi(String definition, String name, URI branch) {

    MDMDefinition mdmDefinition = getDefinition(definition);
    MDMEntryDescriptor descriptor = getEntryDescriptor(mdmDefinition, name, branch);

    return new MDMEntryApiImpl(self, mdmDefinition, descriptor, objectApi, collectionApi,
        invocationApi, branchApi,
        valueSetApi, localeSettingApi, sessionApi, compareApi);

  }

  @Override
  public MDMEntryApi getApiSafe(String definition, String name, URI branchUri) {
    MDMDefinition mdmDefinition = getDefinition(definition);
    if (mdmDefinition == null) {
      return null;
    }
    MDMEntryDescriptor descriptor = mdmDefinition.getDescriptors().get(name);
    if (descriptor == null) {
      return null;
    }
    return new MDMEntryApiImpl(self, mdmDefinition, descriptor, objectApi, collectionApi,
        invocationApi, branchApi,
        valueSetApi, localeSettingApi, sessionApi, compareApi);
  }

  @Override
  public MDMEntryDescriptor getEntryDescriptor(MDMDefinition definition, String entryName,
      URI branch) {
    // branch = getValidBranch(definition, branch);
    Map<String, MDMEntryDescriptor> descriptors = getEntryDescriptors(definition, branch);
    MDMEntryDescriptor descriptor = descriptors.get(entryName);
    if (descriptor == null) {
      throw new IllegalArgumentException(
          MessageFormat.format(
              localeSettingApi.get("mdm.entry.notfound"), entryName,
              definition));
    }
    return descriptor;
  }

  @Override
  public MDMEntryDescriptor getEntryDescriptor(String definitionName, String entryName,
      URI branch) {
    MDMDefinition definition = getDefinition(definitionName);
    if (definition == null) {
      throw new IllegalArgumentException(MessageFormat.format(
          localeSettingApi.get("mdm.definition.notfound"),
          definition));
    }
    return getEntryDescriptor(definition, entryName, branch);
  }

  @Override
  public Map<String, MDMEntryDescriptor> getEntryDescriptors(MDMDefinition definition,
      URI branch) {
    // branch = getValidBranch(definition, branch);
    Map<String, MDMEntryDescriptor> descriptors = definition.getDescriptors();
    if (branch != null) {
      ObjectNode stateNode = objectApi.loadLatest(definition.getState());
      MDMDefinitionState state = stateNode.getObject(MDMDefinitionState.class);
      descriptors.putAll(getModificationFromStateNotNull(state, branch).getDescriptors());
    }
    return descriptors;
  }

  @Override
  public MDMDefinition getDefinition(String definition) {
    synchronizeOptions();
    ObjectCacheEntry<MDMDefinition> cacheEntry = objectApi.getCacheEntry(MDMDefinition.class);

    StoredMap map = collectionApi.map(SCHEMA, MAP_DEFINITIONS);

    MDMDefinition result = null;
    URI definitionUri = map.uris().get(definition);
    if (definitionUri != null) {
      // result = cacheEntry.get(definitionUri);
      result = objectApi.loadLatest(definitionUri).getObject(MDMDefinition.class);
    }
    if (result == null) {
      throw new IllegalArgumentException(MessageFormat.format(
          localeSettingApi.get("mdm.definition.notfound"),
          definition));
    }
    return result;
  }

  /**
   * This function is updating currently the code level registered {@link MDMDefinition}s and their
   * option in the storage. All the definition are registered by name in the
   * {@link #MAP_DEFINITIONS} map.
   */
  private final void synchronizeOptions() {
    if (optionsSaved) {
      return;
    }
    optionsSaved = true;

    if (options != null) {
      StoredMap map = collectionApi.map(SCHEMA, MAP_DEFINITIONS);
      map.update(m -> {
        if (m == null) {
          m = new HashMap<>();
        }
        // TODO merge instead of synchronization...
        for (MDMDefinitionOption o : options) {
          m.put(o.getDefinition().getName(), constructNewEntries(m, o, null));
        }
        return m;
      });
      synchronizeObjectDefinitions();
      synchronizeValueSets();
      synchronizeSearchIndices();
    }
    if (setups != null) {
      for (MDMEntrySetup entrySetup : setups) {
        if (entrySetup.getEntriesToSetup() != null) {
          Map<String, MDMEntryApi> entryApis = entrySetup.getEntriesToSetup().stream().map(e -> {
            try {
              return getApiSafe(entrySetup.getDefinitionToSetup(), e);
            } catch (Exception ex) {
              log.info("Unable to setup the " + e + " entry of the "
                  + entrySetup.getDefinitionToSetup() + " MDM definition.", ex);
            }
            return null;
          }).filter(Objects::nonNull).collect(toMap(a -> a.getName(), a -> a));
          entrySetup.setupEntries(entryApis);
        }
      }
    }
  }

  @Override
  public URI addNewEntries(MDMDefinitionOption o, URI branch) {
    if (o == null || o.getDefinition() == null || o.getDefinition().getDescriptors() == null) {
      return null;
    }
    StoredMap map = collectionApi.map(SCHEMA, MAP_DEFINITIONS);
    URI definitionUri = map.uris().get(o.getDefinition().getName());
    if (definitionUri == null) {
      return null;
    }

    // Setup the new entries to fill the default values.

    // Update the entries if we already have the definition.
    constructNewEntries(map.uris(), o, branch);

    synchronizeObjectDefinitions();
    synchronizeValueSets();
    synchronizeSearchIndices();

    return definitionUri;
  }

  private URI constructNewEntries(Map<String, URI> currentMap, MDMDefinitionOption o, URI branch) {
    URI uri = currentMap.get(o.getDefinition().getName());
    // Set the name of the searchIndex and inherit security group name
    o.getDefinition().getDescriptors().values().forEach(d -> {
      if (!StringConstant.isValidCode(d.getName())) {
        throw new IllegalArgumentException(
            "MDMDefinition cannot be created with this invalid name: " + d.getName());
      }

      d.setSearchIndexForEntries(
          BranchedObjectEntry.class.getSimpleName() + StringConstant.DOT + d.getName());
      d.setAdminGroupName(constructEntrySecurityGroupName(o.getDefinition(), d));
    });
    if (uri == null) {
      if (branch != null) {
        throw new IllegalStateException("MDMDefinition cannot be created on branch");
      }
      // We create a new definition and its state and add to the map.
      // o.getDefinition()
      // .setDescriptors(o.getDefinition().getDescriptors().entrySet().stream()
      // .collect(toMap(Entry::getKey, e -> e.getValue())));
      uri = objectApi.saveAsNew(SCHEMA,
          o.getDefinition().state(objectApi.saveAsNew(SCHEMA, new MDMDefinitionState())));
    } else {
      // We simply update the current entry but reserve the states.
      ObjectNode definitionNode = objectApi.loadLatest(uri);
      uri = definitionNode.getObjectUri();

      // Inherit the admin group if it is not set.
      for (Entry<String, MDMEntryDescriptor> descEntry : o.getDefinition().getDescriptors()
          .entrySet()) {
        if (Strings.isNullOrEmpty(descEntry.getValue().getAdminGroupName())) {
          descEntry.getValue()
              .setAdminGroupName(definitionNode.getValueAsString(MDMDefinition.ADMIN_GROUP_NAME));
        }
      }

      o.getDefinition().uri(uri);
      // final URI finalBranch = getValidBranch(o.getDefinition(), branch);
      definitionNode.modify(MDMDefinition.class, def -> {
        if (branch == null) {
          if (def.getDescriptors() == null) {
            def.setDescriptors(new HashMap<>());
          }
          putEntryDescriptorsInMap(def.getDescriptors(), o);
        }
        return def
            .branchingStrategy(o.getDefinition().getBranchingStrategy())
            .adminGroupName(o.getDefinition().getAdminGroupName())
            .adminApproverGroupName(o.getDefinition().getAdminApproverGroupName());
      });
      if (branch != null) {
        definitionNode.ref(MDMDefinition.STATE).get().modify(
            MDMDefinitionState.class, state -> {

              MDMModification modification = getModificationFromStateNotNull(state, branch);
              if (modification.getDescriptors() == null) {
                modification.setDescriptors(new HashMap<>());
              }
              putEntryDescriptorsInMap(modification.getDescriptors(), o);
              return state;
            });
      }
      uri = objectApi.save(definitionNode);
    }
    return uri;

  }

  @Override
  public MDMModification getModificationFromState(MDMDefinitionState state, URI branch) {
    MDMModification modification = state.getGlobalModification();
    if (modification != null
        && !objectApi.equalsIgnoreVersion(branch, modification.getBranchUri())) {
      // not global change
      modification = null;
    }
    if (modification == null) {
      // no global present or not global is changed
      modification = state.getActiveModifications().stream()
          .filter(m -> objectApi.equalsIgnoreVersion(branch, m.getBranchUri()))
          .findFirst()
          .orElse(null);
    }
    if (modification == null && state.getArchive() != null) {
      ObjectHistoryIterator iter = objectApi.objectHistory(state.getArchive());
      iter.reverse(true);
      while (iter.hasNext()) {
        ObjectNode archiveNode = iter.next();
        MDMModificationArchive archive = archiveNode.getObject(MDMModificationArchive.class);
        if (objectApi.equalsIgnoreVersion(branch, archive.getModification().getBranchUri())) {
          modification = archive.getModification();
          break;
        }
      }
    }
    return modification;

  }

  private MDMModification getModificationFromStateNotNull(MDMDefinitionState state, URI branch) {
    MDMModification modification = getModificationFromState(state, branch);
    if (modification == null) {
      throw new IllegalArgumentException("Modification not found by branchUri");
    }
    return modification;
  }

  // private URI getValidBranch(MDMDefinition definition, URI branch) {
  // URI globalBranchUri = getGlobalBranch(definition);
  // if (!objectApi.equalsIgnoreVersion(branch, globalBranchUri)) {
  // log.warn("Unknown branchUri, using global (null)! branch {}", branch);
  // return null;
  // }
  // return branch;
  // }
  //
  private void putEntryDescriptorsInMap(Map<String, MDMEntryDescriptor> current,
      MDMDefinitionOption o) {
    for (Entry<String, MDMEntryDescriptor> descEntry : o.getDefinition().getDescriptors()
        .entrySet()) {
      // MDMEntryDescriptor currentDesc = current.get(descEntry.getKey());
      // TODO merge later.
      current.put(descEntry.getKey(), descEntry.getValue());
    }
  }

  private String constructEntrySecurityGroupName(MDMDefinition definition, MDMEntryDescriptor d) {
    return d.getAdminGroupName() != null ? d.getAdminGroupName() : definition.getAdminGroupName();
  }

  /**
   * Synchronize the value sets for the {@link MDMEntryDescriptor}s of the {@link MDMDefinition}s.
   * {@link ValueSetDefinitionData} will be saved for every entry. The schema will be the name of
   * the definition and the name will be the name of the entry.
   */
  private final void synchronizeValueSets() {
    StoredMap map = collectionApi.map(SCHEMA, MAP_DEFINITIONS);
    List<MDMDefinition> definitions =
        map.uris().values().stream()
            .map(u -> objectApi.loadLatest(u).getObject(MDMDefinition.class))
            .collect(toList());

    for (MDMDefinition definition : definitions) {
      getAllEntryDescriptors(definition).values().stream().forEach(descriptor -> {
        ValueSetDefinitionData definitionData =
            valueSetApi.getDefinitionData(definition.getName(), descriptor.getName());
        if (definitionData == null) {
          valueSetApi.save(definition.getName(),
              new ValueSetDefinitionData()
                  .qualifiedName(descriptor.getName())
                  .kind(ValueSetDefinitionKind.LIST)
                  .storageSchema(descriptor.getSchema())
                  .containerName(getPublishedListName(descriptor))
                  .typeClass(descriptor.getTypeQualifiedName()));
        }
      });
    }
  }

  /**
   */
  private final void synchronizeObjectDefinitions() {
    StoredMap map = collectionApi.map(SCHEMA, MAP_DEFINITIONS);
    List<MDMDefinition> definitions =
        map.uris().values().stream()
            .map(u -> objectApi.loadLatest(u).getObject(MDMDefinition.class))
            .collect(toList());

    for (MDMDefinition definition : definitions) {
      getAllEntryDescriptors(definition).values().stream().forEach(descriptor -> {
        String objectDefitionName = constructObjectDefinitionName(definition, descriptor);

        ObjectDefinition<?> entryObjectDefinition =
            objectDefinitionApi
                .definition(
                    objectDefitionName);

        entryObjectDefinition.reloadDefinitionData();

        objectDefinitionApi.addReference(new ReferenceDefinitionData()
            .propertyPath(BranchedObjectEntry.ORIGINAL_URI).aggregation(AggregationKind.COMPOSITE)
            .propertyKind(ReferencePropertyKind.REFERENCE).sourceObjectName(objectDefitionName)
            .targetObjectName(descriptor.getTypeQualifiedName()));
        objectDefinitionApi.addReference(new ReferenceDefinitionData()
            .propertyPath(BranchedObjectEntry.BRANCH_URI).aggregation(AggregationKind.COMPOSITE)
            .propertyKind(ReferencePropertyKind.REFERENCE).sourceObjectName(objectDefitionName)
            .targetObjectName(descriptor.getTypeQualifiedName()));

        entryObjectDefinition.builder()
            .addProperty(BranchedObjectEntry.BRANCHING_STATE, BranchingStateEnum.class)
            .addProperty(BranchedObjectEntry.ORIGINAL_URI, URI.class)
            .addProperty(BranchedObjectEntry.BRANCH_URI, URI.class)
            .commit();
        entryObjectDefinition.getDefinitionData().setUriProperty(ObjectDefinition.URI_PROPERTY);
      });
    }

  }

  private Map<String, MDMEntryDescriptor> getAllEntryDescriptors(MDMDefinition definition) {
    ObjectNode stateNode = objectApi.loadLatest(definition.getState());
    MDMDefinitionState state = stateNode.getObject(MDMDefinitionState.class);
    Map<String, MDMEntryDescriptor> descriptors = definition.getDescriptors();
    if (state.getGlobalModification() != null) {
      descriptors.putAll(state.getGlobalModification().getDescriptors());
    }
    state.getActiveModifications().forEach(mod -> descriptors.putAll(mod.getDescriptors()));
    return descriptors;
  }

  @Override
  public String constructObjectDefinitionName(MDMDefinition definition,
      MDMEntryDescriptor descriptor) {
    return BranchedObjectEntry.class.getName() + StringConstant.DOT
        + definition.getName() + StringConstant.DOT + descriptor.getName();
  }

  /**
   * Synchronize the {@link SearchIndex}es for the {@link MDMEntryDescriptor}s of the
   * {@link MDMDefinition}s. {@link SearchIndex} is created from every entry. The column mapping can
   * be automatic. In this case all the properties from the object will be mapped as column. In case
   * of a little bit more complex object the {@link MDMEntryDescriptor#getTableColumns()} can
   * describe the columns.
   */
  private final void synchronizeSearchIndices() {
    StoredMap map = collectionApi.map(SCHEMA, MAP_DEFINITIONS);
    List<MDMDefinition> definitions =
        map.uris().values().stream()
            .map(u -> objectApi.loadLatest(u).getObject(MDMDefinition.class))
            .collect(toList());

    for (MDMDefinition definition : definitions) {
      getAllEntryDescriptors(definition).values().stream().forEach(descriptor -> {
        collectionApi.searchIndexComputeIfAbsent(definition.getName(), descriptor.getName(),
            () -> createSearchIndexForEntry(definition, descriptor), Object.class);
        collectionApi.searchIndexComputeIfAbsent(definition.getName(),
            descriptor.getSearchIndexForEntries(),
            () -> createSearchIndexForEntryInstance(definition, descriptor),
            BranchedObjectEntry.class);
      });
    }

  }

  /**
   * Add a property to the search index with complex processing mechanism. Depending on the state of
   * the given object it will show the original object or the branched one. So we will have a
   * heterogeneous list of objects in the table data.
   *
   * @param searchIndex
   * @param propertyName
   * @param aspect The aspect of the shadow object that contains the last known integration version
   *        of the given object.
   * @param typeClass
   * @param length
   * @param path
   */
  private final void addEntryPropertyToSearchIndex(SearchIndexImpl<?> searchIndex,
      String propertyName, String aspect, Class<?> typeClass, int length, String... path) {
    // The object node is an BranchedObjectEntry.definition.entry node and can be used by the
    // ObjectApi
    // to navigate to every property let it be original or branched.
    searchIndex.mapComplex(propertyName, typeClass, length, node -> {
      BranchingStateEnum stateEnum =
          node.getValue(BranchingStateEnum.class, BranchedObjectEntry.BRANCHING_STATE);

      ObjectNode objectNode;

      if (stateEnum == BranchingStateEnum.NOP || stateEnum == BranchingStateEnum.DELETED) {
        objectNode =
            getNodeOrElseAspect(aspect, node.ref(BranchedObjectEntry.ORIGINAL_URI).getObjectUri());
      } else {
        objectNode =
            getNodeOrElseAspect(aspect, node.ref(BranchedObjectEntry.BRANCH_URI).getObjectUri());
      }
      return objectNode.getValue(path);
    });
  }

  private final ObjectNode getNodeOrElseAspect(String aspect, URI branchedUri) {
    ObjectNode objectNode;
    objectNode = objectApi.load(branchedUri);
    ObjectAspect objectAspect;
    // Get an ObjectNode to resolve
    if (objectNode.aspects().get() != null
        && (objectAspect = objectNode.aspects().get().get(aspect)) != null) {
      objectNode =
          objectApi.create(StringConstant.EMPTY, objectAspect.getObjectAsMap());
    }
    return objectNode;
  }

  private final SearchIndex<BranchedObjectEntry> createSearchIndexForEntryInstance(
      MDMDefinition def,
      MDMEntryDescriptor entryDescriptor) {
    SearchIndexImpl<BranchedObjectEntry> result =
        new SearchIndexImpl<>(def.getName(),
            entryDescriptor.getSearchIndexForEntries(),
            entryDescriptor.getSchema(),
            BranchedObjectEntry.class);
    result.map(BranchedObjectEntry.BRANCHING_STATE, BranchingStateEnum.class,
        BranchedObjectEntry.BRANCHING_STATE);
    result.map(BranchedObjectEntry.ORIGINAL_URI, URI.class, BranchedObjectEntry.ORIGINAL_URI);
    result.map(BranchedObjectEntry.BRANCH_URI, URI.class, BranchedObjectEntry.BRANCH_URI);

    ObjectDefinition<?> objectDefinition =
        objectApi.definition(entryDescriptor.getTypeQualifiedName());
    if (!entryDescriptor.getTableColumns().isEmpty()) {
      entryDescriptor.getTableColumns().stream().forEach(
          tcd -> {
            String[] path = tcd.getPath().toArray(StringConstant.EMPTY_ARRAY);
            addEntryPropertyToSearchIndex(result, tcd.getName(),
                tcd.getAspectName(), getTypeOfColumn(objectDefinition, tcd), -1,
                path);
          });
    } else {
      // Navigate to the nearest referred object.
      Map<String, ReferenceDefinition> outgoingReferences =
          objectDefinition.getOutgoingReferences();
      objectDefinition.getPropertiesByName().entrySet()
          .forEach(e -> {
            if (!outgoingReferences.containsKey(e.getKey())) {
              Class<?> typeClass = getClazz(e.getValue().getTypeClass(), String.class);
              addEntryPropertyToSearchIndex(result, e.getValue().getName(), null, typeClass, -1,
                  e.getValue().getName());
            }
          });
    }
    // Setup the result index and call the init to initialize all the inner constructions.
    result.setup(objectApi, storageApi, crudApi, tableDataApi, ctx, entityManager, localeSettingApi,
        filterExpressionApi, comparatorProvider);
    try {
      result.afterPropertiesSet();
    } catch (Exception e) {
      log.error("Unable to initialize the search index for the {} - {}", def.getName(),
          entryDescriptor);
    }
    return result;
  }

  private final Class<?> getClazz(String className, Class<?> defaultClass) {
    Class<?> typeClass;
    try {
      typeClass = Class.forName(className);
    } catch (ClassNotFoundException | NullPointerException e1) {
      typeClass = defaultClass;
    }
    return typeClass;
  }

  private final SearchIndex<Object> createSearchIndexForEntry(MDMDefinition def,
      MDMEntryDescriptor entryDescriptor) {
    SearchIndexImpl<?> result =
        new SearchIndexImpl<>(def.getName(), entryDescriptor.getName(), entryDescriptor.getSchema(),
            getClazz(entryDescriptor.getTypeQualifiedName(), Object.class));
    // The normal index for the published objects has the same column structure. But in this case
    // the mapping is simple because we get directly get the list of objects not BranchedObjectEntry
    // list.
    result.mapComplex(BranchedObjectEntry.BRANCHING_STATE, BranchingStateEnum.class, 50,
        o -> BranchingStateEnum.NOP);
    result.mapComplex(BranchedObjectEntry.ORIGINAL_URI, URI.class, 500,
        o -> o.getObjectUri());
    result.mapComplex(BranchedObjectEntry.BRANCH_URI, URI.class, 500,
        o -> null);

    ObjectDefinition<?> objectDefinition =
        objectApi.definition(entryDescriptor.getTypeQualifiedName());

    if (!entryDescriptor.getTableColumns().isEmpty()) {
      entryDescriptor.getTableColumns().stream().forEach(
          tcd -> {
            String[] path = tcd.getPath().toArray(StringConstant.EMPTY_ARRAY);
            if (tcd.getAspectName() == null) {
              result.map(tcd.getName(),
                  getTypeOfColumn(objectDefinition, tcd),
                  path);
            } else {
              result.mapComplex(tcd.getName(),
                  node -> {
                    final Map<String, ObjectAspect> map = node.aspects().get();
                    if (map == null || map.isEmpty()) {
                      return null;
                    }

                    ObjectAspect objectAspect = map.get(tcd.getAspectName());
                    // Get an ObjectNode to resolve
                    if (objectAspect == null) {
                      return null;
                    }

                    ObjectNode objectNode = objectApi.create(StringConstant.EMPTY,
                        objectDefinitionApi.definition(objectAspect.getTypeQualifiedName()),
                        objectAspect.getObjectAsMap());
                    return objectNode.getValue(getTypeOfColumn(objectDefinition, tcd), path);
                  });
            }
          });
    } else {
      // Navigate to the nearest referred object.
      Map<String, ReferenceDefinition> outgoingReferences =
          objectDefinition.getOutgoingReferences();
      objectDefinition.getPropertiesByName().entrySet()
          .forEach(e -> {
            if (!outgoingReferences.containsKey(e.getKey())) {
              Class<?> typeClass;
              try {
                typeClass = Class.forName(e.getValue().getTypeClass());
              } catch (ClassNotFoundException e1) {
                typeClass = String.class;
              }
              result.map(e.getKey(), typeClass,
                  e.getValue().getName());
            }
          });
    }
    // Setup the result index and call the init to initialize all the inner constructions.
    result.setup(objectApi, storageApi, crudApi, tableDataApi, ctx, entityManager, localeSettingApi,
        filterExpressionApi, comparatorProvider);
    try {
      result.afterPropertiesSet();
    } catch (Exception e) {
      log.error("Unable to initialize the search index for the {} - {}", def.getName(),
          entryDescriptor);
    }
    return (SearchIndex<Object>) result;
  }

  private Class<?> getTypeOfColumn(ObjectDefinition<?> objectDefinition,
      MDMTableColumnDescriptor tcd) {
    Class<?> type = null;
    if (!Strings.isNullOrEmpty(tcd.getTypeClass())) {
      try {
        type = Class.forName(tcd.getTypeClass());
      } catch (ClassNotFoundException e) {
        log.warn("ClassNotFound by table columns typeClass: {}, {}",
            tcd.getName(), tcd.getTypeClass());
      }
    }
    if (type == null) {
      type = objectDefinitionApi.getTypeOfProperty(objectDefinition, String.class,
          tcd.getPath().toArray(StringConstant.EMPTY_ARRAY));
    }
    return type;
  }

  public static final String getPublishedListName(MDMEntryDescriptor descriptor) {
    return descriptor.getPublishedListName() != null
        ? descriptor.getPublishedListName()
        : descriptor.getName() + LIST;
  }

  public static final String getPublishedListName(String mdmName) {
    return mdmName + LIST;
  }

  @Override
  public String initiateModificationBranch(String definitionName,
      String branchCaption) {
    String id = UUID.randomUUID().toString();
    MDMDefitionStateWrapper resultStateWrapper = modifyDefinitionState(definitionName, state -> {
      UserActivityLog createActivityLog = null;
      if (sessionApi == null) {
        log.warn("Unable to create created activity log. The Sessionapi is missing!");
      } else {
        createActivityLog = sessionApi.createActivityLog();
      }

      return state.addActiveModificationsItem(new MDMModification()
          .id(id)
          .name(branchCaption)
          .state(MDMModificationState.ACTIVE)
          .created(createActivityLog)
          .branchUri(objectApi.getLatestUri(branchApi.makeBranch(branchCaption).getUri())));
    }, (prevState, currState) -> {
      return currState.getActiveModifications().stream()
          .filter(mod -> id.equals(mod.getId()))
          .map(MDMModification::getBranchUri)
          .findFirst()
          .orElse(null);
    }, state -> {
      if (state.getActiveModifications().stream()
          .anyMatch(mod -> Objects.equals(branchCaption, mod.getName()))) {
        throw new IllegalStateException(MessageFormat.format(
            localeSettingApi.get("mdm.branch.alreadyexists"),
            branchCaption));
      }
    });
    fireModificationEvent(MODIFICATION_STARTED, null, getDefinition(definitionName).getUri(),
        resultStateWrapper.getCurrentStateUri(), resultStateWrapper.prevState,
        resultStateWrapper.branchUri);
    return id;
  }

  @Override
  public MDMModificationApi getModificationApi(String definitionName, String id) {
    MDMDefinition definition = getDefinition(definitionName);
    Lock lock = objectApi.getLock(definition.getUri());
    lock.lock();
    try {
      MDMDefinitionState state = objectApi.loadLatest(definition.getUri()).ref(MDMDefinition.STATE)
          .get().getObject(MDMDefinitionState.class);
      Optional<MDMModificationApiImpl> apiOptional =
          state.getActiveModifications().stream().filter(m -> Objects.equals(id, m.getId()))
              .findFirst()
              .map(m -> new MDMModificationApiImpl(definition, state, m, false, self, objectApi,
                  sessionApi, branchApi, invocationApi, localeSettingApi));
      if (!apiOptional.isPresent()) {
        throw new IllegalStateException(MessageFormat.format(
            localeSettingApi.get("mdm.modification.notfound"),
            id, definitionName));
      }
      return apiOptional.get();
    } finally {
      lock.unlock();
    }
  }

  @Override
  public MDMModification getModificationEditingByUser(String definitionName, URI userUri) {
    MDMDefinition definition = getDefinition(definitionName);
    MDMDefinitionState state = objectApi.loadLatest(definition.getUri()).ref(MDMDefinition.STATE)
        .get().getObject(MDMDefinitionState.class);
    Optional<MDMModification> modOptional =
        state.getActiveModifications().stream()
            .filter(m -> m.getCurrentEditors().stream()
                .anyMatch(u -> objectApi.equalsIgnoreVersion(u, userUri)))
            .findFirst();
    return modOptional.orElse(null);
  }

  @Override
  public MDMModificationApi getGlobalModificationApi(String definitionName) {
    MDMDefinition definition = getDefinition(definitionName);
    Lock lock = objectApi.getLock(definition.getUri());
    lock.lock();
    try {
      URI stateUri =
          objectApi.loadLatest(definition.getUri()).ref(MDMDefinition.STATE).getObjectUri();
      ObjectNode stateNode = objectApi.loadLatest(stateUri);
      MDMDefinitionState state = stateNode.getObject(MDMDefinitionState.class);
      if (state.getGlobalModification() == null) {
        throw new IllegalStateException(MessageFormat.format(
            localeSettingApi.get("mdm.globalbranch.empty"),
            definitionName));
      }
      return new MDMModificationApiImpl(definition, state, state.getGlobalModification(), true,
          self, objectApi, sessionApi, branchApi, invocationApi, localeSettingApi);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public MDMModificationApi getModificationApiForUser(String definitionName, URI userUri) {
    MDMDefinition definition = getDefinition(definitionName);
    MDMBranchingStrategy strategy = definition.getBranchingStrategy();
    if (strategy == null || strategy == MDMBranchingStrategy.NONE) {
      return null;
    }
    if (strategy == MDMBranchingStrategy.GLOBAL) {
      if (getGlobalBranch(definition) == null) {
        return null;
      }
      return getGlobalModificationApi(definitionName);
    }
    if (strategy == MDMBranchingStrategy.STRICT_PARALLEL) {
      MDMModification currentModification = getModificationEditingByUser(definitionName, userUri);
      if (currentModification == null) {
        return null;
      }
      return getModificationApi(definitionName, currentModification.getId());
    }
    throw new IllegalStateException("Unhandled MDMBranchingStrategy " + strategy);
  }

  @Override
  public URI initiateGlobalBranch(String definitionName, String branchCaption) {
    MDMDefitionStateWrapper resultStateWrapper = modifyDefinitionState(definitionName, state -> {
      UserActivityLog createActivityLog = null;
      if (sessionApi == null) {
        log.warn("Unable to create created activity log. The Sessionapi is missing!");
      } else {
        createActivityLog = sessionApi.createActivityLog();
      }

      return state
          .globalModification(new MDMModification()
              .id(UUID.randomUUID().toString())
              .name(branchCaption)
              .state(MDMModificationState.ACTIVE)
              .created(createActivityLog)
              .branchUri(objectApi.getLatestUri(branchApi.makeBranch(branchCaption).getUri())));
    }, (prevState, currState) -> {
      if (currState.getGlobalModification() == null) {
        return null;
      }
      return currState.getGlobalModification().getBranchUri();
    }, state -> {
      if (state.getGlobalModification() != null) {
        throw new IllegalStateException(MessageFormat.format(
            localeSettingApi.get("mdm.globalbranch.alreadyexists"),
            definitionName));
      }
    }, state -> {
      if (state.getActiveModifications() != null
          && !state.getActiveModifications().isEmpty()) {
        throw new IllegalStateException(MessageFormat.format(
            localeSettingApi.get("mdm.entriesbranch.notempty"),
            definitionName));
      }
    });
    fireModificationEvent(MODIFICATION_STARTED, null, getDefinition(definitionName).getUri(),
        resultStateWrapper.getCurrentStateUri(), resultStateWrapper.prevState,
        resultStateWrapper.branchUri);

    log.info("Global branch opened for [ definition: {} ]", definitionName);
    if (log.isDebugEnabled()) {
      logDefinitionStateChange(resultStateWrapper);
    }

    return resultStateWrapper.getCurrentStateUri();
  }

  private void logDefinitionStateChange(MasterDataManagementApiImpl.MDMDefitionStateWrapper state) {
    if (state == null) {
      log.debug("Attempted to log definition state change, but state is null!");
    } else {
      log.debug("Prev state: {} | Current state: {} | branch: {} ",
          state.prevState, state.getCurrentStateUri(), state.branchUri);
    }
  }

  @Override
  public URI getGlobalBranch(String definition) {
    MDMDefinition mdmDefinition = getDefinition(definition);
    return getGlobalBranch(mdmDefinition);
  }

  private URI getGlobalBranch(MDMDefinition mdmDefinition) {
    return objectApi.loadLatest(mdmDefinition.getUri()).getValue(URI.class, MDMDefinition.STATE,
        MDMDefinitionState.GLOBAL_MODIFICATION, MDMModification.BRANCH_URI);
  }

  @Override
  public URI mergeGlobal(String definitionName) {
    MDMDefitionStateWrapper stateWrapper = mergeGlobalInner(definitionName);
    return stateWrapper.getCurrentStateUri();
  }

  protected MDMDefitionStateWrapper mergeGlobalInner(String definitionName) {
    MDMDefitionStateWrapper stateWrapper = modifyDefinitionState(definitionName, state -> {
      URI branch = state.getGlobalModification().getBranchUri();
      if (sessionApi != null) {
        UserActivityLog merged = sessionApi.createActivityLog();
        getDefinition(definitionName).getDescriptors().keySet().stream()
            .map(descriptorName -> getApi(definitionName, descriptorName, branch))
            .filter(entryApi -> entryApi.getBranchingList().stream()
                .anyMatch(e -> e.getBranchingState() != BranchingStateEnum.NOP))
            .forEach(entryApi -> entryApi.setBranchedEntriesMerged(merged));
      }

      branchApi.merge(branch);
      state.getGlobalModification().state(MDMModificationState.APPROVED);
      return removeGlobalModification(state);
    }, (prevState, currState) -> {
      if (prevState.getGlobalModification() == null) {
        return null;
      }
      return prevState.getGlobalModification().getBranchUri();
    }, state -> noGlobalBranchValidation(definitionName, state));
    fireModificationEvent(MODIFICATION_FINALIZED, null,
        getDefinition(definitionName).getUri(),
        stateWrapper.getCurrentStateUri(), stateWrapper.prevState, stateWrapper.branchUri);

    log.info("Finalised and merged global branch for [ definition: {} ]", definitionName);
    if (log.isDebugEnabled()) {
      logDefinitionStateChange(stateWrapper);
    }

    MDMDefinitionState state =
        objectApi.load(stateWrapper.prevState).getObject(MDMDefinitionState.class);
    Map<String, MDMEntryDescriptor> descriptors = state.getGlobalModification().getDescriptors();
    if (!ObjectUtils.isEmpty(descriptors)) {
      MDMDefinitionOption option = new MDMDefinitionOption(getDefinition(definitionName));
      descriptors.entrySet()
          .forEach(entry -> addDescriptorToDefinition(option.getDefinition(), entry.getValue()));
      addNewEntries(option, null);
    }

    return stateWrapper;
  }

  @Override
  public void addDescriptorToDefinition(MDMDefinition definition, MDMEntryDescriptor descriptor) {
    if (descriptor.getBranchingStrategy() == null) {
      descriptor.setBranchingStrategy(definition.getBranchingStrategy());
    }
    definition.putDescriptorsItem(descriptor.getName(), descriptor);
  }

  @Override
  public URI dropGlobal(String definitionName) {
    MDMDefitionStateWrapper stateWrapper = modifyDefinitionState(definitionName, state -> {
      state.getGlobalModification().state(MDMModificationState.DISPOSED);
      return removeGlobalModification(state);
    }, (prevState, currState) -> {
      if (prevState.getGlobalModification() == null) {
        return null;
      }
      return prevState.getGlobalModification().getBranchUri();
    },
        state -> noGlobalBranchValidation(definitionName, state));
    fireModificationEvent(MODIFICATION_CANCELLED, null,
        getDefinition(definitionName).getUri(),
        stateWrapper.getCurrentStateUri(), stateWrapper.prevState, stateWrapper.branchUri);

    log.info("Global branch dropped for [ definition: {} ]", definitionName);
    if (log.isDebugEnabled()) {
      logDefinitionStateChange(stateWrapper);
    }

    return stateWrapper.getCurrentStateUri();
  }

  private MDMDefinitionState removeGlobalModification(MDMDefinitionState state) {
    archiveModification(state, state.getGlobalModification());
    return state.globalModification(null);
  }

  // TODO duplication! MasterDataManagementApiImpl and MDMModificationApiImpl.archiveModification
  private void archiveModification(MDMDefinitionState state, MDMModification modification) {
    MDMModificationArchive archiveObject = new MDMModificationArchive()
        .modification(modification)
        .archival(sessionApi.createActivityLog());
    URI archiveUri = state.getArchive();
    if (archiveUri == null) {
      archiveUri = objectApi.saveAsNew(MasterDataManagementApi.SCHEMA, archiveObject);
    } else {
      ObjectNode archiveNode = objectApi.loadLatest(archiveUri);
      archiveNode.modify(MDMModificationArchive.class, arch -> archiveObject);
      archiveUri = objectApi.save(archiveNode);
    }
    state.archive(archiveUri);
  }

  private void noGlobalBranchValidation(String definitionName, MDMDefinitionState state) {
    if (state.getGlobalModification() == null) {
      throw new IllegalStateException(MessageFormat.format(
          localeSettingApi.get("mdm.globalbranch.empty"),
          definitionName));
    }
  }

  private void globalBranchUnderApprovalValidation(String definitionName,
      MDMDefinitionState state) {
    if (state.getGlobalModification() != null
        && state.getGlobalModification().getApprover() != null) {
      throw new IllegalStateException(MessageFormat.format(
          localeSettingApi.get("mdm.globalbranch.underapproval"),
          definitionName));
    }
  }

  @SafeVarargs
  final MDMDefitionStateWrapper modifyDefinitionState(String definitionName,
      UnaryOperator<MDMDefinitionState> modification,
      BiFunction<MDMDefinitionState, MDMDefinitionState, URI> branchUriProducer,
      Consumer<MDMDefinitionState>... validations) {
    MDMDefinition definition = getDefinition(definitionName);
    Lock lock = objectApi.getLock(definition.getUri());
    lock.lock();
    try {
      URI stateUri =
          objectApi.loadLatest(definition.getUri()).ref(MDMDefinition.STATE).getObjectUri();
      ObjectNode stateNode = objectApi.loadLatest(stateUri);
      URI prevStateUri = stateNode.getObjectUri();
      ObjectNode prevStateNode = objectApi.load(prevStateUri);
      MDMDefinitionState state = stateNode.getObject(MDMDefinitionState.class);
      if (validations != null) {
        for (int i = 0; i < validations.length; i++) {
          validations[i].accept(state);
        }
      }
      stateNode.modify(MDMDefinitionState.class,
          modification);
      objectApi.save(stateNode);
      MDMDefinitionState currentState = stateNode.getObject(MDMDefinitionState.class);
      MDMDefinitionState prevState = prevStateNode.getObject(MDMDefinitionState.class);
      URI branchUri = branchUriProducer.apply(prevState, currentState);
      return new MDMDefitionStateWrapper(currentState, prevStateUri, branchUri);
    } finally {
      lock.unlock();
    }
  }

  void fireModificationEvent(String event, String scope, URI definition, URI state,
      URI prevState, URI branchUri) {
    invocationApi
        .publisher(
            MasterDataManagementApi.class,
            MDMSubscriberApi.class,
            STATE_CHANGED)
        .publish(api -> api.stateChanged(event, scope, definition, state, prevState, branchUri));
  }

  @Override
  public void saveVectorCollectionDescriptor(String definitionName, String entryName,
      VectorCollectionDescriptor vectorCollectionDescriptor) {
    MDMDefinition definition = getDefinition(definitionName);
    Lock lock = objectApi.getLock(definition.getUri());
    lock.lock();
    try {
      ObjectNode definitionNode = objectApi.loadLatest(definition.getUri());
      definitionNode.modify(MDMDefinition.class, def -> {
        MDMEntryDescriptor entryDescriptor = def.getDescriptors().get(entryName);
        entryDescriptor.vectorCollection(vectorCollectionDescriptor);
        return def;
      });
      objectApi.save(definitionNode);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void modifyEntry(String definitionName, MDMEntryDescriptor entry, URI branch) {
    MDMDefinition definition = getDefinition(definitionName);
    // branch = getValidBranch(definition, branch);
    Lock lock = objectApi.getLock(definition.getUri());
    lock.lock();
    try {
      ObjectNode definitionNode = objectApi.loadLatest(definition.getUri());
      if (branch == null) {
        definitionNode.modify(MDMDefinition.class, def -> {
          def.putDescriptorsItem(entry.getName(), entry);
          return def;
        });
      } else {
        definitionNode.ref(MDMDefinition.STATE).get().modify(
            MDMDefinitionState.class, state -> {
              getModificationFromStateNotNull(state, branch)
                  .putDescriptorsItem(entry.getName(), entry);
              return state;
            });
      }
      objectApi.save(definitionNode);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void executeMdmDefinitionUpdate(String definitionName) {
    MDMDefinition definition = getDefinition(definitionName);
    Lock lock = objectApi.getLock(definition.getUri());
    lock.lock();
    InvocationRequest updateRequest = definition.getUpdateRequest();
    try {
      invocationApi.invoke(updateRequest);
    } catch (ApiNotFoundException e) {
      log.error(e.getMessage(), e);
    } finally {
      lock.unlock();
    }
  }

  protected static class MDMDefitionStateWrapper {
    MDMDefinitionState currentState;
    URI prevState;
    URI branchUri;

    public URI getCurrentStateUri() {
      return currentState.getUri();
    }

    public MDMDefitionStateWrapper(MDMDefinitionState currentState, URI prevState, URI branchUri) {
      java.util.Objects.requireNonNull(currentState);
      this.currentState = currentState;
      this.prevState = prevState;
      this.branchUri = branchUri;
    }
  }

  @Override
  public MDMErrorLog importData(String definitionName, String entryName,
      MDMModificationRequest modificationRequest, URI branchUri) {
    MDMErrorLog errorLog = new MDMErrorLog();
    boolean globalBranchInit = false;
    try {
      if (branchUri == null) {
        initiateGlobalBranch(definitionName, "Import session - " + LocalDateTime.now());
        branchUri = getGlobalBranch(definitionName);
        globalBranchInit = true;
      }

      MDMEntryApi entryApi =
          getApi(definitionName, entryName, branchUri);

      entryApi.updateList(null, modificationRequest.getData().getDefinition().stream()
          .map(objMap -> constructHierarchicalMap(objMap)).collect(toList()));

      if (globalBranchInit) {
        if (errorLog.getData().isEmpty()) {
          mergeGlobal(definitionName);
        } else {
          dropGlobal(definitionName);
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      errorLog.addDataItem(new MDMErrorLogData().error(e.getMessage()));
      if (globalBranchInit) {
        dropGlobal(definitionName);
      }
    }
    return errorLog;
  }

  /**
   * This function restructure the map and create sub maps if the key of a value is a path. The path
   * looks like this innerobject/another/property. In this case we will have an innerobject key in
   * the root map that is map and a another map again and the property will be placed into this.
   *
   * @param data The original flatten map.
   * @return The resulting map with the inner structure.
   */
  public static final Map<String, Object> constructHierarchicalMap(Map<String, String> data) {
    Objects.requireNonNull(data);
    Map<String, Object> result = new HashMap<>();
    for (Entry<String, String> entry : data.entrySet()) {
      String path = entry.getKey();
      String value = entry.getValue();
      addValue(result, path, path, value);
    }
    return result;
  }

  private static void addValue(Map<String, Object> result, String originalPath, String path,
      String value) {
    if (!path.contains(PATH_SEPARATOR)) {
      result.put(path, value);
    } else {
      // split string with PATH_SEPARATOR if it can
      int firstSepIndex = path.indexOf(PATH_SEPARATOR);
      String subMapName = path.substring(0, firstSepIndex);
      Object subMap = result.computeIfAbsent(subMapName, s -> new HashMap<>());
      if (subMap instanceof Map) {
        addValue((Map<String, Object>) subMap, originalPath, path.substring(firstSepIndex + 1),
            value);
      } else {
        log.error(
            "Unable to {} property because the {} is a sub object and a simple property at the same time.",
            originalPath, subMapName);
      }
    }
  }

  @Override
  public final VectorCollection getVectorCollection(
      VectorCollectionDescriptor vectorCollectionDescriptor) {
    MDMEntryApi vectorDBEntryApi =
        getApi(MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION,
            PlatformApiConfig.VECTOR_DB_CONNECTIONS);
    ServiceConnection vectorDBConnection =
        objectApi.asType(ServiceConnection.class,
            vectorDBEntryApi.lookup().findByUnique(new ObjectPropertyValue()
                .addPathItem(ServiceConnection.NAME)
                .value(vectorCollectionDescriptor.getVectorDBConnection())));
    MDMEntryApi embeddingEntryApi =
        getApi(MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION,
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
