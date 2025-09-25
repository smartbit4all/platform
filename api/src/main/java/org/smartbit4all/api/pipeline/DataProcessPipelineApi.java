package org.smartbit4all.api.pipeline;

import java.util.List;
import org.smartbit4all.api.collection.bean.SearchIndexDefinitionData;
import org.smartbit4all.api.storage.bean.DataSeries;
import org.smartbit4all.api.storage.bean.DataSeriesInterval;
import org.smartbit4all.domain.data.TableData;

/**
 * For retreiveing and storing the source and result of the data processing functionalities. Can be
 * used to organize a stream api like data processing pipeline.
 */
public interface DataProcessPipelineApi {

  DataSeries retrieveObjectStorageFragment(String schema, String className,
      DataSeriesInterval interval);

  TableData<?> tableDataOfDataSeries(List<DataSeries> dataSeries, SearchIndexDefinitionData searchIndex);

}
