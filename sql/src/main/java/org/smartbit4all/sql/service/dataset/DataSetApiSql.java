package org.smartbit4all.sql.service.dataset;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.jdbc.JDBCDataConverterHelper;
import org.smartbit4all.domain.service.dataset.DataSetApi;
import org.smartbit4all.domain.service.dataset.DataSetEntry;
import org.smartbit4all.sql.config.SQLConfig;
import org.smartbit4all.sql.config.SQLDBParameter;
import org.smartbit4all.sql.config.SQLDBParameterBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
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

  @Autowired
  private JDBCDataConverterHelper helper;

  /**
   * TODO More stable solution for generating unique identifier.
   */
  private Random rnd = new Random();

  private static final Logger log = LoggerFactory.getLogger(DataSetApiSql.class);

  @Override
  public DataSetEntry activate(Property<?> property, Set<?> values) {
    // TODO Not too safe but there is no need to use ID.
    if (values == null || values.isEmpty()) {
      // We return the data set but it's empty.
      return null;
    }
    String tableName;
    SQLDBParameter db = sqlConfig.db(SQLDBParameterBase.DEFAULT);
    if (property.type().isAssignableFrom(String.class)) {
      tableName = db.getStringDataSetTableName();
    } else if (property.type().isAssignableFrom(Long.class)
        || property.type().isAssignableFrom(Integer.class)) {
      tableName = db.getIntegerDataSetTableName();
    } else {
      throw new IllegalArgumentException("Unable to activate temporary data set in the " + db
          + ". The platform doesn't support the  " + property + " data type.");
    }
    Long id = (long) rnd.nextInt(Integer.MAX_VALUE);
    // TODO Correct URI!
    DataSetEntry result = new DataSetEntry(property.getUri(), tableName, id);
    String sql = "INSERT INTO " + tableName + " (ID, VAL) VALUES (?, ?)";
    try {
      executeInsert(values, id, sql);
    } catch (DataAccessException e) {
      // Could happen because of the missing table. We try to create the table and execute again.
      if (tableName.contains(SQLDBParameterBase.SB4DSINT)) {
        db.createIntegerDataSetTable(jdbcTemplate);
      } else {
        db.createStringDataSetTable(jdbcTemplate);
      }
      try {
        executeInsert(values, id, sql);
      } catch (Exception e1) {
        log.warn("Unable to insert temporary data set. " + property, e1);
        return null;
      }
    }
    result.setIdConverter(helper.from(Long.class));
    return result;
  }

  private final void executeInsert(Set<?> values, Long id, String sql) {
    Iterator<?> iterValues = values.iterator();
    jdbcTemplate.batchUpdate(sql,
        new BatchPreparedStatementSetter() {

          @Override
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            ps.setLong(1, id);
            Object oValue = iterValues.next();
            if (oValue instanceof Long) {
              ps.setLong(2, (Long) oValue);
            } else if (oValue instanceof Integer) {
              ps.setInt(2, (Integer) oValue);
            } else if (oValue instanceof String) {
              ps.setString(2, (String) oValue);
            } else if (oValue != null) {
              throw new IllegalArgumentException(
                  "Unable to bind the " + oValue + " (" + sql
                      + ") while insertint into the tempoarary table.");
            }
          }

          @Override
          public int getBatchSize() {
            return values.size();
          }
        });
  }

  @Override
  public int getLimit() {
    SQLDBParameter db = sqlConfig.db(SQLDBParameterBase.DEFAULT);
    return db.saveInDataSetLimit();
  }

}
