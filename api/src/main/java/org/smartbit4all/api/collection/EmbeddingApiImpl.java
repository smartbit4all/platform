package org.smartbit4all.api.collection;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.collection.bean.VectorValue;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.contribution.PrimaryApiImpl;
import org.smartbit4all.api.invocation.bean.ServiceConnection;
import org.smartbit4all.api.mdm.MDMEntryApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.object.bean.ObjectPropertyValue;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The default implementation of the {@link EmbeddingApi}.
 * 
 * @author Peter Boros
 */
public final class EmbeddingApiImpl extends PrimaryApiImpl<EmbeddingContributionApi>
    implements EmbeddingApi {

  private static final String TEXT_PROPERTY = "TEXT";

  @Autowired
  ObjectApi objectApi;
  @Autowired
  MasterDataManagementApi masterDataManagementApi;

  public EmbeddingApiImpl() {
    super(EmbeddingContributionApi.class);
  }

  @Override
  public VectorValue embed(String serviceConnectionName, Map<String, Object> object,
      List<String> pathes) {
    ServiceConnection serviceConnection = getEmbeddingConnection(serviceConnectionName);
    if (serviceConnection == null) {
      throw new IllegalArgumentException(
          "There service connection named " + serviceConnectionName + " is missing!");
    }
    EmbeddingContributionApi api = getContributionApi(serviceConnection.getApiName());
    if (api == null) {
      throw new IllegalArgumentException(
          "The contribution api \"" + serviceConnection.getApiName()
              + "\" is not registered in the application context");
    }
    return api.embed(serviceConnection, object, pathes);
  }

  @Override
  public VectorValue embed(String serviceConnectionName, Map<String, Object> object) {
    return embed(serviceConnectionName, object, null);
  }

  @Override
  public VectorValue embed(String serviceConnectionName, String text) {
    Map<String, Object> obj = new LinkedHashMap<>();
    obj.put(TEXT_PROPERTY, text);
    return embed(serviceConnectionName, obj);
  }

  private ServiceConnection getEmbeddingConnection(String serviceConnectionName) {
    MDMEntryApi vectorDBEntryApi =
        masterDataManagementApi.getApi(MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION,
            PlatformApiConfig.EMBEDDING_CONNECTIONS);
    return objectApi.asType(ServiceConnection.class,
        vectorDBEntryApi.lookup().findByUnique(new ObjectPropertyValue()
            .addPathItem(ServiceConnection.NAME)
            .value(serviceConnectionName)));
  }

}
