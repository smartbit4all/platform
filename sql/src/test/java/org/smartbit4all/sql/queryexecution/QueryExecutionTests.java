package org.smartbit4all.sql.queryexecution;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.utility.crud.Crud;
import org.smartbit4all.sql.testmodel.AddressDef;
import org.smartbit4all.sql.testmodel.TicketDef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@SpringBootTest(classes = {
    QueryExecutionTestConfig.class,
})
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class QueryExecutionTests {

  @Autowired
  private AddressDef addressDef;
  
  @Autowired
  private TicketDef ticketDef;
  
  @Test
  public void twoQueriesFromDifferentDb() throws Exception {
    TableData<AddressDef> addresses = Crud.read(addressDef).selectAllProperties().listData();
    TableData<TicketDef> tickets = Crud.read(ticketDef).selectAllProperties().listData();
    
    assertTrue(addresses.size() >= 11);
    assertTrue(tickets.size() >= 6);
  }
  
}
