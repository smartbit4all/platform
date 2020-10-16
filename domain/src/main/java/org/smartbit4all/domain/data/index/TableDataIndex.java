package org.smartbit4all.domain.data.index;

import java.lang.ref.WeakReference;
import java.util.List;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.Property;

/**
 * The common super class of the indices on the TableData.
 * 
 * @author Peter Boros
 */
public abstract class TableDataIndex {

  /**
   * The generated index name that contains the type (UNIQUE, NONUNIQUE) and the column(s). Looks
   * like this: UNIQUE(ID) or NONUNIQUE(NAME, BIRTHDATE)
   */
  String indexName;

  public static enum IndexType {
    UNIQUE, NONUNIQUE
  }

  /**
   * The type of the given index.
   */
  IndexType type;

  /**
   * The flag defines if the index is composite (consists of more than one value) or not.
   */
  boolean composite;

  /**
   * The reference to the table data that this index refers to.
   */
  protected WeakReference<TableData<?>> tableDataRef;

  /**
   * Constructs the index.
   * 
   * @param tableData
   * @param type
   * @param composite
   */
  public TableDataIndex(TableData<?> tableData, IndexType type, boolean composite) {
    super();
    this.tableDataRef = new WeakReference<TableData<?>>(tableData);
    this.type = type;
    this.composite = composite;
  }

  static String constructIndexName(IndexType type, List<Property<?>> properties) {
    StringBuilder sb = new StringBuilder();
    for (Property<?> property : properties) {
      if (sb.length() > 0) {
        sb.append(StringConstant.COMMA_SPACE);
      }
      sb.append(property.getName());
    }
    return type + StringConstant.LEFT_PARENTHESIS + sb.toString()
        + StringConstant.RIGHT_PARENTHESIS;
  }

}
