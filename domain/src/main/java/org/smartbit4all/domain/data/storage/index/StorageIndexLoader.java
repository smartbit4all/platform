package org.smartbit4all.domain.data.storage.index;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.index.StorageLoader;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;

/**
 * StorageIndexLoader can be used for Storages, which use platform evaluation for loading objects
 * with expressions.
 * 
 * Content storage is used to calculate and fill the index values at some point of the evaluation.
 * Indexes are used for getting the calculators, which provides the values of the index fields by
 * running the calculators on the object.
 * 
 * @author Zoltan Szegedi
 *
 * @param <T> Type of the stored object.
 */
public class StorageIndexLoader<T> extends StorageLoader {

  private Storage storage;

  private Property<URI> contentKey;

  private String contentKeyName;

  /**
   * Index fields by properties, filled from the given indexes. Indexes added at constructor, but
   * can be added later.
   */
  private Map<Property<?>, StorageIndexField<T, ?>> indexesByProperties;

  public StorageIndexLoader(
      EntityDefinition entityDef,
      List<StorageIndexField<T, ?>> indexes,
      Storage storage,
      Property<URI> contentKey) {

    this(entityDef, indexes, storage);
    this.contentKey = contentKey;
  }

  public StorageIndexLoader(
      EntityDefinition entityDef,
      List<StorageIndexField<T, ?>> indexes,
      Storage storage,
      String contentKeyName) {

    this(entityDef, indexes, storage);
    this.contentKeyName = contentKeyName;
  }

  private StorageIndexLoader(
      EntityDefinition entityDef,
      List<StorageIndexField<T, ?>> indexes,
      Storage storage) {

    super(entityDef);

    this.storage = storage;
    this.indexesByProperties = new HashMap<>();
    for (StorageIndexField<T, ?> index : indexes) {
      addIndex(index);
    }
  }

  public void addIndex(StorageIndexField<T, ?> index) {
    getIndices().add(index);
    this.indexesByProperties.put(index.getValueField(), index);
  }

  @Override
  protected void fillRow(TableData<?> tableData, DataRow rowToFill) {
    URI uri = rowToFill.get(getKeyField());

    @SuppressWarnings("unchecked")
    StorageObject<T> loadedData = (StorageObject<T>) load(uri);
    if (loadedData.isPresent()) {
      fillRow(tableData, rowToFill, loadedData.getObject());
    } else {
      throw new IllegalArgumentException("Object with key: " + uri + " does not exists");
    }
  }

  @Override
  public void loadAllRows(TableData<?> tableData) {
    // TODO Auto-generated method stub
  }

  private void fillRow(TableData<?> tableData, DataRow rowToFill, T data) {
    DataRow row = getRow(tableData, rowToFill);

    for (DataColumn<?> column : tableData.columns()) {
      Property<?> property = column.getProperty();

      StorageIndexField<T, ?> storageIndexFS = indexesByProperties.get(property);

      if (storageIndexFS != null) {
        Optional<?> columnValue = storageIndexFS.getCalculator().apply(data);
        tableData.setObject(column, row, columnValue.orElse(null));
      }
    }
  }

  private StorageObject<?> load(URI uri) {

    return storage.load(uri);
  }

  private DataRow getRow(TableData<?> tableData, DataRow dataRow) {
    for (DataRow actualRow : tableData.rows()) {
      if (dataRow == actualRow) {
        return actualRow;
      }
    }
    return tableData.addRow();
  }

  @SuppressWarnings("unchecked")
  public Property<URI> getKeyField() {
    if (contentKey != null) {
      return contentKey;
    } else {
      return (Property<URI>) getEntityDef().getProperty(contentKeyName);
    }
  }

}
