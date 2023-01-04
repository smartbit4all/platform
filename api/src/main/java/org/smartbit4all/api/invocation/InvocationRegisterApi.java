package org.smartbit4all.api.invocation;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.contribution.PrimaryApi;
import org.smartbit4all.api.invocation.bean.ApiRegistryData;
import org.smartbit4all.api.invocation.bean.AsyncChannelScheduledInvocationList;
import org.smartbit4all.api.invocation.bean.AsyncInvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.domain.application.ApplicationRuntimeApi;

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

  /**
   * Save the invocation request into the asynchronous channel for persisted execution if it is
   * possible. At the end it enqueue the async invocation.
   * 
   * @param request The original request
   * @param channel The channel to use for saving.
   * @return If the channel exists and we have an runtime management it will save and return the
   *         {@link AsyncInvocationRequest}. If we don't have an active
   *         {@link ApplicationRuntimeApi} then the object will be constructed but not saved.
   */
  AsyncInvocationRequestEntry saveAndEnqueueAsyncInvocationRequest(InvocationRequest request,
      String channel);

  /**
   * Save the invocation request into the asynchronous channel for persisted execution if it is
   * possible. It will save the request to the {@link AsyncChannelScheduledInvocationList} for
   * further execution.
   * 
   * @param request The original request
   * @param channel The channel to use for saving.
   * @param executeAt The exact time when the invocation should be executed at.
   * @return The request.
   */
  AsyncInvocationRequest saveAndScheduleAsyncInvocationRequest(InvocationRequest request,
      String channel, OffsetDateTime executeAt);

  /**
   * Remove the invocation request from the asynchronous channel belong to the current runtime.
   * 
   * @param request The request to remove.
   */
  void removeAsyncInvovationRequest(AsyncInvocationRequestEntry request);

}
