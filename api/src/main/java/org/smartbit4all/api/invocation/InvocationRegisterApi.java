package org.smartbit4all.api.invocation;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.contribution.PrimaryApi;
import org.smartbit4all.api.invocation.bean.ApiRegistryData;
import org.smartbit4all.api.invocation.bean.AsyncChannelScheduledInvocationList;
import org.smartbit4all.api.invocation.bean.AsyncInvocationRequest;
import org.smartbit4all.api.invocation.bean.EventSubscriptionData;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationResult;
import org.smartbit4all.api.invocation.bean.MethodTemplate;
import org.smartbit4all.core.object.ObjectNode;

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

  MethodTemplate getMethodTemplate(String methodFQN);

  List<UUID> getRuntimesForApi(URI apiDataUri);

  /**
   * Save the invocation request into the asynchronous channel for persisted execution if it is
   * possible. At the end it enqueue the async invocation.
   *
   * @param request The original request
   * @param channel The channel to use for saving.
   */
  void saveAndEnqueueAsyncInvocationRequest(InvocationRequest request,
      String channel, AsyncCompletableFuture future);

  /**
   * Save the invocation request into the asynchronous channel for persisted execution if it is
   * possible. At the end it enqueue the async invocation.
   *
   * @param asynRequest The original request
   */
  void saveAndEnqueueAsyncInvocationRequest(ObjectNode asynRequest);

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
   * Evaluate the result of the current invocation and make a decision about the next steps.
   *
   * @param request The current request.
   * @param result
   */
  void saveAsyncInvocationResult(AsyncInvocationRequestEntry request, InvocationResult result);

  /**
   * Return all the subscriptions for the given interface
   *
   * @param interfaceName
   * @return
   */
  List<EventSubscriptionData> getSubscriptions(String interfaceName);

  void initRegistry();

  /**
   * Check whether or not the given asynchronous channel configured and exists.
   * 
   * @param channel The channel name.
   * @return True if the channel is configured so the subsequent
   *         {@link #invokeAsync(InvocationRequest, String)} is going to be executed successfuly.
   */
  boolean asyncChannelExists(String channel);


}
