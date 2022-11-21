package org.smartbit4all.core.utility;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

public class TimeUtils {

  public static Double millisToHours(Long timeInMillis) {
    if (timeInMillis == null) {
      return 0.0;
    }

    Double result = Double.valueOf(timeInMillis.doubleValue() / 3600000);
    BigDecimal bigDecimal = new BigDecimal(Double.toString(result));
    bigDecimal = bigDecimal.setScale(2, 0);

    return bigDecimal.doubleValue();
  }

  public static Long hoursToMillis(Double timeInHours) {
    if (timeInHours == null) {
      return 0L;
    }
    return Double.valueOf(timeInHours * 3600000).longValue();
  }

  public static Long calculateTimeSpentInMillis(LocalDateTime startTime, LocalDateTime endTime) {
    Duration duration = Duration.between(startTime, endTime);

    return duration.toMillis();
  }

  public static String makeStringFromDuration(Duration duration) {
    long seconds = duration.getSeconds();
    String time = String.format("%d:%02d:%02d:%02d",
        seconds / 28800,
        (seconds % 28800) / 3600,
        (seconds % 3600) / 60,
        seconds % 60);

    return time;
  }

}
