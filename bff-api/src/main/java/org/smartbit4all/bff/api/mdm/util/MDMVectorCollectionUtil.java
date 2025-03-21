package org.smartbit4all.bff.api.mdm.util;

import static java.util.stream.Collectors.toList;
import java.util.List;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.formdefinition.bean.SmartFormWidgetType;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.invocation.bean.ServiceConnection;
import org.smartbit4all.api.mdm.MDMEntryApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.value.bean.Value;

public class MDMVectorCollectionUtil {

  public static final List<Value> getVectorDbConnectionValueList(MasterDataManagementApi mdmApi) {
    MDMEntryApi entryApi = mdmApi.getApi(MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION,
        PlatformApiConfig.VECTOR_DB_CONNECTIONS);
    return entryApi.getList().nodesFromCache()
        .map(node -> {
          String name = node.getValueAsString(ServiceConnection.NAME);
          return new Value().code(name).displayValue(name);
        }).collect(toList());
  }

  public static final List<Value> getEmbeddingConnectionValueList(MasterDataManagementApi mdmApi) {
    MDMEntryApi entryApi = mdmApi.getApi(MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION,
        PlatformApiConfig.EMBEDDING_CONNECTIONS);
    return entryApi.getList().nodesFromCache()
        .map(node -> {
          String name = node.getValueAsString(ServiceConnection.NAME);
          return new Value().code(name).displayValue(name);
        }).collect(toList());
  }

  public static final SmartWidgetDefinition getVectorDbConnectionWidget(String key, String label,
      MasterDataManagementApi masterDataManagementApi) {
    return new SmartWidgetDefinition()
        .type(SmartFormWidgetType.SELECT)
        .key(key)
        .label(label)
        .values(getVectorDbConnectionValueList(masterDataManagementApi));
  }

  public static final SmartWidgetDefinition getEmbeddingConnectionWidget(String key, String label,
      MasterDataManagementApi masterDataManagementApi) {
    return new SmartWidgetDefinition()
        .type(SmartFormWidgetType.SELECT)
        .key(key)
        .label(label)
        .values(getEmbeddingConnectionValueList(masterDataManagementApi));
  }

  private MDMVectorCollectionUtil() {}

}
