package org.smartbit4all.domain.utility.serialize;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.binarydata.BinaryData;
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
   * The list of the row indices loaded from the file.
   */
  private List<Long> rowIndices;

  private EntityManager entityManager;

  private Class<E> entityDefClazz;

  private long rowIndicesPointer;

  private RafGetter rafGetter;

  /**
   * Constructs a table data pager. Load the structure of the table data and the row indices.
   * 
   * @param entityDefClazz
   * @throws IOException
   */
  private TableDataPager(Class<E> entityDefClazz, RafGetter rafGetter, EntityManager entityManager)
      throws Exception {
    this.entityDefClazz = entityDefClazz;
    this.entityManager = entityManager;
    this.rafGetter = rafGetter;
    RandomAccessFile raf = rafGetter.getRaf();
    readRowIndices(raf);
    readEntityDefMeta(raf);
    raf.close();
  }

  private TableDataPager(Class<E> entityDefClazz, File file, EntityManager entityManager)
      throws Exception {
    this(entityDefClazz, () -> new RandomAccessFile(file, "r"), entityManager);
  }

  private TableDataPager(Class<E> entityDefClazz, BinaryData binaryData,
      EntityManager entityManager) throws Exception {
    this(entityDefClazz, () -> binaryData.asRandomAccessFile(), entityManager);
  }

  private void readRowIndices(RandomAccessFile raf) throws IOException {
    if (raf.length() < Long.BYTES) {
      throw new IllegalArgumentException(
          "The underlying file is not enough long to contain a table data as content. (length="
              + raf.length() + ")");
    }
    raf.seek(raf.length() - Long.BYTES);
    rowIndicesPointer = readLong(raf);
    raf.seek(rowIndicesPointer);
    int numberOfRows = readInt(raf);
    rowIndices = new ArrayList<>(numberOfRows);
    while (numberOfRows > 0) {
      rowIndices.add(readLong(raf));
      numberOfRows--;
    }
  }

  @SuppressWarnings("unchecked")
  private void readEntityDefMeta(RandomAccessFile raf) throws Exception {
    raf.seek(0);
    URI entityDefUri = readObject(URI.class, raf);
    EntityDefinition entityDef = entityManager.definition(entityDefUri);
    if (entityDef == null) {
      throw new Exception("EntityDefinition uri can not be parsed from file!");
    }
    if (entityDefClazz != null && !entityDefClazz.isAssignableFrom(entityDef.getClass())) {
      throw new Exception(
          "EntityDefinition mismatch! The TableDataPager has been inizialized with a"
              + " different EntityDefinitionin subclass!");
    }
    activePage = (TableData<E>) TableDatas.of(entityDef);
    int numberOfColumns = readInt(raf);
    while (numberOfColumns > 0) {
      URI columnPropertyUri = readObject(URI.class, raf);
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
  private <T> T readObject(Class<T> clazz, RandomAccessFile raf) throws IOException {
    byte typeValue = raf.readByte();
    SerializationType<T> type = (SerializationType<T>) SerializationType.types[typeValue];
    SerializationType requestedType = SerializationType.getType(clazz);
    if (type != requestedType && type != SerializationType.NULL
        && type != SerializationType.NULL_VALUE) {
      throw new IllegalArgumentException("Type mismatch when reading " + clazz
          + " from the underlying file (" + type + " was found instead)");
    }
    if (type == SerializationType.NULL_VALUE) {
      return null;
    }
    int length = raf.readInt();
    byte[] bytes = new byte[length];
    raf.read(bytes);
    Object object = type.getDeserializer().apply(bytes);
    return (T) object;
  }

  /**
   * The {@link RandomAccessFile} parameter is already at the position!
   * 
   * @return
   * @throws IOException
   */
  private int readInt(RandomAccessFile raf) throws IOException {
    byte bytes[] = new byte[Integer.BYTES];
    raf.read(bytes);
    return Ints.fromByteArray(bytes);
  }

  /**
   * The {@link RandomAccessFile} parameter is already at the position!
   * 
   * @return
   * @throws IOException
   */
  private long readLong(RandomAccessFile raf) throws IOException {
    byte bytes[] = new byte[Long.BYTES];
    raf.read(bytes);
    return Longs.fromByteArray(bytes);
  }

  /**
   * @return The total number of rows of the stored data.
   */
  public int getTotalRowCount() {
    return rowIndices.size();
  }

  /**
   * Read the whole saved {@link TableData} at once into memory.
   * 
   * @return
   * @throws Exception
   */
  public TableData<E> fetchAll() throws Exception {
    return fetch(0, getTotalRowCount());
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

    RandomAccessFile raf = rafGetter.getRaf();
    long pointer = offsetIdx.longValue();
    activePageFirstRowIdx = pointer;
    raf.seek(pointer);
    while (limit > 0 && pointer < rowIndicesPointer) {
      readRow(raf);
      activePageLastRowIdx = pointer;
      pointer = raf.getFilePointer();
      limit--;
    }

    raf.close();
    return activePage;
  }

  private void readRow(RandomAccessFile raf) throws Exception {
    DataRow newRow = activePage.addRow();
    for (DataColumn<?> column : activePage.columns()) {
      Object value = readObject(column.getProperty().type(), raf);
      newRow.setObject(column, value);
    }
  }


  public static <T extends EntityDefinition> TableDataPager<T> create(File file,
      EntityManager entityManager) throws Exception {
    return new TableDataPager<>(null, file, entityManager);
  }

  public static <T extends EntityDefinition> TableDataPager<T> create(Class<T> entityDefClazz,
      File file, EntityManager entityManager) throws Exception {
    return new TableDataPager<>(entityDefClazz, file, entityManager);
  }

  public static <T extends EntityDefinition> TableDataPager<T> create(Class<T> entityDefClazz,
      BinaryData binaryData, EntityManager entityManager) throws Exception {
    return new TableDataPager<>(entityDefClazz, binaryData, entityManager);
  }

  private static interface RafGetter {
    RandomAccessFile getRaf() throws FileNotFoundException;
  }
}
