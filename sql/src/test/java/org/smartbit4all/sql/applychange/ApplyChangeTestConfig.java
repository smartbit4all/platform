package org.smartbit4all.sql.applychange;

import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;
import javax.sql.DataSource;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.jdbc.JDBCDataConverterConfig;
import org.smartbit4all.domain.service.identifier.NextIdentifier;
import org.smartbit4all.domain.service.modify.ApplyChangeObjectConfig;
import org.smartbit4all.domain.utility.crud.Crud;
import org.smartbit4all.sql.config.SQLConfig;
import org.smartbit4all.sql.config.SQLDBParameter;
import org.smartbit4all.sql.config.SQLDBParameterBase;
import org.smartbit4all.sql.config.SQLDBParameterH2;
import org.smartbit4all.sql.service.identifier.SQLIdentifierService;
import org.smartbit4all.sql.testmodel_with_uri.AddressDef;
import org.smartbit4all.sql.testmodel_with_uri.PersonDef;
import org.smartbit4all.sql.testmodel_with_uri.TestmodelEntityConfig;
import org.smartbit4all.sql.testmodel_with_uri.TicketDef;
import org.smartbit4all.sql.testmodel_with_uri.beans.TicketFCC;
import org.smartbit4all.sql.testmodel_with_uri.beans.TicketFCC.Address;
import org.smartbit4all.sql.testmodel_with_uri.beans.TicketFCC.Person;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;



@Configuration
@Import({
    SQLConfig.class,
    SQLIdentifierService.class,
    JDBCDataConverterConfig.class,
    TestmodelEntityConfig.class
})
public class ApplyChangeTestConfig {



  @Bean(name = SQLDBParameterBase.DEFAULT)
  public SQLDBParameter h2Parameter() {
    SQLDBParameter result = new SQLDBParameterH2();
    return result;
  }

  @Bean
  @Primary
  @ConfigurationProperties(prefix = "spring.datasource")
  public DataSource dataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("org.h2.Driver");
    dataSource.setUrl("jdbc:h2:mem:testdb;MODE=Oracle;DB_CLOSE_DELAY=-1");
    dataSource.setUsername("sa");
    dataSource.setPassword("");
    return dataSource;
  }

  @Bean
  public JdbcTemplate applicationDataConnection(DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }

  @Bean
  public ObjectDefinition<TicketFCC> masterBeanDef() {
    ObjectDefinition<TicketFCC> result = new ObjectDefinition<>(TicketFCC.class);
    result.setIdGetter(TicketFCC::getCustomNamedId);
    result.setIdSetter(TicketFCC::setCustomNamedId);
    return result;
  }


  @Bean
  public Supplier<ApplyChangeObjectConfig> ticketACOConfig(TicketDef ticketDef, PersonDef personDef,
      AddressDef addressDef, SQLIdentifierService idService) {
// @formatter:off
    return () -> {
      ApplyChangeObjectConfig personConfig = createPersonMapping(personDef, addressDef);
      return ApplyChangeObjectConfig.builder(TicketFCC.class, ticketDef)
          .entityIdProperty(ticketDef.idString())
          .entityPrimaryKeyIdProvider(stringId -> 
                createTickedPrimaryKeyId(ticketDef, idService, stringId))
          .addPropertyMapping(TicketFCC.TITLE, ticketDef.title())
          .addPropertyMapping(TicketFCC.URI, ticketDef.uri())
          .addReferenceMapping(TicketFCC.PRIMARY_PERSON, ticketDef.primaryPersonId())
            .config(personConfig)
            .and()
          .addReferenceMapping(TicketFCC.SECONDARY_PERSON, ticketDef.secondaryPersonId())
            .config(personConfig)
            .and()
          .build();
    };
  }

  private Map<Property<?>, Object> createTickedPrimaryKeyId(TicketDef ticketDef, SQLIdentifierService idService,
      Object stringId) {
    Long id = null;
    try {
      id = Crud.read(ticketDef)
        .select(ticketDef.id())
        .where(ticketDef.idString().eq((String) stringId))
        .onlyOneValue(ticketDef.id()).orElse(null);
    } catch (Exception e) {
      // nop
    }
    
    if(id == null) {
      id = getNextId(idService, "SQL_TEST_EXISTS_SEQ");
    }
    return Collections.singletonMap(ticketDef.id(), id);
  }
  
  private ApplyChangeObjectConfig createPersonMapping(PersonDef personDef, AddressDef addressDef) {
     return ApplyChangeObjectConfig.builder(Person.class, personDef)
       .addPropertyMapping(Person.NAME, personDef.name())
       .addCollectionMapping(Person.ADDRESSES, addressDef.personId())
         .config(createAddressMapping(addressDef))
         .and()
       .build();
   }
  
   private ApplyChangeObjectConfig createAddressMapping(AddressDef addressDef) {
     return ApplyChangeObjectConfig.builder(Address.class, addressDef)
         .addPropertyMapping(Address.ZIP, addressDef.zip())
         .addPropertyMapping(Address.CITY, addressDef.city())
         .build();
   }
// @formatter:on

  private Long getNextId(SQLIdentifierService idService, String sequence) {
    try {
      NextIdentifier next = idService.next();
      next.setInput(sequence);
      next.execute();
      return next.output();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
