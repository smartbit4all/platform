package org.smartbit4all.domain.service.dataset;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.TableData;

/**
 * 
 * TODO An in memory quick implementation to be enhance later on.
 * 
 * @author Peter Boros
 *
 */
public class TableDataApiImpl implements TableDataApi {

  private Map<URI, TableData<?>> tableDatas = new HashMap<>();

  @Override
  public URI save(TableData<?> tableData) {
    LocalDateTime now = LocalDateTime.now();
    UUID uuid = UUID.randomUUID();
    URI uri = URI.create("tabledata" + StringConstant.COLON + StringConstant.SLASH
        + now.getYear() + StringConstant.SLASH + now.getMonthValue() + StringConstant.SLASH
        + now.getDayOfMonth() + StringConstant.SLASH + now.getHour() + StringConstant.SLASH
        + now.getMinute() + StringConstant.SLASH
        + uuid);
    tableDatas.put(uri, tableData);
    return uri;
  }

  @Override
  public TableData<?> read(URI uri) {
    return tableDatas.get(uri);
  }

  @Override
  public void delete(URI uri) {
    tableDatas.remove(uri);
  }

}
