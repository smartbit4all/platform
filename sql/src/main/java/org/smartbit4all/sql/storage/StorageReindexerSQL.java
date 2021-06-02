package org.smartbit4all.sql.storage;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.storage.index.StorageReindexer;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.utility.crud.Crud;

public class StorageReindexerSQL implements StorageReindexer {
  
  private EntityDefinition contentEntityDef;
  
  private String contentFieldName;
  
  public StorageReindexerSQL(
      EntityDefinition contentEntityDef,
      String contentFieldName) {

    this.contentEntityDef = contentEntityDef;
    this.contentFieldName = contentFieldName;
  }
  
  @Override
  public List<URI> listAllUris() throws Exception {
    TableData<EntityDefinition> datas = Crud.read(contentEntityDef)
        .select(getContentKey())
        .listData();
    
    List<URI> result = new ArrayList<>();
    
    for (DataRow row : datas.rows()) {
      result.add(row.get(getContentKey()));
    }
    
    return result;
  }

  @SuppressWarnings("unchecked")
  private Property<URI> getContentKey() {
    return (Property<URI>) contentEntityDef.getProperty(contentFieldName);
  }
  
}
