package org.smartbit4all.domain.data.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.storage.bean.StorageArchiveBatch;
import org.smartbit4all.api.storage.bean.StorageArchiveBatchEntry;
import org.smartbit4all.api.storage.bean.StorageArchiveProcessConfig;
import org.smartbit4all.api.storage.bean.StorageArchiveProcessExecution;
import org.smartbit4all.api.storage.bean.StorageArchiveProcessExecution.StateEnum;
import org.smartbit4all.core.io.utility.FileIO;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectNodeReference;
import org.smartbit4all.core.utility.CronExpressionUtility;
import org.smartbit4all.domain.application.ApplicationRuntimeApi;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.toList;

/*
 * Delete unnecessary files from file system
 */
public class StorageArchiveApiImpl implements StorageArchiveApi {
  private static final Logger log = LoggerFactory.getLogger(StorageApiImpl.class);

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private ApplicationRuntimeApi runtimeApi;

  @Autowired
  private StorageApi storageApi;

  @Autowired
  private InvocationApi invocationApi;

  @Override
  public void scheduleArchival() {}

  @Override
  public int executeArchive(URI config) {
    Lock lock = objectApi.getLock(config);
    int result = 0;
    if (lock.tryLock()) {
      try {
        boolean runArchival = false;
        ObjectNode configNode = objectApi.load(config);
        OffsetDateTime now = OffsetDateTime.now();
        URI executionUri = configNode.getValue(URI.class, StorageArchiveProcessConfig.EXECUTION);
        if (executionUri == null) {
          // It is the fist time to run so create the execution object, save it and start the
          // process.
          executionUri = objectApi.saveAsNew(SCHEMA_ARCHIVAL,
              new StorageArchiveProcessExecution().config(config)
                  .nextRunAt(now).state(StateEnum.READY));
          configNode.setValue(executionUri, StorageArchiveProcessConfig.EXECUTION);
          objectApi.save(configNode);
          runArchival = true;
        }
        ObjectNode executionNode = objectApi.load(executionUri);
        StateEnum stateEnum =
            executionNode.getValue(StorageArchiveProcessExecution.StateEnum.class,
                StorageArchiveProcessExecution.STATE);
        runArchival = checkRun(runArchival, now, executionNode, stateEnum);
        if (runArchival) {
          // If we have to run at the end then allocate the execution for the current runtime.
          executionNode.setValue(runtimeApi.self().getUuid(),
              StorageArchiveProcessExecution.RUNTIME);
          if (stateEnum == StateEnum.READY) {
            executionNode.setValue(StateEnum.COLLECTING, StorageArchiveProcessExecution.STATE);
          }
          result = archive(objectApi.save(executionNode));
        }
      } finally {
        lock.unlock();
      }
    }
    return result;
  }

  private final int archive(URI executionUri) {
    int result = 0;
    {
      ObjectNode executionNode = objectApi.loadLatest(executionUri);
      StateEnum state =
          executionNode.getValue(StorageArchiveProcessExecution.StateEnum.class,
              StorageArchiveProcessExecution.STATE);
      if (state == StateEnum.COLLECTING) {
        collecting(executionNode);
      }
    }
    {
      ObjectNode executionNode = objectApi.loadLatest(executionUri);
      StateEnum state =
          executionNode.getValue(StorageArchiveProcessExecution.StateEnum.class,
              StorageArchiveProcessExecution.STATE);
      if (state == StateEnum.ARCHIVING) {
        result = archiving(executionNode);
      }
    }
    return result;
  }

  private int archiving(ObjectNode executionNode) {
    StorageArchiveBatch archiveBatch =
        executionNode.ref(StorageArchiveProcessExecution.ARCHIVE_BATCH).get()
            .getObject(StorageArchiveBatch.class);
    Storage storage = storageApi.get(archiveBatch.getStorage());
    List<URI> toRemove = null;
    if (storage != null) {
      toRemove = archiveBatch.getEntries().stream()
          .filter(e -> e.getVersionRanges() == null).map(e -> e.getObjectUri()).collect(toList());
      log.debug(toRemove.toString());
      storage.remove(toRemove);
    }
    setReady(executionNode);
    objectApi.save(executionNode);
    return toRemove == null ? 0 : toRemove.size();
  }

