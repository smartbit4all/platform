package org.smartbit4all.domain.utility.serialize;

import java.io.File;
import java.io.FileOutputStream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.smartbit4all.domain.config.DomainConfig;
import org.smartbit4all.domain.data.DataRow;
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

    TableData<UserAccountDef> tableData =
        TableDatas.of(userAccountDef, userAccountDef.allProperties());

    TableDataSerializer serializer =
        TableDataSerializer.to(new FileOutputStream(tempFile)).tableData(tableData);

    for (int i = 0; i < 100; i++) {
      DataRow row = serializer.addRow();
      row.set(userAccountDef.id(), Long.valueOf(i));
      row.set(userAccountDef.firstname(), "firstname-" + i);
      row.set(userAccountDef.lastname(), "lastname-" + i);
      row.set(userAccountDef.fullname(), null);
      row.set(userAccountDef.name(), "firstname lastname" + i);
      // row.set(userAccountDef.getUri(), URI.create("users:/user#" + i));
    }

    serializer.finish();

    TableDataPager<UserAccountDef> pager = new TableDataPager<>(UserAccountDef.class, tempFile);

    System.out.println(tempFile.getPath());

  }

}
