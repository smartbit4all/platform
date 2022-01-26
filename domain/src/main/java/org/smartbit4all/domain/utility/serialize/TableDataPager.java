package org.smartbit4all.domain.utility.serialize;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.TableDatas;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.service.entity.EntityManager;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

/**
 * The pager is based on a RandomAccess file that contains the {@link TableData} serialized by the
 * {@link TableDataSerializer}. The pager has a working {@link TableData} for the active frame in
 * the memory.
 * 
 * @author Peter Boros
 */
public final class TableDataPager<E extends EntityDefinition> {

  /**
   * The active page located in the memory currently.
   */
  private TableData<E> activePage;

  /**
   * The first row index of the original table data that is the 0. row in the active page.
   */
  private Long activePageFirstRowIdx = Long.valueOf(-1l);

  /**
   * The last row index of the original table data that is the size() - 1. row in the active page.
   */
  private Long activePageLastRowIdx = Long.valueOf(-1l);;

  /**
   * True if we have a loaded active page and false if the memory page is empty.
   */
  private boolean hasActivePage = false;

  /**
   * The file that contains the table data.
   */
  private RandomAccessFile tableDataContent;

  /**
   * The file.
   */
  private File file;

  /**
   * The list of the row indices loaded from the file.
   */
  private List<Long> rowIndices;

  private EntityManager entityManager;

  private Class<E> entityDefClazz;

  private long rowIndicesPointer;

  /**
   * Constructs a table data pager. Load the structure of the table data and the row indices.
   * 
   * @param entityDefClazz
   * @param file
   * @throws IOException
   */
  private TableDataPager(Class<E> entityDefClazz, File file, EntityManager entityManager)
      throws Exception {
    this.entityDefClazz = entityDefClazz;
    this.file = file;
    this.entityManager = entityManager;
    tableDataContent = new RandomAccessFile(file, "r");
    readRowIndices();
    readEntityDefMeta();
  }

  private void readRowIndices() throws IOException {
    if (tableDataContent.length() < Long.BYTES) {
      throw new IllegalArgumentException(
          "The " + file + " file is not enough long to contain a table data as content. (length="
              + tableDataContent.length() + ")");
    }
    tableDataContent.seek(tableDataContent.length() - Long.BYTES);
    rowIndicesPointer = readLong();
    tableDataContent.seek(rowIndicesPointer);
    int numberOfRows = readInt();
    rowIndices = new ArrayList<>(numberOfRows);
    while (numberOfRows > 0) {
      rowIndices.add(readLong());
      numberOfRows--;
    }
  }

  @SuppressWarnings("unchecked")
  private void readEntityDefMeta() throws Exception {
    tableDataContent.seek(0);
    URI entityDefUri = readObject(URI.class);
    EntityDefinition entityDef = entityManager.definition(entityDefUri);
    if (entityDef == null) {
      throw new Exception("EntityDefinition uri can not be parsed from file!");
    }
    if (!entityDefClazz.isAssignableFrom(entityDef.getClass())) {
      throw new Exception(
          "EntityDefinition mismatch! The TableDataPager has been inizialized with a"
              + " different EntityDefinitionin subclass!");
    }
    activePage = (TableData<E>) TableDatas.of(entityDef);
    int numberOfColumns = readInt();
    while (numberOfColumns > 0) {
      URI columnPropertyUri = readObject(URI.class);
      Property<?> columnProperty = entityManager.property(columnPropertyUri);
      activePage.addColumnOwn(columnProperty);
      numberOfColumns--;
    }
    hasActivePage = true;
  }

  /**
   * Deserialize an object from the given position of the {@link RandomAccessFile}. Seek to the
   * position and read the class.
   * 
   * @param <T>
   * @param clazz The recommended class that is the return value or null.
   * @return The deserialized value object or null.
   * @throws IOException
   */
  @SuppressWarnings("unchecked")
  private <T> T readObject(Class<T> clazz) throws IOException {
    byte typeValue = tableDataContent.readByte();
    SerializationType<T> type = (SerializationType<T>) SerializationType.types[typeValue];
    SerializationType requestedType = SerializationType.getType(clazz);
    if (type != requestedType && type != SerializationType.NULL
        && type != SerializationType.NULL_VALUE) {
      throw new IllegalArgumentException("Type mismatch when reading " + clazz + " from the "
          + file + " (" + type + " was found instead)");
    }
    if (type == SerializationType.NULL_VALUE) {
      return null;
    }
    int length = tableDataContent.readInt();
    byte[] bytes = new byte[length];
    tableDataContent.read(bytes);
    Object object = type.getDeserializer().apply(bytes);
    return (T) object;
  }

  /**
   * The {@link #tableDataContent} is already at the position!
   * 
   * @return
   * @throws IOException
   */
  private int readInt() throws IOException {
    byte bytes[] = new byte[Integer.BYTES];
    tableDataContent.read(bytes);
    return Ints.fromByteArray(bytes);
  }

  /**
   * The {@link #tableDataContent} is already at the position!
   * 
   * @return
   * @throws IOException
   */
  private long readLong() throws IOException {
    byte bytes[] = new byte[Long.BYTES];
    tableDataContent.read(bytes);
    return Longs.fromByteArray(bytes);
  }

  /**
   * @return The total number of rows of the stored data.
   */
  public int getTotalRowCount() {
    return rowIndices.size();
  }

  /**
   * 
   * @param offset The row index to fetch the data from.
   * @param limit The number of rows to fetch.
   * @return Returns the TableData with the fetched rows.
   * @throws Exception
   */
  public TableData<E> fetch(int offset, int limit) throws Exception {
    if (!hasActivePage) {
      throw new IllegalStateException("TableDataPager has not been propertly initialized!");
    }
    int currentOffset = rowIndices.indexOf(activePageFirstRowIdx);
    int currentLimit = rowIndices.indexOf(activePageLastRowIdx) + 1 - currentOffset;
    if (offset == currentOffset && limit == currentLimit) {
      return activePage;
    }
    activePage.clearRows();
    activePageFirstRowIdx = -1l;
    activePageLastRowIdx = -1l;


    if (rowIndices.size() < offset) {
      // FIXME return an empty TableData or throw exception?
      return activePage;
    }
    Long offsetIdx = rowIndices.get(offset);

    long pointer = offsetIdx.longValue();
    activePageFirstRowIdx = pointer;
    tableDataContent.seek(pointer);
    while (limit > 0 && pointer < rowIndicesPointer) {
      readRow();
      activePageLastRowIdx = pointer;
      pointer = tableDataContent.getFilePointer();
      limit--;
    }

    return activePage;
  }

  private void readRow() throws Exception {
    DataRow newRow = activePage.addRow();
    for (DataColumn<?> column : activePage.columns()) {
      Object value = readObject(column.getProperty().type());
      newRow.setObject(column, value);
    }
  }

  /**
   * Closes the pager to free allocated resources.
   */
  public void close() throws IOException {
    tableDataContent.close();
  }

  public static <T extends EntityDefinition> TableDataPager<T> create(Class<T> entityDefClazz,
      File file, EntityManager entityManager) throws Exception {
    return new TableDataPager<>(entityDefClazz, file, entityManager);
  }
}
