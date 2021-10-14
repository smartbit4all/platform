package org.smartbit4all.sql.applychange;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ChangeState;
import org.smartbit4all.core.object.CollectionChange;
import org.smartbit4all.core.object.CollectionObjectChange;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectChange;
import org.smartbit4all.core.object.ObjectChangeSimple;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.PropertyChange;
import org.smartbit4all.core.object.ReferenceChange;
import org.smartbit4all.core.object.ReferencedObjectChange;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.TableDatas;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertyRef;
import org.smartbit4all.domain.meta.PropertySet;
import org.smartbit4all.domain.service.modify.ApplyChangeOperation;
import org.smartbit4all.domain.service.modify.ApplyChangeOperation.ChangeOperation;
import org.smartbit4all.domain.service.transfer.ObjectChangeApplyChangeOperationMapping;
import org.smartbit4all.domain.service.transfer.ObjectChangeApplyChangeOperationMapping.CollectionMappingItem;
import org.smartbit4all.domain.service.transfer.ObjectChangeApplyChangeOperationMapping.PropertyMappingItem;
import org.smartbit4all.domain.service.transfer.ObjectChangeApplyChangeOperationMapping.ReferenceMappingItem;
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
  private ObjectApi objectApi;

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
  public void test() throws Exception {

    TicketFCC ticket = createTicketWithPersonsAndAddresses();


    ApiObjectRef ticketObjectRef = new ApiObjectRef(ticket.getId(), ticket, descriptors);
    ObjectChange objectChange = ticketObjectRef.renderAndCleanChanges().orElse(null);

    assertNotNull(objectChange);
    System.out.println(objectChange);

    ObjectChangeApplyChangeOperationMapping mapping = createMapping();

    // =======
    ApplyChangeOperation aco = createApplyChangeOperation(objectChange, mapping, ticket);
    aco.execute();
    // =======

    PropertySet select = ticketDef.allProperties();
    select.addAll(ticketDef.primaryPerson().allProperties());
    select.addAll(ticketDef.secondaryPerson().allProperties());
    TableData<TicketDef> ticketTd = Crud.read(ticketDef)
        .select(select)
        .where(ticketDef.id().eq(ticket.getId()))
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
        .where(addressDef.id().like("UUID-ADDRESS%"))
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

    ApplyChangeOperation acoAfterModify1 =
        createApplyChangeOperation(changesAfterModify1, mapping, ticketV2);
    acoAfterModify1.execute();

    ticketTd = Crud.read(ticketDef)
        .select(ticketDef.allProperties())
        .where(ticketDef.id().eq(ticket.getId()))
        .listData();
    row = ticketTd.rows().get(0);
    System.out.println("----\nQueried result:\n" + ticketTd);
    assertEquals(ticketV2.getTitle(), row.get(ticketDef.title()));
    assertEquals(ticketV2.getPrimaryPerson().getName(), row.get(ticketDef.primaryPerson().name()));

    addressTd = Crud.read(addressDef)
        .selectAllProperties()
        .where(addressDef.id().eq("UUID-ADDRESS3"))
        .listData();
    System.out.println("----\nQueried address result:\n" + addressTd);
    DataRow addressRow = addressTd.rows().get(0);
    assertEquals(ticketV2.getSecondaryPerson().getAddresses().get(1).getZip(),
        addressRow.get(addressDef.zip()));

  }

  private ApplyChangeOperation createApplyChangeOperation(ObjectChange objectChange,
      ObjectChangeApplyChangeOperationMapping mapping, Object rootObject) {
    EntityDefinition rootEntity = mapping.rootEntity;
    ChangeOperation changeOperation = getChangeOperation(objectChange.getOperation());
    if (changeOperation == null || !objectChange.hasChange()) {
      return null;
    }
    TableData<EntityDefinition> rootTable = TableDatas.of(rootEntity);
    PropertySet primaryKeys = rootEntity.PRIMARYKEYDEF();
    if (primaryKeys.size() > 1) {
      throw new IllegalStateException("Not handled entity: it has multiple primary keys!");
    }
    String rootId = getObjectId(rootObject);
    Property<?> primaryKey = primaryKeys.iterator().next();
    rootTable.addColumnOwn(primaryKey);
    DataRow rootRow = rootTable.addRow();
    rootRow.setObject(primaryKey, rootId);

    ApplyChangeOperation aco = new ApplyChangeOperation(rootTable, changeOperation);

    for (PropertyChange pChange : objectChange.getProperties()) {
      Object newValue = pChange.getNewValue();
      // TODO convert the value
      PropertyMappingItem propertyMappingItem = mapping.propertyMappings.get(pChange.getName());
      if (propertyMappingItem == null) {
        continue;
      }
      boolean isPropertyReferred = propertyMappingItem.property instanceof PropertyRef;
      if (!isPropertyReferred) {
        rootTable.addColumnOwn(propertyMappingItem.property);

        // TODO do proper conversion instead!
        if (propertyMappingItem.property.type().equals(String.class)) {
          newValue = newValue.toString();
        }

        rootRow.setObject(propertyMappingItem.property, newValue);
      } else {
        // it is a containment

        // we should know the uuid
        // maybe it should be a design rule, to keep these in references
        // else the contained element's uuid must be in the flat object too, and it should be
        // configurable to the mapping...
      }
    }

    // manage references: add recursively created ApplyChangeOperation to preCalls
    List<ReferenceChange> references = objectChange.getReferences();
    for (int i = 0; i < references.size(); i++) {
      ReferenceChange rChange = references.get(i);
      ReferenceMappingItem referenceMappingItem =
          mapping.referenceMappings.get(rChange.getName());
      if (referenceMappingItem == null) {
        continue;
      }

      // create ApplyChangeOperation with recursive method
      ObjectChange changedReference = rChange.getChangedReference();
      ObjectChangeApplyChangeOperationMapping referenceMapping = referenceMappingItem.oc2AcoMapping;

      ReferencedObjectChange referencedObjectChange = objectChange.getReferencedObjects().get(i);
      Object changedReferredObject = referencedObjectChange.getChange().getObject();

      ApplyChangeOperation referenceAco =
          createApplyChangeOperation(changedReference, referenceMapping, changedReferredObject);
      if (referenceAco != null) {

        String referredId = getObjectId(changedReferredObject);

        // set the referenced uid to the tabeleData
        Objects.requireNonNull(referredId, "Unique ID of referred object can not be null!"); // FIXME
        rootTable.addColumnOwn(referenceMappingItem.refferringRootProperty);
        rootRow.setObject(referenceMappingItem.refferringRootProperty, referredId);

        // add preCall
        aco.preCalls().call(referenceAco);
      }
    }

    List<CollectionChange> collections = objectChange.getCollections();
    for (int i = 0; i < collections.size(); i++) {
      CollectionChange cChange = collections.get(i);
      CollectionObjectChange collectionObjectChange = objectChange.getCollectionObjects().get(i);
      CollectionMappingItem collectionMappingItem =
          mapping.collectionMappings.get(cChange.getName());
      if (collectionMappingItem == null) {
        continue;
      }

      List<ObjectChange> changes = cChange.getChanges();
      for (int j = 0; j < changes.size(); j++) {
        ObjectChange collectionChangeObject = changes.get(j);
        ObjectChangeSimple objectChangeSimple = collectionObjectChange.getChanges().get(j);
        Object collectionObject = objectChangeSimple.getObject();
        ApplyChangeOperation collectionAco = createApplyChangeOperation(collectionChangeObject,
            collectionMappingItem.oc2AcoMapping,
            collectionObject);

        if (collectionAco != null) {

          collectionAco.getTableData().addColumnOwn(collectionMappingItem.refferringDetailProperty);
          collectionAco.getRow().setObject(collectionMappingItem.refferringDetailProperty, rootId);

          aco.postCalls().call(collectionAco);
        }
      }

    }
    return aco;
  }

  @SuppressWarnings("unchecked")
  private <T> String getObjectId(T object) {
    ObjectDefinition<T> referredObjectDef =
        (ObjectDefinition<T>) objectApi.definition(object.getClass());
    String referredId = referredObjectDef.getId(object);
    return referredId;
  }

  private ChangeOperation getChangeOperation(ChangeState operation) {
    switch (operation) {
      case DELETED:
        return ChangeOperation.DELETE;
      case MODIFIED:
        return ChangeOperation.MODIFY;
      case NEW:
        return ChangeOperation.CREATE;
      case NOP:
      default:
        return null;
    }
  }

  private ObjectChangeApplyChangeOperationMapping createMapping() {
    ObjectChangeApplyChangeOperationMapping mapping = new ObjectChangeApplyChangeOperationMapping();
    // config the mapping in init
    mapping.rootEntity = ticketDef;

    PropertyMappingItem titleMI = new PropertyMappingItem();
    titleMI.name = "Title";
    titleMI.property = ticketDef.title();
    mapping.propertyMappings.put(titleMI.name, titleMI);

    PropertyMappingItem uriMI = new PropertyMappingItem();
    uriMI.name = "Uri";
    uriMI.property = ticketDef.uri();
    mapping.propertyMappings.put(uriMI.name, uriMI);

    ReferenceMappingItem primaryPersonMI = new ReferenceMappingItem();
    primaryPersonMI.name = "PrimaryPerson";
    primaryPersonMI.refferringRootProperty = ticketDef.primaryPersonId();
    ObjectChangeApplyChangeOperationMapping personMapping = createPersonMapping();
    primaryPersonMI.oc2AcoMapping = personMapping;
    mapping.referenceMappings.put(primaryPersonMI.name, primaryPersonMI);

    ReferenceMappingItem secondaryPersonMI = new ReferenceMappingItem();
    secondaryPersonMI.name = "SecondaryPerson";
    secondaryPersonMI.refferringRootProperty = ticketDef.secondaryPersonId();
    secondaryPersonMI.oc2AcoMapping = personMapping;
    mapping.referenceMappings.put(secondaryPersonMI.name, secondaryPersonMI);
    return mapping;
  }

  private ObjectChangeApplyChangeOperationMapping createPersonMapping() {
    ObjectChangeApplyChangeOperationMapping personMapping =
        new ObjectChangeApplyChangeOperationMapping();
    personMapping.rootEntity = personDef;
    PropertyMappingItem personNameMI = new PropertyMappingItem();
    personNameMI.name = "Name";
    personNameMI.property = personDef.name();
    personMapping.propertyMappings.put(personNameMI.name, personNameMI);

    CollectionMappingItem addressMI = new CollectionMappingItem();
    addressMI.name = "Addresses";
    addressMI.refferringDetailProperty = addressDef.personId();
    addressMI.oc2AcoMapping = createAddressMapping();
    personMapping.collectionMappings.put(addressMI.name, addressMI);
    return personMapping;
  }

  private ObjectChangeApplyChangeOperationMapping createAddressMapping() {
    ObjectChangeApplyChangeOperationMapping addressMapping =
        new ObjectChangeApplyChangeOperationMapping();
    addressMapping.rootEntity = addressDef;
    PropertyMappingItem zipMI = new PropertyMappingItem();
    zipMI.name = "Zip";
    zipMI.property = addressDef.zip();
    addressMapping.propertyMappings.put(zipMI.name, zipMI);

    PropertyMappingItem cityMI = new PropertyMappingItem();
    cityMI.name = "City";
    cityMI.property = addressDef.city();
    addressMapping.propertyMappings.put(cityMI.name, cityMI);

    return addressMapping;
  }

  private TicketFCC createTicketWithPersonsAndAddresses() {
    TicketFCC ticket = new TicketFCC();
    String ticketId = "UUID-TICKET";
    ticket.setId(ticketId);
    ticket.setUri(URI.create("uri-" + ticketId));
    ticket.setTitle("Ticket-1");

    Person primaryPerson = new Person();
    primaryPerson.setName("Primary Person");
    primaryPerson.setId("UUID-PRIMARY_PERSON");

    Person secondaryPerson = new Person();
    secondaryPerson.setName("Secondary Person");
    secondaryPerson.setId("UUID-SECONDARY_PERSON");

    Address address1 = new Address();
    address1.setId("UUID-ADDRESS1");
    address1.setCity("Address1");
    address1.setZip("9910");

    Address address2 = new Address();
    address2.setId("UUID-ADDRESS2");
    address2.setCity("Address2");
    address2.setZip("9920");

    Address address3 = new Address();
    address3.setId("UUID-ADDRESS3");
    address3.setCity("Address3");
    address3.setZip("9930");

    primaryPerson.setAddresses(Collections.singletonList(address1));
    secondaryPerson.setAddresses(Arrays.asList(address2, address3));

    ticket.setPrimaryPerson(primaryPerson);
    ticket.setSecondaryPerson(secondaryPerson);
    return ticket;
  }

}
