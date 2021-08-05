package org.smartbit4all.sql.queryexecution;

import org.junit.jupiter.api.Test;
import org.smartbit4all.domain.meta.AssociationDefinition;
import org.smartbit4all.domain.service.query.QueryApi;
import org.smartbit4all.domain.service.query.Retrieval;
import org.smartbit4all.domain.service.query.RetrievalRequest;
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
public class RetrievalExecutionTests {

  @Autowired
  private AddressDef addressDef;

  @Autowired
  private TicketDef ticketDef;

  @Autowired
  private QueryApi queryApi;

  @Test
  public void twoQueriesFromDifferentDb() throws Exception {
    RetrievalRequest retrievalRequest = new RetrievalRequest();
    AssociationDefinition assoc = new AssociationDefinition();
    retrievalRequest.node(Crud.read(addressDef).selectAllProperties().input(),
        addressDef.zip().eq("6000"));
    Retrieval retrieval = queryApi.prepare(retrievalRequest);

  }

}
