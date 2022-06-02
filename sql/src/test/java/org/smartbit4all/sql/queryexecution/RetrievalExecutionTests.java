package org.smartbit4all.sql.queryexecution;

import org.junit.jupiter.api.Test;
import org.smartbit4all.domain.service.CrudApi;
import org.smartbit4all.domain.service.query.Retrieval;
import org.smartbit4all.domain.service.query.RetrievalRequest;
import org.smartbit4all.domain.service.query.RetrievalRequestNode;
import org.smartbit4all.domain.utility.crud.Crud;
import org.smartbit4all.sql.testmodel.AddressDef;
import org.smartbit4all.sql.testmodel.PersonDef;
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
  private PersonDef personDef;

  @Autowired
  private TicketDef ticketDef;

  @Autowired
  private CrudApi queryApi;

  @Test
  public void twoQueriesFromDifferentDb() throws Exception {
    RetrievalRequest retrievalRequest = new RetrievalRequest();
    RetrievalRequestNode addressNode =
        retrievalRequest.node(Crud.read(addressDef).selectAllProperties().input(),
            addressDef.zip().eq("6000"));
    RetrievalRequestNode personNode =
        retrievalRequest.node(Crud.read(personDef).selectAllProperties().input());

    addressNode.edge(addressDef.getReference("person").association().getReferred(),
        personNode);

    Retrieval retrieval = queryApi.prepareRetrieval(retrievalRequest);
    queryApi.executeRetrieval(retrieval);
  }

}
