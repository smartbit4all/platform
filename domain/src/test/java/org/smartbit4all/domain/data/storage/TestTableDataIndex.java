package org.smartbit4all.domain.data.storage;

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.index.NonUniqueIndex;
import org.smartbit4all.domain.data.index.StorageNonUniqueIndex;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.Expression2Operand;
import org.smartbit4all.domain.meta.Property;

public class TestTableDataIndex extends StorageNonUniqueIndex<String, Long> {
  
  TableData<? extends EntityDefinition> tableData;

  Property<Long> pk;

  Property<String> indexedProperty;

  private NonUniqueIndex<String> nonUnique;

  public TestTableDataIndex(
      TableData<? extends EntityDefinition> tableData,
      Property<Long> pk,
      Property<String> indexedProperty) {

    this.tableData = tableData;
    this.pk = pk;
    this.indexedProperty = indexedProperty;

    nonUnique = tableData.index().nonUnique(indexedProperty);
  }

  @Override
  public boolean canUseFor(Expression expression) {
    if (expression instanceof Expression2Operand<?>) {
      Expression2Operand<?> expression2operand = (Expression2Operand<?>) expression;
      if (indexedProperty == expression2operand.getOp().property()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public List<Long> get(String key) {
    List<Long> result = new ArrayList<>();

    for (DataRow dataRow : nonUnique.get(key)) {
      Long value = dataRow.get(pk);
      if (value != null) {
        result.add(value);
      }
    }

    return result;
  }
  
}
