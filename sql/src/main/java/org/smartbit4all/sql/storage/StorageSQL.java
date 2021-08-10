package org.smartbit4all.sql.storage;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.TableDatas;
import org.smartbit4all.domain.data.storage.ObjectStorage;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.utility.crud.Crud;
import org.smartbit4all.types.binarydata.BinaryData;
import org.smartbit4all.types.binarydata.BinaryDataObjectSerializer;

public class StorageSQL<T> implements ObjectStorage<T> {

  private EntityDefinition entityDef;

  private Property<URI> key;

  private Property<BinaryData> contentField;

  private String keyName;

  private String contentFieldName;

  private Class<T> clazz;

  private Function<T, URI> uriProvider;

  private BinaryDataObjectSerializer serializer;

  public StorageSQL(
      EntityDefinition entityDef,
      Property<URI> key,
      Property<BinaryData> contentField,
      Function<T, URI> uriProvider,
      BinaryDataObjectSerializer serializer,
      Class<T> clazz) {

    this(entityDef, uriProvider, serializer, clazz);

    this.key = key;
    this.contentField = contentField;
    this.serializer = serializer;
  }

  public StorageSQL(
      EntityDefinition entityDef,
      String keyName,
      String contentName,
      Function<T, URI> uriProvider,
      BinaryDataObjectSerializer serializer,
      Class<T> clazz) {

    this(entityDef, uriProvider, serializer, clazz);

    this.keyName = keyName;
    this.contentFieldName = contentName;
  }

  private StorageSQL(EntityDefinition entityDef, Function<T, URI> uriProvider,
      BinaryDataObjectSerializer serializer, Class<T> clazz) {
    this.entityDef = entityDef;
    this.uriProvider = uriProvider;
    this.clazz = clazz;
    this.serializer = serializer;
  }

  @Override
  public void save(T object, URI uri) throws Exception {
    BinaryData binaryData = serializer.toJsonBinaryData(object, clazz);

    TableData<EntityDefinition> tdUpdate = TableDatas.builder(entityDef)
        .addRow()
        .set(getKey(), uri)
        .set(getContentField(), binaryData)
        .build();

    StorageSQLUtil.createOrUpdate(tdUpdate, getKey(), uri);
  }

  @Override
  public void save(T object) throws Exception {
    save(object, uriProvider.apply(object));
  }

  @Override
  public List<T> loadAll() throws Exception {
    TableData<EntityDefinition> allDatasTable = Crud.read(entityDef)
        .select(getContentField())
        .listData();
    
    if(allDatasTable.isEmpty()) {
      return Collections.emptyList();
    }
    
    List<T> datas = new ArrayList<>();
    for (DataRow row : allDatasTable.rows()) {
      Optional<T> data = loadBinaryDataFromRow(row);
      if(data.isPresent()) {
        datas.add(data.get());
      }
    }
    
    return datas;
  }
  
  @Override
  public Optional<T> load(URI uri) throws Exception {
    Optional<DataRow> row = Crud.read(entityDef)
        .select(getContentField())
        .where(getKey().eq(uri))
        .firstRow();

    if (row.isPresent()) {
      return loadBinaryDataFromRow(row.get());
    }

    return Optional.empty();
  }

  @Override
  public List<T> load(List<URI> uri) throws Exception {
    TableData<EntityDefinition> tdObjects = Crud.read(entityDef)
        .select(getContentField())
        .where(getKey().in(uri))
        .listData();

    if (tdObjects.size() < 1) {
      return Collections.emptyList();
    }

    List<T> result = new ArrayList<>();

    for (DataRow row : tdObjects.rows()) {
      BinaryData content = row.get(getContentField());
      Optional<T> object = serializer.fromJsonBinaryData(content, clazz);

      if (object.isPresent()) {
        result.add(object.get());
      }
    }

    return result;
  }

  @Override
  public boolean delete(URI uri) throws Exception {
    if (StorageSQLUtil.indexExists(entityDef, getKey(), uri)) {
      TableData<EntityDefinition> tdDelete = TableDatas.builder(entityDef)
          .addRow()
          .set(getKey(), uri)
          .build();

      Crud.delete(tdDelete);
      return true;
    }

    return false;
  }

  private Optional<T> loadBinaryDataFromRow(DataRow row) {
    BinaryData content = row.get(getContentField());
    return serializer.fromJsonBinaryData(content, clazz);
  }
  
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

}
