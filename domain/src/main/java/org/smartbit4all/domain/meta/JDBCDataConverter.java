package org.smartbit4all.domain.meta;

/**
 * The {@link JDBCDataConverter} is a specific {@link DataConverter} to fulfill the requirements of
 * the the JDBC type handling. A type handler is responsible for accessor and mutator operations of
 * a property. The data of the {@link Property} is stored in a type at application level and can be
 * stored in another type at database level. It could be as trivial as having String at both side.
 * But could differ in case of Date for example where we would like to use the LocalDate at
 * application level but we should bind a java.sql.Date at database level.
 * 
 * <p>
 * The implementations are named by the following syntax: JDBC + AppTypeName + JDBCTypeName and +
 * Impl in case of the implementation. The converter itself is also a service so it can be defined,
 * implemented and configured at every level of the application.
 * </p>
 * 
 * <p>
 * There is a lookup for the registered implementation where we figure out the implementation by the
 * type of the property. So if we have String at property level then we can get the default
 * JDBCString without any additional configuration. (All the by default working converters are
 * registered at application level.) If we have JDBCDate for example then we will have this
 * converter as default in case of java.util.Date but also LocalDate. The logic of this default
 * types are managed by the from static function.
 * 
 * @author Peter Boros
 *
 * @param <S> It is the application level data type.
 * @param <T> It might be the basic JDBC level type. At JDBC level this handler will convert between
 *        the stored object and the JDBC type.
 */
public interface JDBCDataConverter<S, T> extends DataConverter<S, T> {

  public static enum JDBCType {
    BIGDECIMAL, BINARYDATA, DATE, DOUBLE, LONG, STRING, TIME, TIMESTAMP
  }

  /**
   * Return the JDBC level type for the given type.
   * 
   * @return
   */
  int SQLType();

  /**
   * The supported JDBC type that must be used during the bind or during the fetch.
   * 
   * @return
   */
  JDBCType bindType();

}
