package org.smartbit4all.sql.applychange;

import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;
import javax.sql.DataSource;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertyRef;
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
import org.smartbit4all.sql.testmodel_with_uri.TestmodelEntityConfig;
import org.smartbit4all.sql.testmodel_with_uri.TicketDef;
import org.smartbit4all.sql.testmodel_with_uri.beans.TicketFCC;
import org.smartbit4all.sql.testmodel_with_uri.beans.TicketFCC.Address;
import org.smartbit4all.sql.testmodel_with_uri.beans.TicketFCC.Person;
import org.smartbit4all.sql.testmodel_with_uri.refbeans.ACT_A_FCC;
import org.smartbit4all.sql.testmodel_with_uri.refbeans.ACT_D;
import org.smartbit4all.sql.testmodel_with_uri.refbeans.ADef;
import org.smartbit4all.sql.testmodel_with_uri.refbeans.BDef;
import org.smartbit4all.sql.testmodel_with_uri.refbeans.CDef;
import org.smartbit4all.sql.testmodel_with_uri.refbeans.DDef;
import org.smartbit4all.sql.testmodel_with_uri.refbeans.EDef;
import org.smartbit4all.sql.testmodel_with_uri.refbeans.FDef;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired
  SQLIdentifierService idService;

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
  public ObjectDefinition<TicketFCC> tickedFccDef() {
    ObjectDefinition<TicketFCC> result = new ObjectDefinition<>(TicketFCC.class);
    result.setIdGetter(TicketFCC::getCustomNamedId);
    result.setIdSetter(TicketFCC::setCustomNamedId);
    return result;
  }

  @Bean
  public ObjectDefinition<ACT_A_FCC> actAFccDef() {
    ObjectDefinition<ACT_A_FCC> result = new ObjectDefinition<>(ACT_A_FCC.class);
    result.setIdGetter(ACT_A_FCC::getUid);
    result.setIdSetter(ACT_A_FCC::setUid);
    return result;
  }

  @Bean
  public Supplier<ApplyChangeObjectConfig> acoRefbeanConfig(ADef aDef, BDef bDef, CDef cDef,
      EDef eDef, FDef fDef,
      DDef dDef, SQLIdentifierService idService) {
// @formatter:off
    return () -> {
      return ApplyChangeObjectConfig.builder(ACT_A_FCC.class, aDef)
          .entityIdProperty(aDef.uid())
          .entityPrimaryKeyIdProvider(stringId -> 
            createPrimarayKeyIdProvider((String) stringId, aDef.id(), aDef.uid()))
          .addPropertyMapping("af1", aDef.af1())
          .addPropertyMapping("cf1", aDef.bDef().cDef().cf1())
          .addReferenceMapping("actB")
            .addPropertyMapping("af2", aDef.af2())
            .addPropertyMapping("cf2", aDef.bDef().cDef().cf2())
            .addPropertyMapping("bf2", aDef.bDef().bf2())
            .end()
          .addReferenceMapping("actC")
            .addPropertyMapping("cf3", aDef.bDef().cDef().cf3())
            .addPropertyMapping("ff1", aDef.bDef().cDef().fDef().ff1())
            .addCollectionMapping("actdList", dDef.cId(), 
                ApplyChangeObjectConfig.builder(ACT_D.class, dDef)
                  .entityIdProperty(dDef.uid())
                  .entityPrimaryKeyIdProvider(stringId -> 
                    createPrimarayKeyIdProvider((String) stringId, dDef.id(), dDef.uid()))
                  .addPropertyMapping("df1", dDef.df1())
                  .addPropertyMapping("ef1", dDef.eDef().ef1())
                  .addEntityReferenceDescriptor(cDef.uid(), 
                      uid -> createPrimarayKeyIdProvider((String) uid, cDef.id(), cDef.uid()))
                  .addEntityReferenceDescriptor(eDef.uid(), 
                      uid -> createPrimarayKeyIdProvider((String) uid, eDef.id(), eDef.uid()))
                .build())
              .referredProperty(aDef.bDef().cDef().id())
              .end()
          .addEntityReferenceDescriptor(bDef.uid(), 
              uid -> createPrimarayKeyIdProvider((String) uid, bDef.id(), bDef.uid()))
          .addEntityReferenceDescriptor(cDef.uid(), 
              uid -> createPrimarayKeyIdProvider((String) uid, cDef.id(), cDef.uid()))
          .addEntityReferenceDescriptor(fDef.uid(), 
              uid -> createPrimarayKeyIdProvider((String) uid, fDef.id(), fDef.uid()))
          .build();
    };
  }
