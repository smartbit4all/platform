package org.smartbit4all.core.utility;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.SimpleTimeZone;
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

  private static final CronSequenceGenerator get(String cronExpression, ZoneOffset offset) {
    // fyi: by default the CronSequenceGenerator uses the default
    // TimeZone which handles day light saving causing calculation errors when parsing to
    // OffsetDateTime. To avoid this, we explicitly set the TimeZone based on a given offset.
    try {
      return cache.get(cronExpression + offset.getTotalSeconds(),
          () -> new CronSequenceGenerator(cronExpression,
              new SimpleTimeZone(offset.getTotalSeconds() * 1000, "cronTimeZone")));
    } catch (ExecutionException e) {
      throw new IllegalArgumentException(
          "Unable to initiate CronSequenceGenerator with the " + cronExpression + " pattern.");
    }
  }

  public static OffsetDateTime computeNext(String cronExpression, OffsetDateTime baseTime) {
    CronSequenceGenerator sequenceGenerator = get(cronExpression, baseTime.getOffset());
    Date next = sequenceGenerator.next(Date.from(baseTime.toInstant()));
    return next.toInstant().atOffset(baseTime.getOffset());
  }

  public static boolean isValidExpression(String cronExpression) {
    return CronSequenceGenerator.isValidExpression(cronExpression);
  }


}
