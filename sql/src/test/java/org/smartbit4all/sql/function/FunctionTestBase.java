package org.smartbit4all.sql.function;

import org.smartbit4all.sql.testmodel.AddressDef;
import org.smartbit4all.sql.testmodel.PersonDef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(classes = {
    PropertyFunctionTestConfig.class,
})
@Sql({"/script/exists_schema.sql", "/script/exists_data_01.sql"})
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public abstract class FunctionTestBase {

  @Autowired
  protected AddressDef addressDef;
  
  @Autowired
  protected PersonDef personDef;
  
}
