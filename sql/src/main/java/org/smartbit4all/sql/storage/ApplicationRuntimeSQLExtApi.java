package org.smartbit4all.sql.storage;

import static java.util.stream.Collectors.toList;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.invocation.bean.ApplicationRuntimeData;
import org.smartbit4all.api.storage.bean.StorageObjectData;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.core.utility.UriUtils;
import org.smartbit4all.domain.application.ApplicationRuntimeApiStorageImpl;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.TableDatas;
import org.smartbit4all.domain.data.storage.ObjectNotFoundException;
import org.smartbit4all.domain.data.storage.ObjectStorage;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageLoadOption;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.smartbit4all.domain.utility.crud.Crud;
import org.smartbit4all.sql.storage.StorageSQL.UriInfo;
import org.springframework.beans.factory.annotation.Autowired;

public class ApplicationRuntimeSQLExtApi implements StorageSQLExtensionApi {

  private static final Logger log = LoggerFactory.getLogger(ApplicationRuntimeSQLExtApi.class);

  @Autowired
  private ApplicationRuntimeDef applicationRuntimeDef;

  @Autowired
  private ObjectDefinitionApi objectDefinitionApi;

  private ObjectDefinition<ApplicationRuntimeData> applicationRuntimeDataDef;

  private final List<ManagedObject> managedObjects =
      Arrays.asList(new ManagedObject(ApplicationRuntimeApiStorageImpl.SCHEMA,
          ApplicationRuntimeData.class.getName()));

  @Override
  public List<ManagedObject> getManagedObjects() {
    return managedObjects;
  }

  private final ObjectDefinition<ApplicationRuntimeData> getDefinition() {
    if (applicationRuntimeDataDef == null) {
      applicationRuntimeDataDef = objectDefinitionApi.definition(ApplicationRuntimeData.class);
    }
    return applicationRuntimeDataDef;
  }

  @Override
  public <T> List<StorageObject<T>> loadBatch(ObjectStorage objectStorage, Storage storage,
      List<UriInfo> uriInfos,
      Class<T> clazz,
      StorageLoadOption... options) {
    Set<String> uniqueBaseUris = uriInfos.stream()
        .map(info -> info.uri.toString())
        .collect(Collectors.toSet());
    Map<String, DataRow> objectEntryRows = Crud.read(applicationRuntimeDef)
        .select(applicationRuntimeDef.allProperties())
        .where(applicationRuntimeDef.uri().in(uniqueBaseUris))
        .listData()
        .rows()
        .stream()
        .collect(Collectors.toMap(
            row -> row.get(applicationRuntimeDef.uri()),
            row -> row));
    if (objectEntryRows.size() != uniqueBaseUris.size()) {
      throw new ObjectNotFoundException(uniqueBaseUris, clazz, "Object not found.");
    }
    List<StorageObject<T>> result = new ArrayList<>();
    for (UriInfo uriInfo : uriInfos) {
      StorageObject<T> storageObject =
          readFromRow(objectStorage, storage, objectEntryRows.get(uriInfo.uri.toString()), clazz);
      if (storageObject == null) {
        throw new ObjectNotFoundException(uniqueBaseUris, clazz, "Object not found.");
      }
      result.add(storageObject);
    }
    return result;
  }

  private final <T> StorageObject<T> readFromRow(ObjectStorage objectStorage, Storage storage,
      DataRow r, Class<T> clazz) {
    try {
      URI uri = URI.create(r.get(applicationRuntimeDef.uri()));
      StorageObjectData storageObjectData = new StorageObjectData()
          .className(clazz.getName())
          .uri(uri);
      @SuppressWarnings("unchecked") // it's always ApplicationRuntimeData
      StorageObject<T> storageObject = (StorageObject<T>) objectStorage.instanceOf(
          storage, getDefinition(), uri, storageObjectData, null);

      storageObject.setLastModified(r.get(applicationRuntimeDef.lastTouchTime()));
      storageObject.setObjectAsMap(
          getDefinition().deserializeAsMap(r.get(applicationRuntimeDef.objectContent())));
      return storageObject;

    } catch (Exception e) {
      log.error("Failed to load application runtime row: " + r, e);
    }
    return null;
  }


  @Override
  public <T> List<URI> readAllUris(ObjectStorage objectStorage, Storage storage, String setName,
      String clazzName) {
    // Check if the given directory exists or not.
    ObjectDefinition<?> objectDefinition = objectDefinitionApi.definition(clazzName);

    String storageScheme = storage.getScheme();
    String setPath =
        storageScheme + StringConstant.COLON + StringConstant.SLASH + objectDefinition.getAlias()
            + (Strings.isBlank(setName) ? StringConstant.EMPTY
                : StringConstant.SLASH + setName);

    TableData<ApplicationRuntimeDef> objectList;
    try {
      if (log.isTraceEnabled()) {
        log.trace("readAll: setName={}", setName);
      }
      objectList = Crud.read(applicationRuntimeDef)
          .select(applicationRuntimeDef.uri())
          .where(
              applicationRuntimeDef.uri().like(setPath + StringConstant.PERCENT))
          .listData();
      List<URI> result = objectList.rows().stream()
          .map(r -> UriUtils.asUri(r.get(applicationRuntimeDef.uri())))
          .collect(toList());
      if (log.isTraceEnabled()) {
        log.trace("readAll: setName={} size{}", setName, result.size());
      }
      return result;
    } catch (Exception e) {
      log.debug("Unable to read all the objects from the set.", e);
      return Collections.emptyList();
    }
  }

