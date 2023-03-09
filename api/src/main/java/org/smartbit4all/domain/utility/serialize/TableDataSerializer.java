package org.smartbit4all.domain.utility.serialize;

import static org.smartbit4all.domain.utility.serialize.SerializationType.NULL_VALUE;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.TableDatas;
import org.smartbit4all.domain.meta.Property;
import com.google.common.io.ByteStreams;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

/**
 * The table data serializer is a stateful output stream writer that writes the rows of a table data
 * directly to an output stream. The first step is to initiate the {@link TableData} and write the
 * meta data (columns and so on). The table data always has one row in memory and before start
 * adding a new row the serializer write the current row to the stream. When we finish the whole
 * process the row index information is written to the end of the stream. The last long in the
 * stream defines the start of the index table. The index table defines the index of the rows. The
 * meta is started at the very beginning of the stream.
 *
 * @author Peter Boros
 *
 */
public final class TableDataSerializer {

  /**
   * The output stream for writing.
   */
  private OutputStream os;

  /**
   * ObjectApi for serializing complex objects.
   */
  private ObjectApi objectApi;

  /**
   * The staring indices of the rows in the os. When we start a new row then the
   * {@link #currentPosition} is saved.
   */
  private List<Long> rowIndices = new ArrayList<>();

  /**
   * The current position of the writer. When we write one by to the output then it will be the
   * index of this new byte.
   */
  private long currentPosition = 0l;

  /**
   * The table data as a working variable. Its one and only one row is used as staging area for
   * writing the rows.
   */
  private TableData<?> tableData;

  /**
   * The staging row is the row where we can collect the values while we start a new row. The row
   * contained by the {@link #tableData}.
   */
  private DataRow stagingRow = null;

  /**
   * Construct a new serializer with an {@link OutputStream} as output.
   *
   * @param os The already opened output stream.
   */
  private TableDataSerializer(OutputStream os, ObjectApi objectApi) {
    this.os = os;
    this.objectApi = objectApi;
  }

  /**
   * Set the table data, writes the meta information and after this call we are ready to accept the
   * rows with {@link #addRow()}.
   *
   * @param tableData
   * @return
   * @throws IOException
   */
  public TableDataSerializer tableData(TableData<?> tableData) throws IOException {
    this.tableData = tableData;
    this.tableData.clearRows();
    writeMeta(tableData);
    return this;
  }

  /**
   * Add a new row to the serialization. It will be the new staging row and we already have one then
   * it will be written to the output stream.
   *
   * @return
   * @throws IOException
   */
  public DataRow addRow() throws IOException {
    if (tableData == null) {
      throw new IllegalStateException("There is no meta TableData set!");
    }
    if (stagingRow != null) {
      writeStagingRow();
    } else {
      stagingRow = tableData.addRow();
    }
    return stagingRow;
  }

  /**
   * Write out the current staging row.
   *
   * @throws IOException
   */
  private void writeStagingRow() throws IOException {
    // Save the row index.
    rowIndices.add(currentPosition);
    // Write the staged row into the output stream.
    for (DataColumn<?> col : tableData.columns()) {
      writeObject(stagingRow.get(col.getProperty()));
      stagingRow.set(col, null);
    }
  }

  /**
   * Finish the whole table data serialization process. After this call the instance will be no
   * longer usable. It closes the underlying output stream.
   *
   * @throws IOException
   */
  public void finish() throws IOException {
    if (stagingRow != null) {
      writeStagingRow();
    }
    writeRowIndices();
    os.close();
  }

  /**
   * The static method that creates a new instance from the serializer points to the given output
   * stream.
   *
   * @param os
   * @return
   */
  public static final TableDataSerializer to(OutputStream os) {
    return to(os, null);
  }

  public static final TableDataSerializer to(OutputStream os, ObjectApi objectApi) {

    Objects.requireNonNull(os, "The output stream is missing to serialize the table data.");
    return new TableDataSerializer(os, objectApi);
  }

  /**
   * Save an already existing table data.
   *
   * @param tableData
   * @param os
   * @throws IOException
   */
  public static final void save(TableData<?> tableData, OutputStream os, ObjectApi objectApi)
      throws IOException {
    if (tableData == null) {
      return;
    }
    TableDataSerializer result = to(os, objectApi);
    result.tableData(TableDatas.copyMeta(tableData));
    for (DataRow row : tableData.rows()) {
      DataRow addRow = result.addRow();
      TableDatas.copyRow(tableData.properties(), row, addRow);
    }
    result.finish();
  }

  /**
   * Writes the meta as: &ltMETA>&ltentityDef uri>&ltCOLS>&ltnumber of cols>&ltcol1 uri>&ltcol1
   * type>&ltcol2 uri>&ltcol2 type>...&ltcolN uri>&ltcolN type>
   */
  private void writeMeta(TableData<?> tableData) throws IOException {
    writeObject(tableData.entity().getUri());
    Collection<DataColumn<?>> columns = tableData.columns();
    writeInt(columns.size());
    for (DataColumn<?> col : columns) {
      Property<?> property = col.getProperty();
      writeObject(property.getUri());
      // writeType(SerializationType.getType(property.type())); //FIXME the property contains the
      // type
    }

  }

  @SuppressWarnings("unchecked")
  private void writeRowIndices() throws IOException {
    Long rowIdxStartingPosition = currentPosition;
    writeInt(rowIndices.size());
    for (Long idx : rowIndices) {
      writeLong(idx);
    }
    // Write the long directly because it's fix and always exists
    writeLong(rowIdxStartingPosition);
  }

  @SuppressWarnings("unchecked")
  private <T> void writeObject(T value) throws IOException {
    if (value == null) {
      writeNullValue();
      return;
    }
    Class<? extends Object> typeClass = value.getClass();
    byte[] bytes;
    if (typeClass.isEnum()) {
      SerializationType<String> type =
          (SerializationType<String>) SerializationType.getType(String.class);
      bytes = type.getSerializer().apply(value.toString());
      writeType(type);
      writeInt(bytes.length);
      writeBytes(bytes);
      return;
    }
    SerializationType<T> type = (SerializationType<T>) SerializationType.getType(typeClass);
    if (type == SerializationType.OTHER) {
      // type will always be set or OTHER
      BinaryData data = objectApi.definition(typeClass).serialize(value);
      writeType(type);
      Long length = data.length();
      writeInt(length.intValue());
      writeByteStream(data.inputStream(), length.intValue());
      return;
    }
    bytes = type.getSerializer().apply(value);
    writeType(type);
    writeInt(bytes.length);
    writeBytes(bytes);
  }

  private void writeInt(int i) throws IOException {
    writeBytes(Ints.toByteArray(i));
  }

  private void writeLong(long l) throws IOException {
    writeBytes(Longs.toByteArray(l));
  }

  @SuppressWarnings("rawtypes")
  private void writeType(SerializationType type) throws IOException {
    writeByte(type.getValue());
  }

  private void writeNullValue() throws IOException {
    writeType(NULL_VALUE);
  }

  private void writeByte(int b) throws IOException {
    this.currentPosition += 1;
    this.os.write(b);
  }

  private void writeBytes(byte[] b) throws IOException {
    this.currentPosition += b.length;
    this.os.write(b);
  }

  private void writeByteStream(InputStream is, int length) throws IOException {
    this.currentPosition += length;
    ByteStreams.copy(is, this.os);
  }

}
