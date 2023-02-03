package org.smartbit4all.api.grid;

import java.util.List;
import java.util.Map;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.domain.data.TableData;

public interface GridApi {

  <T> GridModel modelOf(Class<T> clazz, List<T> o, Map<String, String> columns);

  GridModel modelOf(TableData<?> tableData);

}
