package org.smartbit4all.api.invocation;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
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
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredReference;
import org.smartbit4all.api.contribution.ContributionApi;
import org.smartbit4all.api.contribution.PrimaryApi;
import org.smartbit4all.api.invocation.bean.ApiData;
import org.smartbit4all.api.invocation.bean.ApiRegistryData;
import org.smartbit4all.api.invocation.bean.AsyncChannelScheduledInvocationList;
import org.smartbit4all.api.invocation.bean.AsyncInvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.RuntimeAsyncChannel;
import org.smartbit4all.api.invocation.bean.RuntimeAsyncChannelList;
import org.smartbit4all.api.invocation.bean.RuntimeAsyncChannelRegistry;
import org.smartbit4all.api.invocation.bean.ScheduledInvocationRequest;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinitionApiImpl;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.application.ApplicationRuntime;
import org.smartbit4all.domain.application.ApplicationRuntimeApi;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.smartbit4all.domain.data.storage.StorageObjectLock;
import org.smartbit4all.storage.fs.StorageTransactionManagerFS;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

public class InvocationRegisterApiIml implements InvocationRegisterApi, DisposableBean {

  private static final Logger log = LoggerFactory.getLogger(InvocationRegisterApiIml.class);

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

  /**
   * The transaction manager (there must be only one in one application) that is configured. Used to
   * identify if there is an active transaction initiated on the current {@link Thread}. The lock of
   * the objects are collected in the {@link #transactionManager} and at the end of the transaction
   * all of them are finalized. The finalize is nothing else but the atomic move of the temporary
   * files generated by the transaction.
   */
  @Autowired(required = false)
  private StorageTransactionManagerFS transactionManager;

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  private ObjectApi objectApi;

  @Autowired
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
   * All api instances that our application provides in case invocation request.
   */
  private final Map<URI, Object> apiInstanceByApiDataUri = new HashMap<>();

  /**
   * The runtime instances known in the cluster by Api uri. This map is always updated by the
   * maintenance.
   */
  private Map<URI, List<UUID>> runtimesByApis = new HashMap<>();

  /**
   * Prevent from accessing the registry before the first maintain cycle.
   */
  private CountDownLatch maintainLatch = new CountDownLatch(1);

  /**
   * All {@link ApiData} uri that our application runtime provides.
   */
  private Set<URI> apis = new HashSet<>();

  /**
   * All the api providers that are provided by the given application.
   */
  @Autowired(required = false)
  private List<ProviderApiInvocationHandler<?>> providedApis;

  /**
   * 
   */
  private boolean initialized = false;

  /**
   * All the asynchronous channels we have in the current configuration.
   */
  @Autowired(required = false)
  private List<AsyncInvocationChannel> channels;

  /**
   * The channels by name that is the processed map of the {@link #channels} autowired list.
   */
  private Map<String, AsyncInvocationChannel> channelsByName;

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

