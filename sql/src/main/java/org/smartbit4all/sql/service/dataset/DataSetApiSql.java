package org.smartbit4all.sql.service.dataset;

import java.util.Random;
import java.util.Set;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.service.dataset.DataSetApi;
import org.smartbit4all.domain.service.dataset.DataSetEntry;
import org.smartbit4all.sql.config.SQLConfig;
import org.smartbit4all.sql.config.SQLDBParameter;
import org.smartbit4all.sql.config.SQLDBParameterBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * SQL implementation of the {@link DataSetApi}. It saves the data set into a temporary SQL table.
 * The result set is identified by a unique identifier that can be used for the SQL statement as
 * well.
 * 
 * @author Peter Boros
 */
public class DataSetApiSql implements DataSetApi {

  @Autowired
  JdbcTemplate jdbcTemplate;

  @Autowired
  private SQLConfig sqlConfig;

  /**
   * TODO More stable solution for generating unique identifier.
   */
  private Random rnd = new Random();

  @Override
  public DataSetEntry activate(Property<?> property, Set<?> values) {
    if (property.type().isAssignableFrom(String.class)) {

    } else if (property.type().isAssignableFrom(Long.class)) {

    }
    SQLDBParameter db = sqlConfig.db(SQLDBParameterBase.DEFAULT);
    Long id = rnd.nextLong();
    DataSetEntry result = new DataSetEntry(property.getUri(), id);
    // jdbcTemplate.batchUpdate("INSERT INTO " + sqlConfig., pss)
    return result;
  }

}
