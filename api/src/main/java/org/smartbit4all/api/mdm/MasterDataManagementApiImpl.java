package org.smartbit4all.api.mdm;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.net.URI;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.FilterExpressionApi;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.collection.SearchIndexImpl;
import org.smartbit4all.api.collection.StoredMap;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMDefinitionState;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMModification;
import org.smartbit4all.api.object.BranchApi;
import org.smartbit4all.api.object.bean.AggregationKind;
import org.smartbit4all.api.object.bean.BranchedObjectEntry;
import org.smartbit4all.api.object.bean.BranchedObjectEntry.BranchingStateEnum;
import org.smartbit4all.api.object.bean.ReferenceDefinitionData;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.setting.LocaleSettingApi;
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

public class MasterDataManagementApiImpl implements MasterDataManagementApi {

  public static final String MAP_DEFINITIONS = "definitions";

  private static final Logger log = LoggerFactory.getLogger(MasterDataManagementApiImpl.class);

  @Autowired(required = false)
  private List<MDMDefinitionOption> options;

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

  @Override
  public MDMEntryApi getApi(String definition, String name) {

    MDMDefinition mdmDefinition = getDefinition(definition);
    MDMEntryDescriptor descriptor = getEntryDescriptor(mdmDefinition, name);

    return new MDMEntryApiImpl(self, mdmDefinition, descriptor, objectApi, collectionApi,
        invocationApi, branchApi,
        valueSetApi, localeSettingApi);

  }

  @Override
  public MDMEntryDescriptor getEntryDescriptor(MDMDefinition definition, String entryName) {

    MDMEntryDescriptor descriptor = definition.getDescriptors().get(entryName);

    if (descriptor == null) {
      throw new IllegalArgumentException(
          MessageFormat.format(
              localeSettingApi.get("mdm.entry.notfound"), entryName,
              definition));
    }
    return descriptor;
  }

  @Override
  public MDMEntryDescriptor getEntryDescriptor(String definitionName, String entryName) {
    MDMDefinition definition = getDefinition(definitionName);
    if (definition == null) {
      throw new IllegalArgumentException(MessageFormat.format(
          localeSettingApi.get("mdm.definition.notfound"),
          definition));
    }
    return getEntryDescriptor(definition, entryName);
  }

