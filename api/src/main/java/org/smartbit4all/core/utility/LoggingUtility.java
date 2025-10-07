package org.smartbit4all.core.utility;

import org.slf4j.MDC;
import org.springframework.util.StringUtils;

public class LoggingUtility {
  private LoggingUtility() {
    // NOP
  }


  public static boolean putIntoMDC(String key, String value) {
    if (MDC.get(key) != null) {
      return false;
    }
    if (StringUtils.hasLength(key) && value != null) {
      MDC.put(key, value);
    }
    return true;
  }

  public static void removeFromMDC(String key) {
    if (StringUtils.hasLength(key)) {
      MDC.remove(key);
    }
  }

}
