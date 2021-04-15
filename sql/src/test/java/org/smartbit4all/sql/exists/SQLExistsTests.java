package org.smartbit4all.sql.exists;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.smartbit4all.domain.data.TableData;
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

@SpringBootTest(classes = {
    ExistTestConfig.class,
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
  
  //@Test
  public void detailExistsTest_01() throws Exception {
    
    // we are looking for the persons with Budapest addresses (ZIP code 10xx)
    TableData<PersonDef> tickets = Crud.read(personDef)
      .select(personDef.id(), personDef.name())
      .where(personDef.exists(addressDef.person(), addressDef.zip().like("10%")))
      .listData();
    
    /* Expected:
     * Matching addresses: 22,41,50 -> matching persons: 2,4,5
     */
    
    if(tickets.isEmpty()) {
      fail("Empty ticket results");
    }
    List<Long> expectedIds = Arrays.asList(Long.valueOf(2l), Long.valueOf(4l), Long.valueOf(5l));
    List<Long> resultIds = tickets.rows().stream().map(r -> r.get(personDef.id())).collect(Collectors.toList());
    assertTrue(resultIds.containsAll(expectedIds));
    
  }

  //@Test
  public void detailExistsTest_02() throws Exception {
  
    // we are looking for the tickets with primary persons with Budapest addresses (ZIP code 10xx)
    TableData<TicketDef> tickets = Crud.read(ticketDef)
      .select(ticketDef.id(), ticketDef.title())
      .where(ticketDef.primaryPerson().exists(addressDef.person(), addressDef.zip().like("10%")))
      .listData();
    
    /* Expected:
     * Matching addresses: 22,41,50 -> matching persons: 2,4,5
     * matching tickets from 400 series: 402, 404, 405.
     */
    
    if(tickets.isEmpty()) {
      fail("Empty ticket results");
    }
    List<Long> expectedIds = Arrays.asList(Long.valueOf(402l), Long.valueOf(404l), Long.valueOf(405l));
    List<Long> resultIds = tickets.rows().stream().map(r -> r.get(ticketDef.id())).collect(Collectors.toList());
    assertTrue(resultIds.containsAll(expectedIds));
    
  }
  
}
