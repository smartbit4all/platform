/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.domain.config;

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
    return new ConverterImpl<String, Long>(Long.class, (String s) -> Long.valueOf(s), String.class,
        (Long l) -> l.toString());
  }

  @Bean
  public Converter<Long, String> long2StringConverter() {
    return new ConverterImpl<Long, String>(String.class,
        (Long l) -> l.toString(), Long.class, (String s) -> s == null ? null : Long.valueOf(s));
  }

}
