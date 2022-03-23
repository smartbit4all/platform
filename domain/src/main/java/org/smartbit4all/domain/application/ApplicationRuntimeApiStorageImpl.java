package org.smartbit4all.domain.application;

import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.logging.log4j.util.Strings;
import org.smartbit4all.api.invocation.bean.ApplicationRuntimeData;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject.VersionPolicy;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * The application runtime api implementation via {@link StorageApi}.
 * 
 * TODO Should solve the problem of system startup where we have to wait for the available self
 * instance.
 * 
 * @author Peter Boros
 */
public class ApplicationRuntimeApiStorageImpl implements ApplicationRuntimeApi, InitializingBean {

  private static final String CLUSTER = "cluster";

  /**
   * The self bean of this application instance. It can be used after property set time.
   */
  private ApplicationRuntime self;

  /**
   * The uri of the saved runtime.
   */
  private URI runtimeUri;

  /**
   * This object is initiated by the {@link #afterPropertiesSet()} and collects all the parameters.
   */
  private ApplicationRuntime myRuntime;

  /**
   * The runtime instances known in the cluster. This map is always updated by the maintenance.
   */
  private Map<UUID, ApplicationRuntime> runtimes = new HashMap<>();

  /**
   * The read write lock ensures that the maintenance will modify the map in a Thread safe way.
   */
  private final ReadWriteLock rwlRuntimes = new ReentrantReadWriteLock();

  @Autowired
  private StorageApi storageApi;

  /**
   * The cluster management {@link Storage} instance.
   */
  private Storage storageCluster;

  /**
   * Auto wires the port number that we are serving currently. Need to have alternatives if it's not
   * available.
   */
  @Value("${server.port:-1}")
  private int port;

  @Value("${applicationruntime.maintain.fixeddelay:5000}")
  private String schedulePeriodString;

  private Long schedulePeriod;

  @Autowired
  private Environment environment;

  @Override
  public ApplicationRuntime self() {
    return self;
  }

  @Override
  public ApplicationRuntime get(UUID uuid) {
    rwlRuntimes.readLock().lock();
    try {
      return runtimes.get(uuid);
    } finally {
      rwlRuntimes.readLock().unlock();
    }
  }

  private final long getSchedulePeriod() {
    if (schedulePeriod == null) {
      schedulePeriod = Long.valueOf(schedulePeriodString);
    }
    return schedulePeriod.longValue();
  }

  @Scheduled(fixedDelayString = "${applicationruntime.maintain.fixeddelay:5000}")
  public void maintain() {
    // TODO sync the times!
    long currentTimeMillis = System.currentTimeMillis();
    if (self == null) {
      // Save the self and set as self. From that time the runtime is officially registered.
      ApplicationRuntimeData runtimeData = myRuntime.getData();
      runtimeData.setLastTouchTime(currentTimeMillis);
      runtimeUri = storageCluster.saveAsNew(runtimeData, "active");
      myRuntime.getData().setUri(runtimeUri);
      self = myRuntime;
    } else {
      // The application runtime is already exists and must be updated in the storage.
      storageCluster.update(runtimeUri, ApplicationRuntimeData.class, r -> {
        return r.lastTouchTime(currentTimeMillis);
      });
      self.getData().setLastTouchTime(currentTimeMillis);
    }
    // If we successfully saved ourself then read all the active runtime we have in this register.
    List<ApplicationRuntimeData> activeRuntimes =
        storageCluster.readAll("active", ApplicationRuntimeData.class);
    // Manage the invalid runtimes, move them into the archive set.
    List<ApplicationRuntimeData> invalidRuntimes = new ArrayList<>();
    Map<UUID, ApplicationRuntime> activeRuntimesMap = new HashMap<>();
    for (ApplicationRuntimeData runtimeData : activeRuntimes) {
      if (runtimeData.getLastTouchTime() < (currentTimeMillis - getSchedulePeriod() * 3)) {
        // This is an invalid runtime. Remove it from the list and from the set.
        invalidRuntimes.add(runtimeData);
      } else {
        activeRuntimesMap.put(runtimeData.getUuid(), runtimeOf(runtimeData));
      }
      System.out.println("Runtime: " + runtimeData.toString());
    }
    // Set the new runtimes. This is an atomic operation there is no need to lock.
    runtimes = activeRuntimesMap;
    // Remove the invalid runtimes from the set.
    for (ApplicationRuntimeData invalidRuntime : invalidRuntimes) {
      storageCluster.archive(invalidRuntime.getUri());
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    storageCluster = storageApi.get(CLUSTER);
    storageCluster.setVersionPolicy(VersionPolicy.SINGLEVERSION);
    ApplicationRuntimeData runtimeData = new ApplicationRuntimeData()
        .ipAddress(InetAddress.getLocalHost().getHostAddress()).serverPort(getPort())
        .uuid(UUID.randomUUID()).startupTime(System.currentTimeMillis());
    myRuntime = new ApplicationRuntime(runtimeData);
  }

  private final int getPort() {
    if (port == -1) {
      String serverPort = environment.getProperty("local.server.port");
      if (Strings.isNotEmpty(serverPort)) {
        port = Integer.valueOf(serverPort);
      }
    }
    return port;
  }

  /**
   * Constructs the {@link ApplicationRuntimeData} for the storage. The URI is calculated to store
   * the runtime into the active set. The URI is hierarchical so it can define a set.
   * 
   * @return The application runtime data
   */
  public static final ApplicationRuntime runtimeOf(ApplicationRuntimeData runtimeData) {
    ApplicationRuntime result = new ApplicationRuntime(runtimeData);
    // TODO Figure out how to setup a standard time!
    result.setTimeOffset(0);
    return result;
  }

}
