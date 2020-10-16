package org.smartbit4all.domain.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.core.utility.ListBasedMap;
import org.smartbit4all.domain.data.index.UniqueIndex;
import org.smartbit4all.domain.data.index.UniqueIndex2;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Reference;
import org.smartbit4all.domain.meta.Reference.Join;

/**
 * This reference is used by two entity data table. It optionally refers one row from the target
 * {@link TableData} for each and every source row.
 * 
 * It contains the references for every row of the source entity data table. The references are
 * maintained from both direction. Let's assume that we have a User, a Role and a UserRole entity in
 * the database. We have two reference UserRole --> User and UserRole --> Role. In the TableData
 * model we will have the two DataReference with the same meta. The DataReference "UserRole --> User
 * will" contain a list of row references for the UserRole rows. It defines the referred User rows
 * for every UserRole row. But at the same time it has a list of row lists that defines every User
 * row the rows of the referencing UserRole rows.
 * 
 * @author Peter Boros
 *
 */
public final class DataReference<S extends EntityDefinition, T extends EntityDefinition> {

  private static final Logger log = LoggerFactory.getLogger(DataReference.class);

  /**
   * A source entity of the reference it has one or more foreign key columns to implement and
   * persist the reference.
   */
  private TableData<S> source;

  /**
   * The target entity data table. It has one or more unique keys that can be referred by the
   * foreign keys of the source entity.
   */
  private TableData<T> target;

  /**
   * The meta data of the reference. It contains the algorithm for the unique key and so on.
   */
  private Reference<S, T> referenceDef;

  /**
   * The join between the properties of the source and the target. The key is the foreign column of
   * the source and the value is the key column from the target. This is typically one to one
   * mapping so we use this special map to avoid unnecessary map administration.
   */
  ListBasedMap<DataColumn<?>, DataColumn<?>> joins = new ListBasedMap<>();

  /**
   * The first source column from the join.
   */
  DataColumn<? extends Comparable<?>> sourceColumn1 = null;

  /**
   * The second source column from the join.
   */
  DataColumn<? extends Comparable<?>> sourceColumn2 = null;

  /**
   * The first target column from the join.
   */
  DataColumn<? extends Comparable<?>> targetColumn1 = null;

  /**
   * The second target column from the join.
   */
  DataColumn<? extends Comparable<?>> targetColumn2 = null;

  /**
   * The target index in case of the one column join.
   */
  UniqueIndex<?> targetIndex1 = null;

  /**
   * The target index in case of the two columns join.
   */
  UniqueIndex2<?, ?> targetIndex2 = null;

  /**
   * The referred row contains the row references of the target entity data for each and every row
   * in the source. The list has the same order than the stored columns of the {@link #source}. So
   * the {@link DataRow#getDataIndex()} can be used for the access.
   */
  private final ArrayList<DataRow> referredTargetRows = new ArrayList<>();

  /**
   * In this list by the order of the {@link #target} rows we find the referring rows of the source.
   */
  private final ArrayList<List<DataRow>> detailSourceRows = new ArrayList<>();

  /**
   * It constructs the data reference from the source to the target. The forign key - key pairs be
   * added later on.
   * 
   * @param source The source entity data table. It contains foreign keys.
   * @param target The target entity data table that has the referred key values.
   * @param referenceDef The meta data of the given reference.
   */
  public DataReference(Reference<S, T> referenceDef, TableData<S> source, TableData<T> target) {
    super();
    this.referenceDef = referenceDef;
    setSource(source);
    setTarget(target);
    addMappings();
    initReferences();
  }

  /**
   * Initialize the references between the rows of the {@link TableData}s.
   */
  private final void initReferences() {
    // The source refers the target rows. We create a map for the target rows to get the row by the
    // values from the join values.
    switch (joins.size()) {
      case 1:
        // If we have one column to join then we can use simple index.
        targetIndex1 = target.index().unique(targetColumn1);
        break;

      case 2:
        // If we have two columns to join then we can use the two columns index.
        targetIndex2 = target.index().unique(targetColumn1, targetColumn2);
        break;

      default:
        log.error(
            "The more than 2 columns join is not supported in the table data reference." + joins);
        return;
    }

    // Re initialize the references.
    referredTargetRows.clear();
    detailSourceRows.clear();
    // Fill the back references with empty lists
    for (int i = 0; i < target.size(); i++) {
      detailSourceRows.add(new ArrayList<DataRow>(5));
    }
    for (DataRow sourceRow : source.rows()) {
      DataRow referredRow = findTargetRowBySourceRow(sourceRow);
      referredTargetRows.add(referredRow);
      // Add this row to the back reference list.
      List<DataRow> backReferenceList = detailSourceRows.get(referredRow.getRowDataIndex());
      backReferenceList.add(sourceRow);
    }
  }

