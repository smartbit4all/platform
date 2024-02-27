package org.smartbit4all.api.invocation;

import org.smartbit4all.api.invocation.bean.ServiceConnection;

public abstract class ServiceIntegrationApiImpl implements ServiceIntegrationApi {

  protected ServiceConnection connection;

  @Override
  public ServiceIntegrationApi connection(ServiceConnection connection) {
    this.connection = connection;
    return this;
  }

}
