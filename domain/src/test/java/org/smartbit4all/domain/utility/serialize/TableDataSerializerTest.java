package org.smartbit4all.domain.utility.serialize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;
import java.io.FileOutputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.smartbit4all.domain.config.DomainConfig;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.TableDatas;
import org.smartbit4all.domain.security.SecurityEntityConfiguration;
import org.smartbit4all.domain.security.UserAccountDef;
import org.smartbit4all.domain.service.entity.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(
    classes = {
        DomainConfig.class,
        SecurityEntityConfiguration.class
    })
public class TableDataSerializerTest {

  @TempDir
  File tempDir;

  @Autowired
  private UserAccountDef userAccountDef;

  @Autowired
  private EntityManager entityManager;

  @Test
  public void test01() throws Exception {

    File tempFile = new File(tempDir, "TableDataSerializerTest-test01.temp");

    TableData<UserAccountDef> tableData =
        TableDatas.of(userAccountDef, userAccountDef.allProperties());

    TableDataSerializer serializer =
        TableDataSerializer.to(new FileOutputStream(tempFile)).tableData(tableData);

    final int testRowCount = 100;
    for (int i = 0; i < testRowCount; i++) {
      DataRow row = serializer.addRow();
      row.set(userAccountDef.id(), Long.valueOf(i));
      row.set(userAccountDef.firstname(), "firstname-" + i);
      row.set(userAccountDef.lastname(), "lastname-" + i);
      row.set(userAccountDef.fullname(), null);
      row.set(userAccountDef.name(), "firstname lastname" + i);
      // row.set(userAccountDef.getUri(), URI.create("users:/user#" + i));
    }

    serializer.finish();

    TableDataPager<UserAccountDef> pager =
        TableDataPager.create(UserAccountDef.class, tempFile, entityManager);

    int totalRowCount = pager.getTotalRowCount();
    assertEquals(testRowCount, totalRowCount);

    TableData<UserAccountDef> fetchedData = pager.fetch(0, 50);
    assertEquals(50, fetchedData.size());
    assertEquals(Long.valueOf(0l), fetchedData.rows().get(0).get(userAccountDef.id()));
    assertEquals(Long.valueOf(49l), fetchedData.rows().get(49).get(userAccountDef.id()));

    fetchedData = pager.fetch(10, 15);
    assertEquals(15, fetchedData.size());
    assertEquals(Long.valueOf(10l), fetchedData.rows().get(0).get(userAccountDef.id()));
    assertEquals(Long.valueOf(24l), fetchedData.rows().get(14).get(userAccountDef.id()));
    final DataRow row = fetchedData.rows().get(0);

    // fetching the same page returns the same result
    fetchedData = pager.fetch(10, 15);
    assertTrue(row == fetchedData.rows().get(0)); // using == operator is intentional here

    fetchedData = pager.fetch(90, 105);
    assertEquals(10, fetchedData.size());
    assertEquals(Long.valueOf(90l), fetchedData.rows().get(0).get(userAccountDef.id()));
    assertEquals(Long.valueOf(99l), fetchedData.rows().get(9).get(userAccountDef.id()));

    fetchedData = pager.fetch(105, 1000);
    assertEquals(0, fetchedData.size());

    pager.close();

  }

}
