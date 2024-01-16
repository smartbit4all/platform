package org.smartbit4all.core.utility;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import org.springframework.scheduling.support.CronSequenceGenerator;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class CronExpressionUtility {

  private static final Cache<String, CronSequenceGenerator> cache =
      CacheBuilder.newBuilder().build();

  private CronExpressionUtility() {
    super();
  }

  private static final CronSequenceGenerator get(String cronExpression) {
    try {
      return cache.get(cronExpression, () -> new CronSequenceGenerator(cronExpression));
    } catch (ExecutionException e) {
      throw new IllegalArgumentException(
          "Unable to initiate CronSequenceGenerator with the " + cronExpression + " pattern.");
    }
  }

  public static OffsetDateTime computeNext(String cronExpression, OffsetDateTime baseTime) {
    CronSequenceGenerator sequenceGenerator = get(cronExpression);
    Date next = sequenceGenerator.next(Date.from(baseTime.toInstant()));
    return next.toInstant().atOffset(baseTime.getOffset());
  }

  public static boolean isValidExpression(String cronExpression) {
    return CronSequenceGenerator.isValidExpression(cronExpression);
  }


}