  @Override
  public MDMDefinition getDefinition(String definition) {
    synchronizeOptions();
    ObjectCacheEntry<MDMDefinition> cacheEntry = objectApi.getCacheEntry(MDMDefinition.class);

    StoredMap map = collectionApi.map(SCHEMA, MAP_DEFINITIONS);

    MDMDefinition result = null;
    URI definitionUri = map.uris().get(definition);
    if (definitionUri != null) {
      result = cacheEntry.get(definitionUri);
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
        Map<String, URI> currentMap = m;
        // TODO merge instead of synchronization...
        m.putAll(options.stream().collect(toMap(o -> o.getDefinition().getName(), o -> {
          return constructNewEntries(currentMap, o);
        })));
        return m;
      });
      synchronizeObjectDefinitions();
      synchronizeValueSets();
      synchronizeSearchIndices();
      synchronizeSecurityOptions();
    }
    optionsSaved = true;
  }

  @Override
  public URI addNewEntries(MDMDefinitionOption o) {
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
    constructNewEntries(map.uris(), o);

    synchronizeObjectDefinitions();
    synchronizeValueSets();
    synchronizeSearchIndices();
    synchronizeSecurityOptions();

    return definitionUri;
  }

  private URI constructNewEntries(Map<String, URI> currentMap, MDMDefinitionOption o) {
    URI uri = currentMap.get(o.getDefinition().getName());
    // Set the name of the searchIndex and inherit security group name
    o.getDefinition().getDescriptors().values().forEach(d -> {
      d.setSearchIndexForEntries(
          BranchedObjectEntry.class.getSimpleName() + StringConstant.DOT + d.getName());
      d.setAdminGroupName(constructEntrySecurityGroupName(o.getDefinition(), d));
    });
    if (uri == null) {
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

      definitionNode.modify(MDMDefinition.class, def -> {
        for (Entry<String, MDMEntryDescriptor> descEntry : o.getDefinition().getDescriptors()
            .entrySet()) {
          // MDMEntryDescriptor currentDesc = def.getDescriptors().get(descEntry.getKey());
          // TODO merge later.
          def.putDescriptorsItem(descEntry.getKey(), descEntry.getValue());
        }
        return def;
      });
      objectApi.save(definitionNode);
    }
    return uri;
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
        definition.getDescriptors().values().stream().forEach(descriptor -> {
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
      definition.getDescriptors().values().stream().forEach(descriptor -> {
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
      definition.getDescriptors().values().stream().forEach(descriptor -> {
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
      definition.getDescriptors().values().stream().forEach(descriptor -> {
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
   * @param typeClass
   * @param length
   * @param path
   */
  private final void addEntryPropertyToSearchIndex(SearchIndexImpl<?> searchIndex,
      String propertyName, Class<?> typeClass, int length, String... path) {
    // The object node is an BranchedObjectEntry.definition.entry node and can be used by the
    // ObjectApi
    // to navigate to every property let it be original or branched.
    searchIndex.mapComplex(propertyName, typeClass, length, node -> {
      BranchingStateEnum stateEnum =
          node.getValue(BranchingStateEnum.class, BranchedObjectEntry.BRANCHING_STATE);
      ObjectNode nodeObject;
      if (stateEnum == BranchingStateEnum.NOP || stateEnum == BranchingStateEnum.DELETED) {
        URI originalUri = node.ref(BranchedObjectEntry.ORIGINAL_URI).getObjectUri();
        nodeObject = objectApi.load(originalUri);
      } else {
        URI branchedUri = node.ref(BranchedObjectEntry.BRANCH_URI).getObjectUri();
        nodeObject = objectApi.load(branchedUri);
      }
      return nodeObject.getValue(path);
    });
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
                objectDefinitionApi.getTypeOfProperty(objectDefinition, String.class, path),
                -1,
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
              addEntryPropertyToSearchIndex(result, e.getValue().getName(), typeClass, -1,
                  e.getValue().getName());
            }
          });
    }
    // Setup the result index and call the init to initialize all the inner constructions.
    result.setup(objectApi, storageApi, crudApi, tableDataApi, ctx, entityManager, localeSettingApi,
        filterExpressionApi);
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
            String[] pathes = tcd.getPath().toArray(StringConstant.EMPTY_ARRAY);
            result.map(tcd.getName(),
                objectDefinitionApi.getTypeOfProperty(objectDefinition, String.class, pathes),
                pathes);
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
        filterExpressionApi);
    try {
      result.afterPropertiesSet();
    } catch (Exception e) {
      log.error("Unable to initialize the search index for the {} - {}", def.getName(),
          entryDescriptor);
    }
    return (SearchIndex<Object>) result;
  }

  public static final String getPublishedListName(MDMEntryDescriptor descriptor) {
    return descriptor.getPublishedListName() != null
        ? descriptor.getPublishedListName()
        : descriptor.getName() + "List";
  }

  @Override
  public URI initiateGlobalBranch(String definitionName, String branchCaption) {
    return modifyDefinitionState(definitionName, state -> {
      return state
          .globalModification(new MDMModification()
              // TODO created, etc.
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
    }).getUri();
  }

  @Override
  public URI initiateBranchForEntry(String definition, String title, String entryName) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void mergeGlobal(String definitionName) {
    modifyDefinitionState(definitionName, state -> {
      branchApi.merge(state.getGlobalModification().getBranchUri());
      return state
          .globalModification(null);
    }, state -> noGlobalBranchValidation(definitionName, state));
  }

  @Override
  public void dropGlobal(String definitionName) {
    modifyDefinitionState(definitionName, state -> {
      return state
          .globalModification(null);
    }, state -> noGlobalBranchValidation(definitionName, state));
  }

  private void noGlobalBranchValidation(String definitionName, MDMDefinitionState state) {
    if (state.getGlobalModification() == null) {
      throw new IllegalStateException(MessageFormat.format(
          localeSettingApi.get("mdm.globalbranch.empty"),
          definitionName));
    }
  }

  @SafeVarargs
  private final MDMDefinitionState modifyDefinitionState(String definitionName,
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
      return stateNode.getObject(MDMDefinitionState.class);
    } finally {
      lock.unlock();
    }
  }

}
