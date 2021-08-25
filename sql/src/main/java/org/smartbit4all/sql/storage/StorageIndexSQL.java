package org.smartbit4all.sql.storage;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.storage.index.StorageIndex;
import org.smartbit4all.domain.data.storage.index.StorageIndexField;
import org.smartbit4all.domain.data.storage.index.StorageIndexUtil;
import org.smartbit4all.domain.data.storage.index.StorageIndexer;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.utility.crud.Crud;

public class StorageIndexSQL<T> implements StorageIndexer<T> {

  @Override
  public <V> List<URI> listUris(Property<URI> key, Property<V> indexField, V value)
      throws Exception {

    TableData<EntityDefinition> data = Crud.read(indexField.getEntityDef())
        .select(key)
        .where(indexField.eq(value))
        .listData();

    return data.values(key);
  }

  @Override
  public List<URI> listUris(StorageIndex<T> index, Expression expression) throws Exception {
    TableData<EntityDefinition> datas = Crud.read(index.getEntityDef())
        .select(index.getKey())
        .where(expression)
        .listData();

    if(datas.size() < 1) {
      return Collections.emptyList();
    }
    
    List<URI> result = new ArrayList<>();
    
    for (DataRow row : datas.rows()) {
      result.add(row.get(index.getKey()));
    }
    
    return result;
  }
  
  @Override
  public void updateIndex(T object, StorageIndex<T> index) throws Exception {
    URI uri = index.getObjectUriProvider().apply(object);
    Property<URI> key = index.getKey();

    TableData<? extends EntityDefinition> tableData = index.createTableDataWithColumns();
    DataRow row = tableData.addRow();

    row.set(key, uri);

    for (StorageIndexField<T, ?> field : index.getFields()) {
      field.setRowValue(row, object);
    }

    StorageSQLUtil.createOrUpdate(tableData, key, uri);
  }

  @Override
  public <F> boolean canUseFor(Property<F> valueField, Expression expression) {
    return StorageIndexUtil.twoOperandPropertyIndex(valueField, expression);
  }

}