  @Override
  public Long saveObject(StorageObject<?> object, BinaryData relationBinaryData) {
    ApplicationRuntimeData runtimeData = getDefinition().fromMap(object.getObjectAsMap());
    DataRow objectRow;
    try {
      String uriString = object.getUri().toString();
      if (log.isTraceEnabled()) {
        log.trace("saveObject read: uriWithoutVersion={}", uriString);
      }
      objectRow = Crud.read(applicationRuntimeDef)
          .select(applicationRuntimeDef.allProperties())
          .where(applicationRuntimeDef.uri().eq(uriString)).lock()
          .onlyOne()
          .orElse(null);
    } catch (Exception e) {
      objectRow = null;
    }

    if (objectRow != null) {
      // It is an already existing object so it is an update
      OffsetDateTime now = OffsetDateTime.now();
      // If it is a single version then we update the one and only one version of the object.
      objectRow.set(applicationRuntimeDef.uri(), runtimeData.getUri().toString());
      objectRow.set(applicationRuntimeDef.uuid(), runtimeData.getUuid().toString());
      objectRow.set(applicationRuntimeDef.baseUrl(), runtimeData.getBaseUrl());
      objectRow.set(applicationRuntimeDef.serverPort(), Long.valueOf(runtimeData.getServerPort()));
      objectRow.set(applicationRuntimeDef.startupTime(), runtimeData.getStartupTime());
      objectRow.set(applicationRuntimeDef.stopTime(), runtimeData.getStopTime());
      objectRow.set(applicationRuntimeDef.lastTouchTime(), runtimeData.getLastTouchTime());
      objectRow.set(applicationRuntimeDef.objectContent(), object.serializeMapAware());
      Crud.update(objectRow.tableData());
    } else {
      // It is a brand new object insert simply.
      URI uri = object.getUri();
      OffsetDateTime now = OffsetDateTime.now();
      Crud.create(TableDatas
          .builder(applicationRuntimeDef, applicationRuntimeDef.allProperties())
          .addRow()
          .set(applicationRuntimeDef.uri(), runtimeData.getUri().toString())
          .set(applicationRuntimeDef.uuid(), runtimeData.getUuid().toString())
          .set(applicationRuntimeDef.baseUrl(), runtimeData.getBaseUrl())
          .set(applicationRuntimeDef.serverPort(), Long.valueOf(runtimeData.getServerPort()))
          .set(applicationRuntimeDef.startupTime(), runtimeData.getStartupTime())
          .set(applicationRuntimeDef.stopTime(), runtimeData.getStopTime())
          .set(applicationRuntimeDef.lastTouchTime(), runtimeData.getLastTouchTime())
          .set(applicationRuntimeDef.objectContent(), object.serializeMapAware())
          .build());
    }
    return StorageSQL.FIRST_VERSION;
  }

  @Override
  public boolean exists(URI uri) {
    String uriWithoutVersion = StorageSQL.getUriString(
        StorageSQL.getUriWithoutVersion(uri));
    try {
      return Crud.read(applicationRuntimeDef)
          .select(applicationRuntimeDef.allProperties())
          .where(applicationRuntimeDef.uri().eq(uriWithoutVersion))
          .listData()
          .size() > 0;
    } catch (Exception e) {
      log.error("Failed to check exist for application runtime: " + uri, e);
      return false;
    }
  }

  @Override
  public boolean move(URI uri, URI targetUri) {
    String uriWithoutVersion = StorageSQL.getUriString(
        StorageSQL.getUriWithoutVersion(uri));
    String targetUriWithoutVersion = StorageSQL.getUriString(
        StorageSQL.getUriWithoutVersion(targetUri));

    try {
      Optional<DataRow> applicationRuntimeRow = Crud.read(applicationRuntimeDef)
          .select(applicationRuntimeDef.allProperties())
          .where(applicationRuntimeDef.uri().eq(uriWithoutVersion))
          .firstRow();
      if (applicationRuntimeRow.isPresent()) {
        applicationRuntimeRow.get().set(applicationRuntimeDef.uri(), targetUriWithoutVersion);
        Crud.update(applicationRuntimeRow.get().tableData());
        return true;
      }
    } catch (Exception e) {
      log.error("Failed to move application runtime: " + uri + " to " + targetUri, e);
      return false;
    }

    return false;
  }

}
