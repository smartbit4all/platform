/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.domain.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.smartbit4all.core.SB4Configuration;
import org.smartbit4all.domain.application.TimeManagementService;
import org.smartbit4all.domain.application.TimeManagementServiceImpl;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageApiImpl;
import org.smartbit4all.domain.service.query.QueryApi;
import org.smartbit4all.domain.service.query.QueryApiImpl;
import org.smartbit4all.domain.service.transfer.TransferService;
import org.smartbit4all.domain.service.transfer.TransferServiceImpl;
import org.smartbit4all.domain.service.transfer.convert.Converter;
import org.smartbit4all.domain.service.transfer.convert.ConverterImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Must not be referred directly! Use SQL or remote.
 * 
 * @author Peter Boros
 */
@Configuration
public class DomainServiceConfig extends SB4Configuration {

  @Bean
  @Primary
  public TimeManagementService timeManagementService() {
    return new TimeManagementServiceImpl();
  }

  @Bean
  public TransferService transferService() {
    return new TransferServiceImpl();
  }

  @Bean
  public Converter<String, Long> string2LongConverter() {
    return new ConverterImpl<>(Long.class, Long::valueOf, String.class,
        (Long l) -> l.toString());
  }

  @Bean
  public Converter<Long, String> long2StringConverter() {
    return new ConverterImpl<>(String.class,
        (Long l) -> l.toString(), Long.class, (String s) -> s == null ? null : Long.valueOf(s));
  }

  @Bean
  public Converter<LocalDateTime, XMLGregorianCalendar> localDateTime2XMLGregorianCalendarConverter()
      throws DatatypeConfigurationException {
    // Assume that the DatatypeFactory is thread safe!
    // see:
    // https://stackoverflow.com/questions/7346508/datatypefactory-usage-in-creating-xmlgregoriancalendar-hits-performance-badly
    DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
    return new ConverterImpl<LocalDateTime, XMLGregorianCalendar>(XMLGregorianCalendar.class,
        (LocalDateTime d) -> datatypeFactory
            .newXMLGregorianCalendar(
                GregorianCalendar.from(ZonedDateTime.of(d, ZoneId.systemDefault()))),
        LocalDateTime.class,
        (XMLGregorianCalendar c) -> c.toGregorianCalendar().toZonedDateTime()
            .withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
  }

  @Bean
  public Converter<XMLGregorianCalendar, LocalDateTime> xMLGregorianCalendar2localDateTimeConverter()
      throws DatatypeConfigurationException {
    DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
    return new ConverterImpl<XMLGregorianCalendar, LocalDateTime>(LocalDateTime.class,
        (XMLGregorianCalendar c) -> c.toGregorianCalendar().toZonedDateTime()
            .withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime(),
        XMLGregorianCalendar.class,
        (LocalDateTime d) -> datatypeFactory
            .newXMLGregorianCalendar(
                GregorianCalendar.from(ZonedDateTime.of(d, ZoneId.systemDefault()))));
  }

  @Bean
  public Converter<LocalDate, XMLGregorianCalendar> localDate2XMLGregorianCalendarConverter()
      throws DatatypeConfigurationException {
    // Assume that the DatatypeFactory is thread safe!
    // see:
    // https://stackoverflow.com/questions/7346508/datatypefactory-usage-in-creating-xmlgregoriancalendar-hits-performance-badly
    DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
    return new ConverterImpl<LocalDate, XMLGregorianCalendar>(XMLGregorianCalendar.class,
        (LocalDate d) -> datatypeFactory
            .newXMLGregorianCalendar(
                GregorianCalendar.from(d.atStartOfDay(ZoneId.systemDefault()))),
        LocalDate.class,
        (XMLGregorianCalendar c) -> c.toGregorianCalendar().toZonedDateTime().toLocalDate());
  }

  @Bean
  public Converter<XMLGregorianCalendar, LocalDate> xMLGregorianCalendar2LocalDateConverter()
      throws DatatypeConfigurationException {
    // Assume that the DatatypeFactory is thread safe!
    // see:
    // https://stackoverflow.com/questions/7346508/datatypefactory-usage-in-creating-xmlgregoriancalendar-hits-performance-badly
    DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
    return new ConverterImpl<XMLGregorianCalendar, LocalDate>(LocalDate.class,
        (XMLGregorianCalendar c) -> c.toGregorianCalendar().toZonedDateTime().toLocalDate(),
        XMLGregorianCalendar.class,
        (LocalDate d) -> datatypeFactory
            .newXMLGregorianCalendar(
                GregorianCalendar.from(d.atStartOfDay(ZoneId.systemDefault()))));
  }

  @Bean
  public Converter<LocalDateTime, OffsetDateTime> localDateTime2OffsetDateTimeConverter() {
    return new ConverterImpl<LocalDateTime, OffsetDateTime>(OffsetDateTime.class,
        (LocalDateTime d) -> ZonedDateTime.of(d, ZoneId.systemDefault())
            .toOffsetDateTime(),
        LocalDateTime.class,
        OffsetDateTime::toLocalDateTime);
  }

  @Bean
  public Converter<OffsetDateTime, LocalDateTime> offsetDateTime2localDateTimeConverter() {
    return new ConverterImpl<OffsetDateTime, LocalDateTime>(LocalDateTime.class,
        OffsetDateTime::toLocalDateTime, OffsetDateTime.class,
        (LocalDateTime d) -> ZonedDateTime.of(d, ZoneId.systemDefault())
            .toOffsetDateTime());
  }

  @Bean
  public QueryApi getQueryApi() {
    return new QueryApiImpl();
  }

  @Bean
  public StorageApi getStorageApi() {
    return new StorageApiImpl();
  }

}
