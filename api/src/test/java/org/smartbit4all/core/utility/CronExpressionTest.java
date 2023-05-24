package org.smartbit4all.core.utility;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class CronExpressionTest {

  @Test
  void test() {
    OffsetDateTime now = OffsetDateTime.now();
    OffsetDateTime computeNext = CronExpressionUtility.computeNext("0 9 * * * *", now);
    System.out.println(computeNext);
  }

}
