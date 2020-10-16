package org.smartbit4all.domain.meta.jdbc;

import org.smartbit4all.core.SB4Configuration;
import org.smartbit4all.domain.meta.jdbc.impl.JDBCBigDecimalImpl;
import org.smartbit4all.domain.meta.jdbc.impl.JDBCBinaryDataImpl;
import org.smartbit4all.domain.meta.jdbc.impl.JDBCBooleanStringImpl;
import org.smartbit4all.domain.meta.jdbc.impl.JDBCByteArray2BinaryDataImpl;
import org.smartbit4all.domain.meta.jdbc.impl.JDBCDoubleImpl;
import org.smartbit4all.domain.meta.jdbc.impl.JDBCIntegerLongImpl;
import org.smartbit4all.domain.meta.jdbc.impl.JDBCLocalDateSqlDateImpl;
import org.smartbit4all.domain.meta.jdbc.impl.JDBCLocalDateTimeSqlDateImpl;
import org.smartbit4all.domain.meta.jdbc.impl.JDBCLocalTimeSqlDateImpl;
import org.smartbit4all.domain.meta.jdbc.impl.JDBCLongImpl;
import org.smartbit4all.domain.meta.jdbc.impl.JDBCSqlTimeImpl;
import org.smartbit4all.domain.meta.jdbc.impl.JDBCSqlTimestampImpl;
import org.smartbit4all.domain.meta.jdbc.impl.JDBCStringImpl;
import org.smartbit4all.domain.meta.jdbc.impl.JDBCUtilDateSqlDateImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JDBCDataConverterConfig extends SB4Configuration {

  @Bean
  public JDBCDataConverterHelper jdbcDataConverterHelper() {
    return new JDBCDataConverterHelper();
  }

  @Bean
  public JDBCBigDecimal jdbcBigDecimal() {
    return createProxy(JDBCBigDecimal.class, new JDBCBigDecimalImpl());
  }

  @Bean
  public JDBCByteArray2BinaryData jdbcByteArray() {
    return createProxy(JDBCByteArray2BinaryData.class, new JDBCByteArray2BinaryDataImpl());
  }

  @Bean
  public JDBCBinaryData jdbcBinaryData() {
    return createProxy(JDBCBinaryData.class, new JDBCBinaryDataImpl());
  }

  @Bean
  public JDBCUtilDateSqlDate jdbcUtilDateSqlDate() {
    return createProxy(JDBCUtilDateSqlDate.class, new JDBCUtilDateSqlDateImpl());
  }

  @Bean
  public JDBCDouble jdbcDouble() {
    return createProxy(JDBCDouble.class, new JDBCDoubleImpl());
  }

  @Bean
  public JDBCIntegerLong jdbcIntegerLong() {
    return createProxy(JDBCIntegerLong.class, new JDBCIntegerLongImpl());
  }

  @Bean
  public JDBCLong jdbcLong() {
    return createProxy(JDBCLong.class, new JDBCLongImpl());
  }

  @Bean
  public JDBCString jdbcString() {
    return createProxy(JDBCString.class, new JDBCStringImpl());
  }

  @Bean
  public JDBCSqlTime jdbcSqlTime() {
    return createProxy(JDBCSqlTime.class, new JDBCSqlTimeImpl());
  }

  @Bean
  public JDBCSqlTimestamp jdbcSqlTimestamp() {
    return createProxy(JDBCSqlTimestamp.class, new JDBCSqlTimestampImpl());
  }

  @Bean
  public JDBCBooleanString jdbcBooleanString() {
    return createProxy(JDBCBooleanString.class, new JDBCBooleanStringImpl());
  }

  @Bean
  public JDBCLocalDateSqlDate jdbcLocalDateSqlDate() {
    return createProxy(JDBCLocalDateSqlDate.class, new JDBCLocalDateSqlDateImpl());
  }

  @Bean
  public JDBCLocalTimeSqlDate jdbcLocalTimeSqlDate() {
    return createProxy(JDBCLocalTimeSqlDate.class, new JDBCLocalTimeSqlDateImpl());
  }

  @Bean
  public JDBCLocalDateTimeSqlDate jdbcLocalDateTimeSqlDate() {
    return createProxy(JDBCLocalDateTimeSqlDate.class, new JDBCLocalDateTimeSqlDateImpl());
  }

}
