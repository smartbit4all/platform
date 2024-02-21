package org.smartbit4all.api.invocation;

/**
 * The service connections are generic third party service access definitions. If an application
 * could have an access to a third party service then the accessor api should be marked with the
 * {@link ServiceIntegrationApi}.
 * 
 * @author Peter Boros
 */
public interface ServiceConnectionApi {

  static final String SERVICE_CONNECTIONS = "service_connections";

  <T extends ServiceIntegrationApi> T get(Class<T> clazz);

}