  /**
   * Looking for the target row by the source row using the mapping.
   * 
   * @param sourceRow The source row.
   * @return The target row.
   */
  private final DataRow findTargetRowBySourceRow(DataRow sourceRow) {
    switch (joins.size()) {
      case 1:
        return targetIndex1.getObject(source.get(sourceColumn1, sourceRow));

      case 2:
        // If we have two columns to join then we can use the two columns index.
        return targetIndex2.getObjects(source.get(sourceColumn1, sourceRow),
            source.get(sourceColumn2, sourceRow));

      default:
        return null;
    }
  }

  /**
   * Add a new mapping for the reference.
   * 
   * @param foreignKey
   * @param key
   */
  @SuppressWarnings("unchecked")
  private final void addMappings() {
    for (Join<?> join : referenceDef.joins()) {
      DataColumn<?> sourceColumn = source.addColumn(join.getSourceProperty());
      DataColumn<?> targetColumn = target.addColumn(join.getTargetProperty());
      joins.put(sourceColumn, targetColumn);
      switch (joins.size()) {
        case 1:
          sourceColumn1 = (DataColumn<? extends Comparable<?>>) sourceColumn;
          targetColumn1 = (DataColumn<? extends Comparable<?>>) targetColumn;
          break;

        case 2:
          sourceColumn2 = (DataColumn<? extends Comparable<?>>) sourceColumn;
          targetColumn2 = (DataColumn<? extends Comparable<?>>) targetColumn;
          break;

        default:
          break;
      }
    }
  }

  /**
   * @return
   */
  public TableData<S> source() {
    return source;
  }

  /**
   * @return
   */
  public TableData<T> target() {
    return target;
  }

  public final void setSource(TableData<S> source) {
    this.source = source;
  }

  public final void setTarget(TableData<T> target) {
    this.target = target;
  }

  public final Reference<S, T> getReferenceDef() {
    return referenceDef;
  }

  final List<DataRow> getReferredTargetRows() {
    return referredTargetRows;
  }

  final void ensureCapacityReferredTargetRows(int size) {
    referredTargetRows.ensureCapacity(size);
  }

  final List<List<DataRow>> getDetailSourceRows() {
    return detailSourceRows;
  }

  final void ensureCapacityDetailSourceRows(int size) {
    detailSourceRows.ensureCapacity(size);
  }

  /**
   * Set the reference of a given source row to the target row.
   * 
   * @param sourceRow If this row is null then nothing will happen. There is no row to modify.
   * @param targetRow If the target row is not null then we add this reference. If the reference is
   *        already set then the previous value will be removed. If this target is null then we
   *        remove the reference if it's already set.
   */
  public void set(DataRow sourceRow, DataRow targetRow) {
    if (sourceRow == null) {
      return;
    }
    DataRow currentTargetRow = referredTargetRows.get(sourceRow.getRowDataIndex());
    if (currentTargetRow != null) {
      // We must remove this reference from the detail list of the target row no matter what is the
      // value of the new reference.
      List<DataRow> currentBackReference = detailSourceRows.get(currentTargetRow.getRowDataIndex());
      // TODO Far not optimal to remove the row by searching in a list.
      currentBackReference.remove(sourceRow);
    }
    referredTargetRows.set(sourceRow.getRowDataIndex(), targetRow);
    // We fill the foreign columns with the key values by the mapping.
    for (Entry<DataColumn<?>, DataColumn<?>> join : joins.entrySet()) {
      join.getKey().setValue(sourceRow, join.getValue().getValue(targetRow));
    }
    List<DataRow> backReference = detailSourceRows.get(targetRow.getRowDataIndex());
    if (backReference == null) {
      // This is the first time when the given target row is referred.
      backReference = new ArrayList<>();
      detailSourceRows.set(targetRow.getRowDataIndex(), backReference);
    }
    // TODO Check the duplications or avoid having duplicated rows!!!
    backReference.add(sourceRow);
  }

}
