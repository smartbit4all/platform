package org.smartbit4all.domain.data.storage.index;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.index.StorageNonUniqueIndex;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.Property;

/**
 * StorageIndexField maps a property to a value calculator.
 * The value calculator can calculate the value of the property field.
 * StorageIndexField can retrieve the URIs for a value (of the index field).
 * 
 * @author Zoltan Szegedi
 *
 * @param <T> Type of the stored object.
 * @param <F> Type of the value field.
 */
public class StorageIndexField<T, F> extends StorageNonUniqueIndex<F, URI> {

  private EntityDefinition entityDef;
  
  private Property<URI> keyField;
  
  private Property<F> valueField;

  private String keyFieldName;
  
  private String valueFieldName;
  
  private StorageIndexer<T> indexApi;
  
  private Function<T, Optional<F>> calculator;

  public StorageIndexField(
      EntityDefinition entityDef,
      Property<URI> key,
      Property<F> value,
      Function<T, Optional<F>> calculator,
      StorageIndexer<T> indexApi) {

    this(entityDef, calculator, indexApi);
    
    this.keyField = key;
    this.valueField = value;
  }

  public StorageIndexField(
      EntityDefinition entityDef,
      String keyFieldName,
      String valueFieldName,
      Function<T, Optional<F>> calculator,
      StorageIndexer<T> indexApi) {

    this(entityDef, calculator, indexApi);
    
    this.keyFieldName = keyFieldName;
    this.valueFieldName = valueFieldName;
  }
  
  public StorageIndexField(
      EntityDefinition entityDef,
      Function<T, Optional<F>> calculator,
      StorageIndexer<T> indexApi) {

    this.entityDef = entityDef;
    this.calculator = calculator;
    this.indexApi = indexApi;
  }
  
  @Override
  public boolean canUseFor(Expression expression) {
    return indexApi.canUseFor(getValueField(), expression);
  }

  @Override
  public List<URI> get(F key) {
    try {

      return indexApi.listUris(getKeyField(), getValueField(), key);

    } catch (Exception e) {

      StringBuilder message = new StringBuilder()
          .append("Cannot list object URIs of field ")
          .append(getValueField().getEntityDef().entityDefName())
          .append("-")
          .append(getValueField().getName())
          .append(" with value: ")
          .append(key);

      throw new RuntimeException(message.toString(), e);

    }
  }

  public DataRow setRowValue(DataRow row, T object) {
    row.set(getValueField(), getCalculator().apply(object).orElse(null));
    return row;
  }
  
  public Function<T, Optional<F>> getCalculator() {
    return calculator;
  }

  public String getName() {
    return getValueField().getName();
  }
  
  public String getIndexName() {
    return entityDef.entityDefName();
  }

  @SuppressWarnings("unchecked")
  private Property<URI> getKeyField() {
    if(keyField != null) {
      return keyField;
    } else {
      return (Property<URI>) entityDef.getProperty(keyFieldName);
    }
  }
  
  @SuppressWarnings("unchecked")
  public Property<F> getValueField() {
    if(valueField != null) {
      return valueField;
    } else {
      return (Property<F>) entityDef.getProperty(valueFieldName);
    }
  }
  
}
