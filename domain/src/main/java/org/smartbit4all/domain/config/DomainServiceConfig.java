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

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.function.Supplier;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.smartbit4all.core.SB4Configuration;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.domain.application.TimeManagementService;
import org.smartbit4all.domain.application.TimeManagementServiceImpl;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageApiImpl;
import org.smartbit4all.domain.data.storage.history.ObjectHistoryApi;
import org.smartbit4all.domain.data.storage.history.ObjectHistoryApiImpl;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.service.entity.EntityManager;
import org.smartbit4all.domain.service.entity.EntityManagerImpl;
import org.smartbit4all.domain.service.modify.applychange.ApplyChangeObjectConfig;
import org.smartbit4all.domain.service.modify.applychange.ApplyChangeService;
import org.smartbit4all.domain.service.modify.applychange.ApplyChangeServiceImpl;
import org.smartbit4all.domain.service.query.Queries;
import org.smartbit4all.domain.service.query.QueryApiImpl;
import org.smartbit4all.domain.service.transfer.TransferService;
import org.smartbit4all.domain.service.transfer.TransferServiceImpl;
import org.smartbit4all.domain.service.transfer.convert.Converter;
import org.smartbit4all.domain.service.transfer.convert.ConverterImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

/**
 * Must not be referred directly! Use SQL or remote.
 * 
 * @author Peter Boros
 */
@Configuration
@Import({
    QueryApiImpl.class,
    Queries.class
})
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
  public Converter<Integer, String> integer2StringConverter() {
    return new ConverterImpl<>(String.class,
        (Integer l) -> l.toString(), Integer.class,
        (String s) -> s == null ? null : Integer.valueOf(s));
  }

  @Bean
  public Converter<LocalDateTime, XMLGregorianCalendar> localDateTime2XMLGregorianCalendarConverter()
      throws DatatypeConfigurationException {
    // Assume that the DatatypeFactory is thread safe!
    // see:
    // https://stackoverflow.com/questions/7346508/datatypefactory-usage-in-creating-xmlgregoriancalendar-hits-performance-badly
    DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
    return new ConverterImpl<>(XMLGregorianCalendar.class,
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
    return new ConverterImpl<>(LocalDateTime.class,
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
    return new ConverterImpl<>(XMLGregorianCalendar.class,
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
    return new ConverterImpl<>(LocalDate.class,
        (XMLGregorianCalendar c) -> c.toGregorianCalendar().toZonedDateTime().toLocalDate(),
        XMLGregorianCalendar.class,
        (LocalDate d) -> datatypeFactory
            .newXMLGregorianCalendar(
                GregorianCalendar.from(d.atStartOfDay(ZoneId.systemDefault()))));
  }

  @Bean
  public Converter<LocalDateTime, OffsetDateTime> localDateTime2OffsetDateTimeConverter() {
    return new ConverterImpl<>(OffsetDateTime.class,
        ltd -> ltd == null ? null
            : ZonedDateTime.of(ltd, ZoneId.systemDefault())
                .toOffsetDateTime(),
        LocalDateTime.class,
        odt -> odt == null ? null
            : odt.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
  }

  @Bean
  public Converter<OffsetDateTime, LocalDateTime> offsetDateTime2localDateTimeConverter() {
    return new ConverterImpl<>(LocalDateTime.class,
        odt -> odt == null ? null : odt.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime(),
        OffsetDateTime.class,
        ltd -> ltd == null ? null
            : ZonedDateTime.of(ltd, ZoneId.systemDefault())
                .toOffsetDateTime());
  }

  @Bean
  public Converter<OffsetDateTime, LocalDate> offsetDateTime2localDateConverter() {
    return new ConverterImpl<>(LocalDate.class,
        OffsetDateTime::toLocalDate, OffsetDateTime.class,
        (LocalDate d) -> ZonedDateTime.of(d, LocalTime.MIN, ZoneId.systemDefault())
            .toOffsetDateTime());
  }

  @Bean
  public Converter<URI, String> uri2String() {
    return new ConverterImpl<>(String.class, URI::toString, URI.class, s -> URI.create(s));
  }

  @Bean
  public StorageApi getStorageApi() {
    return new StorageApiImpl();
  }

  @Bean
  public ObjectHistoryApi storageObjectHistoryApi(StorageApi storageApi) {
    return new ObjectHistoryApiImpl(storageApi);
  }

  @Bean
  public ApplyChangeService applyChangeService(ObjectApi objectApi, TransferService transferService,
      List<Supplier<ApplyChangeObjectConfig>> configFactories) {
    return new ApplyChangeServiceImpl(objectApi, transferService, configFactories);
  }

  @Bean
  public EntityManager entitManager(List<EntityDefinition> entityDefs) {
    return new EntityManagerImpl(entityDefs);
  }

}
