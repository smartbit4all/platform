package org.smartbit4all.domain.data.storage.index;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.TableDatas;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.Property;

/**
 * StorageIndex can be used for indexing objects based on entity definition, properties.
 * The main purpose of the StorageIndex for storing the StorageIndexFields, the indexer.
 * StorageIndex can update the indexes of the object, and list the object URIs for an expression.
 * 
 * @author Zoltan Szegedi
 *
 * @param <T> Type of the indexed object.
 */
public class StorageIndex<T> {

  private EntityDefinition entityDef;
  
  private Property<URI> key;
  
  private String keyName;
  
  private List<StorageIndexField<T, ?>> fields;

  private Function<T, URI> objectUriProvider;
 
  private StorageIndexer<T> storageIndexer;
  
  public StorageIndex(
      EntityDefinition entityDef,
      Property<URI> key,
      StorageIndexer<T> storageIndexer,
      List<StorageIndexField<T, ?>> fields,
      Function<T, URI> objectUriProvider) {
    
    this(entityDef, storageIndexer, fields, objectUriProvider);
    this.key = key;
  }

  public StorageIndex(
      EntityDefinition entityDef,
      String keyName,
      StorageIndexer<T> storageIndexer,
      List<StorageIndexField<T, ?>> fields,
      Function<T, URI> objectUriProvider) {
    
    this(entityDef, storageIndexer, fields, objectUriProvider);
    this.keyName = keyName;
  }
  
  private StorageIndex(
      EntityDefinition entityDef,
      StorageIndexer<T> storageIndexer,
      List<StorageIndexField<T, ?>> fields,
      Function<T, URI> objectUriProvider) {
    
    this.entityDef = entityDef;
    this.storageIndexer = storageIndexer;
    this.objectUriProvider = objectUriProvider;

    this.fields = new ArrayList<>();
    this.fields.addAll(fields);
  }

  /**
   * List the object URIs, evaluating the given expression.
   * Expression must only use the entity definition defined in construct time.
   * 
   * @param expression
   * @return
   * @throws Exception
   */
  public List<URI> listUris(Expression expression) throws Exception {
    return storageIndexer.listUris(this, expression);
  }

  /**
   * Update the index stored values.
   * 
   * @param object
   * @throws Exception
   */
  public void updateIndex(T object) throws Exception {
    storageIndexer.updateIndex(object, this);
  }
  
  public Function<T, URI> getObjectUriProvider() {
    return objectUriProvider;
  }

  public List<StorageIndexField<T, ?>> getFields() {
    return fields;
  }

  public EntityDefinition getEntityDef() {
    return entityDef;
  }

  public String getName() {
    return entityDef.entityDefName();
  }
  
  public List<Property<?>> getProperties() {
    List<Property<?>> properties = new ArrayList<>();
    
    properties.add(getKey());
    
    for (StorageIndexField<T, ?> field : getFields()) {
      properties.add(field.getValueField());
    }
    
    return properties;
  }
  
  public TableData<? extends EntityDefinition> createTableDataWithColumns() {
    List<Property<?>> properties = getProperties();
    return TableDatas.builder(entityDef, properties).build();
  }
  
  @SuppressWarnings("unchecked")
  public Property<URI> getKey() {
     if(key != null) {
       return key;
     } else {
       return (Property<URI>) entityDef.getProperty(keyName);
     }
  }
 
}
