package org.smartbit4all.domain.meta.jdbc;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.smartbit4all.domain.meta.JDBCDataConverter;
import org.smartbit4all.types.binarydata.BinaryData;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class JDBCDataConverterHelper implements ApplicationContextAware {

  private ApplicationContext ctx;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.ctx = applicationContext;
  }

  @SuppressWarnings("unchecked")
  public <S> JDBCDataConverter<S, ?> from(Class<S> fromClass) {
    // The basic types are collected here to help dynamic getting for the type handlers.
    if (fromClass.isAssignableFrom(BigDecimal.class)) {
      return (JDBCDataConverter<S, ?>) ctx.getBean(JDBCBigDecimal.class);
    }
    if (fromClass.isAssignableFrom(Double.class)) {
      return (JDBCDataConverter<S, ?>) ctx.getBean(JDBCDouble.class);
    }
    if (fromClass.isAssignableFrom(Long.class)) {
      return (JDBCDataConverter<S, ?>) ctx.getBean(JDBCLong.class);
    }
    if (fromClass.isAssignableFrom(byte[].class)) {
      return (JDBCDataConverter<S, ?>) ctx.getBean(JDBCByteArray2BinaryData.class);
    }
    if (fromClass.isAssignableFrom(BinaryData.class)) {
      return (JDBCDataConverter<S, ?>) ctx.getBean(JDBCBinaryData.class);
    }
    if (fromClass.isAssignableFrom(java.util.Date.class)) {
      return (JDBCDataConverter<S, ?>) ctx.getBean(JDBCUtilDateSqlDate.class);
    }
    if (fromClass.isAssignableFrom(Time.class)) {
      return (JDBCDataConverter<S, ?>) ctx.getBean(JDBCSqlTime.class);
    }
    if (fromClass.isAssignableFrom(Timestamp.class)) {
      return (JDBCDataConverter<S, ?>) ctx.getBean(JDBCSqlTimestamp.class);
    }
    if (fromClass.isAssignableFrom(Boolean.class)) {
      return (JDBCDataConverter<S, ?>) ctx.getBean(JDBCBooleanString.class);
    }
    if (fromClass.isAssignableFrom(LocalDateTime.class)) {
      return (JDBCDataConverter<S, ?>) ctx.getBean(JDBCLocalDateTimeSqlDate.class);
    }
    if (fromClass.isAssignableFrom(LocalDate.class)) {
      return (JDBCDataConverter<S, ?>) ctx.getBean(JDBCLocalDateSqlDate.class);
    }
    if (fromClass.isAssignableFrom(LocalTime.class)) {
      return (JDBCDataConverter<S, ?>) ctx.getBean(JDBCLocalTimeSqlDate.class);
    }

    // In any other cases we believe that this must be String based.
    return (JDBCDataConverter<S, ?>) ctx.getBean(JDBCString.class);
  }

}
