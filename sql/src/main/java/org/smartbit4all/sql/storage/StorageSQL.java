package org.smartbit4all.sql.storage;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.storage.bean.ObjectHistoryEntry;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.domain.data.storage.ObjectStorageImpl;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageLoadOption;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.smartbit4all.domain.data.storage.StorageObjectLock;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;

public class StorageSQL extends ObjectStorageImpl {

  private EntityDefinition entityDef;

  private Property<URI> key;

  private Property<BinaryData> contentField;

  private String keyName;

  private String contentFieldName;

  public StorageSQL(
      EntityDefinition entityDef,
      Property<URI> key,
      Property<BinaryData> contentField, ObjectApi objectApi) {

    this(entityDef, objectApi);

    this.key = key;
    this.contentField = contentField;
  }

  public StorageSQL(
      EntityDefinition entityDef,
      String keyName,
      String contentName, ObjectApi objectApi) {

    this(entityDef, objectApi);

    this.keyName = keyName;
    this.contentFieldName = contentName;
  }

  private StorageSQL(EntityDefinition entityDef, ObjectApi objectApi) {
    super(objectApi);
    this.entityDef = entityDef;
  }

  @Override
  public URI save(StorageObject<?> object) {
    StorageObjectLock storageObjectLock = getLock(object.getUri());
    try {
      // Load the StorageObjectData that is the api object of the storage itself.
      // File objectDataFile = getObjectDataFile(object);
      // // The temporary file of the StorageObjectData will be identified by the transaction id as
      // // extension.
      // StorageObjectData storageObjectData;
      // ObjectVersion newVersion;
      // if (objectDataFile.exists()) {
      // // This is an existing data file.
      // BinaryData dataFile = new BinaryData(objectDataFile, false);
      // Optional<StorageObjectData> optStorageObject = storageObjectDataDef.deserialize(dataFile);
      // storageObjectData = optStorageObject.get();
      // // Extract the current version and create the new one based on this.
      // ObjectVersion currentVersion = storageObjectData.getCurrentVersion();
      // // Increment the serial number. The given object is locked in the meantime so there is no
      // // need to worry about the parallel modification.
      // newVersion = new ObjectVersion().serialNo(currentVersion.getSerialNo() + 1);
      // } else {
      // // The first version in the new object.
      // newVersion = new ObjectVersion();
      // // This will be a new data file, fists we create the StorageObjectData save it into a new
      // // data file.
      // storageObjectData = new StorageObjectData().uri(object.getUri());
      // }
      // // The version is updated with the information attached.
      // storageObjectData.currentVersion(newVersion);
      // newVersion.transactionId(object.getTransactionId()).createdAt(ZonedDateTime.now());
      // storageObjectData.addVersionsItem(newVersion);
      // // TODO Add dependency to UserSession!!
      // File objectDataFileTemp =
      // new File(objectDataFile.getPath() + StringConstant.DOT + object.getTransactionId());
      // if (object.getObject() != null) {
      // File objectVersionFile = getObjectVersionFile(objectDataFile, newVersion);
      // // Write the version file first
      // FileIO.write(objectVersionFile,
      // object.definition().serialize(object.getObject()));
      // newVersion.setEmpty(false);
      // }
      // // Write the data temporary file
      // FileIO.write(objectDataFileTemp,
      // storageObjectDataDef.serialize(storageObjectData));
      // // Atomic move of the temp file.
      // Files.move(objectDataFileTemp.toPath(), objectDataFile.toPath(),
      // StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);

    } finally {
      storageObjectLock.release();
    }
    return object.getUri();
  }

  @Override
  public <T> StorageObject<T> load(Storage storage, URI uri, Class<T> clazz,
      StorageLoadOption... options) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<ObjectHistoryEntry> loadHistory(Storage storage, URI uri,
      ObjectDefinition<?> definition) {
    return Collections.emptyList();
  }

