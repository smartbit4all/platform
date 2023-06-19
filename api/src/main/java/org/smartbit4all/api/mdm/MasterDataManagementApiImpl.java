package org.smartbit4all.api.mdm;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredMap;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMDefinitionState;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptorState;
import org.smartbit4all.api.object.BranchApi;
import org.smartbit4all.api.value.ValueSetApi;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectCacheEntry;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.toMap;

public class MasterDataManagementApiImpl implements MasterDataManagementApi {

  public static final String MAP_DEFINITIONS = "definitions";

  @Autowired(required = false)
  private List<MDMDefinitionOption> options;

  private boolean optionsSaved = false;

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private InvocationApi invocationApi;

  @Autowired
  private BranchApi branchApi;

  @Autowired
  private ValueSetApi valueSetApi;

  @Autowired
  private MasterDataManagementApi self;

  @SuppressWarnings("unchecked")
  @Override
  public MDMEntryApi getApi(String definition, String name) {
    synchronizeOptions();
    ObjectCacheEntry<MDMDefinition> cacheEntry = objectApi.getCacheEntry(MDMDefinition.class);

    StoredMap map = collectionApi.map(SCHEMA, MAP_DEFINITIONS);

    MDMDefinition mdmDefinition = null;
    URI definitionUri = map.uris().get(definition);
    if (definitionUri != null) {
      mdmDefinition = cacheEntry.get(definitionUri);
    }
    if (mdmDefinition == null) {
      throw new IllegalArgumentException(
          "Unable to find the " + definition + " master data definition.");
    }

    // Get the descriptor from the definition;
    MDMEntryDescriptor descriptor = mdmDefinition.getDescriptors().get(name);

    if (descriptor == null) {
      throw new IllegalArgumentException(
          "Unable to find the " + name + " entry in the " + definition
              + " master data definition.");
    }

    return new MDMEntryApiImpl(self, descriptor, objectApi, collectionApi, invocationApi, branchApi,
        valueSetApi);

    // MDMEntryApi<T> entryApi = (MDMEntryApi<T>) apiEntries.get(name);
    // if (entryApi instanceof MDMEntryApiImpl) {
    // // We always refresh the master info for the given api.
    // MasterDataManagementInfo masterDataManagementInfo = getOrCreateInfo();
    // MDMEntryApiImpl apiImpl = (MDMEntryApiImpl) entryApi;
    // apiImpl.setInfo(masterDataManagementInfo);
    // // Refresh the value set of the api.
    // apiImpl.refreshValueSetDefinition();
    // }
    // return entryApi;
  }

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
        m.putAll(options.stream().collect(toMap(o -> o.getDefinition().getName(), o -> {
          URI uri = currentMap.get(o.getDefinition().getName());
          if (uri == null) {
            // We create a new definition and its state and add to the map.
            o.getDefinition()
                .setDescriptors(o.getDefinition().getDescriptors().entrySet().stream()
                    .collect(toMap(Entry::getKey, e -> e.getValue()
                        .state(objectApi.saveAsNew(SCHEMA, new MDMEntryDescriptorState())))));
            uri = objectApi.saveAsNew(SCHEMA,
                o.getDefinition().state(objectApi.saveAsNew(SCHEMA, new MDMDefinitionState())));
          } else {
            // We simply update the current entry but reserve the states.
            ObjectNode definitionNode = objectApi.loadLatest(uri);
            URI stateUri = definitionNode.getValue(URI.class, MDMDefinition.STATE);
            Map<String, URI> statesByName = definitionNode.list(MDMDefinition.DESCRIPTORS)
                .nodeStream().collect(toMap(on -> on.getValueAsString(MDMEntryDescriptor.NAME),
                    on -> on.getValue(URI.class, MDMEntryDescriptor.STATE)));
            definitionNode.modify(MDMDefinition.class, def -> {
              o.getDefinition()
                  .setDescriptors(o.getDefinition().getDescriptors().entrySet().stream()
                      .collect(toMap(Entry::getKey, e -> {
                        URI currentState = statesByName.get(e.getValue().getName());
                        if (currentState == null) {
                          currentState = objectApi.saveAsNew(SCHEMA, new MDMEntryDescriptorState());
                        }
                        return e.getValue()
                            .state(currentState);
                      })));
              return o.getDefinition().state(stateUri);
            });
            objectApi.save(definitionNode);
          }
          return uri;
        })));
        return m;
      });
    }
    optionsSaved = true;
  }

}
