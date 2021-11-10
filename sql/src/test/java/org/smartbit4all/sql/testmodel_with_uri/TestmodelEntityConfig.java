package org.smartbit4all.sql.testmodel_with_uri;

import org.smartbit4all.domain.meta.EntityConfiguration;
import org.smartbit4all.sql.testmodel_with_uri.refbeans.ADef;
import org.smartbit4all.sql.testmodel_with_uri.refbeans.BDef;
import org.smartbit4all.sql.testmodel_with_uri.refbeans.CDef;
import org.smartbit4all.sql.testmodel_with_uri.refbeans.DDef;
import org.smartbit4all.sql.testmodel_with_uri.refbeans.EDef;
import org.smartbit4all.sql.testmodel_with_uri.refbeans.FDef;
import org.smartbit4all.sql.testmodel_with_uri.refbeans.GDef;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestmodelEntityConfig extends EntityConfiguration {

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

  @Bean(ADef.ENTITY_NAME)
  public ADef aDef() {
    ADef perosnDef = createEntityProxy(ADef.class);
    return perosnDef;
  }

  @Bean(BDef.ENTITY_NAME)
  public BDef bDef() {
    BDef bDef = createEntityProxy(BDef.class);
    return bDef;
  }

  @Bean(CDef.ENTITY_NAME)
  public CDef cDef() {
    CDef cDef = createEntityProxy(CDef.class);
    return cDef;
  }

  @Bean(DDef.ENTITY_NAME)
  public DDef dDef() {
    DDef dDef = createEntityProxy(DDef.class);
    return dDef;
  }

  @Bean(EDef.ENTITY_NAME)
  public EDef eDef() {
    EDef eDef = createEntityProxy(EDef.class);
    return eDef;
  }

  @Bean(FDef.ENTITY_NAME)
  public FDef fDef() {
    FDef fDef = createEntityProxy(FDef.class);
    return fDef;
  }

  @Bean(GDef.ENTITY_NAME)
  public GDef gDef() {
    GDef gDef = createEntityProxy(GDef.class);
    return gDef;
  }

}
