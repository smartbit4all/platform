package org.smartbit4all.sql.exists;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.ExpressionIn;
import org.smartbit4all.domain.meta.OperandComposite;
import org.smartbit4all.domain.meta.OperandProperty;
import org.smartbit4all.domain.service.query.QueryApi;
import org.smartbit4all.domain.service.query.QueryExecutionPlan;
import org.smartbit4all.domain.service.query.QueryInput;
import org.smartbit4all.domain.service.query.QueryOutput;
import org.smartbit4all.domain.service.query.QueryResult;
import org.smartbit4all.domain.utility.CompositeValue;
import org.smartbit4all.domain.utility.crud.Crud;
import org.smartbit4all.sql.testmodel.AddressDef;
import org.smartbit4all.sql.testmodel.PersonDef;
import org.smartbit4all.sql.testmodel.TicketDef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(
    classes = {
        ExistTestConfig.class,
    },
    properties = {
        "platform.sql.temptable-autocreate.enabled=true"
    })
@Sql({"/script/exists_schema.sql", "/script/exists_data_01.sql"})
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class SQLExistsTests {

  @Autowired
  private AddressDef addressDef;

  @Autowired
  private PersonDef personDef;

  @Autowired
  private TicketDef ticketDef;

  @Autowired
  private QueryApi queryApi;

  @Test
  public void initDbTest() throws Exception {

    TableData<PersonDef> persons =
        Crud.read(personDef).select(personDef.allProperties()).listData();

    TableData<AddressDef> addresses =
        Crud.read(addressDef).select(addressDef.allProperties()).listData();

    TableData<TicketDef> tickets =
        Crud.read(ticketDef).select(ticketDef.allProperties()).listData();

    System.out.println(persons);
    System.out.println(addresses);
    System.out.println(tickets);

    assertFalse(persons.isEmpty());
    assertFalse(addresses.isEmpty());
    assertFalse(tickets.isEmpty());
  }

  @Test
  public void detailExistsTest_01() throws Exception {

    // we are looking for the persons with Budapest addresses (ZIP code 10xx)
    TableData<PersonDef> tickets = Crud.read(personDef)
        .select(personDef.id(), personDef.name())
        .where(personDef.exists(addressDef.person().join(), addressDef.zip().like("10%")))
        .listData();

    /*
     * Expected: Matching addresses: 22,41,50 -> matching persons: 2,4,5
     */

    if (tickets.isEmpty()) {
      fail("Empty ticket results");
    }
    List<Long> expectedIds = Arrays.asList(Long.valueOf(2l), Long.valueOf(4l), Long.valueOf(5l));
    List<Long> resultIds =
        tickets.rows().stream().map(r -> r.get(personDef.id())).collect(Collectors.toList());
    assertTrue(resultIds.containsAll(expectedIds));

  }

  @Test
  public void detailExistsTest_02() throws Exception {

    // we are looking for the tickets with primary persons with Budapest addresses (ZIP code 10xx)
    TableData<TicketDef> tickets = Crud.read(ticketDef)
        .select(ticketDef.id(), ticketDef.title())
        .where(ticketDef.primaryPerson().exists(addressDef.person().join(),
            addressDef.zip().like("10%")))
        .listData();

    /*
     * Expected: Matching addresses: 22,41,50 -> matching persons: 2,4,5 matching tickets from 400
     * series: 402, 404, 405.
     */

    if (tickets.isEmpty()) {
      fail("Empty ticket results");
    }
    List<Long> expectedIds =
        Arrays.asList(Long.valueOf(402l), Long.valueOf(404l), Long.valueOf(405l));
    List<Long> resultIds =
        tickets.rows().stream().map(r -> r.get(ticketDef.id())).collect(Collectors.toList());
    assertTrue(resultIds.containsAll(expectedIds));

  }

  @Test
  public void detailExistsTest_03() throws Exception {

    TableData<TicketDef> tickets = Crud.read(ticketDef)
        .select(ticketDef.id(), ticketDef.title())
        .where(ticketDef.primaryPerson().exists(personDef.name().eq("Ond")))
        .listData();

    /*
     * Expected: matching persons: 3 matching tickets from 400 series: 403.
     */

    if (tickets.isEmpty()) {
      fail("Empty ticket results");
    }
    List<Long> expectedIds =
        Arrays.asList(Long.valueOf(403l));
    List<Long> resultIds =
        tickets.rows().stream().map(r -> r.get(ticketDef.id())).collect(Collectors.toList());
    assertTrue(resultIds.containsAll(expectedIds));

  }

  @Test
  public void tooManyValuesInTest_01() throws Exception {

    Set<Long> inValues = new HashSet<>();
    for (long i = 0; i < 1000; i++) {
      inValues.add(i);
    }

    QueryInput query = personDef.services().crud().read()
        .select(personDef.id(), personDef.name())
        .where(personDef.id().in(inValues).AND(personDef.name().in(Arrays.asList("Tas", "Huba"))))
        .getQuery();

    QueryExecutionPlan executionPlan = queryApi.prepare(query);

    System.out.println(executionPlan);

    QueryResult queryResult = queryApi.execute(executionPlan);

    for (QueryOutput qry : queryResult.getResults()) {
      TableData<?> resultData = qry.getTableData();
      System.out.println(resultData);
      List<Long> resultIds =
          resultData.rows().stream().map(r -> r.get(personDef.id())).collect(Collectors.toList());
      assertTrue(resultIds.containsAll(Arrays.asList(Long.valueOf(5l), Long.valueOf(6l))));
    }

  }

  @Test
  public void tooManyValuesInCrudReadTest_01() throws Exception {

    Set<Long> inValues = new HashSet<>();
    for (long i = 0; i < 1000; i++) {
      inValues.add(i);
    }

    TableData<?> resultData = Crud.read(personDef).select(personDef.id(), personDef.name())
        .where(personDef.id().in(inValues).AND(personDef.name().in(Arrays.asList("Tas", "Huba"))))
        .listData();

    System.out.println(resultData);
    List<Long> resultIds =
        resultData.rows().stream().map(r -> r.get(personDef.id())).collect(Collectors.toList());
    assertTrue(resultIds.containsAll(Arrays.asList(Long.valueOf(5l), Long.valueOf(6l))));
  }

  @Test
  public void tooManyValuesInTest_02() throws Exception {

    Set<String> inValues = new HashSet<>();
    inValues.add("Tas");
    inValues.add("Huba");
    for (long i = 0; i < 1000; i++) {
      inValues.add("Value" + i);
    }

    QueryInput query = personDef.services().crud().read()
        .select(personDef.id(), personDef.name())
        .where(personDef.name().in(inValues).AND(personDef.id().in(Arrays.asList(5l, 6l))))
        .getQuery();

    QueryExecutionPlan executionPlan = queryApi.prepare(query);

    System.out.println(executionPlan);

    QueryResult queryResult = queryApi.execute(executionPlan);

    for (QueryOutput qry : queryResult.getResults()) {
      TableData<?> resultData = qry.getTableData();
      System.out.println(resultData);
      List<Long> resultIds =
          resultData.rows().stream().map(r -> r.get(personDef.id())).collect(Collectors.toList());
      assertTrue(resultIds.containsAll(Arrays.asList(Long.valueOf(5l), Long.valueOf(6l))));
    }

  }

  @Test
  public void noDetailResultTest() throws Exception {

    // we are looking for the persons with zip code that is not in the db
    TableData<PersonDef> tickets = Crud.read(personDef)
        .select(personDef.id(), personDef.name())
        .where(personDef.exists(addressDef.person().join(), addressDef.zip().like("xxx%")))
        .listData();

    assertTrue(tickets.isEmpty());

  }

  @Test
  public void withNullDetailResultTest() throws Exception {

    // we are looking for the persons with zip, but there are matching zips with no person set.
    TableData<PersonDef> tickets = Crud.read(personDef)
        .select(personDef.id(), personDef.name())
        .where(personDef.exists(addressDef.person().join(), addressDef.zip().like("60%")))
        .listData();

    assertTrue(tickets.size() >= 2);

  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Test
  public void expreessionInMultipleProperties() throws Exception {

    Set<CompositeValue> inValues = new HashSet<>();
    inValues.add(new CompositeValue("Ügy 100", Long.valueOf(1)));
    inValues.add(new CompositeValue("Ügy 401", Long.valueOf(1)));
    inValues.add(new CompositeValue("Ügy 401", Long.valueOf(2)));

    TableData<TicketDef> tickets = Crud.read(ticketDef).selectAllProperties()
        .where(new ExpressionIn<>(new OperandComposite(new OperandProperty(ticketDef.title()),
            new OperandProperty(ticketDef.primaryPersonId())), inValues))
        .listData();

    Long countValue = (Long) Crud.read(ticketDef).count().singleValue().get();

    System.out.println(countValue);

    assertTrue(tickets.size() == 2);

    TableData<TicketDef> inverseTickets = Crud.read(ticketDef).all()
        .where(new ExpressionIn<>(new OperandComposite(new OperandProperty(ticketDef.title()),
            new OperandProperty(ticketDef.primaryPersonId())), inValues).NOT())
        .listData();

    assertTrue(inverseTickets.size() == (countValue - tickets.size()));
  }

}
