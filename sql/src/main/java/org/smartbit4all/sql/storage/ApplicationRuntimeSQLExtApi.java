package org.smartbit4all.sql.storage;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.invocation.bean.ApplicationRuntimeData;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.domain.application.ApplicationRuntimeApiStorageImpl;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableDatas;
import org.smartbit4all.domain.data.storage.ObjectNotFoundException;
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
      Arrays.asList(new ManagedObject(ApplicationRuntimeApiStorageImpl.CLUSTER,
          ApplicationRuntimeData.class.getName()));

  @Override
  public List<ManagedObject> getManagedObjects() {
    return managedObjects;
  }

  private final ObjectDefinition<ApplicationRuntimeData> getDefintion() {
    if (applicationRuntimeDataDef == null) {
      applicationRuntimeDataDef = objectDefinitionApi.definition(ApplicationRuntimeData.class);
    }
    return applicationRuntimeDataDef;
  }

  @Override
  public <T> List<StorageObject<T>> loadBatch(Storage storage, List<UriInfo> uriInfos,
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
          readFromRow(storage, objectEntryRows.get(uriInfo.uri.toString()), clazz);
      if (storageObject == null) {
        throw new ObjectNotFoundException(uniqueBaseUris, clazz, "Object not found.");
      }
      result.add(storageObject);
    }
    return result;
  }

  private final <T> StorageObject<T> readFromRow(Storage storage, DataRow r, Class<T> clazz) {
    try {
      StorageObject<T> storageObject = storage.instanceOf(clazz);
      storageObject
          .setLastModified(r.get(applicationRuntimeDef.lastTouchTime()));
      storageObject.setObjectAsMap(getDefintion().deserializeAsMap(
          r.get(applicationRuntimeDef.objectContent())));
      return storageObject;

    } catch (Exception e) {
      log.error("Failed to load application runtime row: " + r, e);
    }
    return null;
  }


  @Override
  public Long saveObject(StorageObject<?> object, BinaryData relationBinaryData) {
    ApplicationRuntimeData runtimeData = getDefintion().fromMap(object.getObjectAsMap());
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

}