  /**
   * This is the OrgApi scheme where we save the settings for the notify.
   */
  private Supplier<Storage> storage = new Supplier<Storage>() {

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
  public void initRegistry() {
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
        for (ProviderApiInvocationHandler<?> apiHandler : providedApis) {
          ApiData apiData = apiHandler.getData();
          if (!storage.get().exists(apiData.getUri())) {
            storage.get().saveAsNew(apiData);
            if (!r.getApiList().contains(apiData.getUri())) {
              r.addApiListItem(apiData.getUri());
            }
          }
          apis.add(apiData.getUri());
          addToApiRegister(apiData);
          apiInstanceByApiDataUri.put(apiData.getUri(), apiHandler.getApiInstance());
        }
      }
      return r;
    });

    // refresh primary api map
    fillPrimaryApiMap();
  }

  @EventListener(ApplicationReadyEvent.class)
  private void initRuntimeChannels() {
    // Manage the asynchronous invocation channels.
    InvocationApi invocationApi = applicationContext.getBean(InvocationApi.class);
    channelsByName = new HashMap<>();
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
      applicationRuntimeApi.setApis(new ArrayList<>(apis));
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

  @Override
  @Scheduled(fixedDelayString = "${invocationregistry.refresh.fixeddelay:5000}")
  public void refreshRegistry() {
    if (storage.get() == null || !storage.get().exists(REGISTER_URI) || !initialized) {
      return;
    }
    if (applicationRuntimeApi == null) {
      // if there is ApplicationRuntimeApi, then we can't refresh the apis
      return;
    }
    UUID myRuntimeUUID = applicationRuntimeApi.self().getUuid();
    // Get apis from all active runtime. The current instance is an exception because we know what
    // we are providing.
    List<ApplicationRuntime> activeOtherRuntimes = applicationRuntimeApi.getActiveRuntimes()
        .stream()
        .filter(r -> !r.getUuid().equals(myRuntimeUUID)).collect(toList());

    Set<URI> activeApis = new HashSet<>();
    Map<URI, List<UUID>> activeRuntimesByApisMap = new HashMap<>();
    // First of all fill our own apis. We don't need the getApis call to know what we are providing.
    if (!CollectionUtils.isEmpty(apis)) {
      for (URI api : apis) {
        List<UUID> runtimes =
            activeRuntimesByApisMap.computeIfAbsent(api, r -> new ArrayList<>());
        runtimes.add(myRuntimeUUID);
      }

      activeApis.addAll(apis);
    }

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
    manageAsyncChannels(applicationRuntimeApi.getActiveRuntimes());
    maintainLatch.countDown();
  }

  /**
   * Manage the central registry of the {@link RuntimeAsyncChannelRegistry} that contains the async
   * channels managed by the runtimes. The current implementation detects the inactive runtimes and
   * pick up their lost invocations.
   * 
   * @param activeRuntimes
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
  }

  public void enqueueScheduledInvocations(AsyncInvocationChannel channel) {
    StoredReference<AsyncChannelScheduledInvocationList> refScheduled =
        collectionApi.reference(Invocations.INVOCATION_SCHEME,
            scheduledInvocationReferenceName(channel.getName()),
            AsyncChannelScheduledInvocationList.class);
    OffsetDateTime limitTime = OffsetDateTime.now().minusSeconds(5);
    if (refScheduled.exists()) {
      refScheduled.update(scheduledList -> {
        // The list of invocation is always ordered by the schedule time.
        List<ScheduledInvocationRequest> toExecute = new ArrayList<>();
        for (int idx = 0; idx < scheduledList.getInvocationRequests().size(); idx++) {
          ScheduledInvocationRequest scheduledInvocationRequest =
              scheduledList.getInvocationRequests().get(idx);
          if (scheduledInvocationRequest.getScheduledAt().isBefore(limitTime)) {
            // If we found the first scheduled invocation that before the limit time then we stop
            // because until this item we had all the requests to be enqueued. The idx is now on the
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
      storageAsyncReg.update(asyncInvocationChannel.getUri(), RuntimeAsyncChannel.class, rac -> {
        if (requestUris != null) {
          requestUris.forEach(rac::addInvocationRequestsItem);
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
              storageRequest.load(asyncRequestUri, AsyncInvocationRequest.class);
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
   * Enqueue the given entry even if we have active {@link #transactionManager} or not. If we are in
   * an active transaction then the execution starts at the successful finish of the transaction
   * (onCommit). Else the execution starts right now.
   * 
   * @param asyncInvocationRequestEntry
   */
  private final void enqueueAsyncRequest(AsyncInvocationRequestEntry asyncInvocationRequestEntry) {
    if (transactionManager != null && transactionManager.isInTransaction()) {
      transactionManager.addOnSucceed(asyncInvocationRequestEntry);
    } else {
      asyncInvocationRequestEntry.invoke();
    }
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
        storage.get().load(new ArrayList<>(apiUris), ApiData.class);

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
    }
  }

  private void addApis(Set<URI> apiUris) {
    List<StorageObject<ApiData>> apiDataSos =
        storage.get().load(new ArrayList<>(apiUris), ApiData.class);

    for (StorageObject<ApiData> apiDataSo : apiDataSos) {
      ApiData apiData = apiDataSo.getObject();
      addToApiRegister(apiData);
      registerToPrimaryApi(apiData);
    }
  }

  private ApiDescriptor addToApiRegister(ApiData apiData) {
    Map<String, ApiDescriptor> apisByName =
        apiRegister.computeIfAbsent(apiData.getInterfaceName(), n -> new HashMap<>());
    ApiDescriptor apiDescriptor =
        apisByName.computeIfAbsent(apiData.getName(), n -> new ApiDescriptor(apiData));
    return apiDescriptor;
  }

  /**
   * If the api is a {@link ContributionApi}, then register to his {@link PrimaryApi}
   * 
   * @param apiData
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
  public List<UUID> getRuntimesForApi(URI apiDataUri) {
    try {
      maintainLatch.await();
    } catch (InterruptedException e) {
      log.error("Wait for maintain interrupted.", e);
    }
    return runtimesByApis.get(apiDataUri);
  }

  @Override
  public void destroy() throws Exception {
    for (AsyncInvocationChannel channel : channelsByName.values()) {
      AsyncInvocationChannelSetup channelSetup = (AsyncInvocationChannelSetup) channel;
      channelSetup.stop();
    }
  }

  @Override
  public AsyncInvocationRequestEntry saveAndEnqueueAsyncInvocationRequest(InvocationRequest request,
      String channel) {
    AsyncInvocationChannel asyncInvocationChannel = checkChannel(channel);
    AsyncInvocationRequest asyncInvocationRequest = new AsyncInvocationRequest().request(request);
    if (applicationRuntimeApi != null) {
      // Save the request to remember to execute if this runtime fails. We set the runtime
      // identifier
      // to see which runtime is responsible for the invocation currently.
      ApplicationRuntime applicationRuntime = applicationRuntimeApi.self();
      asyncInvocationRequest.runtimeUri(applicationRuntime.getUri());
      asyncInvocationRequest
          .uri(objectApi.saveAsNew(Invocations.INVOCATION_SCHEME, asyncInvocationRequest));
      // We save the given invocation into a list related to the application runtime.
      Storage storageAsyncReg = storageApi.get(Invocations.ASYNC_CHANNEL_REGISTRY);
      storageAsyncReg.update(asyncInvocationChannel.getUri(), RuntimeAsyncChannel.class, rac -> {
        return rac
            .addInvocationRequestsItem(objectApi.getLatestUri(asyncInvocationRequest.getUri()));
      });
    }

    AsyncInvocationRequestEntry result =
        new AsyncInvocationRequestEntry(asyncInvocationChannel, asyncInvocationRequest);
    enqueueAsyncRequest(result);
    return result;
  }

  @Override
  public AsyncInvocationRequest saveAndScheduleAsyncInvocationRequest(
      InvocationRequest request, String channelName, OffsetDateTime executeAt) {
    AsyncInvocationChannel channel = checkChannel(channelName);
    AsyncInvocationRequest asyncInvocationRequest =
        new AsyncInvocationRequest().request(request);
    asyncInvocationRequest
        .uri(objectApi.saveAsNew(Invocations.INVOCATION_SCHEME, asyncInvocationRequest));
    StoredReference<AsyncChannelScheduledInvocationList> refScheduled =
        collectionApi.reference(Invocations.INVOCATION_SCHEME,
            scheduledInvocationReferenceName(channel.getName()),
            AsyncChannelScheduledInvocationList.class);
    refScheduled.update(scheduledList -> {
      AsyncChannelScheduledInvocationList updatedList =
          scheduledList == null ? new AsyncChannelScheduledInvocationList() : scheduledList;
      ListIterator<ScheduledInvocationRequest> iterList =
          updatedList.getInvocationRequests().listIterator();
      while (iterList.hasNext()) {
        ScheduledInvocationRequest scheduledInvocationRequest =
            iterList.next();
        if (scheduledInvocationRequest.getScheduledAt().isAfter(executeAt)) {
          break;
        }
      }
      iterList.add(new ScheduledInvocationRequest().requestUri(asyncInvocationRequest.getUri())
          .scheduledAt(executeAt));
      return updatedList;
    });
    return asyncInvocationRequest;
  }

  private final AsyncInvocationChannel checkChannel(String channel) {
    AsyncInvocationChannel asyncInvocationChannel = channelsByName.get(channel);
    if (asyncInvocationChannel == null) {
      throw new IllegalArgumentException(
          "Unable to find the " + channel + " asynchronous execution channel.");
    }
    return asyncInvocationChannel;
  }

  @Override
  public void removeAsyncInvovationRequest(AsyncInvocationRequestEntry requestEntry) {
    if (applicationRuntimeApi != null) {
      // We remove the given request from the list related to the application runtime.
      Storage storageAsyncReg = storageApi.get(Invocations.ASYNC_CHANNEL_REGISTRY);
      storageAsyncReg.update(requestEntry.channel.getUri(), RuntimeAsyncChannel.class, rac -> {
        /*
         * Looks a bit not optimal but don't forget the ordered execution of the requests. If we
         * have reasonable number of requests in the channel then we usually remove the first ones.
         */
        rac.getInvocationRequests().remove(objectApi.getLatestUri(requestEntry.request.getUri()));
        return rac;
      });
    }
  }

  private final String scheduledInvocationReferenceName(String channelName) {
    return channelName + StringConstant.MINUS_SIGN + "scheduled";
  }

}
