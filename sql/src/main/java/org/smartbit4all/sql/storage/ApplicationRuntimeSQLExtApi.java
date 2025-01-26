package org.smartbit4all.sql.storage;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.invocation.bean.ApplicationRuntimeData;
import org.smartbit4all.api.storage.bean.ObjectVersion;
import org.smartbit4all.api.storage.bean.StorageObjectData;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.domain.application.ApplicationRuntimeApiStorageImpl;
import org.smartbit4all.domain.data.DataRow;
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
        .map(info -> info.baseUri)
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
      throw new ObjectNotFoundException(uniqueBaseUris, null, "Object not found.");
    }
    return null;
  }

  private final <T> StorageObject<T> readFromRow(Storage storage, DataRow r, Class<T> clazz) {
    try {
      URI uri = URI.create(r.get(applicationRuntimeDef.uri()));
      StorageObjectData storageObjectData = new StorageObjectData().className(clazz.getName())
          .uri(uri).currentVersion(new ObjectVersion().serialNoData(Long.valueOf(0)));

      ApplicationRuntimeData applicationRuntimeData = new ApplicationRuntimeData().uri(uri)
          .uuid(UUID.fromString(r.get(applicationRuntimeDef.uuid())))
          .baseUrl(r.get(applicationRuntimeDef.baseUrl()))
          .startupTime(r.get(applicationRuntimeDef.startupTime()).toInstant().toEpochMilli())
          .stopTime(r.get(applicationRuntimeDef.stopTime()).toInstant().toEpochMilli())
          .timeOffset(r.get(applicationRuntimeDef.timeOffset()))
          .lastTouchTime(r.get(applicationRuntimeDef.lastTouchTime()).toInstant().toEpochMilli());

      StorageObject<T> storageObject = storage.instanceOf(clazz);
      storageObject.setLastModified(applicationRuntimeData.getLastTouchTime());
      storageObject.setObjectAsMap(getDefintion().toMap(applicationRuntimeData));
      return storageObject;

    } catch (Exception e) {
      log.error("Failed to load application runtime row: " + r, e);
    }
    return null;
  }


  @Override
  public Long saveObject(StorageObject<?> object, BinaryData relationBinaryData) {
    // TODO Auto-generated method stub
    return null;
  }

}
