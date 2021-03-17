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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.smartbit4all.core.SB4Configuration;
import org.smartbit4all.domain.application.TimeManagementService;
import org.smartbit4all.domain.application.TimeManagementServiceImpl;
import org.smartbit4all.domain.service.transfer.TransferService;
import org.smartbit4all.domain.service.transfer.TransferServiceImpl;
import org.smartbit4all.domain.service.transfer.convert.Converter;
import org.smartbit4all.domain.service.transfer.convert.ConverterImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Must not be referred directly! Use SQL or remote.
 * 
 * @author Peter Boros
 */
@Configuration
public class DomainServiceConfig extends SB4Configuration {

  public static final String BUDAPEST_ZONE_ID = "Europe/Budapest";

  public static final ZoneId SYSTEM_ZONE_ID = ZoneId.of(BUDAPEST_ZONE_ID);

  @Bean
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

}
