package org.smartbit4all.api.runtime;

import java.net.InetAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.logging.log4j.util.Strings;
import org.smartbit4all.api.invocation.bean.ApiRegistryData;
import org.smartbit4all.api.invocation.bean.ApplicationRuntimeData;
import org.smartbit4all.core.utility.StringConstant;
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
  private final Map<UUID, ApplicationRuntime> runtimes = new HashMap<>();

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
  @Value("${local.server.port:-1}")
  private int port;

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

  @Scheduled(fixedDelayString = "${applicationruntime.maintain.fixeddelay:5000}")
  public void maintain() {
    long currentTimeMillis = System.currentTimeMillis();
    if (self == null) {
      // Save the self and set as self. From that time the runtime is officially registered.
      ApplicationRuntimeData runtimeData = dataOf(myRuntime);
      runtimeData.setLastTouchTime(currentTimeMillis);
      runtimeUri = storageCluster.saveAsNew(runtimeData);
      self = myRuntime;
    } else {
      // The application runtime is already exists and must be updated in the storage.
      storageCluster.update(runtimeUri, ApplicationRuntimeData.class, r -> {
        return r.lastTouchTime(currentTimeMillis);
      });
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    storageCluster = storageApi.get(CLUSTER);
    storageCluster.setVersionPolicy(VersionPolicy.SINGLEVERSION);
    myRuntime = new ApplicationRuntime();
    myRuntime.setIp(InetAddress.getLocalHost().getHostAddress());
    myRuntime.setPort(getPort());
    myRuntime.setUuid(UUID.randomUUID());
    myRuntime.setStartupTime(System.currentTimeMillis());
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
  public static final ApplicationRuntimeData dataOf(ApplicationRuntime runtime) {
    return new ApplicationRuntimeData().uuid(runtime.getUuid()).ipAddress(runtime.getIp())
        .serverPort(runtime.getPort()).timeOffset(runtime.getTimeOffset())
        .uri(URI.create(CLUSTER + StringConstant.COLON + StringConstant.SLASH
            + ApiRegistryData.class.getName().replace('.', '/') + StringConstant.SLASH + "active"
            + StringConstant.SLASH + runtime.getUuid()));
  }

}
