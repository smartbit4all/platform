package org.smartbit4all.domain.transfer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.domain.config.DomainConfig;
import org.smartbit4all.domain.security.SecurityEntityConfiguration;
import org.smartbit4all.domain.service.transfer.TransferService;
import org.smartbit4all.domain.service.transfer.convert.Converter;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class TransferServiceTest {

  protected static AnnotationConfigApplicationContext ctx;

  @BeforeAll
  static void setup() {
    ctx = new AnnotationConfigApplicationContext();
    ctx.register(DomainConfig.class);
    ctx.register(SecurityEntityConfiguration.class);
    ctx.refresh();
  }

  @AfterAll
  static void tearDown() {
    ctx.close();
  }

  @Test
  void long2String() {
    TransferService transferService = ctx.getBean(TransferService.class);
    Converter<Long, String> convertTo = transferService.converterByType(Long.class, String.class);
    Converter<String, Long> convertFrom = transferService.converterByType(String.class, Long.class);
    Long testValue = 2021l;
    Assertions.assertEquals(testValue, convertFrom.convertTo(convertTo.convertTo(testValue)));
  }

  @Test
  void localDateTime2XMLGregorianCalendar() {
    TransferService transferService = ctx.getBean(TransferService.class);
    Converter<LocalDateTime, XMLGregorianCalendar> convertTo =
        transferService.converterByType(LocalDateTime.class, XMLGregorianCalendar.class);
    Converter<XMLGregorianCalendar, LocalDateTime> convertFrom =
        transferService.converterByType(XMLGregorianCalendar.class, LocalDateTime.class);
    LocalDateTime testValue = LocalDateTime.now();
    Assertions.assertEquals(testValue, convertFrom.convertTo(convertTo.convertTo(testValue)));
  }

  @Test
  void localDate2XMLGregorianCalendar() {
    TransferService transferService = ctx.getBean(TransferService.class);
    Converter<LocalDate, XMLGregorianCalendar> convertTo =
        transferService.converterByType(LocalDate.class, XMLGregorianCalendar.class);
    Converter<XMLGregorianCalendar, LocalDate> convertFrom =
        transferService.converterByType(XMLGregorianCalendar.class, LocalDate.class);
    LocalDate testValue = LocalDate.now();
    Assertions.assertEquals(testValue, convertFrom.convertTo(convertTo.convertTo(testValue)));
  }

  @Test
  void localDateTime2OffsetDateTime() {
    TransferService transferService = ctx.getBean(TransferService.class);
    Converter<LocalDateTime, OffsetDateTime> convertTo =
        transferService.converterByType(LocalDateTime.class, OffsetDateTime.class);
    Converter<OffsetDateTime, LocalDateTime> convertFrom =
        transferService.converterByType(OffsetDateTime.class, LocalDateTime.class);
    LocalDateTime testValue = LocalDateTime.now();
    Assertions.assertEquals(testValue, convertFrom.convertTo(convertTo.convertTo(testValue)));
  }

}