  private void collecting(ObjectNode executionNode) {
    List<StorageArchiveBatchEntry> batchEntries = collect(executionNode);
    if (!batchEntries.isEmpty()) {
      ObjectNodeReference refBatch =
          executionNode.ref(StorageArchiveProcessExecution.ARCHIVE_BATCH);
      ObjectNode batchNode;
      if (refBatch.isEmpty()) {
        batchNode = objectApi.create(SCHEMA_ARCHIVAL,
            new StorageArchiveBatch()
                .storage(executionNode.ref(StorageArchiveProcessExecution.CONFIG).get()
                    .getValueAsString(StorageArchiveProcessConfig.STORAGE)));
        refBatch.set(batchNode);
      } else {
        batchNode = refBatch.get();
      }
      batchNode.setValue(batchEntries, StorageArchiveBatch.ENTRIES);
      executionNode.setValue(StateEnum.ARCHIVING, StorageArchiveProcessExecution.STATE);
    } else {
      setReady(executionNode);
    }
    objectApi.save(executionNode);
  }

  private final void setReady(ObjectNode executionNode) {
    executionNode.setValue(StateEnum.READY, StorageArchiveProcessExecution.STATE);
    String cronExpression = executionNode.ref(StorageArchiveProcessExecution.CONFIG).get()
        .getValueAsString(StorageArchiveProcessConfig.CRON_EXPRESSION);
    executionNode.setValue(getNextRun(cronExpression),
        StorageArchiveProcessExecution.NEXT_RUN_AT);
    executionNode.setValue(null,
        StorageArchiveProcessExecution.RUNTIME);
  }

  private final OffsetDateTime getNextRun(String cronExpression) {
    return CronExpressionUtility.computeNext(cronExpression, OffsetDateTime.now());
  }

  private List<StorageArchiveBatchEntry> collect(ObjectNode executionNode) {
    StorageArchiveProcessConfig archiveProcessConfig =
        executionNode.ref(StorageArchiveProcessExecution.CONFIG).get()
            .getObject(StorageArchiveProcessConfig.class);
    Storage storage = storageApi.get(archiveProcessConfig.getStorage());
    if (archiveProcessConfig.getBeforeDurationInMillis() != null) {
      OffsetDateTime now = OffsetDateTime.now();
      OffsetDateTime endOfArchival =
          now.minus(archiveProcessConfig.getBeforeDurationInMillis(), ChronoUnit.MILLIS);
      List<StorageArchiveBatchEntry> result = new ArrayList<>();
      for (String className : archiveProcessConfig.getTypeClassNames()) {
        Iterator<URI> iterOldest = storage.readOldests(null, className).iterator();
        if (iterOldest.hasNext()) {
          URI oldestUri = iterOldest.next();
          StorageObject<?> storageObject = storage.load(oldestUri);
          OffsetDateTime createdAt = storageObject.getCreatedAt();
          // Starting from the created at we iterate through the objects.
          Stream<List<URI>> collectorStream = storage.streamOfTimeSeries(null, className,
              createdAt.toLocalDateTime(), endOfArchival.toLocalDateTime(), ChronoUnit.HOURS);
          InvocationRequest objectPredicate = archiveProcessConfig.getObjectPredicate();
          result.addAll(collectorStream.flatMap(l -> l.stream())
              .filter(u -> invokePredicate(objectPredicate, u))
              .map(u -> new StorageArchiveBatchEntry().objectUri(u))
              .collect(toList()));
        }
      }
      return result;
    }
    return Collections.emptyList();
  }

  private boolean invokePredicate(InvocationRequest objectPredicate, URI u) {
    if (objectPredicate == null) {
      return true;
    }
    try {
      return Boolean.TRUE.equals(invocationApi.invoke(objectPredicate, u).getValue());
    } catch (Exception e) {
      log.error("Unable to archive the {} object.", u, e);
      return false;
    }
  }

  private final boolean checkRun(boolean runArchival, OffsetDateTime now, ObjectNode executionNode,
      StateEnum stateEnum) {
    if (!runArchival) {
      // Check if we can run by reaching the next run time.
      OffsetDateTime nextRunAt = executionNode.getValue(OffsetDateTime.class,
          StorageArchiveProcessExecution.NEXT_RUN_AT);
      if ((stateEnum == StateEnum.READY && now.isAfter(nextRunAt))) {
        runArchival = true;
      }
    }
    if (!runArchival) {
      // Check if we found an archival that was not finished by the previous runtime.
      UUID runtime = executionNode.getValue(UUID.class,
          StorageArchiveProcessExecution.RUNTIME);
      if ((stateEnum != StateEnum.READY && !runtimeApi.isActive(runtime))) {
        runArchival = true;
      }
    }
    return runArchival;
  }

