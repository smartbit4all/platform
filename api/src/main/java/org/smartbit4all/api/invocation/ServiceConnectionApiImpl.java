package org.smartbit4all.api.invocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.invocation.bean.ServiceConnection;
import org.smartbit4all.api.mdm.MDMEntryApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.object.bean.ObjectPropertyValue;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.toMap;

public class ServiceConnectionApiImpl implements ServiceConnectionApi, InitializingBean {

  @Autowired
  private MasterDataManagementApi mdmApi;

  @Autowired(required = false)
  private List<ServiceIntegrationApi> apis;

  /**
   * The connections collected from the context.
   */
  private Map<String, ServiceConnectionRecord> connections = new HashMap<>();

  @Override
  public void afterPropertiesSet() throws Exception {
    if (apis != null) {
      connections.putAll(apis.stream()
          .collect(toMap(a -> a.getClass().getName(), a -> new ServiceConnectionRecord(null, a))));
    }
  }

  private class ServiceConnectionRecord {

    public ServiceConnectionRecord(ServiceConnection connection, ServiceIntegrationApi api) {
      super();
      this.connection = connection;
      this.api = api;
    }

    ServiceConnection connection;

    ServiceIntegrationApi api;

  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends ServiceIntegrationApi> T get(Class<T> clazz) {
    if (clazz == null) {
      return null;
    }
    ServiceConnectionRecord connectionRecord = connections.get(clazz.getName());
    if (connectionRecord == null) {
      // The given integration is not registered it is missing from the application context.
      throw new IllegalStateException(
          "The " + clazz + " integration api is not registered into the application config.");
    }
    // Try to get the proper ServiceConnection from tha api.
    if (connectionRecord.connection == null) {
      MDMEntryApi entryApi = mdmApi
          .getApi(MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION, SERVICE_CONNECTIONS);
      ServiceConnection serviceConnection = entryApi.lookup().findByUnique(
          new ObjectPropertyValue().addPathItem(ServiceConnection.NAME), ServiceConnection.class);
      if (serviceConnection != null) {
        connectionRecord.connection = serviceConnection;
        connectionRecord.api.connection(serviceConnection);
      }
    }
    return connectionRecord.connection != null ? (T) connectionRecord.api : null;
  }

}
