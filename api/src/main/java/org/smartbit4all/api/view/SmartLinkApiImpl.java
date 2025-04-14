package org.smartbit4all.api.view;

import static java.util.stream.Collectors.toSet;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredMap;
import org.smartbit4all.api.collection.StoredReference;
import org.smartbit4all.api.object.bean.ObjectReferenceById;
import org.smartbit4all.api.view.bean.SmartLinkData;
import org.smartbit4all.api.view.bean.SmartLinkMigrationStatus;
import org.smartbit4all.api.view.bean.SmartLinkMigrationStatus.StatusEnum;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ObjectUtils;
import com.google.common.base.Strings;

public class SmartLinkApiImpl implements SmartLinkApi {

  private static final Logger log = LoggerFactory.getLogger(SmartLinkApiImpl.class);

  private static final String SMAR_LINK_MIGRATION = "smarLinkMigration";

  private static final String SCHEMA = "smartLink";

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private StorageApi storageApi;

  private final Map<String, SmartLinkMigrationStatus> migrationsByChannel = new HashMap<>();

  /**
   * To protect the migration status channel.
   */
  Lock lockMigrationsByChannel = new ReentrantLock();

  ObjectDefinition<ObjectReferenceById> referenceDefinition;

  @Value("${view.smartlink.upgrade:false}")
  boolean upgradeStorage = false;

  @Override
  public URI publishView(String channel, View view) {
    return publishView(channel, view, null);
  }

  private final SmartLinkMigrationStatus checkStorageUpdated(String channel) {
    if (upgradeStorage == false) {
      // In this case there is no wait for migration.
      return null;
    }

    StoredMap map = collectionApi.map(ViewContextService.SCHEMA, channel);
    if (!map.exists() || map.uris().isEmpty()) {
      return null;
    }

    lockMigrationsByChannel.lock();
    try {
      SmartLinkMigrationStatus migrationStatus = migrationsByChannel.get(channel);
      if (migrationStatus != null && migrationStatus.getStatus() == StatusEnum.DONE) {
        return migrationStatus;
      }
      StoredReference<SmartLinkMigrationStatus> refMigrationStatus =
          collectionApi.reference(ViewContextService.SCHEMA,
              getMigrationStatusName(channel),
              SmartLinkMigrationStatus.class);
      if (refMigrationStatus.exists()) {
        migrationStatus = refMigrationStatus.get();
        if (migrationStatus.getStatus() == StatusEnum.DONE) {
          migrationsByChannel.put(channel, migrationStatus);
          return migrationStatus;
        }
      }
      throw new IllegalStateException("Awaiting for result of the smart link migration.");
    } finally {
      lockMigrationsByChannel.unlock();
    }
  }

  private final String getMigrationStatusName(String channel) {
    return SMAR_LINK_MIGRATION + StringConstant.MINUS_SIGN + channel;
  }

  @Override
  public URI publishView(String channel, View view, URI aclUri) {
    return publishView(channel, view, aclUri, null);
  }

  @Override
  public URI publishView(String channel, View view, URI aclUri, UUID smartLinkId) {
    Objects.requireNonNull(view, "view cannot be null!");
    Objects.requireNonNull(channel, "channel cannot be null!");

    checkStorageUpdated(channel);

    view.putParametersItem(PARAM_OPENED_FROM_SMART_LINK, Boolean.TRUE);
    // TODO sanitize channel name
    // TODO basePath in url?
    UUID uuid = smartLinkId == null ? UUID.randomUUID() : smartLinkId;
    SmartLinkData smartLinkData = new SmartLinkData()
        .uuid(uuid)
        .view(view)
        .url("/" + channel + "/" + uuid.toString());
    if (!ObjectUtils.isEmpty(aclUri)) {
      smartLinkData.acl(aclUri);
    }
    ObjectNode smartLinkNode = objectApi.create(channel, smartLinkData);
    URI smartLinkUri = objectApi.save(smartLinkNode);
    if (upgradeStorage) {
      objectApi.saveAsNew(SCHEMA,
          new ObjectReferenceById().id(uuid.toString()).refObjectUri(smartLinkUri));
    } else {
      StoredMap linkMap = collectionApi.map(ViewContextService.SCHEMA, channel);
      linkMap.put(uuid.toString(), smartLinkUri);
    }
    return smartLinkUri;
  }

