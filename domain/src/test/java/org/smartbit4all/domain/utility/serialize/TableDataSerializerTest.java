package org.smartbit4all.domain.utility.serialize;

import java.io.File;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.domain.config.DomainConfig;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.TableDatas;
import org.smartbit4all.domain.security.SecurityEntityConfiguration;
import org.smartbit4all.domain.security.UserAccountDef;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TableDataSerializerTest {

  protected static AnnotationConfigApplicationContext ctx;

  @BeforeAll
  static void setup() {
    ctx = new AnnotationConfigApplicationContext();
    ctx.register(DomainConfig.class);
    ctx.register(SecurityEntityConfiguration.class);
    ctx.refresh();
  }

  @AfterAll
  static void tearDown() {
    ctx.close();
  }

  @TempDir
  File tempDir;

  @Test
  public void test01() throws Exception {

    File tempFile = new File(tempDir, "TableDataSerializerTest-test01.temp");

    UserAccountDef userAccountDef = ctx.getBean(UserAccountDef.class);

    // @formatter:off
    TableData<UserAccountDef> tableData = TableDatas.builder(userAccountDef)
        .addRow()
          .set(userAccountDef.firstname(), "Karoly").set(userAccountDef.lastname(), "Nagy")
        .addRow()
          .set(userAccountDef.firstname(), "Robert").set(userAccountDef.lastname(), "Kiss")
        .build();
    // @formatter:on

    BinaryData binaryData = TableDataSerializer.serialize(tableData, tempFile);
    System.out.println(tempFile.getPath());

  }

}
