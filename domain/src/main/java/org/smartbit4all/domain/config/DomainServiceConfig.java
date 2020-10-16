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
        (Long l) -> l.toString(), Long.class, (String s) -> Long.valueOf(s));
  }

}
