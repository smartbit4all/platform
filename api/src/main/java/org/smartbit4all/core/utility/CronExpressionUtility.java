package org.smartbit4all.core.utility;

import java.time.OffsetDateTime;
import java.util.Date;
import org.springframework.scheduling.support.CronSequenceGenerator;

public class CronExpressionUtility {

  private CronExpressionUtility() {
    super();
  }

  public static OffsetDateTime computeNext(String cronExpression, OffsetDateTime baseTime) {
    CronSequenceGenerator sequenceGenerator =
        new CronSequenceGenerator(cronExpression);
    Date next = sequenceGenerator.next(Date.from(baseTime.toInstant()));
    return next.toInstant().atOffset(baseTime.getOffset());
  }

  public static boolean isValidExpression(String cronExpression) {
    return CronSequenceGenerator.isValidExpression(cronExpression);
  }


}
