package org.smartbit4all.api.pipeline;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.collection.bean.SearchIndexDefinitionData;
import org.smartbit4all.api.storage.bean.DataSeries;
import org.smartbit4all.api.storage.bean.DataSeriesInterval;
import org.smartbit4all.api.storage.bean.DataSeriesItem;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.springframework.beans.factory.annotation.Autowired;

public class DataProcessPipelineApiImpl implements DataProcessPipelineApi {

  @Autowired
  private StorageApi storageApi;

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  private ObjectApi objectApi;

  @Override
  public DataSeries retrieveObjectStorageFragment(String schema, String className,
      DataSeriesInterval interval) {
    Storage storage = storageApi.get(schema);
    OffsetDateTime lowerDataTime = interval.getLower().getDataTimeValue();
    OffsetDateTime upperDataTime = interval.getUpper().getDataTimeValue();
    List<DataSeriesItem> items = storage
        .streamOfTimeSeries(null, className, lowerDataTime.toLocalDateTime(),
            upperDataTime.toLocalDateTime(), ChronoUnit.HOURS)
        .flatMap(l -> l.stream()).map(u -> new DataSeriesItem().uri(u)).toList();
    return new DataSeries().qualifiedName(className).interval(interval).items(items);
  }

  @Override
  public TableData<?> tableDataOfDataSeries(List<DataSeries> dataSeries,
      SearchIndexDefinitionData searchIndexDefinition) {
    SearchIndex<?> searchIndex =
        collectionApi.searchIndexComputeIfAbsent(searchIndexDefinition.getDescriptor().getSchema(),
            searchIndexDefinition.getDescriptor().getName(),
            () -> collectionApi.constructSearchIndex(searchIndexDefinition), null);
    return searchIndex.tableDataOfObjectNodes(dataSeries.stream().flatMap(d -> {
      ObjectDefinition<?> definition = objectApi.definition(d.getQualifiedName());
      return d.getItems().stream().map(i -> objectApi.create(null, definition, i.getObjectAsMap()));
    }));
  }

}