  @Override
  public ObjectNode getSmartLink(String channel, UUID smartLinkUuid) {
    if (smartLinkUuid == null) {
      return null;
    }
    URI linkUri;
    if (upgradeStorage) {
      checkStorageUpdated(channel);
      linkUri =
          objectApi.loadLatest(SCHEMA, getReferenceDefinition(), smartLinkUuid.toString())
              .getValue(URI.class, ObjectReferenceById.REF_OBJECT_URI);
    } else {
      StoredMap linkMap = collectionApi.map(ViewContextService.SCHEMA, channel);
      linkUri = linkMap.uris().get(smartLinkUuid.toString());
    }
    if (linkUri == null) {
      return null;
    }
    return objectApi.loadLatest(linkUri);
  }

  final ObjectDefinition<ObjectReferenceById> getReferenceDefinition() {
    if (referenceDefinition == null) {
      referenceDefinition = objectApi.definition(ObjectReferenceById.class);
    }
    return referenceDefinition;
  }

  @Override
  public void migrate(String channel) {
    if (upgradeStorage) {
      log.info("Migrating smartLinks in channel {}", channel);
      StoredReference<SmartLinkMigrationStatus> refMigrationStatus =
          collectionApi.reference(ViewContextService.SCHEMA,
              getMigrationStatusName(channel),
              SmartLinkMigrationStatus.class);
      refMigrationStatus.update(s -> {
        if (s == null) {
          s = new SmartLinkMigrationStatus();
        }
        return s.channel(channel);
      });
      Lock migrationLock = objectApi.getLock(refMigrationStatus.getUri());
      migrationLock.lock();
      try {
        SmartLinkMigrationStatus migrationStatus = refMigrationStatus.get();
        if (migrationStatus.getStatus() == null) {
          // We have to start the migration in this server.
          log.info("Migrating starts now {}", channel);
          refMigrationStatus.update(
              s -> s.channel(channel).startAt(OffsetDateTime.now()).status(StatusEnum.RUNNING));
          StoredMap linkMap = collectionApi.map(ViewContextService.SCHEMA, channel);
          Map<String, URI> uris = linkMap.uris();
          log.info("URIs to migrate: {}", uris.size());
          int counter = 0;
          for (Entry<String, URI> smartLinkEntry : uris.entrySet()) {
            objectApi.saveAsNew(SCHEMA,
                new ObjectReferenceById().id(smartLinkEntry.getKey())
                    .refObjectUri(smartLinkEntry.getValue()));
            counter++;
            if (counter % 100 == 0) {
              log.info("Created {} next-gen smartlink", counter);
            }
          }
          log.info("Created {} next-gen smartlink", counter);
          refMigrationStatus.update(
              s -> s.finishedAt(OffsetDateTime.now()).status(StatusEnum.DONE));
        }
      } finally {
        migrationLock.unlock();
      }
      log.info("Migrating finished for smartLinks in channel {}", channel);
    }
  }

  @Override
  public List<URI> remove(Collection<? extends UUID> smartLinkUuids) {
    if (smartLinkUuids == null || smartLinkUuids.isEmpty()) {
      log.debug("remove - no UUIDs supplied, terminating early.");
      return Collections.emptyList();
    }

    final Storage smartLinkStorage = storageApi.get(SCHEMA);
    final Set<URI> urisToRemove = smartLinkUuids.stream()
        .filter(Objects::nonNull)
        .map(String::valueOf)
        .map(uuid -> smartLinkStorage.constructUriForId(referenceDefinition, uuid))
        .collect(toSet());
    return smartLinkStorage.remove(urisToRemove);
  }

  @Override
  public void removeLegacyChannels(Collection<? extends String> channels) {
    if (channels == null || channels.isEmpty()) {
      log.debug("removeLegacyChannels - no channel names supplied, terminating early.");
      return;
    }

    for (final String channel : channels) {
      if (Strings.isNullOrEmpty(channel)) {
        log.warn("Encountered null/empty channel name, skipping...");
        continue;
      }

      collectionApi.map(SCHEMA, channel).update(it -> new HashMap<>());
    }
  }

}
