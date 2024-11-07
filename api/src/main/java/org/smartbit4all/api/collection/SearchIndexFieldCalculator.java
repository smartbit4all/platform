package org.smartbit4all.api.collection;

import static org.smartbit4all.core.utility.StringConstant.joinDot;
import java.util.function.Function;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;

public class SearchIndexFieldCalculator {

  String propertyName;
  EntityDefinition entityDefintion;
  Function<SearchIndexDataRowWrapper, Object> calculator;
  String prefix;

  public SearchIndexFieldCalculator(String propertyName, EntityDefinition entityDefintion,
      Function<SearchIndexDataRowWrapper, Object> calculator, String prefix) {
    super();
    this.propertyName = propertyName;
    this.entityDefintion = entityDefintion;
    this.calculator = calculator;
    this.prefix = prefix;
  }

  public void calculate(DataRow row) {
    TableData<?> tableData = row.tableData();
    Property property = entityDefintion.getProperty(joinDot(prefix, propertyName));
    DataColumn column = tableData.getColumn(property);
    if (column == null) {
      column = tableData.addColumn(property);
    }
    Object value = calculator.apply(new SearchIndexDataRowWrapper(row, entityDefintion, prefix));
    row.set(column, value);
  }



}