  // @Override
  // public URI save(T object, URI uri) {
  // URI result = constructUri(object, uri);
  // BinaryData binaryData = serializer.toJsonBinaryData(object, clazz);
  //
  // TableData<EntityDefinition> tdUpdate = TableDatas.builder(entityDef)
  // .addRow()
  // .set(getKey(), uri)
  // .set(getContentField(), binaryData)
  // .build();
  //
  // try {
  // StorageSQLUtil.createOrUpdate(tdUpdate, getKey(), uri);
  // } catch (Exception e) {
  // String msg =
  // "Unable to save object (" + uri + ") into " + clazz + " storage (" + entityDef + ")";
  // log.error(msg, e);
  // throw new IllegalStateException(msg, e);
  // }
  // return result;
  // }
  //
  // @Override
  // public URI save(T object) {
  // return save(object, uriAccessor.apply(object));
  // }
  //
  // @Override
  // public List<T> loadAll() {
  // try {
  // TableData<EntityDefinition> allDatasTable = Crud.read(entityDef)
  // .select(getContentField())
  // .listData();
  //
  // if (allDatasTable.isEmpty()) {
  // return Collections.emptyList();
  // }
  //
  // List<T> datas = new ArrayList<>();
  // for (DataRow row : allDatasTable.rows()) {
  // Optional<T> data = loadBinaryDataFromRow(row);
  // if (data.isPresent()) {
  // datas.add(data.get());
  // }
  // }
  //
  // return datas;
  // } catch (Exception e) {
  // String msg = "Unable to load all objects from " + clazz + " storage (" + entityDef + ")";
  // log.error(msg, e);
  // throw new IllegalStateException(msg, e);
  // }
  // }
  //
  // @Override
  // public Optional<T> load(URI uri) {
  // Optional<DataRow> row = Optional.empty();
  // try {
  // row = Crud.read(entityDef)
  // .select(getContentField())
  // .where(getKey().eq(uri))
  // .firstRow();
  // } catch (Exception e) {
  // String msg =
  // "Unable to load object (" + uri + ") from " + clazz + " storage (" + entityDef + ")";
  // log.error(msg, e);
  // throw new IllegalStateException(msg, e);
  // }
  //
  // if (row.isPresent()) {
  // return loadBinaryDataFromRow(row.get());
  // }
  //
  // return Optional.empty();
  // }
  //
  // @Override
  // public List<T> load(List<URI> uri) {
  // try {
  // TableData<EntityDefinition> tdObjects = Crud.read(entityDef)
  // .select(getContentField())
  // .where(getKey().in(uri))
  // .listData();
  //
  // if (tdObjects.size() < 1) {
  // return Collections.emptyList();
  // }
  //
  // List<T> result = new ArrayList<>();
  //
  // for (DataRow row : tdObjects.rows()) {
  // BinaryData content = row.get(getContentField());
  // Optional<T> object = serializer.fromJsonBinaryData(content, clazz);
  //
  // if (object.isPresent()) {
  // result.add(object.get());
  // }
  // }
  //
  // return result;
  // } catch (Exception e) {
  // String msg = "Unable to load object from " + clazz + " storage (" + entityDef + ")";
  // log.error(msg, e);
  // throw new IllegalStateException(msg, e);
  // }
  // }
  //
  // @Override
  // public boolean delete(URI uri) {
  // try {
  // if (StorageSQLUtil.indexExists(entityDef, getKey(), uri)) {
  // TableData<EntityDefinition> tdDelete = TableDatas.builder(entityDef)
  // .addRow()
  // .set(getKey(), uri)
  // .build();
  //
  // Crud.delete(tdDelete);
  // return true;
  // }
  // } catch (Exception e) {
  // String msg = "Unable to delete object from " + clazz + " storage (" + entityDef + ")";
  // log.error(msg, e);
  // throw new IllegalStateException(msg, e);
  // }
  //
  // return false;
  // }

  // private Optional<T> loadBinaryDataFromRow(DataRow row) {
  // BinaryData content = row.get(getContentField());
  // return serializer.fromJsonBinaryData(content, clazz);
  // }

  @SuppressWarnings("unchecked")
  private Property<URI> getKey() {
    if (key != null) {
      return key;
    } else {
      return (Property<URI>) entityDef.getProperty(keyName);
    }
  }

  @SuppressWarnings("unchecked")
  private Property<BinaryData> getContentField() {
    if (contentField != null) {
      return contentField;
    } else {
      return (Property<BinaryData>) entityDef.getProperty(contentFieldName);
    }
  }

  @Override
  public boolean exists(URI uri) {
    return true;
  }

}
