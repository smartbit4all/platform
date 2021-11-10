package org.smartbit4all.sql.applychange;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import org.smartbit4all.domain.data.TableDatas;
import org.smartbit4all.domain.meta.PropertySet;
import org.smartbit4all.domain.service.modify.applychange.ApplyChangeService;
import org.smartbit4all.domain.utility.crud.Crud;
import org.smartbit4all.sql.testmodel_with_uri.AddressDef;
import org.smartbit4all.sql.testmodel_with_uri.PersonDef;
import org.smartbit4all.sql.testmodel_with_uri.TicketDef;
import org.smartbit4all.sql.testmodel_with_uri.beans.TicketFCC;
import org.smartbit4all.sql.testmodel_with_uri.beans.TicketFCC.Address;
import org.smartbit4all.sql.testmodel_with_uri.beans.TicketFCC.Person;
import org.smartbit4all.sql.testmodel_with_uri.refbeans.ACT_A_FCC;
import org.smartbit4all.sql.testmodel_with_uri.refbeans.ACT_B;
import org.smartbit4all.sql.testmodel_with_uri.refbeans.ACT_C;
import org.smartbit4all.sql.testmodel_with_uri.refbeans.ACT_D;
import org.smartbit4all.sql.testmodel_with_uri.refbeans.ADef;
import org.smartbit4all.sql.testmodel_with_uri.refbeans.DDef;
import org.smartbit4all.sql.testmodel_with_uri.refbeans.GDef;
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
    "/script/applychanges/applychanges_refentities_schema.sql",
    "/script/applychanges/applychanges_data_01.sql"
})
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class ApplyChangeTests {

  private static Map<Class<?>, ApiBeanDescriptor> ticketDescriptors;

  private static Map<Class<?>, ApiBeanDescriptor> refbeanDescriptors;

  @Autowired
  private TicketDef ticketDef;

  @Autowired
  private PersonDef personDef;

  @Autowired
  private AddressDef addressDef;

  @Autowired
  private ADef aDef;

  @Autowired
  private DDef dDef;

  @Autowired
  private GDef gDef;

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
    ticketDescriptors = ApiBeanDescriptor.of(domainBeans);

    domainBeans = new HashSet<>();
    domainBeans.add(ACT_A_FCC.class);
    domainBeans.add(ACT_B.class);
    domainBeans.add(ACT_C.class);
    domainBeans.add(ACT_D.class);
    domainBeans.add(URI.class);
    refbeanDescriptors = ApiBeanDescriptor.of(domainBeans);
  }

  @Test
  public void testApplyChangeWithReferences() throws Exception {

    ACT_A_FCC actA = createActA();

    applyChangeService.createBean(actA, refbeanDescriptors);

    assertActA(actA);
    assertActDs(actA);

    TableData<GDef> gTable = Crud.read(gDef).selectAllProperties().listData();
    System.out.println("Queried G def result:\n" + TableDatas.toStringAdv(gTable));
    assertEquals(2, gTable.size());

    ACT_A_FCC actAV2 = createModifiedActA();

    applyChangeService.updateBean(actA, actAV2, refbeanDescriptors);

    assertActA(actAV2);
    assertActDs(actAV2);
  }

  private ACT_A_FCC createModifiedActA() {
    ACT_A_FCC actAV2 = createActA();
    actAV2.setAf1("af1_modified");
    actAV2.setCf1("cf1_modified");
    actAV2.getActB().setAf2("af2_modified");
    actAV2.getActC().getActdList().get(1).setDf1("df1_modified");
    actAV2.getActC().getActdList().get(0).setEf1("ef1_modified");
    return actAV2;
  }

  private void assertActDs(ACT_A_FCC actA) throws Exception {
    PropertySet dSelect = dDef.allProperties();
    dSelect.addAll(dDef.eDef().allProperties());
    TableData<DDef> dDefTd = Crud.read(dDef)
        .select(dSelect)
        .order(dDef.uid())
        .listData();
    System.out.println("----\nQueried D def result:\n" + TableDatas.toStringAdv(dDefTd));
    assertFalse(dDefTd.isEmpty());
    assertTrue(dDefTd.size() == 2);
    DataRow dRow = dDefTd.rows().get(0);
    ACT_D actD = actA.getActC().getActdList().get(0);
    assertD(dRow, actD);
    dRow = dDefTd.rows().get(1);
    actD = actA.getActC().getActdList().get(1);
    assertD(dRow, actD);
  }

  private void assertActA(ACT_A_FCC actA) throws Exception {
    PropertySet aSelect = aDef.allProperties();
    aSelect.addAll(aDef.bDef().allProperties());
    aSelect.addAll(aDef.bDef().cDef().allProperties());
    aSelect.addAll(aDef.bDef().cDef().fDef().allProperties());
    TableData<ADef> aDefTd = Crud.read(aDef)
        .select(aSelect)
        .listData();
    System.out.println("----\nQueried FFC result:\n" + TableDatas.toStringAdv(aDefTd));
    assertFalse(aDefTd.isEmpty());
    DataRow aRow = aDefTd.rows().get(0);
    assertEquals(actA.getUid(), aRow.get(aDef.uid()));
    assertEquals(actA.getAf1(), aRow.get(aDef.af1()));
    assertEquals(actA.getCf1(), aRow.get(aDef.bDef().cDef().cf1()));
    assertEquals(actA.getActB().getAf2(), aRow.get(aDef.af2()));
    assertEquals(actA.getActB().getBf2(), aRow.get(aDef.bDef().bf2()));
    assertEquals(actA.getActB().getCf2(), aRow.get(aDef.bDef().cDef().cf2()));
    assertEquals(actA.getActC().getCf3(), aRow.get(aDef.bDef().cDef().cf3()));
    assertEquals(actA.getActC().getFf1(), aRow.get(aDef.bDef().cDef().fDef().ff1()));
    assertEquals(actA.getActB().getBf2() + "_CALCULATED",
        aRow.get(aDef.bDef().cDef().fDef().ff2()));
  }

  private void assertD(DataRow dRow, ACT_D actD) {
    assertEquals(actD.getDf1(), dRow.get(dDef.df1()));
    assertEquals(actD.getEf1(), dRow.get(dDef.eDef().ef1()));
  }

  private ACT_A_FCC createActA() {

    ACT_A_FCC actA = new ACT_A_FCC();
    ACT_B actB = new ACT_B();
    ACT_C actC = new ACT_C();
    ACT_D actD1 = new ACT_D();
    ACT_D actD2 = new ACT_D();

    actA.setUid("ACT_A_UUID");
    actA.setAf1("af1_value");
    actA.setCf1("cf1_value");
    actA.setActB(actB);
    actA.setActC(actC);
    actB.setAf2("af2_value");
    actB.setBf2("bf2_value");
    actB.setCf2("cf2_value");
    actC.setCf3("cf3_value");
    actC.setFf1("ff1_value");
    actC.setActdList(Arrays.asList(actD1, actD2));
    actD1.setDf1("d1_df1_value");
    actD1.setEf1("d1_ef1_value");
    actD2.setDf1("d2_df1_value");
    actD2.setDf1("d2_ef1_value");

    actA.getBuids().add(URI.create("b_0"));
    actA.getBuids().add(URI.create("b_1"));
    return actA;
  }

  @Test
  public void testApplyChangeWithCreateAndModify() throws Exception {

    TicketFCC ticket = createTicketWithPersonsAndAddresses();


    ApiObjectRef ticketObjectRef =
        new ApiObjectRef(ticket.getCustomNamedId(), ticket, ticketDescriptors);
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
    System.out.println("----\nQueried result:\n" + TableDatas.toStringAdv(ticketTd));

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
    System.out.println("----\nQueried address result:\n" + TableDatas.toStringAdv(addressTd));
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
    System.out.println("----\nQueried result:\n" + TableDatas.toStringAdv(ticketTd));
    assertEquals(ticketV2.getTitle(), row.get(ticketDef.title()));
    assertEquals(ticketV2.getPrimaryPerson().getName(), row.get(ticketDef.primaryPerson().name()));

    addressTd = Crud.read(addressDef)
        .selectAllProperties()
        .where(addressDef.city().eq("Address3"))
        .listData();
    System.out.println("----\nQueried address result:\n" + TableDatas.toStringAdv(addressTd));
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
