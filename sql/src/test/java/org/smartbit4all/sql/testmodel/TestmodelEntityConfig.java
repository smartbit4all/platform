package org.smartbit4all.sql.testmodel;

import org.smartbit4all.domain.meta.EntityConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestmodelEntityConfig extends EntityConfiguration{

  @Bean(AddressDef.ENTITY_NAME)
  public AddressDef addressDef() {
    AddressDef addressDef = createEntityProxy(AddressDef.class);
    return addressDef;
  }
  
  @Bean(TicketDef.ENTITY_NAME)
  public TicketDef ticketDef() {
    TicketDef ticketDef = createEntityProxy(TicketDef.class);
    return ticketDef;
  }
  
  @Bean(PersonDef.ENTITY_NAME)
  public PersonDef personDef() {
    PersonDef perosnDef = createEntityProxy(PersonDef.class);
    return perosnDef;
  }
  
}