// @formatter:on


  @Bean
  public Supplier<ApplyChangeObjectConfig> ticketACOConfig(TicketDef ticketDef,
      AddressDef addressDef) {
// @formatter:off
    return () -> {
      return ApplyChangeObjectConfig.builder(TicketFCC.class, ticketDef)
          .entityIdProperty(ticketDef.idString())
          .entityPrimaryKeyIdProvider(stringId -> 
                createTickedPrimaryKeyId(ticketDef, idService, stringId))
          .addPropertyMapping(TicketFCC.TITLE, ticketDef.title())
          .addPropertyMapping(TicketFCC.URI, ticketDef.uri())
          .addReferenceMapping(TicketFCC.PRIMARY_PERSON/*, ticketDef.primaryPersonId()*/)
            .addPropertyMapping(Person.NAME, ticketDef.primaryPerson().name())
            .addCollectionMapping(Person.ADDRESSES, addressDef.personId(), createAddressMapping(addressDef))
              .referredProperty(ticketDef.primaryPerson().id())
              .end()
            
            
          .addReferenceMapping(TicketFCC.SECONDARY_PERSON)
            .addPropertyMapping(Person.NAME, ticketDef.secondaryPerson().name())
            .addCollectionMapping(Person.ADDRESSES, addressDef.personId(), createAddressMapping(addressDef))
              .referredProperty(ticketDef.secondaryPerson().id())
          .build();
    };
  }
  
  private Map<Property<?>, Object> createPrimarayKeyIdProvider(String uid, Property<Long> idProp, Property<String> uidProp) {
    return createPrimarayKeyIdProvider(uid, idProp, uidProp, () -> getNextId(idService, "SQL_TEST_EXISTS_SEQ"));
  }
  
  private Map<Property<?>, Object> createPrimarayKeyIdProvider(String uid, Property<Long> idProp, Property<String> uidProp, Supplier<Long> idProvider) {
    Long id = null;
    if(idProp instanceof PropertyRef) {
      idProp = ((PropertyRef) idProp).getReferredOwnedProperty();
    }
    if(uidProp instanceof PropertyRef) {
      uidProp = ((PropertyRef) uidProp).getReferredOwnedProperty();
    }
    try {
      id = Crud.read(idProp.getEntityDef())
        .select(idProp)
        .where(uidProp.eq(uid))
        .onlyOneValue(idProp).orElse(null);
    } catch (Exception e) {
      // nop
    }
    
    if(id == null) {
      id = idProvider.get();
    }
    return Collections.singletonMap(idProp, id);
  }
  
  private Map<Property<?>, Object> createRefBeanAPrimaryKeyId(ADef aDef, SQLIdentifierService idService,
      Object stringId) {
    Long id = null;
    try {
      id = Crud.read(aDef)
        .select(aDef.id())
        .where(aDef.uid().eq((String) stringId))
        .onlyOneValue(aDef.id()).orElse(null);
    } catch (Exception e) {
      // nop
    }
    
    if(id == null) {
      id = getNextId(idService, "SQL_TEST_EXISTS_SEQ");
    }
    return Collections.singletonMap(aDef.id(), id);
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
  
//  private ApplyChangeObjectConfig createPersonMapping(PersonDef personDef, AddressDef addressDef) {
//     return ApplyChangeObjectConfig.builder(Person.class, personDef)
//       .addPropertyMapping(Person.NAME, personDef.name())
//       .addCollectionMapping(Person.ADDRESSES, addressDef.personId())
//         .config(createAddressMapping(addressDef))
//         .and()
//       .build();
//   }
  
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
