package org.smartbit4all.api.collection;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.PropertyMeta;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.EntityDefinitionBuilder;
import org.smartbit4all.domain.service.CrudApi;
import org.smartbit4all.domain.service.dataset.TableDataApi;
import org.smartbit4all.domain.service.query.QueryInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class SearchIndexWithFilterBean<F, O> extends SearchIndexImpl {

  private ObjectDefinition<F> filterDefinition;

  /**
   * {@link LinkedHashMap} to preserve the parameterization order in the entity definition.
   */
  private Map<String, String[]> pathByPropertyName = new LinkedHashMap<>();

  private String indexedObjectSchema;

  private ObjectDefinition<O> indexedObjectDefinition;

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private StorageApi storageApi;

  @Autowired
  private CrudApi crudApi;

  @Autowired
  private TableDataApi tableDataApi;

  @Autowired
  private ApplicationContext ctx;

  private Class<F> filterDefinitionClass;

  private Class<O> indexedObjectDefinitionClass;

  public SearchIndexWithFilterBean(String logicalSchema, String name,
      Class<F> filterDefinitionClass, String indexedObjectSchema,
      Class<O> indexedObjectDefinitionClass) {
    super(logicalSchema, name);
    this.filterDefinitionClass = filterDefinitionClass;
    this.indexedObjectSchema = indexedObjectSchema;
    this.indexedObjectDefinitionClass = indexedObjectDefinitionClass;
  }

  public SearchIndexWithFilterBean<F, O> map(String propertyName, String... pathes) {
    Objects.requireNonNull(pathes);
    if (pathes.length > 0) {
      pathByPropertyName.put(propertyName, pathes);
    }
    return this;
  }

  @Override
  public TableData<?> executeSearch(QueryInput queryInput) {
    queryInput.setTableDataUri(tableDataApi.save(readAllObjects()));
    return crudApi.executeQuery(queryInput).getTableData();
  }

  private final TableData<?> readAllObjects() {
    EntityDefinition def = getDefinition();
    TableData<?> tableData = new TableData<>(def);
    tableData.addColumns(def.allProperties());

    Storage storage = storageApi.get(indexedObjectSchema);
    List<URI> allObjectUris = storage.readAllUris(indexedObjectDefinition().getClazz());
    allObjectUris.stream().map(u -> objectApi.load(u))
        .forEach(n -> {
          DataRow row = tableData.addRow();
          for (DataColumn<?> col : tableData.columns()) {
            String[] path = pathByPropertyName.get(col.getProperty().getName());
            if (path != null) {
              tableData.setObject(col, row, n.getValue(path));
            }
          }
        });

    return tableData;
  }

  @Override
  public EntityDefinition constructDefinition() {
    EntityDefinitionBuilder builder = EntityDefinitionBuilder.of(ctx)
        .name(name())
        .domain(logicalSchema());

    Map<String, PropertyMeta> properties = filterDefinition().meta().getProperties();

    for (String propertyName : pathByPropertyName.keySet()) {
      PropertyMeta propertyMeta = properties.get(propertyName.toUpperCase());
      if (propertyMeta != null) {
        builder.ownedProperty(propertyName, propertyMeta.getType());
      }
    }

    return builder.build();
  }

  private ObjectDefinition<F> filterDefinition() {
    if (filterDefinition == null) {
      filterDefinition = objectApi.definition(filterDefinitionClass);
    }
    return filterDefinition;
  }

  private ObjectDefinition<O> indexedObjectDefinition() {
    if (indexedObjectDefinition == null) {
      indexedObjectDefinition = objectApi.definition(indexedObjectDefinitionClass);
    }
    return indexedObjectDefinition;
  }

}
