package org.smartbit4all.domain.application;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.domain.data.storage.StorageApi;

/**
 * The application runtime is responsible for the management of the application running instances.
 * This must be a singleton in an application. If we have one and only one instance and the default
 * implementation is {@link StorageApi} based. We can implement it's functionality with other
 * techniques like Api registry implementations, Hazelcast and so on.
 * 
 * @author Peter Boros
 */
public interface ApplicationRuntimeApi {

  /**
   * This function returns the current instance object.
   * 
   * @return
   */
  ApplicationRuntime self();

  /**
   * To check the given application instance we can get it via the UUID.
   * 
   * @param uuid The uuid of the instance.
   * @return If the application instance exists then we get it back els it will be null.
   */
  ApplicationRuntime get(UUID uuid);

  List<ApplicationRuntime> getActiveRuntimes();

  List<URI> getApis(UUID uuid);

  void setApis(List<URI> apiDataUris);

}
