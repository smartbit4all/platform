package org.smartbit4all.domain.data.filtering;

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.index.NonUniqueIndex;

/**
 * This step is responsible for evaluating the equal, not equal, in, not in expressions
 * 
 * @author Peter Boros
 *
 */
public class EvaluationIndexedNonUnique<T> extends EvaluationStep {

  /**
   * If this is true, then at the end the result will be collected from the non matching rows.
   */
  private final boolean invert;

  /**
   * This is the index for the values. It contains a list of matching rows for every single value.
   * The index is not sorted so it can be used only for the search and not navigate.
   */
  private final NonUniqueIndex<T> idx;

  /**
   * The values contains all the values that must be checked against the index.
   */
  private final List<T> valuesToSearch;

  EvaluationIndexedNonUnique(NonUniqueIndex<T> index, List<T> values, boolean invert) {
    super();
    this.idx = index;
    this.valuesToSearch = values;
    this.invert = invert;
  }

  @Override
  List<DataRow> execute(TableData<?> tableData, List<DataRow> rows) {
    List<DataRow> matchingRows = new ArrayList<>();
    for (T t : valuesToSearch) {
      matchingRows.addAll(idx.get(t));
    }
    matchingRows = DataRow.sortByPosition(matchingRows);

    return invert ? DataRow.minus(rows, matchingRows) : DataRow.intersect(rows, matchingRows);
  }

  @Override
  protected void print(StringBuilder buffer, String prefix, String childrenPrefix) {
    buffer.append(prefix);
    buffer.append("EvaluationIndexedNonUnique");
    buffer.append('\n');
  }

}
