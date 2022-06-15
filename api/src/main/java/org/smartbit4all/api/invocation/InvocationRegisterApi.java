package org.smartbit4all.api.invocation;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.contribution.PrimaryApi;
import org.smartbit4all.api.invocation.bean.ApiRegistryData;

/**
 * The invocation register api is responsible for storing the api informations provided by the
 * current runtime and read the storage about it. Based on the storage it will modify the
 * RemoteApiProxy to have available end points. Depends on the call policy the call of the interface
 * methods will fail or simply just save the fact of the call.
 * 
 * @author Peter Boros
 */
public interface InvocationRegisterApi {

  /**
   * The refresh registry is responsible for detecting the changes in the {@link ApiRegistryData} in
   * the storage. Evaluate the changes and modify the {@link ApiInvocationHandler}s and the
   * {@link PrimaryApi}s to have an updated state with the currently available apis in the tenant.
   */
  void refreshRegistry();

  Object getApiInstance(URI apiDataUri);

  ApiDescriptor getApi(String interfaceClass, String name);

  List<UUID> getRuntimesForApi(URI apiDataUri);

  void setInvocationApi(InvocationApi invocationApi);
}
