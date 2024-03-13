package org.smartbit4all.api.mdm;

import java.net.URI;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.DefaultComparatorProvider;
import org.smartbit4all.api.collection.FilterExpressionApi;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.collection.SearchIndexImpl;
import org.smartbit4all.api.collection.StoredMap;
import org.smartbit4all.api.collection.bean.VectorCollectionDescriptor;
import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMDefinitionState;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMModification;
import org.smartbit4all.api.mdm.bean.MDMModificationNote;
import org.smartbit4all.api.mdm.bean.MDMTableColumnDescriptor;
import org.smartbit4all.api.object.BranchApi;
import org.smartbit4all.api.object.CompareApi;
import org.smartbit4all.api.object.bean.AggregationKind;
import org.smartbit4all.api.object.bean.BranchedObjectEntry;
import org.smartbit4all.api.object.bean.BranchedObjectEntry.BranchingStateEnum;
import org.smartbit4all.api.object.bean.ReferenceDefinitionData;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.bean.Group;
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
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

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
  public MDMEntryApi getApiSafe(String definition, String name) {
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

    branch = getValidBranch(definition, branch);
    Map<String, MDMEntryDescriptor> descriptors = definition.getDescriptors();
    if (branch != null) {
      descriptors.putAll(getDescriptorsOnGlobalBranch(definition));
    }

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
    branch = getValidBranch(definition, branch);
    if (branch == null) {
      return definition.getDescriptors();
    }
    // global branch only
    return getAllEntryDescriptors(definition);
  }

  private Map<String, MDMEntryDescriptor> getDescriptorsOnGlobalBranch(MDMDefinition definition) {
    ObjectNode stateNode = objectApi.loadLatest(definition.getState());
    // handle global branch now, handle others later
    Map<String, MDMEntryDescriptor> descriptors = stateNode.getValueAsMap(MDMEntryDescriptor.class,
        MDMDefinitionState.GLOBAL_MODIFICATION,
        MDMModification.DESCRIPTORS);
    return descriptors != null ? descriptors : new HashMap<>();
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
      synchronizeSecurityOptions();
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
    optionsSaved = true;
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
    synchronizeSecurityOptions();

    return definitionUri;
  }

  private URI constructNewEntries(Map<String, URI> currentMap, MDMDefinitionOption o, URI branch) {
    URI uri = currentMap.get(o.getDefinition().getName());
    // Set the name of the searchIndex and inherit security group name
    o.getDefinition().getDescriptors().values().forEach(d -> {
      d.setSearchIndexForEntries(
          BranchedObjectEntry.class.getSimpleName() + StringConstant.DOT + d.getName());
      d.setAdminGroupName(constructEntrySecurityGroupName(o.getDefinition(), d));
    });
    if (uri == null) {
      if (branch != null) {
        throw new IllegalStateException("MDMDefinition cannot be created on branch");
      }
      // We create a new definition and its state and add to the map.
      o.getDefinition()
          .setDescriptors(o.getDefinition().getDescriptors().entrySet().stream()
              .collect(toMap(Entry::getKey, e -> e.getValue())));
      uri = objectApi.saveAsNew(SCHEMA,
          o.getDefinition().state(objectApi.saveAsNew(SCHEMA, new MDMDefinitionState())));
    } else {
      // We simply update the current entry but reserve the states.
      ObjectNode definitionNode = objectApi.loadLatest(uri);

      // Inherit the admin group if it is not set.
      for (Entry<String, MDMEntryDescriptor> descEntry : o.getDefinition().getDescriptors()
          .entrySet()) {
        if (Strings.isEmpty(descEntry.getValue().getAdminGroupName())) {
          descEntry.getValue()
              .setAdminGroupName(definitionNode.getValueAsString(MDMDefinition.ADMIN_GROUP_NAME));
        }
      }

      o.getDefinition().uri(uri);
      final URI finalBranch = getValidBranch(o.getDefinition(), branch);
      definitionNode.modify(MDMDefinition.class, def -> {
        if (finalBranch == null) {
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
      if (finalBranch != null) {
        definitionNode.ref(MDMDefinition.STATE).get().modify(
            MDMDefinitionState.class, state -> {
              if (state.getGlobalModification().getDescriptors() == null) {
                state.getGlobalModification().setDescriptors(new HashMap<>());
              }
              putEntryDescriptorsInMap(state.getGlobalModification().getDescriptors(), o);
              return state;
            });
      }
      objectApi.save(definitionNode);
    }
    return uri;
  }

  private URI getValidBranch(MDMDefinition definition, URI branch) {
    URI globalBranchUri = getGlobalBranch(definition);
    if (!objectApi.equalsIgnoreVersion(branch, globalBranchUri)) {
      log.warn("Unknown branchUri, using global (null)! branch {}", branch);
      return null;
    }
    return branch;
  }

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

  private void synchronizeSecurityOptions() {
    if (orgApi == null) {
      log.error(
          "Unable to setup the security rights for the master data management. The OrgApi is missing!");
      return;
    }
    StoredMap map = collectionApi.map(SCHEMA, MAP_DEFINITIONS);
    List<MDMDefinition> definitions =
        map.uris().values().stream()
            .map(u -> objectApi.loadLatest(u).getObject(MDMDefinition.class))
            .collect(toList());

    for (MDMDefinition definition : definitions) {
      // Check whether the admin group is set for the definition and exists in the org repo.
      if (definition.getAdminGroupName() != null) {
        URI definitionGroupUri;
        definitionGroupUri = getOrCreateDefinitionGroup(definition);
        getAllEntryDescriptors(definition).values().stream().forEach(descriptor -> {
          Group descriptionGroup = getOrCreateEntryGroup(definition, descriptor);
          if (descriptionGroup != null) {
            orgApi.addChildGroup(orgApi.getGroup(definitionGroupUri), descriptionGroup);
          }
        });
      } else {
        log.error(
            "Unable to setup the security rights for the {} definition. The admin group name is not set!",
            definition.getName());
      }
    }
  }

  private final Group getOrCreateEntryGroup(MDMDefinition definition,
      MDMEntryDescriptor descriptor) {
    Group descriptionGroup = null;
    String entryGroupName = descriptor.getAdminGroupName();
    // If we set an admin group that is different from definition admin group then we create it.
    if (entryGroupName != null && !entryGroupName.equals(definition.getAdminGroupName())) {
      descriptionGroup = orgApi.getGroupByName(entryGroupName);
      if (descriptionGroup == null) {
        URI saveGroup = orgApi.saveGroup(new Group().name(entryGroupName)
            .builtIn(Boolean.TRUE)
            .title(entryGroupName)
            .description("The administration group for the "
                + definition.getName() + StringConstant.DOT + descriptor.getName()
                + " master data management entry."));
        descriptionGroup = orgApi.getGroup(saveGroup);
      }
    }
    return descriptionGroup;
  }

  private final URI getOrCreateDefinitionGroup(MDMDefinition definition) {
    URI definitionGroupUri;
    Group definitionGroup = orgApi.getGroupByName(definition.getAdminGroupName());
    if (definitionGroup == null) {
      definitionGroupUri = orgApi.saveGroup(new Group().name(definition.getAdminGroupName())
          .builtIn(Boolean.TRUE)
          .title(definition.getAdminGroupName()).description("The administration group for the "
              + definition.getName() + " master data management definition."));
    } else {
      definitionGroupUri = definitionGroup.getUri();
    }
    return definitionGroupUri;
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
    Map<String, MDMEntryDescriptor> descriptors = definition.getDescriptors();
    descriptors.putAll(getDescriptorsOnGlobalBranch(definition));
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
    if (!Strings.isEmpty(tcd.getTypeClass())) {
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
              .created(createActivityLog)
              .branchUri(objectApi.getLatestUri(branchApi.makeBranch(branchCaption).getUri())));
    }, state -> {
      if (state.getGlobalModification() != null) {
        throw new IllegalStateException(MessageFormat.format(
            localeSettingApi.get("mdm.globalbranch.alreadyexists"),
            definitionName));
      }
    }, state -> {
      if (state.getModificationsForEntries() != null
          && !state.getModificationsForEntries().isEmpty()) {
        throw new IllegalStateException(MessageFormat.format(
            localeSettingApi.get("mdm.entriesbranch.notempty"),
            definitionName));
      }
    });
    fireModificationStarted(MODIFICATION_STARTED, null, getDefinition(definitionName).getUri(),
        resultStateWrapper.getCurrentStateUri(), resultStateWrapper.prevState);
    return resultStateWrapper.getCurrentStateUri();
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
  public URI initiateBranchForEntry(String definition, String title, String entryName) {
    // TODO Auto-generated method stub
    return null;
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
      return state
          .globalModification(null);
    }, state -> noGlobalBranchValidation(definitionName, state));
    fireModificationStarted(MODIFICATION_FINALIZED, null,
        getDefinition(definitionName).getUri(),
        stateWrapper.getCurrentStateUri(), stateWrapper.prevState);
    MDMDefinitionState state =
        objectApi.load(stateWrapper.prevState).getObject(MDMDefinitionState.class);
    Map<String, MDMEntryDescriptor> descriptors = state.getGlobalModification().getDescriptors();
    if (!ObjectUtils.isEmpty(descriptors)) {
      MDMDefinitionOption option = new MDMDefinitionOption(getDefinition(definitionName));
      descriptors.entrySet().forEach(entry -> option.addDescriptor(entry.getValue()));
      addNewEntries(option, null);
    }

    return stateWrapper;
  }

  @Override
  public URI dropGlobal(String definitionName) {
    MDMDefitionStateWrapper stateWrapper = modifyDefinitionState(definitionName, state -> {
      return state
          .globalModification(null);
    }, state -> noGlobalBranchValidation(definitionName, state));
    fireModificationStarted(MODIFICATION_CANCELLED, null,
        getDefinition(definitionName).getUri(),
        stateWrapper.getCurrentStateUri(), stateWrapper.prevState);
    return stateWrapper.getCurrentStateUri();
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
  private final MDMDefitionStateWrapper modifyDefinitionState(String definitionName,
      UnaryOperator<MDMDefinitionState> modification, Consumer<MDMDefinitionState>... validations) {
    MDMDefinition definition = getDefinition(definitionName);
    Lock lock = objectApi.getLock(definition.getUri());
    lock.lock();
    try {
      URI stateUri =
          objectApi.loadLatest(definition.getUri()).ref(MDMDefinition.STATE).getObjectUri();
      ObjectNode stateNode = objectApi.loadLatest(stateUri);
      MDMDefinitionState state = stateNode.getObject(MDMDefinitionState.class);
      if (validations != null) {
        for (int i = 0; i < validations.length; i++) {
          validations[i].accept(state);
        }
      }
      stateNode.modify(MDMDefinitionState.class,
          modification);
      objectApi.save(stateNode);
      return new MDMDefitionStateWrapper(stateNode.getObject(MDMDefinitionState.class),
          stateNode.getObjectUri());
    } finally {
      lock.unlock();
    }
  }

  private void fireModificationStarted(String event, String scope, URI definition, URI state,
      URI prevState) {
    invocationApi
        .publisher(
            MasterDataManagementApi.class,
            MDMSubscriberApi.class,
            STATE_CHANGED)
        .publish(api -> api.stateChanged(event, scope, definition, state, prevState));
  }

  @Override
  public void sendForApprovalGlobal(String definitionName, URI approver) {
    MDMDefitionStateWrapper stateWrapper = modifyDefinitionState(definitionName, state -> {
      state.getGlobalModification().approver(approver).updated(sessionApi.createActivityLog());
      return state;
    }, state -> noGlobalBranchValidation(definitionName, state),
        state -> globalBranchUnderApprovalValidation(definitionName, state));
    fireModificationStarted(MODIFICATION_SENT_FOR_APPROVAL, null,
        getDefinition(definitionName).getUri(),
        stateWrapper.getCurrentStateUri(), stateWrapper.prevState);
  }

  @Override
  public void approvalAcceptedGlobal(String definitionName) {
    MDMDefitionStateWrapper stateWrapper = mergeGlobalInner(definitionName);
    fireModificationStarted(MODIFICATION_APPROVED, null, getDefinition(definitionName).getUri(),
        stateWrapper.getCurrentStateUri(), stateWrapper.prevState);
  }

  @Override
  public void approvalRejectedGlobal(String definitionName, String reason) {
    MDMDefitionStateWrapper stateWrapper = modifyDefinitionState(definitionName, state -> {
      state.getGlobalModification()
          .updated(sessionApi.createActivityLog())
          .addNotesItem(new MDMModificationNote()
              .created(sessionApi.createActivityLog())
              .note(reason));
      state.getGlobalModification().approver(null);
      return state;
    }, state -> noGlobalBranchValidation(definitionName, state));
    fireModificationStarted(MODIFICATION_REJECTED, null, getDefinition(definitionName).getUri(),
        stateWrapper.getCurrentStateUri(), stateWrapper.prevState);
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
    branch = getValidBranch(definition, branch);
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
              state.getGlobalModification().putDescriptorsItem(entry.getName(), entry);
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

  private static class MDMDefitionStateWrapper {
    MDMDefinitionState currentState;
    URI prevState;

    public URI getCurrentStateUri() {
      return currentState.getUri();
    }

    public MDMDefitionStateWrapper(MDMDefinitionState currentState, URI prevState) {
      java.util.Objects.requireNonNull(currentState);
      this.currentState = currentState;
      this.prevState = prevState;
    }
  }

}
