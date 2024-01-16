package org.smartbit4all.api.invocation;

import java.time.OffsetDateTime;
import java.util.Map;
import javax.script.ScriptException;
import org.smartbit4all.api.invocation.bean.AsyncInvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationBatchResult;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationRequestBatch;
import org.smartbit4all.api.invocation.bean.InvocationRequestDefinition;
import org.smartbit4all.api.object.bean.ObjectPropertyResolverContext;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectPropertyResolver;

/**
 * The {@link InvocationApi} is a generic api to call other api function.
 *
 * @author Peter Boros
 */
public interface InvocationApi {

  /**
   * This is the constant object to use when we don't want to override the parameter of an
   * INvocationRequest.
   */
  public static final Object LEAVE = new Object();

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
  InvocationParameter invoke(InvocationRequest request, Object... args) throws ApiNotFoundException;

  /**
   * The generic api call executed synchronously.
   *
   * @param batch The invocation batch with a list of request to invoke.
   * @throws ApiNotFoundException
   */
  InvocationBatchResult invokeBatch(InvocationRequestBatch batch) throws ApiNotFoundException;

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
   * @param batch The invocation request batch with a list of invocation call.
   * @param channel The channel that is configured for the execution. If we don't give any parameter
   *        then it will be created with default parameters.
   */
  void invokeAsyncBatch(InvocationRequestBatch batch, String channel);

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

  /**
   * Constructs an event publisher that is responsible for recording an {@link InvocationRequest} by
   * calling a function on the interface. Later on this {@link InvocationRequest} is going to be
   * used as an invocation toward the subscribed api calls.
   *
   * @param <P>
   * @param <S>
   * @param publisherApiInterface The interface that defines the fire operations typically.
   * @param subscriberApiInterface The interface of the subscriber api.
   * @param event The name of the event.
   * @return The event publisher that can make a new published event as an {@link InvocationRequest}
   *         via the {@link EventPublisher#publish(java.util.function.Consumer)} function. It
   *         provides an instance of the publisher api interface and we can call the function in
   *         right syntax.
   */
  <P, S> EventPublisher<P, S> publisher(Class<P> publisherApiInterface,
      Class<S> subscriberApiInterface, String event);

  /**
   * The definition contains an prepared instance from the {@link InvocationRequest} and some
   * mapping between the context object properties and the parameters. With this call we can
   * initiate a new {@link InvocationRequest} ready to call by resolving the referred parameters
   * from the objects provided in the context with the {@link ObjectPropertyResolver}.
   *
   * @param definition The invocation definition.
   * @param context The object context for the resolution.
   * @return
   */
  InvocationRequest resolve(InvocationRequestDefinition definition,
      ObjectPropertyResolverContext context);

  /**
   * Constructs a new invocation request copy and set the parameters by position.
   *
   * @param request The original request
   * @param parameters At a given position we can use the {@link #LEAVE} constant. This is the
   *        constant object to use when we don't want to override the parameter of an
   *        INvocationRequest.
   * @return The result request that can be used for the
   *         {@link #invoke(InvocationRequest, Object...)} for example.
   */
  InvocationRequest prepareByPosition(InvocationRequest request, Object... parameters);

  /**
   * Constructs a new invocation request copy and set the parameters by position.
   *
   * @param request The original request
   * @param parameters The parameters in a map. The key is the name of the parameter.
   * @return The result request that can be used for the
   *         {@link #invoke(InvocationRequest, Object...)} for example.
   */
  InvocationRequest prepareByName(InvocationRequest request, Map<String, Object> parameters);

  /**
   * Executes a script via an available script engine from the current JVM.
   *
   * @param scriptEngine The name of the script engine.
   * @param script The text of the script itself.
   * @param contextObjects The context objects to use to set the input parameters of the script. The
   *        input variable identified by the name of the context objects. Their names are starting
   *        with the names of the context objects.
   * @param inputParams The input
   * @return The value of the evaluation.
   */
  Object executeScript(String scriptEngine, String script, Map<String, ObjectNode> contextObjects,
      Map<String, Object> inputParams) throws ScriptException;

}
