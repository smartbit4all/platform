package org.smartbit4all.domain.data.storage;

import java.io.File;
import java.net.URI;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.core.io.utility.FileIO;
import org.springframework.beans.factory.annotation.Value;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalNotification;

public class BlobObjectStorageAccessFS implements BlobObjectStorageAccessApi {


  private static final Logger log = LoggerFactory.getLogger(BlobObjectStorageAccessFS.class);


  @Value("${BlobObjectStorageAccessFS.maximumCacheSize:8192}")
  private long maximumCacheSize;

  @Value("${BlobObjectStorageAccessFS.cacheConcurrencyLevel:10}")
  private int cacheConcurrencyLevel;

  @Value("${BlobObjectStorageAccessFS.binaryDataInMemoryLimit:8192}")
  private long binaryDataInMemoryLimit;

  /**
   * The default is one hour
   */
  @Value("${BlobObjectStorageAccessFS.expireAfterAccessInMillis:3600000}")
  private int expireAfterAccessInMillis;

  @Value("${BlobObjectStorageAccessFS.useCache:false}")
  private boolean useCache;

  private Cache<URI, List<BinaryData>> versionContentCache = null;

  /**
   * It contains mapped by the latest uri the version uris of the cache.
   */
  private Map<URI, Set<URI>> latestUriCache = null;

  /**
   * To protect the latestUriCache from the concurrent modification.
   */
  private ReadWriteLock rwlLatestUri = new ReentrantReadWriteLock(true);

  public BlobObjectStorageAccessFS() {
    super();
    if (useCache) {
      versionContentCache = CacheBuilder.newBuilder().maximumSize(maximumCacheSize)
          .concurrencyLevel(cacheConcurrencyLevel)
          .expireAfterAccess(Duration.ofMillis(expireAfterAccessInMillis))
          .removalListener((RemovalNotification<URI, List<BinaryData>> notification) -> {
            URI uriWithoutVersion = ObjectStorageImpl.getUriWithoutVersion(notification.getKey());
            rwlLatestUri.writeLock().lock();
            try {
              Set<URI> set = latestUriCache.get(uriWithoutVersion);
              if (set != null) {
                set.remove(notification.getKey());
                if (set.isEmpty()) {
                  latestUriCache.remove(uriWithoutVersion);
                }
              }
            } finally {
              rwlLatestUri.writeLock().unlock();
            }
          }).build();
      latestUriCache = new HashMap<>();
    }
  }

  @Override
  public void writeVersion(File newFile, URI versionUri,
      BinaryData... contents) {
    FileIO.writeMultipart(newFile, contents);
    if (versionContentCache != null) {
      versionContentCache.put(versionUri, Arrays.asList(contents));
      URI uriWithoutVersion = ObjectStorageImpl.getUriWithoutVersion(versionUri);
      rwlLatestUri.writeLock().lock();
      try {
        Set<URI> set = latestUriCache.computeIfAbsent(uriWithoutVersion, u -> new HashSet<>());
        set.add(versionUri);
      } finally {
        rwlLatestUri.writeLock().unlock();
      }
    }
  }

  @Override
  public List<BinaryData> readVersion(File file, URI versionUri) {
    if (versionContentCache != null) {
      try {
        List<BinaryData> list =
            versionContentCache.get(versionUri, () -> FileIO.readMultipart(file)).stream()
                .map(bd -> {
                  bd.loadIntoMemory(binaryDataInMemoryLimit);
                  return bd;
                }).collect(Collectors.toList());
        rwlLatestUri.writeLock().lock();
        try {
          Set<URI> set =
              latestUriCache.computeIfAbsent(ObjectStorageImpl.getUriWithoutVersion(versionUri),
                  u -> new HashSet<>());
          set.add(versionUri);
        } finally {
          rwlLatestUri.writeLock().unlock();
        }
        return list;
      } catch (ExecutionException e) {
        log.debug("Unable to load to cache", e);
      }
    }
    return FileIO.readMultipart(file);
  }

  @Override
  public boolean exists(File objectFile, URI latestUri) {
    boolean result = false;
    if (latestUriCache != null) {
      rwlLatestUri.readLock().lock();
      try {
        result = latestUriCache.containsKey(latestUri);
      } finally {
        rwlLatestUri.readLock().unlock();
      }
    }
    return result == true ? true : objectFile.exists();
  }

}
