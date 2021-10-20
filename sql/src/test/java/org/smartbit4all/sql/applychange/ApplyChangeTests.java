package org.smartbit4all.sql.applychange;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ObjectChange;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.PropertySet;
import org.smartbit4all.domain.service.modify.ApplyChangeService;
import org.smartbit4all.domain.utility.crud.Crud;
import org.smartbit4all.sql.testmodel_with_uri.AddressDef;
import org.smartbit4all.sql.testmodel_with_uri.PersonDef;
import org.smartbit4all.sql.testmodel_with_uri.TicketDef;
import org.smartbit4all.sql.testmodel_with_uri.beans.TicketFCC;
import org.smartbit4all.sql.testmodel_with_uri.beans.TicketFCC.Address;
import org.smartbit4all.sql.testmodel_with_uri.beans.TicketFCC.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(classes = {
    ApplyChangeTestConfig.class,
})
@Sql({"/script/applychanges/applychanges_schema.sql",
    "/script/applychanges/applychanges_data_01.sql"})
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class ApplyChangeTests {

  private static Map<Class<?>, ApiBeanDescriptor> descriptors;

  @Autowired
  private TicketDef ticketDef;

  @Autowired
  private PersonDef personDef;

  @Autowired
  private AddressDef addressDef;

  @Autowired
  private ApplyChangeService applyChangeService;

  @BeforeAll
  public static void initTest() {
    initDescriptors();
  }

  private static void initDescriptors() {
    Set<Class<?>> domainBeans = new HashSet<>();
    domainBeans.add(TicketFCC.class);
    domainBeans.add(Address.class);
    domainBeans.add(Person.class);
    descriptors = ApiBeanDescriptor.of(domainBeans);
  }

  @Test
  public void testApplyChangeWithCreateAndModify() throws Exception {

    TicketFCC ticket = createTicketWithPersonsAndAddresses();


    ApiObjectRef ticketObjectRef = new ApiObjectRef(ticket.getCustomNamedId(), ticket, descriptors);
    ObjectChange objectChange = ticketObjectRef.renderAndCleanChanges().orElse(null);

    assertNotNull(objectChange);
    System.out.println(objectChange);

    // =======
    applyChangeService.applyChange(objectChange, ticket);
    // =======

    PropertySet select = ticketDef.allProperties();
    select.addAll(ticketDef.primaryPerson().allProperties());
    select.addAll(ticketDef.secondaryPerson().allProperties());
    TableData<TicketDef> ticketTd = Crud.read(ticketDef)
        .select(select)
        .where(ticketDef.idString().eq(ticket.getCustomNamedId()))
        .listData();
    System.out.println("----\nQueried result:\n" + ticketTd);

    assertTrue(!ticketTd.isEmpty());
    DataRow row = ticketTd.rows().get(0);

    assertEquals(ticket.getTitle(), row.get(ticketDef.title()));
    assertEquals(ticket.getPrimaryPerson().getName(), row.get(ticketDef.primaryPerson().name()));
    assertEquals(ticket.getSecondaryPerson().getName(),
        row.get(ticketDef.secondaryPerson().name()));

    TableData<AddressDef> addressTd = Crud.read(addressDef)
        .selectAllProperties()
        .where(addressDef.id().like("UUID%"))
        .listData();
    System.out.println("----\nQueried address result:\n" + addressTd);
    assertTrue(addressTd.size() >= 3);

    System.out.println("\n========== CREATION SUCCESSFUL ===============\n");

    TicketFCC ticketV2 = createTicketWithPersonsAndAddresses();
    ticketV2.setTitle("Ticket name modified");
    ticketV2.getPrimaryPerson().setName("Primary Person Modified");
    ticketV2.getSecondaryPerson().getAddresses().get(1).setZip("8888");

    ticketObjectRef.mergeObject(ticketV2);


    ObjectChange changesAfterModify1 = ticketObjectRef.renderAndCleanChanges().orElse(null);
    assertNotNull(changesAfterModify1);
    System.out.println("\n\n----\nObjectChenges:\n" + changesAfterModify1);

    // =======
    applyChangeService.applyChange(changesAfterModify1, ticketV2);
    // =======

    ticketTd = Crud.read(ticketDef)
        .select(select)
        .where(ticketDef.idString().eq(ticket.getCustomNamedId()))
        .listData();
    row = ticketTd.rows().get(0);
    System.out.println("----\nQueried result:\n" + ticketTd);
    assertEquals(ticketV2.getTitle(), row.get(ticketDef.title()));
    assertEquals(ticketV2.getPrimaryPerson().getName(), row.get(ticketDef.primaryPerson().name()));

    addressTd = Crud.read(addressDef)
        .selectAllProperties()
        .where(addressDef.city().eq("Address3"))
        .listData();
    System.out.println("----\nQueried address result:\n" + addressTd);
    DataRow addressRow = addressTd.rows().get(0);
    assertEquals(ticketV2.getSecondaryPerson().getAddresses().get(1).getZip(),
        addressRow.get(addressDef.zip()));

  }

  private TicketFCC createTicketWithPersonsAndAddresses() {
    TicketFCC ticket = new TicketFCC();
    String ticketId = "UUID-TICKET";
    ticket.setCustomNamedId(ticketId);
    ticket.setUri(URI.create("uri-" + ticketId));
    ticket.setTitle("Ticket-1");

    Person primaryPerson = new Person();
    primaryPerson.setName("Primary Person");
    // primaryPerson.setId("UUID-PRIMARY_PERSON");

    Person secondaryPerson = new Person();
    secondaryPerson.setName("Secondary Person");
    // secondaryPerson.setId("UUID-SECONDARY_PERSON");

    Address address1 = new Address();
    // address1.setId("UUID-ADDRESS1");
    address1.setCity("Address1");
    address1.setZip("9910");

    Address address2 = new Address();
    // address2.setId("UUID-ADDRESS2");
    address2.setCity("Address2");
    address2.setZip("9920");

    Address address3 = new Address();
    // address3.setId("UUID-ADDRESS3");
    address3.setCity("Address3");
    address3.setZip("9930");

    primaryPerson.setAddresses(Collections.singletonList(address1));
    secondaryPerson.setAddresses(Arrays.asList(address2, address3));

    ticket.setPrimaryPerson(primaryPerson);
    ticket.setSecondaryPerson(secondaryPerson);
    return ticket;
  }

}
