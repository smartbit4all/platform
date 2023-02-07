package org.smartbit4all.api.collection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.smartbit4all.api.databasedefinition.bean.AlterOperation;
import org.smartbit4all.api.databasedefinition.bean.ColumnDefinition;
import org.smartbit4all.api.databasedefinition.bean.ColumnTypeDefinition;
import org.smartbit4all.api.databasedefinition.bean.DatabaseDefinition;
import org.smartbit4all.api.databasedefinition.bean.DatabaseKind;
import org.smartbit4all.api.databasedefinition.bean.TableDefinition;
import org.smartbit4all.api.databasedefinition.bean.ColumnTypeDefinition.BaseTypeEnum;
import org.smartbit4all.api.rdbms.DatabaseDefinitionApi;
import org.smartbit4all.api.rdbms.DatabaseRendition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {
    CollectionTestConfig.class
})
@TestInstance(Lifecycle.PER_CLASS)
class DatabaseDefinitionApiTest {

  @Autowired
  private DatabaseDefinitionApi databaseDefinitionApi;

  @Test
  void test() {
    DatabaseRendition rendition =
        databaseDefinitionApi.render(new DatabaseDefinition().databaseKind(DatabaseKind.ORACLE)
            .addTablesItem(new TableDefinition().name("APPLE").operation(AlterOperation.CREATE)
                .addColumnsItem(new ColumnDefinition().name("NAME").typeDefinition(
                    new ColumnTypeDefinition().baseType(BaseTypeEnum.VARCHAR).length(10)))));
    System.out.println(rendition.getRootStatement());
    System.out.println(rendition.getScript());
  }

}