  @Override
  public void startArchive(String archiveConfigFile) {

    // if (!ObjectUtils.isEmpty(archiveConfigFile)) {
    // try {
    //
    // final File file = new File(archiveConfigFile);
    //
    // ObjectDefinition<ArchiveConfigData> objectDefinition =
    // objectApi.definition(ArchiveConfigData.class);
    // ArchiveConfigData archiveConfigData =
    // objectDefinition.deserialize(BinaryData.of(new FileInputStream(file))).get();
    //
    // String folder = archiveConfigData.getRootDir();
    //
    // List<String> deleteAll = archiveConfigData.getDeleteAll();
    //
    // for (String path : deleteAll) {
    // String deletePath = concatenatePath(folder, path);
    // deleteFiles(deletePath);
    // }
    // // "/linked-changes/org_smartbit4all_api_storage_bean_StorageSaveEventObject/2022"
    // // "/SB4STARTER/org_smartbit4all_api_binarydata_BinaryDataObject/2022"
    // // "/transaction/org_smartbit4all_api_storage_bean_TransactionData/2022"
    //
    // List<BeforeVersionData> deleteBeforeVersion = archiveConfigData.getDeleteBeforeVersion();
    //
    // for (BeforeVersionData data : deleteBeforeVersion) {
    // String deletePath = concatenatePath(folder, data.getPath());
    // int version = data.getVersion();
    // deleteOldVersions(deletePath, version);
    // }
    // // "/linked-changes/org_smartbit4all_api_storage_bean_ObjectMap"
    // // "/utemezes/hu_it4all_nmhh_lrl_domain_settings_model_UtemezettFutas"
    //
    // } catch (Exception e) {
    // log.error(e.getMessage());
    // }
    // }

  }

  private String concatenatePath(String base, String path) {
    if ((base.endsWith(SLASH) || base.endsWith(BACKSLASH))
        && (path.startsWith(SLASH) || path.startsWith(BACKSLASH))) {
      return base + path.substring(1);
    }
    if (!(base.endsWith(SLASH) || base.endsWith(BACKSLASH))
        && !(path.startsWith(SLASH) || path.startsWith(BACKSLASH))) {
      return base + SLASH + path;
    }
    return base + path;
  }

  private void deleteFiles(String folder) {
    File file = new File(folder);
    File[] list = file.listFiles();

    if (list != null)
      for (File fil : list) {
        if (fil.isDirectory()) {
          deleteFiles(fil.getAbsolutePath());
        } else {
          deleteFile(fil);
        }
      }
  }

  private void deleteOldVersions(String folder, int versionBefore) {
    List<String> files = findFile(".o", new File(folder));

    for (String fileName : files) {
      folder = fileName.substring(0, fileName.length() - 2);
      try (FileReader reader = new FileReader(fileName)) {
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line = bufferedReader.readLine();
        if (line != null) {
          String[] lines = line.split(",");
          if (lines.length < 2) {
            continue;
          }
          String serialNo = lines[1].substring(lines[1].indexOf("\"serialNoData\":"));
          String no = serialNo.substring(serialNo.indexOf(':') + 1);
          int version = Integer.parseInt(no);

          for (int i = 0; i < version - versionBefore; i++) {
            String id = FileIO.constructObjectPathByIndexWithHexaStructure(i);
            File file = new File(folder + id);
            deleteFile(file);
          }
        }
      } catch (IOException | NullPointerException | NumberFormatException e) {
        e.printStackTrace();
      }
    }
  }

  private void deleteFile(File file) {
    try {
      Path path = Paths.get(file.getPath());
      Files.delete(path);
    } catch (SecurityException | IOException e) {
      log.error(e.getMessage());
    }
  }

  private List<String> findFile(String name, File file) {
    File[] list = file.listFiles();
    List<String> fileNames = new ArrayList<>();

    if (list != null)
      for (File fil : list) {
        if (fil.isDirectory()) {
          fileNames.addAll(findFile(name, fil));
        } else if (fil.getName().indexOf(name) >= 0) {
          fileNames.add(fil.getAbsolutePath());
        }
      }

    return fileNames;
  }
}
