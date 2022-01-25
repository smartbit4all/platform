package org.smartbit4all.domain.utility.serialize;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
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
   * The first row of original table data that is the 0. row in the active page.
   */
  private int firstPageRow;

  /**
   * The last row index original table data that is the size() - 1. row in the active page.
   */
  private int lastPageRow;

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

  /**
   * Constructs a table data pager. Load the structure of the table data and the row indices.
   * 
   * @param entityDef
   * @param file
   * @throws IOException
   */
  public TableDataPager(Class<E> entityDef, File file) throws IOException {
    this.file = file;
    tableDataContent = new RandomAccessFile(file, "r");
    readRowIndices();
  }

  private void readRowIndices() throws IOException {
    if (tableDataContent.length() < Long.BYTES) {
      throw new IllegalArgumentException(
          "The " + file + " file is not enough long to contain a table data as content. (length="
              + tableDataContent.length() + ")");
    }
    tableDataContent.seek(tableDataContent.length() - Long.BYTES);
    long startPosition = readLong();
    tableDataContent.seek(startPosition);
    int numberOfRows = readInt();
    rowIndices = new ArrayList<>(numberOfRows);
    while (numberOfRows > 0) {
      rowIndices.add(readLong());
      numberOfRows--;
    }
    // Read the TableData meta
  }

  /**
   * Deserialize an object from the given position of the {@link RandomAccessFile}. Seek to the
   * position and read the class.
   * 
   * @param <T>
   * @param position
   * @param clazz The recommended class that is the return value or null.
   * @return The deserialized value object or null.
   * @throws IOException
   */
  @SuppressWarnings("unchecked")
  private <T> T readObject(long position, Class<T> clazz) throws IOException {
    tableDataContent.seek(position);
    byte typeValue = tableDataContent.readByte();
    SerializationType<T> type = (SerializationType<T>) SerializationType.types[typeValue];
    SerializationType requestedType = SerializationType.getType(clazz);
    if (type != requestedType && type != SerializationType.NULL
        && type != SerializationType.NULL_VALUE) {
      throw new IllegalArgumentException("Type miss match when reading " + clazz + " from the "
          + file + " (" + type + " was found instead)");
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
}
