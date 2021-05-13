package org.smartbit4all.domain.data.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.filtering.ExpressionEvaluationPlan;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;

/**
 * This service is responsible for loading data into a {@link TableData} from a storage. It will
 * have a parameterized instance for every {@link ExpressionEvaluationPlan}.
 * 
 * @author Peter Boros
 *
 * @param <E>
 */
public abstract class StorageLoader {

  private final EntityDefinition entityDef;

  private final Map<Object, DataRow> rowsByPrimaryKey = new HashMap<>();

  /**
   * The {@link StorageIndex} list available for the given loader.
   */
  private final List<StorageIndex> indices = new ArrayList<>();

  public StorageLoader(EntityDefinition entityDef) {
    super();
    this.entityDef = entityDef;
  }

  public void fillRows(TableData<?> tableData, List<DataRow> rowsToFill) {
    if (rowsToFill == null) {
      return;
    }
    rowsToFill.forEach((r) -> {
      fillRow(tableData, r);
    });
  }

  public List<DataRow> appendKeyValues(TableData<?> tableData, Set<?> keyValues) {
    List<DataRow> result = new ArrayList<>();
    for (Object keyValue : keyValues) {
      DataRow dataRow = rowsByPrimaryKey.computeIfAbsent(keyValue, (k) -> {
        return tableData.addRow();
      });
      dataRow.setObject(getPrimaryKey(), keyValue);
      result.add(dataRow);
    }
    return result;
  }

  protected abstract void fillRow(TableData<?> tableData, DataRow rowToFill);

  public abstract void loadAllRows(TableData<?> tableData);

  public final Property<?> getPrimaryKey() {
    // We can use EntityDefinition with only primary key.
    return entityDef.PRIMARYKEYDEF().iterator().next();
  }

  public final Map<Object, DataRow> getRowsByPrimaryKey() {
    return rowsByPrimaryKey;
  }

  public final List<StorageIndex> getIndices() {
    return indices;
  }

  public final EntityDefinition getEntityDef() {
    return entityDef;
  }

}
