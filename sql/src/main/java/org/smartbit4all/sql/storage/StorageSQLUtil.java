package org.smartbit4all.sql.storage;

import java.net.URI;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.utility.crud.Crud;

public class StorageSQLUtil {

  public static void createOrUpdate(
      TableData<? extends EntityDefinition> tableData,
      Property<URI> keyField,
      URI key) throws Exception {

    if (StorageSQLUtil.indexExists(tableData.entity(), keyField, key)) {
      Crud.update(tableData);
    } else {
      Crud.create(tableData);
    }
  }
  
  public static boolean indexExists(
      EntityDefinition entityDef,
      Property<URI> keyField,
      URI key) throws Exception {

    TableData<? extends EntityDefinition> listData = Crud.read(entityDef)
        .select(keyField)
        .where(keyField.eq(key))
        .listData();

    return listData.rows().size() > 0;
  }
  
}
