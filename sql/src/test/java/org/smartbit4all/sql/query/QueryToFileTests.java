package org.smartbit4all.sql.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.service.entity.EntityManager;
import org.smartbit4all.domain.utility.crud.Crud;
import org.smartbit4all.domain.utility.serialize.TableDataPager;
import org.smartbit4all.sql.testmodel.AddressDef;
import org.smartbit4all.sql.testmodel.TicketDef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(classes = {
    QueryToFileTestConfig.class,
})
@Sql({"/script/exists_schema.sql", "/script/exists_data_01.sql"})
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class QueryToFileTests {

  @TempDir
  File tempDir;

  @Autowired
  private AddressDef addressDef;

  @Autowired
  private TicketDef ticketDef;

  @Autowired
  private EntityManager entityManager;

  @Test
  public void test01() throws Exception {

    TableDataPager<AddressDef> addressPager = Crud.read(addressDef)
        .selectAllProperties()
        .pageData(AddressDef.class, entityManager);

    TableData<AddressDef> fetchedTableData1 = addressPager.fetch(0, 3);

    assertEquals(3, fetchedTableData1.size());

    TableData<AddressDef> fetchedTableData2 = addressPager.fetch(0, 20);

    assertEquals(12, fetchedTableData2.size());
  }

}
