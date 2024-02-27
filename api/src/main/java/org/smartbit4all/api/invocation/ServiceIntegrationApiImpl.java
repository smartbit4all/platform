package org.smartbit4all.api.invocation;

import org.smartbit4all.api.invocation.bean.ServiceConnection;
import org.smartbit4all.api.mdm.MDMEntryApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.object.bean.ObjectPropertyValue;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class ServiceIntegrationApiImpl implements ServiceIntegrationApi {

  protected ServiceConnection connection;

  @Autowired
  private MasterDataManagementApi mdmApi;

  @Override
  public ServiceIntegrationApi connection(ServiceConnection connection) {
    this.connection = connection;
    return this;
  }

  @Override
  public void setDefaultConnection() {
    MDMEntryApi entryApi = mdmApi
        .getApi(MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION,
            ServiceConnectionApi.SERVICE_CONNECTIONS);
    ServiceConnection connection = entryApi.lookup().findByUnique(
        new ObjectPropertyValue().addPathItem(ServiceConnection.NAME)
            .value(getClass().getName()),
        ServiceConnection.class);
    this.connection = connection;
  }

}
