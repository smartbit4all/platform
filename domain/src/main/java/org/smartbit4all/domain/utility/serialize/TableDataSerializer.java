package org.smartbit4all.domain.utility.serialize;

import static org.smartbit4all.domain.utility.serialize.SerializationTokens.META;
import static org.smartbit4all.domain.utility.serialize.SerializationTokens.META_COLS;
import static org.smartbit4all.domain.utility.serialize.SerializationTokens.ROWIDX;
import static org.smartbit4all.domain.utility.serialize.SerializationTokens.ROWS;
import static org.smartbit4all.domain.utility.serialize.SerializationTypes.NULL_VALUE;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.Property;

public class TableDataSerializer {

  private OutputStream os;

  private List<Long> rowIndexes = new ArrayList<>();

  private long length = 0l;

  private File file;

  private TableData<?> meta;

  private TableDataSerializer(OutputStream os) {
    this.os = os;
  }

  private TableDataSerializer(File file) throws FileNotFoundException {
    this.file = file;
    this.os = new FileOutputStream(file);
  }

  public TableDataSerializer withMeta(TableData<?> tableData) throws IOException {
    this.meta = tableData;
    writeMeta(tableData);
    return this;
  }

  public TableDataSerializer of(TableData<?> tableData) throws IOException {
    withMeta(tableData);
    for (DataRow row : tableData.rows()) {
      addRow(row);
    }
    return this;
  }

  public TableDataSerializer addRow(DataRow row) throws IOException {
    if (meta == null) {
      throw new IllegalStateException("There is no meta TableData set!");
    }
    addRow();
    for (DataColumn<?> col : meta.columns()) {
      addValue(row.get(col.getProperty()));
    }
    return this;
  }

  public TableDataSerializer addRow() throws IOException {
    if (rowIndexes.isEmpty()) {
      writeToken(ROWS);
    }
    rowIndexes.add(Long.valueOf(length));
    return this;
  }

  public TableDataSerializer addValue(Object object) throws IOException {
    writeObject(object);
    return this;
  }

  public void finish() throws IOException {
    writeRowIndex();
    os.close();
    return;
  }

  public BinaryData finishWithBinaryData() throws IOException {
    finish();
    if (file == null) {
      throw new IllegalStateException("Can not finish with BinaryData when the output file was not"
          + "described. Use the finish() method instead and the OutputStream parameter directly.");
    }
    return new BinaryData(file);
  }

  public static TableDataSerializer create(OutputStream os) {
    return new TableDataSerializer(os);
  }

  public static TableDataSerializer create() throws IOException {
    Path path = Files.createTempFile("sb4", "TableDataSerialize");
    return create(path.toFile());
  }

  public static TableDataSerializer create(File file) throws FileNotFoundException {
    return new TableDataSerializer(file);
  }

  /**
   * Serializes the given {@link TableData} into a temporary file
   */
  public static BinaryData serialize(TableData<?> tableData) throws IOException {
    return create().of(tableData).finishWithBinaryData();
  }

  /**
   * Serializes the given {@link TableData} into the given file
   */
  public static BinaryData serialize(TableData<?> tableData, File file) throws IOException {
    return create(file).of(tableData).finishWithBinaryData();
  }

  /**
   * Writes the meta as: &ltMETA>&ltentityDef uri>&ltCOLS>&ltnumber of cols>&ltcol1 uri>&ltcol1
   * type>&ltcol2 uri>&ltcol2 type>...&ltcolN uri>&ltcolN type>
   */
  private void writeMeta(TableData<?> tableData) throws IOException {
    writeToken(META);
    writeObject(tableData.entity().getUri());

    writeToken(META_COLS);
    Collection<DataColumn<?>> columns = tableData.columns();
    writeLen(columns.size());
    for (DataColumn<?> col : columns) {
      Property<?> property = col.getProperty();
      writeObject(property.getUri());
      writeType(SerializationTypes.getType(property.type()));
    }

  }

  private void writeRowIndex() throws IOException {
    long rowIdxLen = length;
    writeToken(ROWIDX);
    writeLen(rowIndexes.size());
    for (Long idx : rowIndexes) {
      writeObject(idx);
    }
    writeObject(rowIdxLen);
  }


  private <T> void writeObject(T value) throws IOException {
    if (value == null) {
      writeNullValue();
      return;
    }
    SerializationTypes type = SerializationTypes.getType(value.getClass());
    ByteArrayConverter<T> converter = (ByteArrayConverter<T>) type.getConverter();
    byte[] bytes = converter.convert(value);
    writeType(type);
    writeLen(bytes.length);
    writeBytes(bytes);
  }

  private void writeLen(int len) throws IOException {
    byte byteValue = Integer.valueOf(len).byteValue();
    writeByte(byteValue);
  }

  private void writeToken(SerializationTokens token) throws IOException {
    writeByte(token.getValue());
  }

  private void writeType(SerializationTypes type) throws IOException {
    writeByte(type.getValue());
  }

  private void writeNullValue() throws IOException {
    writeType(NULL_VALUE);
  }

  private void writeByte(int b) throws IOException {
    this.length += 1;
    this.os.write(b);
  }

  private void writeBytes(byte[] b) throws IOException {
    this.length += b.length;
    this.os.write(b);
  }

  public static interface ByteArrayConverter<T> {
    byte[] convert(T value) throws IOException;
  }

}
