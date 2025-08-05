package org.smartbit4all.api.invocation.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.invocation.ScheduledTestApi;

public class ScheduledTestApiImpl implements ScheduledTestApi {
  private static final Logger log = LoggerFactory.getLogger(ScheduledTestApiImpl.class);


  public static Integer value = 0;

  public static Integer counter = 0;

  @Override
  public void incrementBy(Integer number) {
    int result = value + number;
    log.info("{} + {} = {}", value, number, result);
    value = result;
    counter++;
  }

}
