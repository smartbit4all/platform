package org.smartbit4all.api.collection;

import static org.smartbit4all.core.utility.StringConstant.joinDot;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.meta.EntityDefinition;

public class SearchIndexDataRowWrapper {

  DataRow dataRow;
  EntityDefinition entityDefintion;
  String prefix;

  public SearchIndexDataRowWrapper(DataRow dataRow, EntityDefinition entityDefintion,
      String prefix) {
    super();
    this.dataRow = dataRow;
    this.entityDefintion = entityDefintion;
    this.prefix = prefix;
  }

  public Object getValue(String column) {
    return dataRow.get(entityDefintion.getProperty(getColumn(column)));
  }

  private String getColumn(String column) {
    if (prefix != null) {
      return joinDot(prefix, column);
    }
    return column;
  }



}
