package org.smartbit4all.sql.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.TableDatas;
import org.smartbit4all.domain.utility.crud.Crud;
import org.smartbit4all.sql.service.query.SQLQueryExecutionApi;
import org.smartbit4all.sql.testmodel.AddressDef;
import org.smartbit4all.sql.testmodel.TicketDef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@SpringBootTest(classes = {
    QuerySeparatedSchemaTestConfig.class,
})
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class QuerySeparatedSchemaTests {

  @Autowired
  private AddressDef addressDef;

  @Autowired
  private TicketDef ticketDef;

  @Autowired
  @Qualifier("queryExecutionApi2")
  private SQLQueryExecutionApi sqlQueryExecutionApi;

  @Test
  public void simpleSeparatedSchemaCrudTest() throws Exception {
    sqlQueryExecutionApi.setSchema("BADSCHEMA");
    assertThrows(Exception.class,
        () -> Crud.read(addressDef).selectAllProperties().listData().size());

    sqlQueryExecutionApi.setSchema(null);
    assertThrows(Exception.class,
        () -> Crud.read(addressDef).selectAllProperties().listData().size());

    sqlQueryExecutionApi.setSchema("");
    assertThrows(Exception.class,
        () -> Crud.read(addressDef).selectAllProperties().listData().size());

    sqlQueryExecutionApi.setSchema("test2");
    TableData<AddressDef> addresses = Crud.read(addressDef).selectAllProperties().listData();
    assertEquals(12, addresses.size());

    Crud.create(TableDatas.builder(addressDef)
        .addRow()
        .set(addressDef.id(), 1L)
        .build());

    assertEquals(13, Crud.read(addressDef).selectAllProperties().listData().size());

    Crud.delete(TableDatas.builder(addressDef)
        .addRow()
        .set(addressDef.id(), 1L)
        .build());

    assertEquals(12, Crud.read(addressDef).selectAllProperties().listData().size());

    assertEquals(13, Crud.read(ticketDef).selectAllProperties().listData().size());

    Crud.create(TableDatas.builder(ticketDef)
        .addRow()
        .set(ticketDef.id(), 1L)
        .build());

    assertEquals(14, Crud.read(ticketDef).selectAllProperties().listData().size());

    Crud.delete(TableDatas.builder(ticketDef)
        .addRow()
        .set(ticketDef.id(), 1L)
        .build());

    assertEquals(13, Crud.read(ticketDef).selectAllProperties().listData().size());
  }

}
