package org.smartbit4all.api.mdm;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredReference;
import org.smartbit4all.api.object.bean.MasterDataManagementEntry;
import org.smartbit4all.api.object.bean.MasterDataManagementInfo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.toMap;

public class MasterDataManagementApiImpl implements MasterDataManagementApi, InitializingBean {

  public static final String GLOBAL_INFO = "globalInfo";

  @Autowired(required = false)
  private List<MDMEntryApi<?>> apis;

  /**
   * The actual entries coming from the context registered {@link MDMEntryApi}s.
   */
  private Map<String, MDMEntryApi<?>> apiEntries = new HashMap<>();

  @Autowired
  private CollectionApi collectionApi;

  @SuppressWarnings("unchecked")
  @Override
  public <T> MDMEntryApi<T> getApi(Class<T> clazz, String name) {
    MDMEntryApi<T> entryApi = (MDMEntryApi<T>) apiEntries.get(name);
    if (entryApi instanceof MDMEntryApiImpl) {
      // We always refresh the master info for the given api.
      MasterDataManagementInfo masterDataManagementInfo = getOrCreateInfo();
      ((MDMEntryApiImpl) entryApi).setInfo(masterDataManagementInfo);
    }
    return entryApi;
  }

  @Override
  public <T> MDMEntryApi<T> getApi(Class<T> clazz) {
    return getApi(clazz, clazz.getSimpleName());
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (apis != null) {
      apiEntries.putAll(apis.stream().collect(toMap(MDMEntryApi::getName, api -> api)));
    }
  }

  private final MasterDataManagementInfo getOrCreateInfo() {
    MasterDataManagementInfo result;
    StoredReference<MasterDataManagementInfo> reference = collectionApi
        .reference(SCHEMA, GLOBAL_INFO, MasterDataManagementInfo.class);
    MasterDataManagementInfo masterDataManagementInfo = reference.get();
    Map<String, MasterDataManagementEntry> entriesToUpdate =
        getEntriesToUpdate(masterDataManagementInfo);

    if (entriesToUpdate != null) {
      reference.update(info -> {
        if (info == null) {
          info = new MasterDataManagementInfo();
        }
        // Add the missing entries to the info record.
        info.getObjects().putAll(entriesToUpdate);
        return info;
      });
    }
    return reference.get();
  }

  private Map<String, MasterDataManagementEntry> getEntriesToUpdate(MasterDataManagementInfo info) {
    Map<String, MasterDataManagementEntry> baseLine =
        info != null ? info.getObjects() : Collections.emptyMap();
    return apiEntries.entrySet().stream().filter(e -> {
      MasterDataManagementEntry entry = baseLine.get(e.getKey());
      // TODO Update if there is any other option.
      if (entry == null) {
        return true;
      }
      return !entry.getCaption().equals(e.getValue().getName());
    }).collect(
        toMap(Entry::getKey, e -> new MasterDataManagementEntry().caption(e.getValue().getName())));
  }

}
