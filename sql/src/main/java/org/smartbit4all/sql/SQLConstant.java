package org.smartbit4all.sql;

import org.smartbit4all.core.utility.StringConstant;

/**
 * This contains the constant values for the SQL statements. This constant list contains the ANSI
 * SQL standards. If we support specific behavior of a database then the specific constants must be
 * placed at the specific module.
 * 
 * @author Peter Boros
 *
 */
public final class SQLConstant {

  /**
   * The and in the statement.
   */
  public static final String AND = " AND ";

  /**
   * The ascending in case of the sort.
   */
  public static final String ASC = "ASC";

  /**
   * The SQL function.
   */
  public static final String AVG = "AVG";

  /**
   * The between for the statement.
   */
  public static final String BETWEEN = "BETWEEN";

  /**
   * The bind marker for the statement.
   */
  public static final String BINDMARK = "?";

  /**
   * The count aggregate function as a column.
   */
  public static final String COUNT = "COUNT(1)";

  /**
   * The start of the delete statement.
   */
  public static final String DELETEFROM = "DELETE FROM";

  /**
   * The descending in case of the sort.
   */
  public static final String DESC = "DESC";

  /**
   * The equal in the statements.
   */
  public static final String EQUAL = StringConstant.EQUAL;

  /**
   * The FALSE constant.
   */
  public static final String FALSE = "FALSE";

  /**
   * Fetch only token for DB2 to avoid locking tables or table segments.
   */
  public static final String FOR_FETCH_ONLY = "FOR FETCH ONLY";

  /**
   * SQLMX constant.
   */
  public static final String FOR_SKIP_CONFLICT_ACCESS = "FOR SKIP CONFLICT ACCESS";

  /**
   * For update token for database lock.
   */
  public static final String FOR_UPDATE = "FOR UPDATE";

  /**
   * For update token for database lock MS SQLServer with no wait option.
   */
  public static final String FOR_UPDATE_NOWAIT_SQLSERVER = "WITH(XLOCK, ROWLOCK, NOWAIT)";

  /**
   * The of token for start naming of the columns to get the lock for.
   */
  public static final String FOR_UPDATE_OF = "OF";

  /**
   * For update token for SQLMX database lock.
   */
  public static final String FOR_UPDATE_SQLMX = "FOR SERIALIZABLE ACCESS IN EXCLUSIVE MODE";

  /**
   * For update token for database lock MS SQLServer.
   */
  public static final String FOR_UPDATE_SQLSERVER = "WITH(XLOCK, ROWLOCK)";

  /**
   * The from in the select statement.
   */
  public static final String FROM = "FROM";

  /**
   * The group by part of the statement.
   */
  public static final String GROUPBY = "GROUP BY";

  /**
   * The IN operator.
   */
  public static final String IN = "IN";

  /**
   * The start of the insert statement.
   */
  public static final String INSERTINTO = "INSERT INTO";

  /**
   * The IS NOT NULL operator.
   */
  public static final String ISNOTNULL = "IS NOT NULL";

  /**
   * The IS NULL operator.
   */
  public static final String ISNULL = "IS NULL";

  /**
   * The inner join section.
   */
  public static final String JOIN = "JOIN";

  /**
   * The left join section.
   */
  public static final String LEFTJOIN = "LEFT JOIN";

  /**
   * The PostgreSQL row limit statement
   */
  public static final String LIMIT = "LIMIT";

  /**
   * The postfix.
   */
  public static final String LOCK_TIMEOUT_POSTFIX_POSTGRESQL = "s'";

  /**
   * Lock timeout for PostgreSQL
   */
  public static final String LOCK_TIMEOUT_POSTGRESQL = "SET LOCAL lock_timeout = '";

  /**
   * Lock timeout for MS SQLServer
   */
  public static final String LOCK_TIMEOUT_SQLSERVER = "SET LOCK_TIMEOUT";

  /**
   * The SQL function.
   */
  public static final String MAX = "MAX";

  /**
   * The SQL function.
   */
  public static final String MIN = "MIN";

  /**
   * The not part of the statement.
   */
  public static final String NOT = "NOT";

  /**
   * The nowait token to force immediate catch or throw SQL exception.
   */
  public static final String NOWAIT = "NOWAIT";

  /**
   * The nulls first order by section.
   */
  public static final String NULLSFIRST = "NULLS FIRST";

  /**
   * The ON in the join section.
   */
  public static final String ON = "ON";

  /**
   * The or in the statement.
   */
  public static final String OR = " OR ";

  /**
   * The order by part of the statement.
   */
  public static final String ORDERBY = "ORDER BY";

  /**
   * The oracle name of the row number implicit column. --> ORA module
   */
  public static final String ROWNUM_ORA = "ROWNUM";

  /**
   * Separates the SQL statements segments.
   */
  public static final String SEGMENTSEPARATOR = StringConstant.SPACE;

  /**
   * The start of the select statement.
   */
  public static final String SELECT = "SELECT";

  /**
   * The set tag in the update.
   */
  public static final String SETCLAUSE = "SET";

  /**
   * The set tag in the update.
   */
  public static final String SETSEPARATOR = StringConstant.COMMA_SPACE;

  /**
   * The SQL function.
   */
  public static final String SUM = "SUM";

  /**
   * The MS SQL Server row limit statement
   */
  public static final String TOP = "TOP";

  /**
   * The TRUE constant.
   */
  public static final String TRUE = "TRUE";

  /**
   * The uninitialized thing will appear in the rendered SQL with this literal.
   */
  public static final String UNINITIALIZED = "N/A";

  /**
   * The start of the update statement.
   */
  public static final String UPDATE = "UPDATE";

  /**
   * In the insert statements the values tag.
   */
  public static final String VALUES = "VALUES";

  /**
   * The wait for the timeout.
   */
  public static final String WAIT = "WAIT";

  /**
   * The where part of the statement.
   */
  public static final String WHERE = "WHERE";

  /**
   * The MS SQL Server row limit modifier to ensure ordered {@link #TOP}.
   */
  public static final String WITH_TIES = "WITH TIES";


  /**
   * The for update timeout into HundredthsOfSecond for PostgreSQL.
   * 
   * @param timeoutInMillis
   * @return
   */
  public static final int convertWaitTimeout2HundredthsOfSecond(long timeoutInMillis) {
    return ((int) (timeoutInMillis / 10)) + 1;
  }

  /**
   * The for update timeout into seconds. For oracle.
   * 
   * @param timeoutInMillis
   * @return
   */
  public static final int convertWaitTimeout2Second(long timeoutInMillis) {
    return ((int) (timeoutInMillis / 1000)) + 1;
  }

  /**
   * To avoid instantiation.
   */
  private SQLConstant() {
    super();
  }

}
