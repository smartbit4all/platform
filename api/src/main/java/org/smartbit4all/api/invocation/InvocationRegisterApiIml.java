package org.smartbit4all.api.invocation;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredReference;
import org.smartbit4all.api.collection.bean.StoredCollectionDescriptor;
import org.smartbit4all.api.collection.bean.StoredCollectionDescriptor.CollectionTypeEnum;
import org.smartbit4all.api.contribution.ContributionApi;
import org.smartbit4all.api.contribution.PrimaryApi;
import org.smartbit4all.api.invocation.bean.ApiData;
import org.smartbit4all.api.invocation.bean.ApiRegistryData;
import org.smartbit4all.api.invocation.bean.AsyncChannelScheduledInvocationList;
import org.smartbit4all.api.invocation.bean.AsyncInvocationRequest;
import org.smartbit4all.api.invocation.bean.EventSubscriptionData;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationResult;
import org.smartbit4all.api.invocation.bean.InvocationResultDecision.DecisionEnum;
import org.smartbit4all.api.invocation.bean.PublishedEventData;
import org.smartbit4all.api.invocation.bean.RuntimeAsyncChannel;
import org.smartbit4all.api.invocation.bean.RuntimeAsyncChannelList;
import org.smartbit4all.api.invocation.bean.RuntimeAsyncChannelRegistry;
import org.smartbit4all.api.invocation.bean.ScheduledInvocationRequest;
import org.smartbit4all.api.invocation.config.InvocationApiMdmConfig;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinitionApiImpl;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.core.utility.UriUtils;
import org.smartbit4all.domain.application.ApplicationRuntime;
import org.smartbit4all.domain.application.ApplicationRuntimeApi;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.smartbit4all.domain.data.storage.StorageObjectLock;
import org.smartbit4all.domain.data.storage.TransactionUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class InvocationRegisterApiIml implements InvocationRegisterApi, DisposableBean {

  private static final Logger log = LoggerFactory.getLogger(InvocationRegisterApiIml.class);

  private static final String ASYNC_REQUESTS_HANDLER = "ASYNC_REQUESTS_HANDLER";

  /**
   * The URI of the global registry.
   */
  public static final URI REGISTER_URI =
      URI.create(Invocations.APIREGISTRATION_SCHEME + StringConstant.COLON + StringConstant.SLASH
          + ObjectDefinitionApiImpl.getDefaultAlias(ApiRegistryData.class));

  @Autowired(required = false)
  private StorageApi storageApi;

  @Autowired(required = false)
  private ApplicationRuntimeApi applicationRuntimeApi;

  /**
   * We need all the local {@link PrimaryApi}s to update them in case of {@link ContributionApi}
   * changes.
   */
  @Autowired(required = false)
  private List<PrimaryApi<?>> primaryApis;

  /**
   * We need to know which {@link ContributionApi} belongs to which{@link PrimaryApi}
   */
  private Map<String, List<PrimaryApi<?>>> primaryApisByContributionClass;

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  @Lazy
  private InvocationRegisterApi self;

  /**
   * The api register is the central repository of all the known apis available all over the tenant.
   * This contains the apis provided by all modules and applications. These are used to fill the
   * call info of the {@link ApiInvocationHandler}s in this module. There can be more than one api
   * for a given interface class. But the most often search pattern is the search with class name.
   * It's important that the {@link ApiDescriptor}s remains the same and updated only. Therefore the
   * {@link ApiInvocationHandler} instances can refer to the {@link ApiDescriptor} objects directly.
   */
  private final Map<String, Map<String, ApiDescriptor>> apiRegister = new HashMap<>();

  /**
   * All api instances that our application provides for invoke an api call.
   */
  private final Map<URI, Object> apiInstanceByApiDataUri = new HashMap<>();

  /**
   * The runtime instances known in the cluster by Api uri. This map is always updated by the
   * maintenance.
   */
  private Map<URI, List<UUID>> runtimesByApis = new HashMap<>();

  /**
   * The {@link #eventSubscriptionsByApis} is a constantly maintained map for the event
   * subscriptions of an api. The value is a list of event subscription that can be filtered by the
   * name of the event.
   */
  private Map<String, List<EventSubscriptionData>> eventSubscriptionsByApis = new HashMap<>();

  /**
   * The {@link #publishedEventsByApis} is a constantly maintained map for the published events of
   * an api. The value is a list of published event that can be filtered by the name of the event.
   */
  private Map<String, List<PublishedEventData>> publishedEventsByApis = new HashMap<>();

  /**
   * Prevent from accessing the registry before the first maintain cycle.
   */
  private CountDownLatch maintainLatch = new CountDownLatch(1);

  /**
   * All the api providers that are provided by the given application.
   */
  @Autowired(required = false)
  @Lazy
  private List<ProviderApiInvocationHandler<?>> providedApis;

  /**
   *
   */
  private boolean initialized = false;

  /**
   * All the asynchronous channels we have in the current configuration.
   */
  @Autowired(required = false)
  @Lazy
  private List<AsyncInvocationChannel> channels;

  /**
   * The channels by name that is the processed map of the {@link #channels} autowired list.
   */
  private Map<String, AsyncInvocationChannel> channelsByName = new HashMap<>();

  /**
   * This executor is responsible for reading and enqueue of the scheduled requests. We need
   * executor to be able to manage the load of this maintenance effort.
   */
  private ThreadPoolExecutor executorService;

  /**
   * Core thread pool size of the {@link #executorService}.
   */
  @Value("${InvocationRegisterApi.readScheduledInvocations.corePoolSize:2}")
  private int corePoolSize = 2;

  /**
   * The maximum thread pool size of the {@link #executorService}.
   */
  @Value("${InvocationRegisterApi.readScheduledInvocations.maximumPoolSize:10}")
  private int maximumPoolSize = 10;

  @Value("${invocationregistry.refresh.fixeddelay:30000}")
  private int refreshFrequency = 30_000;

  @Value("{invocationregistry.refresh-async-channels.fixeddelay:60000")
  private String asyncChannelRefreshFrequency = "60000";

  private StoredCollectionDescriptor apiRegistryList =
      new StoredCollectionDescriptor().collectionType(CollectionTypeEnum.LIST)
          .schema(Invocations.INVOCATION_SCHEME)
          .name(InvocationApiMdmConfig.MDM_ENTRY_APIREGISTRY);

  /**
   * This is the OrgApi scheme where we save the settings for the notify.
   */
  private Supplier<Storage> storage = new Supplier<>() {

    private Storage storageInstance;

    @Override
    public Storage get() {
      if (storageInstance == null && storageApi != null
          && storageApi.getDefaultObjectStorage() != null) {
        storageInstance = storageApi.get(Invocations.APIREGISTRATION_SCHEME);
      }
      return storageInstance;
    }
  };

  @EventListener(ApplicationStartedEvent.class)
  @Override
  public void initRegistry() {
    long startTime = System.currentTimeMillis();

    if (storage.get() == null) {
      return;
    }
    if (!storage.get().exists(REGISTER_URI)) {
      try {
        storage.get().saveAsNew(new ApiRegistryData().uri(REGISTER_URI));
      } catch (Exception e) {
        log.debug("Unable to save the Api registry", e);
      }
    }
    // Update ApiRegistryData apis provided by our runtime
    storage.get().update(REGISTER_URI, ApiRegistryData.class, r -> {
      // We save all the provided apis into the invocation store.
      if (providedApis != null) {
        // TODO A quick win to resolve the subscriptions inside one runtime first. Here we add the
        // published events also...
        Map<ProviderApiInvocationHandler<?>, ApiData> apiDataMap = new HashMap<>();
        for (ProviderApiInvocationHandler<?> apiHandler : providedApis) {
          ApiData apiData = apiHandler.getData();
          apiDataMap.put(apiHandler, apiData);
        }

        for (Entry<ProviderApiInvocationHandler<?>, ApiData> entry : apiDataMap.entrySet()) {
          ApiData apiData = entry.getValue();
          if (!storage.get().exists(apiData.getUri())) {
            storage.get().saveAsNew(apiData);
            if (!r.getApiList().contains(apiData.getUri())) {
              r.addApiListItem(apiData.getUri());
            }
          } else {
            storage.get().update(apiData.getUri(), ApiData.class, a -> apiData);
          }
          addToApiRegister(apiData);
          apiInstanceByApiDataUri.put(apiData.getUri(), entry.getKey().getApiInstance());
        }
      }
      return r;
    });

    // refresh primary api map
    fillPrimaryApiMap();

    initRuntimeChannels();

    // End time
    long endTime = System.currentTimeMillis();
    // Calculate duration and log
    long duration = endTime - startTime;
    log.info("initRegistry execution time: {} ms", duration);
  }

  private void initRuntimeChannels() {
    // Manage the asynchronous invocation channels.
    InvocationApi invocationApi = applicationContext.getBean(InvocationApi.class);
    if (channels != null) {
      for (AsyncInvocationChannel channel : channels) {
        AsyncInvocationChannelSetup channelSetup = (AsyncInvocationChannelSetup) channel;
        channelSetup.setInvocationApi(invocationApi);
        channelSetup.setInvocationRegisterApi(self);
        // Construct a RuntimeAsyncChannel object for this channel and set this.
        channelSetup.start();
        channelsByName.put(channel.getName(), channel);
      }
    }

    // update runtime with our provided apis
    if (applicationRuntimeApi != null) {
      applicationRuntimeApi.setApis(new ArrayList<>(apiInstanceByApiDataUri.keySet()));
      URI runtimeUri = applicationRuntimeApi.self().getUri();

      // Save the async channels for the runtime.
      RuntimeAsyncChannelList runtimeChannelList =
          new RuntimeAsyncChannelList().runtimeUri(applicationRuntimeApi.self().getUri());
      if (channels != null) {
        Storage storageAsyncReg = storageApi.get(Invocations.ASYNC_CHANNEL_REGISTRY);
        for (Entry<String, AsyncInvocationChannel> entry : channelsByName.entrySet()) {
          AsyncInvocationChannelSetup channelSetup = (AsyncInvocationChannelSetup) entry.getValue();
          // Construct a RuntimeAsyncChannel object for this channel and set this.
          URI asynChannelUri =
              storageAsyncReg.saveAsNew(new RuntimeAsyncChannel().runtimeUri(runtimeUri));
          channelSetup
              .setUri(asynChannelUri);
          runtimeChannelList.putChannelsItem(entry.getKey(), asynChannelUri);
        }
      }

      collectionApi.reference(Invocations.INVOCATION_SCHEME, Invocations.ASYNC_CHANNEL_REGISTRY,
          RuntimeAsyncChannelRegistry.class).update(r -> {
            RuntimeAsyncChannelRegistry updatedRegistry =
                r == null ? new RuntimeAsyncChannelRegistry() : r;
            updatedRegistry.addRuntimesItem(
                runtimeChannelList);
            return updatedRegistry;
          });
    }

    executorService =
        new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 1, TimeUnit.MINUTES,
            new LinkedBlockingQueue<>());
    // TODO Improve with CountDownLatch!
    initialized = true;
  }

  // TODO move to another api
  @Scheduled(initialDelayString = "${invocationregistry.refresh-async-channels.fixeddelay:60000}",
      fixedDelayString = "${invocationregistry.refresh-async-channels.fixeddelay:60000}")
  public void refreshAsyncChannlers() {
    URI lockUri =
        UriUtils.constructMethodUri(Invocations.INVOCATION_SCHEME, InvocationRegisterApi.class,
            "refreshAsyncChannlers");
    Lock lock = objectApi.getLock(lockUri);
    if (!lock.tryLock()) {
      return;
    }
    try {
      try {
        maintainLatch.await();
      } catch (InterruptedException e) {
        log.error("Wait for maintain interrupted.", e);
      }
      manageAsyncChannels(applicationRuntimeApi.getActiveRuntimes());
    } finally {
      lock.unlock();
    }
  }

  @Override
  @Scheduled(initialDelayString = "${invocationregistry.refresh.fixeddelay:0}",
      fixedDelayString = "${invocationregistry.refresh.fixeddelay:30000}",
      scheduler = "invocationRegisterScheduler")
  public void refreshRegistry() {

    URI lockUri =
        UriUtils.constructMethodUri(Invocations.INVOCATION_SCHEME, InvocationRegisterApi.class,
            "refreshRegistry");
    Lock lock = objectApi.getLock(lockUri);
    lock.lock();
    try {
      if (storage.get() == null || !storage.get().exists(REGISTER_URI) || !initialized) {
        return;
      }
      if (applicationRuntimeApi == null) {
        // if there is ApplicationRuntimeApi, then we can't refresh the apis
        return;
      }
      UUID myRuntimeUUID = applicationRuntimeApi.self().getUuid();

      Set<URI> activeApis = new HashSet<>();
      Map<URI, List<UUID>> activeRuntimesByApisMap = new HashMap<>();
      // First of all fill our own apis. We don't need the getApis call to know what we are
      // providing.
      for (URI api : apiInstanceByApiDataUri.keySet()) {
        List<UUID> runtimes =
            activeRuntimesByApisMap.computeIfAbsent(api, r -> new ArrayList<>());
        runtimes.add(myRuntimeUUID);
      }

      // Add our apis to the active apis. They are obviously active ones.
      activeApis.addAll(apiInstanceByApiDataUri.keySet());

      // Get apis from all active runtime. The current instance is an exception because we know what
      // we are providing.
      List<ApplicationRuntime> activeOtherRuntimes = applicationRuntimeApi.getActiveRuntimes()
          .stream()
          .filter(r -> !r.getUuid().equals(myRuntimeUUID)).collect(toList());
      for (ApplicationRuntime applicationRuntime : activeOtherRuntimes) {
        List<URI> runtimeApis = applicationRuntimeApi.getApis(applicationRuntime.getUuid());

        if (!CollectionUtils.isEmpty(runtimeApis)) {
          for (URI api : runtimeApis) {
            List<UUID> runtimes =
                activeRuntimesByApisMap.computeIfAbsent(api, r -> new ArrayList<>());
            runtimes.add(applicationRuntime.getUuid());
          }

          activeApis.addAll(runtimeApis);
        }
      }

      runtimesByApis = activeRuntimesByApisMap;

      // Add the MDM registered apis to the active apis.
      activeApis.addAll(collectionApi.list(apiRegistryList).uris());

      List<URI> currentActiveApiUris = apiRegister.values().stream()
          .flatMap(m -> m.values().stream().map(ad -> ad.getApiData().getUri())).collect(toList());

      // apis that become active
      Set<URI> apisToAdd = new HashSet<>(activeApis);
      apisToAdd.removeAll(currentActiveApiUris);

      // apis that are not active anymore
      Set<URI> apisToRemove = new HashSet<>(currentActiveApiUris);
      apisToRemove.removeAll(activeApis);

      // apis = activeApis;
      addApis(apisToAdd);
      removeApis(apisToRemove);
      // At last we manage the channels of the
      maintainLatch.countDown();
    } finally {
      lock.unlock();
    }
  }

  /**
   * Manage the central registry of the {@link RuntimeAsyncChannelRegistry} that contains the async
   * channels managed by the runtimes. The current implementation detects the inactive runtimes and
   * pick up their lost invocations.
   *
   * @param activeRuntimes the currently known active {@link ApplicationRuntime}s, not null
   */
  private void manageAsyncChannels(List<ApplicationRuntime> activeRuntimes) {
    URI runtimeUri = applicationRuntimeApi.self().getUri();
    StoredReference<RuntimeAsyncChannelRegistry> refChannelRegistry =
        collectionApi.reference(Invocations.INVOCATION_SCHEME, Invocations.ASYNC_CHANNEL_REGISTRY,
            RuntimeAsyncChannelRegistry.class);
    Map<URI, ApplicationRuntime> runtimeMap =
        activeRuntimes.stream().collect(toMap(ApplicationRuntime::getUri, ar -> ar));

    List<RuntimeAsyncChannelList> runtimesToRemove = refChannelRegistry.get().getRuntimes().stream()
        .filter(ar -> !runtimeMap.containsKey(ar.getRuntimeUri())).collect(toList());

    if (!runtimesToRemove.isEmpty()) {
      // We have runtimes to remove so lock the channel list objects.

      refChannelRegistry.update(r -> {
        RuntimeAsyncChannelRegistry updatedRegistry =
            (r == null ? new RuntimeAsyncChannelRegistry() : r);

        // Define the to remove again.
        List<RuntimeAsyncChannelList> toRemove = updatedRegistry.getRuntimes().stream()
            .filter(ar -> !runtimeMap.containsKey(ar.getRuntimeUri())).collect(toList());


        List<RuntimeAsyncChannelList> toRemoveFinally = new ArrayList<>();
        for (RuntimeAsyncChannelList removedRuntimeChanelList : toRemove) {
          // Try to enqueue all the channels of the inactive runtime.
          List<String> pickedUpChannels = new ArrayList<>();
          for (Entry<String, URI> entry : removedRuntimeChanelList.getChannels().entrySet()) {
            // First update all of the AsyncRequests to refer this runtime if we can manage the
            // given channel.
            AsyncInvocationChannel asyncInvocationChannel = channelsByName.get(entry.getKey());
            if (asyncInvocationChannel != null) {
              RuntimeAsyncChannel runtimeAsyncChannel =
                  storageApi.getStorage(entry.getValue()).read(entry.getValue(),
                      RuntimeAsyncChannel.class);

              saveAndEnqueueInvocationRequest(asyncInvocationChannel,
                  runtimeAsyncChannel.getInvocationRequests());

              pickedUpChannels.add(entry.getKey());
            }
          }
          pickedUpChannels.stream().forEach(c -> removedRuntimeChanelList.getChannels().remove(c));
          if (removedRuntimeChanelList.getChannels().isEmpty()) {
            toRemoveFinally.add(removedRuntimeChanelList);
          }
        }
        toRemoveFinally.stream().forEach(ac -> updatedRegistry.getRuntimes().remove(ac));
        return updatedRegistry;
      });
    }

  }

  @Scheduled(
      fixedDelayString = "${InvocationRegisterApi.readScheduledInvocations.fixeddelay:5000}")
  public void readScheduledInvocations() {
    URI lockUri =
        UriUtils.constructMethodUri(Invocations.INVOCATION_SCHEME, InvocationRegisterApi.class,
            "readScheduledInvocations-");
    Lock lock = objectApi.getLock(lockUri);
    if (!lock.tryLock()) {
      return;
    }
    try {
      if (executorService != null && channels != null) {
        channels.stream().map(c -> executorService.submit(() -> {
          enqueueScheduledInvocations(c);
        })).forEach(f -> {
          try {
            f.get();
          } catch (InterruptedException e) {
            log.error("The scheduled request processing was interrupted.", e);
          } catch (ExecutionException e) {
            log.error("The scheduled request processing produced exception.", e);
          }
        });
      }
    } finally {
      lock.unlock();
    }
  }

  public List<AsyncInvocationChannel> getChannels() {
    return Collections.unmodifiableList(channels);
  }

  public void enqueueScheduledInvocations(AsyncInvocationChannel channel) {

    StoredReference<AsyncChannelScheduledInvocationList> refScheduled =
        collectionApi.reference(Invocations.INVOCATION_SCHEME,
            scheduledInvocationReferenceName(channel.getName()),
            AsyncChannelScheduledInvocationList.class);
    OffsetDateTime limitTime = OffsetDateTime.now();
    if (refScheduled.exists()) {
      refScheduled.update(scheduledList -> {
        // check if list exists
        scheduledList =
            scheduledList == null ? new AsyncChannelScheduledInvocationList() : scheduledList;
        // The list of invocation is always ordered by the schedule time.
        List<ScheduledInvocationRequest> toExecute = new ArrayList<>();
        for (int idx = 0; idx < scheduledList.getInvocationRequests().size(); idx++) {
          ScheduledInvocationRequest scheduledInvocationRequest =
              scheduledList.getInvocationRequests().get(idx);
          if (scheduledInvocationRequest.getScheduledAt().isAfter(limitTime)) {
            // If we found the first scheduled invocation that before the limit time then we stop
            // because until this item we had all the requests to be enqueued. The idx is now on
            // the
            // first request that shouldn't be enqueued.
            break;
          }
          toExecute.add(scheduledInvocationRequest);
        }
        if (!toExecute.isEmpty()) {
          // We have a list of requests to be executed immediately. Remove them from the scheduled
          // list.
          scheduledList.getInvocationRequests().subList(0, toExecute.size()).clear();
          saveAndEnqueueInvocationRequest(channel,
              toExecute.stream().map(si -> objectApi.getLatestUri(si.getRequestUri()))
                  .collect(toList()));
        }
        return scheduledList;
      });
    }
  }

  private final void saveAndEnqueueInvocationRequest(
      AsyncInvocationChannel asyncInvocationChannel, List<URI> requestUris) {
    if (applicationRuntimeApi != null) {
      // Save the request to remember to execute if this runtime fails. We set the runtime
      // identifier
      // to see which runtime is responsible for the invocation currently.
      ApplicationRuntime applicationRuntime = applicationRuntimeApi.self();

      Storage storageAsyncReg = storageApi.get(Invocations.ASYNC_CHANNEL_REGISTRY);
      log.debug("saveAndEnqueueInvocationRequest, add invocation request item {} - {}",
          asyncInvocationChannel.getName(),
          asyncInvocationChannel.getUri());
      // TODO move it to before commit
      storageAsyncReg.update(asyncInvocationChannel.getUri(), RuntimeAsyncChannel.class, rac -> {
        if (requestUris != null) {
          requestUris.forEach(
              requestUri -> rac.addInvocationRequestsItem(objectApi.getLatestUri(requestUri)));
        }
        return rac;
      });
      // Now we update all the requests to blongs to this runtime and we enqueue them for the entry.
      for (URI asyncRequestUri : requestUris) {

        Storage storageRequest = storageApi.getStorage(asyncRequestUri);
        StorageObjectLock lockRequest = storageRequest.getLock(asyncRequestUri);
        lockRequest.lock();
        try {
          StorageObject<AsyncInvocationRequest> soRequest =
              storageRequest.load(objectApi.getLatestUri(asyncRequestUri),
                  AsyncInvocationRequest.class);
          soRequest.asMap().getObjectAsMap().put(AsyncInvocationRequest.RUNTIME_URI,
              applicationRuntime.getUri());
          enqueueAsyncRequest(
              new AsyncInvocationRequestEntry(asyncInvocationChannel,
                  soRequest.getObject().uri(storageRequest.save(soRequest))));
        } finally {
          lockRequest.unlock();
        }
      }

    }
  }

  /**
   * Enqueue the given entry even if we are in an active transaction or not. If we are in an active
   * transaction then the execution starts at the successful finish of the transaction (onCommit).
   * Else the execution starts right now.
   *
   * @param asyncInvocationRequestEntry the {@link AsyncInvocationRequestEntry} to enqueue, not null
   */
  private final void enqueueAsyncRequest(AsyncInvocationRequestEntry asyncInvocationRequestEntry) {
    if (TransactionSynchronizationManager.isSynchronizationActive()) {
      getAsyncRequestTransactionHandler().addRequestToInvoke(asyncInvocationRequestEntry);
    } else {
      asyncInvocationRequestEntry.invoke();
    }
  }

  private AsyncRequestTransactionHandler getAsyncRequestTransactionHandler() {
    return TransactionUtils.getTransactionHandler(
        ASYNC_REQUESTS_HANDLER,
        AsyncRequestTransactionHandler.class,
        () -> new AsyncRequestTransactionHandler());
  }

  private static class ChannelInfo {
    String name;
    URI uri;
    URI runtimeUri;
    AsyncInvocationChannel localChannel;
    final List<URI> requestsToAdd = new ArrayList<>();
  }

  private static class ScheduledRequestsForChannel {
    String channelName;
    final List<ScheduledRequests> requests = new ArrayList<>();
  }

  private static class ScheduledRequests {
    List<URI> requestUris = new ArrayList<>();
    OffsetDateTime executeAt;
  }


  private final class AsyncRequestTransactionHandler
      implements TransactionSynchronization {

    private final List<Object> requestsToSaveAndEnqueue = new ArrayList<>();
    private final List<AsyncInvocationRequestEntry> requestsToInvoke =
        new ArrayList<>();

    public void addRequestToSaveAndEnqueue(Object request) {
      requestsToSaveAndEnqueue.add(request);
    }

    public void addRequestToInvoke(AsyncInvocationRequestEntry request) {
      requestsToInvoke.add(request);
    }

    @Override
    public void suspend() {
      TransactionSynchronizationManager.unbindResource(ASYNC_REQUESTS_HANDLER);
      log.trace("async suspend");
    }

    @Override
    public void resume() {
      TransactionSynchronizationManager.bindResource(ASYNC_REQUESTS_HANDLER, true);
      log.trace("async resume");
    }

    @Override
    public void beforeCommit(boolean readOnly) {
      saveAndEnqueuAsyncRequests(requestsToSaveAndEnqueue);
    }

    @Override
    public void beforeCompletion() {
      requestsToSaveAndEnqueue.clear();
    }

    @Override
    public void afterCompletion(int status) {
      if (status == TransactionSynchronization.STATUS_COMMITTED) {
        // execute asyncInvocationRequests
        for (AsyncInvocationRequestEntry asyncInvocation : requestsToInvoke) {
          asyncInvocation.invoke();
        }
      } else if (status == TransactionSynchronization.STATUS_UNKNOWN) {
        log.warn("Transaction state is STATUS_UNKNOWN!");
      }
      // remove asyncInvocationRequests regardless of status
      requestsToInvoke.clear();
      TransactionSynchronizationManager.unbindResource(ASYNC_REQUESTS_HANDLER);
    }
  }

  private void saveAndEnqueuAsyncRequests(List<Object> requests) {
    // gather all channel and runtime infos
    Map<String, ChannelInfo> channelInfos = readChannelInfos(requests);
    // save all asyncRequests
    saveRequestObjects(requests, channelInfos);
    // save requests to local channels - order doesn't matter
    saveRequestsIntoLocalChannels(channelInfos);
    // run or "schedule" requests in order
    enqueueOrScheduleRequest(requests, channelInfos);
  }

  private Map<String, ChannelInfo> readChannelInfos(List<Object> requestsToSaveAndEnqueue) {
    Map<String, ChannelInfo> channelInfos = new HashMap<>();
    List<ChannelInfo> remoteChannels = new ArrayList<>();
    // collect all channel names
    Set<String> channelNames = collectChannelNames(requestsToSaveAndEnqueue);
    // local channels
    readLocalChannels(channelInfos, remoteChannels, channelNames);
    // remote channels
    readRemoteChannels(channelInfos, remoteChannels);
    return channelInfos;
  }

  private Set<String> collectChannelNames(List<Object> requestsToSaveAndEnqueue) {
    Set<String> channelNames = new HashSet<>();
    for (Object request : requestsToSaveAndEnqueue) {
      if (request instanceof AsyncInvocationRequest) {
        channelNames.add(
            ((AsyncInvocationRequest) request).getChannel());
      } else if (request instanceof ObjectNode) {
        channelNames.add(
            ((ObjectNode) request).getValueAsString(AsyncInvocationRequest.CHANNEL));
      } else if (request instanceof ScheduledRequestsForChannel) {
        channelNames.add(
            ((ScheduledRequestsForChannel) request).channelName);
      }
    }
    return channelNames;
  }

  private void readLocalChannels(Map<String, ChannelInfo> channelInfos,
      List<ChannelInfo> remoteChannels,
      Set<String> channelNames) {
    URI applicationRuntimeUri = null;
    if (applicationRuntimeApi != null) {
      applicationRuntimeUri = applicationRuntimeApi.self().getUri();
    }
    for (String channel : channelNames) {
      ChannelInfo info = new ChannelInfo();
      info.name = channel;
      AsyncInvocationChannel localChannel = getLocalChannel(info.name);
      if (localChannel != null) {
        info.localChannel = localChannel;
        info.uri = localChannel.getUri();
        info.runtimeUri = applicationRuntimeUri;
        channelInfos.put(channel, info);
      } else {
        remoteChannels.add(info);
      }
    }
  }

  private void readRemoteChannels(Map<String, ChannelInfo> channelInfos,
      List<ChannelInfo> remoteChannels) {
    if (!remoteChannels.isEmpty()) {
      RuntimeAsyncChannelRegistry runtimeAsyncChannelRegistry = collectionApi
          .reference(Invocations.INVOCATION_SCHEME, Invocations.ASYNC_CHANNEL_REGISTRY,
              RuntimeAsyncChannelRegistry.class)
          .get();
      for (ChannelInfo channel : remoteChannels) {
        ObjectNode channelNode = runtimeAsyncChannelRegistry.getRuntimes().stream()
            .filter(racr -> racr.getChannels().containsKey(channel.name)).findFirst()
            .map(racr -> racr.getChannels().get(channel.name))
            .map(cUri -> objectApi.loadLatest(cUri))
            .orElseThrow(
                // no channel found in local nor in external runtimes -> exception
                () -> new IllegalStateException("There is no channel available: " + channel));
        channel.uri = objectApi.getLatestUri(channelNode.getObjectUri());
        channel.runtimeUri = channelNode.getValue(URI.class, RuntimeAsyncChannel.RUNTIME_URI);
        channelInfos.put(channel.name, channel);
      }
    }
  }

  private void saveRequestObjects(List<Object> requests, Map<String, ChannelInfo> channelInfos) {
    if (applicationRuntimeApi != null) {
      for (Object obj : requests) {
        if (obj instanceof AsyncInvocationRequest) {
          AsyncInvocationRequest request = (AsyncInvocationRequest) obj;
          ChannelInfo channel = channelInfos.get(request.getChannel());
          request.runtimeUri(channel.runtimeUri);
          request
              .uri(objectApi.saveAsNew(Invocations.INVOCATION_SCHEME, request));
          channel.requestsToAdd.add(objectApi.getLatestUri(request.getUri()));
        } else if (obj instanceof ObjectNode) {
          ObjectNode request = (ObjectNode) obj;
          ChannelInfo channel =
              channelInfos.get(request.getValueAsString(AsyncInvocationRequest.CHANNEL));
          request.setValue(channel.runtimeUri, AsyncInvocationRequest.RUNTIME_URI);
          URI uri = objectApi.save(request);
          channel.requestsToAdd.add(objectApi.getLatestUri(uri));
        } else if (obj instanceof ScheduledRequestsForChannel) {
          ScheduledRequestsForChannel schReq = (ScheduledRequestsForChannel) obj;
          ChannelInfo channel = channelInfos.get(schReq.channelName);
          List<URI> uris = schReq.requests.stream()
              .flatMap(req -> req.requestUris.stream())
              .distinct()
              .map(objectApi::getLatestUri)
              .collect(toList());
          List<ObjectNode> reqNodes = objectApi.loadLatestBatch(uris);
          for (ObjectNode req : reqNodes) {
            URI currentRuntimeUri = req.getValue(URI.class, AsyncInvocationRequest.RUNTIME_URI);
            if (!objectApi.equalsIgnoreVersion(currentRuntimeUri, channel.runtimeUri)) {
              req.setValue(channel.runtimeUri, AsyncInvocationRequest.RUNTIME_URI);
              objectApi.save(req);
            }
          }
          channel.requestsToAdd.addAll(uris);
        }
      }
    }
  }

  private void saveRequestsIntoLocalChannels(Map<String, ChannelInfo> channelInfos) {
    List<URI> channelUris = channelInfos.values().stream()
        .filter(ch -> ch.localChannel != null)
        .map(ch -> ch.uri)
        .collect(toList());
    List<Lock> channelLocks = objectApi.lockAll(channelUris);
    try {
      for (ChannelInfo channel : channelInfos.values()) {
        if (channel.localChannel != null) {
          Storage storageAsyncReg = storageApi.get(Invocations.ASYNC_CHANNEL_REGISTRY);
          log.debug("saveRequestsToChannel {} - {}", channel.name, channel.uri);
          storageAsyncReg.update(channel.uri, RuntimeAsyncChannel.class, rac -> {
            if (rac.getInvocationRequests() == null) {
              rac.setInvocationRequests(new ArrayList<>());
            }
            rac.getInvocationRequests().addAll(channel.requestsToAdd);
            return rac;
          });
        }
      }
    } finally {
      objectApi.unlockAll(channelLocks);
    }
  }

  private void enqueueOrScheduleRequest(List<Object> requests,
      Map<String, ChannelInfo> channelInfos) {
    OffsetDateTime now = OffsetDateTime.now();
    // we keep all scheduled requests for channel in this map
    Map<String, ScheduledRequestsForChannel> scheduledRequests =
        new HashMap<>();
    for (Object obj : requests) {
      AsyncInvocationRequest request = null;
      if (obj instanceof AsyncInvocationRequest) {
        request = (AsyncInvocationRequest) obj;
      } else if (obj instanceof ObjectNode) {
        request = objectApi.read(((ObjectNode) obj).getResultUri(), AsyncInvocationRequest.class);
      } else if (obj instanceof ScheduledRequestsForChannel) {
        // this is originally requested from outside
        ScheduledRequestsForChannel req = (ScheduledRequestsForChannel) obj;
        ScheduledRequestsForChannel schReq =
            getScheduledRequestForChannel(scheduledRequests, req.channelName);
        schReq.requests.addAll(req.requests);
      }
      if (request != null) {
        ChannelInfo channel = channelInfos.get(request.getChannel());
        if (channel.localChannel != null) {
          enqueueAsyncRequest(new AsyncInvocationRequestEntry(channel.localChannel, request));
        } else {
          // "remote" execution is to schedule this request immediately
          ScheduledRequestsForChannel schReq =
              getScheduledRequestForChannel(scheduledRequests, channel.name);
          ScheduledRequests schReqs = new ScheduledRequests();
          schReqs.requestUris = Arrays.asList(request.getUri());
          schReqs.executeAt = now;
          schReq.requests.add(schReqs);
        }
      }
    }
    Map<String, StoredReference<AsyncChannelScheduledInvocationList>> refsByChannel =
        scheduledRequests.keySet().stream()
            .collect(toMap(
                ch -> ch,
                this::getScheduledInvocationsRef));
    List<URI> refUris = refsByChannel.values().stream()
        .map(StoredReference::getUri)
        .collect(toList());
    List<Lock> refLocks = objectApi.lockAll(refUris);
    try {
      for (ScheduledRequestsForChannel schReq : scheduledRequests.values()) {
        scheduleAsyncInvocationRequestInternal(
            refsByChannel.get(schReq.channelName),
            schReq);
      }
    } finally {
      objectApi.unlockAll(refLocks);
    }
  }

  private ScheduledRequestsForChannel getScheduledRequestForChannel(
      Map<String, ScheduledRequestsForChannel> scheduledRequests, String channelName) {
    return scheduledRequests.computeIfAbsent(channelName, ch -> {
      ScheduledRequestsForChannel result = new ScheduledRequestsForChannel();
      result.channelName = ch;
      return result;
    });
  }


  private void fillPrimaryApiMap() {
    primaryApisByContributionClass = new HashMap<>();
    if (primaryApis != null) {
      for (PrimaryApi<?> primaryApi : primaryApis) {
        Class<?> innerApiClass = primaryApi.getContributionApiClass();
        List<PrimaryApi<?>> primaryApisForClass = primaryApisByContributionClass
            .computeIfAbsent(innerApiClass.getName(), i -> new ArrayList<>());
        primaryApisForClass.add(primaryApi);
      }
    }
  }

  private void removeApis(Set<URI> apiUris) {
    List<StorageObject<ApiData>> apiDataSos =
        storage.get().loadBatch(new ArrayList<>(apiUris), ApiData.class);

    for (StorageObject<ApiData> apiDataSo : apiDataSos) {
      ApiData apiData = apiDataSo.getObject();
      removeFromApiRegister(apiData);
      unregisterFromPrimaryApi(apiData);
    }
  }

  private void removeFromApiRegister(ApiData apiData) {
    Map<String, ApiDescriptor> apisByName = apiRegister.get(apiData.getInterfaceName());
    apisByName.remove(apiData.getName());

    if (apisByName.isEmpty()) {
      apiRegister.remove(apiData.getInterfaceName());
      for (EventSubscriptionData subscription : apiData.getEventSubscriptions()) {
        List<EventSubscriptionData> subscriptions =
            eventSubscriptionsByApis.get(subscription.getApi());
        subscriptions.remove(subscription);
      }
    }
  }

  private void addApis(Set<URI> apiUris) {
    List<StorageObject<ApiData>> apiDataSos =
        storage.get().loadBatch(new ArrayList<>(apiUris), ApiData.class);

    for (StorageObject<ApiData> apiDataSo : apiDataSos) {
      ApiData apiData = apiDataSo.getObject();
      addToApiRegister(apiData);
      registerToPrimaryApi(apiData);
    }
  }

  /**
   * Adds the given {@link ApiData} to the local in memory register. It can be the startup of this
   * runtime from the {@link #initRegistry()} call or it can be a {@link #refreshRegistry()} when
   * the available apis are read from the storage.
   *
   * @param apiData the {@link ApiData} of a service interface to enable remote access, not null
   * @return the {@link ApiDescriptor} describing a remotely callable service interface
   */
  private ApiDescriptor addToApiRegister(ApiData apiData) {
    Map<String, ApiDescriptor> apisByName =
        apiRegister.computeIfAbsent(apiData.getInterfaceName(), n -> new HashMap<>());
    ApiDescriptor apiDescriptor =
        apisByName.computeIfAbsent(apiData.getName(), n -> new ApiDescriptor(apiData));
    for (EventSubscriptionData subscription : apiData.getEventSubscriptions()) {
      List<EventSubscriptionData> subscriptionsByApi =
          eventSubscriptionsByApis.computeIfAbsent(subscription.getApi(), n -> new ArrayList<>());
      subscriptionsByApi.add(subscription);
    }
    List<PublishedEventData> eventsByApi =
        publishedEventsByApis.computeIfAbsent(apiData.getInterfaceName(), n -> new ArrayList<>());
    for (PublishedEventData event : apiData.getPublishedEvents()) {
      eventsByApi.add(event);
    }

    return apiDescriptor;
  }

  /**
   * If the api is a {@link ContributionApi}, then register to his {@link PrimaryApi}
   *
   * @param apiData the {@link ApiData} of the contribution API to register, not null
   */
  private void registerToPrimaryApi(ApiData apiData) {

    List<PrimaryApi<?>> primaryApisForClass =
        primaryApisByContributionClass.get(apiData.getInterfaceName());
    if (primaryApisForClass != null) {
      for (PrimaryApi<?> primaryApi : primaryApisForClass) {
        registerAsRemoteApi(apiData, primaryApi);
      }
    }
  }

  private void unregisterFromPrimaryApi(ApiData apiData) {
    List<PrimaryApi<?>> primaryApisForClass =
        primaryApisByContributionClass.get(apiData.getInterfaceName());
    if (primaryApisForClass != null) {
      for (PrimaryApi<?> primaryApi : primaryApisForClass) {
        primaryApi.unregisterApi(apiData.getName());
      }
    }
  }

  protected <T extends ContributionApi> void registerAsRemoteApi(ApiData apiData,
      PrimaryApi<T> primaryApi) {
    T asRemote = ApiInvocationHandler.createProxy(primaryApi.getContributionApiClass(),
        apiData.getName(), applicationContext.getBean(InvocationApi.class));
    primaryApi.registerApi(asRemote, apiData.getName());
  }

  @Override
  public Object getApiInstance(URI apiDataUri) {
    try {
      maintainLatch.await();
    } catch (InterruptedException e) {
      log.error("Wait for maintain interrupted.", e);
    }
    return apiInstanceByApiDataUri.get(apiDataUri);
  }

  @Override
  public ApiDescriptor getApi(String interfaceClass, String name) {
    try {
      maintainLatch.await();
    } catch (InterruptedException e) {
      log.error("Wait for maintain interrupted.", e);
    }
    Map<String, ApiDescriptor> apisByName = apiRegister.get(interfaceClass);

    if (apisByName != null) {
      return apisByName.size() == 1 ? apisByName.values().iterator().next() : apisByName.get(name);
    }

    return null;
  }

  @Override
  public List<EventSubscriptionData> getSubscriptions(String interfaceName) {
    try {
      maintainLatch.await();
    } catch (InterruptedException e) {
      log.error("Wait for maintain interrupted.", e);
    }
    return eventSubscriptionsByApis.getOrDefault(interfaceName, Collections.emptyList());
  }

  @Override
  public List<UUID> getRuntimesForApi(URI apiDataUri) {
    try {
      maintainLatch.await();
    } catch (InterruptedException e) {
      log.error("Wait for maintain interrupted.", e);
    }
    return runtimesByApis.getOrDefault(apiDataUri, Collections.emptyList());
  }

  @Override
  public void destroy() throws Exception {
    for (AsyncInvocationChannel channel : channelsByName.values()) {
      AsyncInvocationChannelSetup channelSetup = (AsyncInvocationChannelSetup) channel;
      channelSetup.stop();
    }
  }

  @Override
  public void saveAndEnqueueAsyncInvocationRequest(InvocationRequest request,
      String channel) {
    AsyncInvocationChannel localAsyncInvocationChannel = getLocalChannel(channel);
    if (applicationRuntimeApi == null && localAsyncInvocationChannel == null) {
      // no channel was found, and no other runtime is present too search in
      throw new IllegalStateException("There is no channel available: " + channel);
    }
    AsyncInvocationRequest asyncInvocationRequest = new AsyncInvocationRequest()
        .request(request)
        .channel(channel);
    if (TransactionSynchronizationManager.isSynchronizationActive()) {
      getAsyncRequestTransactionHandler().addRequestToSaveAndEnqueue(asyncInvocationRequest);
    } else {
      // saveAndEnqueueAsyncInvocationRequestInternal(asyncInvocationRequest);
      saveAndEnqueuAsyncRequests(Arrays.asList(asyncInvocationRequest));
    }
  }

  // private void saveAndEnqueueAsyncInvocationRequestInternal(AsyncInvocationRequest request) {
  // String channel = request.getChannel();
  // AsyncInvocationChannel localAsyncInvocationChannel = getLocalChannel(channel);
  // if (applicationRuntimeApi != null) {
  // // we may have other runtimes available
  // ApplicationRuntime applicationRuntime = applicationRuntimeApi.self();
  // URI channelUri = null;
  // URI runtimeUri = null;
  // if (localAsyncInvocationChannel == null) {
  // // no local channel found with the given name -> check other runtimes
  // RuntimeAsyncChannelRegistry runtimeAsyncChannelRegistry = collectionApi
  // .reference(Invocations.INVOCATION_SCHEME, Invocations.ASYNC_CHANNEL_REGISTRY,
  // RuntimeAsyncChannelRegistry.class)
  // .get();
  // ObjectNode channelNode = runtimeAsyncChannelRegistry.getRuntimes().stream()
  // .filter(racr -> racr.getChannels().containsKey(channel)).findFirst()
  // .map(racr -> racr.getChannels().get(channel))
  // .map(cUri -> objectApi.loadLatest(cUri))
  // .orElseThrow(
  // // no channel found in local nor in external runtimes -> exception
  // () -> new IllegalStateException("There is no channel available: " + channel));
  // // channel found in other runtime
  // channelUri = objectApi.getLatestUri(channelNode.getObjectUri());
  // runtimeUri = channelNode.getValue(URI.class, RuntimeAsyncChannel.RUNTIME_URI);
  // } else {
  // // set up the with local channel and local runtime
  // channelUri = localAsyncInvocationChannel.getUri();
  // runtimeUri = applicationRuntime.getUri();
  // }
  // // We set the runtime identifier to see which runtime is responsible for the invocation
  // // currently.
  // request.runtimeUri(runtimeUri);
  // request
  // .uri(objectApi.saveAsNew(Invocations.INVOCATION_SCHEME, request));
  //
  //
  // if (runtimeUri.equals(applicationRuntime.getUri())) {
  // // Save the request to remember to execute if this runtime fails.
  // // We save the given invocation into a list related to the application runtime.
  // Storage storageAsyncReg = storageApi.get(Invocations.ASYNC_CHANNEL_REGISTRY);
  // log.debug(
  // "saveAndEnqueueAsyncInvocationRequestInternal, add invocation request item {} - {}",
  // channel, channelUri);
  // storageAsyncReg.update(channelUri, RuntimeAsyncChannel.class, rac -> {
  // return rac
  // .addInvocationRequestsItem(objectApi.getLatestUri(request.getUri()));
  // });
  // } else {
  // // when the channel is in an other runtime, we add it as an instant scheduled request
  // scheduleAsyncInvocationRequest(channel,
  // Arrays.asList(request.getUri()), OffsetDateTime.now());
  // }
  // }
  //
  // if (localAsyncInvocationChannel != null) {
  // // when we have a local channel, try to run it right now
  // AsyncInvocationRequestEntry result =
  // new AsyncInvocationRequestEntry(localAsyncInvocationChannel, request);
  // enqueueAsyncRequest(result);
  // }
  // }

  // @Transactional
  @Override
  public void saveAndEnqueueAsyncInvocationRequest(ObjectNode asyncRequest) {
    // TODO make sure ObjectNode based requests are only local channel??
    // checkLocalChannel(asyncRequest.getValueAsString(AsyncInvocationRequest.CHANNEL));
    if (TransactionSynchronizationManager.isSynchronizationActive()) {
      getAsyncRequestTransactionHandler().addRequestToSaveAndEnqueue(asyncRequest);
    } else {
      // saveAndEnqueueAsyncInvocationRequestInternal(asyncRequest);
      saveAndEnqueuAsyncRequests(Arrays.asList(asyncRequest));
    }
  }

  // private void saveAndEnqueueAsyncInvocationRequestInternal(ObjectNode asyncRequest) {
  //
  // AsyncInvocationChannel asyncInvocationChannel =
  // checkLocalChannel(asyncRequest.getValueAsString(AsyncInvocationRequest.CHANNEL));
  // AsyncInvocationRequest asyncInvocationRequest = null;
  // if (applicationRuntimeApi != null) {
  // ApplicationRuntime applicationRuntime = applicationRuntimeApi.self();
  // asyncRequest.setValue(applicationRuntime.getUri(), AsyncInvocationRequest.RUNTIME_URI);
  // URI uri = objectApi.save(asyncRequest);
  // asyncInvocationRequest = objectApi.read(uri, AsyncInvocationRequest.class);
  // Storage storageAsyncReg = storageApi.get(Invocations.ASYNC_CHANNEL_REGISTRY);
  // storageAsyncReg.update(asyncInvocationChannel.getUri(), RuntimeAsyncChannel.class, rac -> {
  // return rac
  // .addInvocationRequestsItem(objectApi.getLatestUri(uri));
  // });
  // } else {
  // asyncInvocationRequest = asyncRequest.getObject(AsyncInvocationRequest.class);
  // }
  //
  // AsyncInvocationRequestEntry result =
  // new AsyncInvocationRequestEntry(asyncInvocationChannel, asyncInvocationRequest);
  // enqueueAsyncRequest(result);
  // }

  // @Transactional
  @Override
  public AsyncInvocationRequest saveAndScheduleAsyncInvocationRequest(
      InvocationRequest request, String channelName, OffsetDateTime executeAt) {
    AsyncInvocationChannel channel = checkLocalChannel(channelName);
    AsyncInvocationRequest asyncInvocationRequest =
        new AsyncInvocationRequest().request(request);
    asyncInvocationRequest
        .uri(objectApi.saveAsNew(Invocations.INVOCATION_SCHEME, asyncInvocationRequest));

    scheduleAsyncInvocationRequest(channel.getName(),
        Arrays.asList(asyncInvocationRequest.getUri()),
        executeAt);
    return asyncInvocationRequest;
  }

  private final void scheduleAsyncInvocationRequest(String channelName,
      List<URI> requestUris, OffsetDateTime executeAt) {
    if (requestUris == null || requestUris.isEmpty()) {
      return;
    }
    ScheduledRequestsForChannel request = new ScheduledRequestsForChannel();
    request.channelName = channelName;
    ScheduledRequests requests = new ScheduledRequests();
    requests.requestUris = requestUris;
    requests.executeAt = executeAt;
    request.requests.add(requests);
    if (TransactionSynchronizationManager.isSynchronizationActive()) {
      getAsyncRequestTransactionHandler().addRequestToSaveAndEnqueue(request);
    } else {
      saveAndEnqueuAsyncRequests(Arrays.asList(request));
      // scheduleAsyncInvocationRequest(request);
    }
  }

  // private final void scheduleAsyncInvocationRequest(ScheduledRequestsForChannel request) {
  // StoredReference<AsyncChannelScheduledInvocationList> refScheduled =
  // getScheduledInvocationsRef(request.channelName);
  // scheduleAsyncInvocationRequestInternal(refScheduled, request);
  // }

  private StoredReference<AsyncChannelScheduledInvocationList> getScheduledInvocationsRef(
      String channelName) {
    return collectionApi.reference(Invocations.INVOCATION_SCHEME,
        scheduledInvocationReferenceName(channelName),
        AsyncChannelScheduledInvocationList.class);
  }

  private final void scheduleAsyncInvocationRequestInternal(
      StoredReference<AsyncChannelScheduledInvocationList> refScheduled,
      ScheduledRequestsForChannel request) {
    refScheduled.update(scheduledList -> {
      AsyncChannelScheduledInvocationList updatedList =
          scheduledList == null ? new AsyncChannelScheduledInvocationList() : scheduledList;
      for (ScheduledRequests requests : request.requests) {
        ListIterator<ScheduledInvocationRequest> iterList =
            updatedList.getInvocationRequests().listIterator();
        while (iterList.hasNext()) {
          ScheduledInvocationRequest scheduledInvocationRequest =
              iterList.next();
          if (scheduledInvocationRequest.getScheduledAt().isAfter(requests.executeAt)) {
            iterList.previous(); // have to move one step back
            break;
          }
        }
        requests.requestUris
            .forEach(u -> iterList.add(new ScheduledInvocationRequest().requestUri(u)
                .scheduledAt(requests.executeAt)));
      }
      return updatedList;
    });
  }

  private final AsyncInvocationChannel getLocalChannel(String channel) {
    return channelsByName.get(channel);
  }

  private final AsyncInvocationChannel checkLocalChannel(String channel) {
    AsyncInvocationChannel asyncInvocationChannel = getLocalChannel(channel);
    if (asyncInvocationChannel == null) {
      throw new IllegalArgumentException(
          "Unable to find the " + channel + " asynchronous execution channel.");
    }
    return asyncInvocationChannel;
  }

  @Override
  public void saveAsyncInvocationResult(AsyncInvocationRequestEntry requestEntry,
      InvocationResult result) {
    if (applicationRuntimeApi != null) {
      // We remove the given request from the list related to the application runtime.
      Storage storageAsyncReg = storageApi.get(Invocations.ASYNC_CHANNEL_REGISTRY);
      log.debug("saveAsyncInvocationResult, remove invocation request item {} - {}",
          requestEntry.channel.getName(),
          requestEntry.channel.getUri());
      // TODO move it to before commit
      storageAsyncReg.update(requestEntry.channel.getUri(), RuntimeAsyncChannel.class, rac -> {
        /*
         * Looks a bit not optimal but don't forget the ordered execution of the requests. If we
         * have reasonable number of requests in the channel then we usually remove the first ones.
         */
        rac.getInvocationRequests().remove(objectApi.getLatestUri(requestEntry.request.getUri()));
        return rac;
      });
    }
    // Save the result into the request and ensure the decision.
    ObjectNode requestNode = objectApi.loadLatest(requestEntry.request.getUri());
    requestNode.modify(AsyncInvocationRequest.class, air -> {
      return air.addResultsItem(result);
    });
    objectApi.save(requestNode);
    if (result.getDecision().getDecision() == DecisionEnum.CONTINUE
        && result.getDecision().getScheduledAt() == null
        && !requestEntry.request.getAndThen().isEmpty()) {
      // If we can continue and we have and then invocations without scheduling we enqueue
      // immediately
      applyAsyncInvocationParameter(requestEntry.request.getAndThen(), result.getReturnValue());
      saveAndEnqueueInvocationRequest(requestEntry.channel, requestEntry.request.getAndThen());
    } else if (result.getDecision().getDecision() == DecisionEnum.CONTINUE
        && result.getDecision().getScheduledAt() != null
        && requestEntry.request.getAndThen() != null
        && !requestEntry.request.getAndThen().isEmpty()) {
      applyAsyncInvocationParameter(requestEntry.request.getAndThen(), result.getReturnValue());
      scheduleAsyncInvocationRequest(requestEntry.channel.getName(),
          requestEntry.request.getAndThen(),
          result.getDecision().getScheduledAt());
    } else if (result.getDecision().getDecision() == DecisionEnum.RESCHEDULE
        && result.getDecision().getScheduledAt() != null) {
      scheduleAsyncInvocationRequest(requestEntry.channel.getName(),
          Arrays.asList(objectApi.getLatestUri(requestEntry.request.getUri())),
          result.getDecision().getScheduledAt());
    }
  }

  private final void applyAsyncInvocationParameter(List<URI> requests, Object value) {
    for (URI uri : requests) {
      objectApi.save(objectApi.loadLatest(uri).modify(AsyncInvocationRequest.class, air -> {
        air.getRequest().getParameters().get(0).setValue(value);
        return air;
      }));
    }
  }

  private final String scheduledInvocationReferenceName(String channelName) {
    return channelName + StringConstant.MINUS_SIGN + "scheduled";
  }

}
