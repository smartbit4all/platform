package org.smartbit4all.api.invocation;

import java.time.OffsetDateTime;
import org.smartbit4all.api.invocation.bean.AsyncInvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.core.object.ObjectNode;

/**
 * The {@link InvocationApi} is a generic api to call other api function.
 * 
 * @author Peter Boros
 */
public interface InvocationApi {

  /**
   * The builder can be used to produce an {@link InvocationRequest} by calling the method in the
   * {@link InvocationBuilder#build(java.util.function.Consumer)} function.
   * <p>
   * <code>
   *  InvocationRequest request = invocationApi.builder(testApi).build(a -> a.echoMethod(value));<br>
   *  InvocationParameter result = invocationApi.invoke(request);
   * </code>
   * </p>
   * 
   * @param <T>
   * @param apiInterface
   * @return
   */
  <T> InvocationBuilder<T> builder(Class<T> apiInterface);

  /**
   * The builder can be used to produce an {@link AsyncInvocationRequest} by creating multiple
   * invocation chain.
   * <p>
   * <code>
   *  InvocationRequest request = invocationApi.builder(testApi).build(a -> a.echoMethod(value));<br>
   *  InvocationParameter result = invocationApi.invoke(request);
   * </code>
   * </p>
   * 
   * @return
   */
  AsyncInvocationBuilder asyncBuilder();

  /**
   * The generic api call executed synchronously.
   * 
   * @param request
   * @throws ApiNotFoundException
   */
  InvocationParameter invoke(InvocationRequest request) throws ApiNotFoundException;

  void invoke(ObjectNode asyncInvocationNode);

  /**
   * This call register the invocation for the for execute after the successful commit of the
   * current transaction. But on the other hand it will save the given {@link InvocationRequest}
   * into the channel. If the transaction succeeded but the execution is not finished then the
   * invocation api will try to execute it later on.
   * 
   * In this case the execution will inherit the session of the current user. If the session is
   * expired before the invocation is started then the invocation will fail.
   * 
   * To ensure that the invocation is going to be executed then we can assign a technical user must
   * be set in the request to this call. The invocation will create a session and login with the
   * given user.
   * 
   * @param request The invocation request.
   * @param channel The channel that is configured for the execution. If we don't give any parameter
   *        then it will be created with default parameters.
   */
  void invokeAsync(InvocationRequest request, String channel);

  /**
   * This call register the invocation for the for execute after the successful commit of the
   * current transaction. But on the other hand it will save the given {@link InvocationRequest}
   * into the channel. If the transaction succeeded but the execution is not finished then the
   * invocation api will try to execute it later on.
   * 
   * In this case the execution will inherit the session of the current user. If the session is
   * expired before the invocation is started then the invocation will fail.
   * 
   * To ensure that the invocation is going to be executed then we can assign a technical user must
   * be set in the request to this call. The invocation will create a session and login with the
   * given user.
   * 
   * @param request The invocation request.
   * @param channel The channel that is configured for the execution. If we don't give any parameter
   *        then it will be created with default parameters.
   * @param executeAt The exact time when the invocation should be executed at.
   */
  void invokeAt(InvocationRequest request, String channel, OffsetDateTime executeAt);

  <T> EventPublisher<T> publisher(Class<T> apiInterface, String event);

}
