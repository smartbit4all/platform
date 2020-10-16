package org.smartbit4all.domain.meta.jdbc.impl;

import java.sql.Time;
import org.smartbit4all.domain.meta.jdbc.JDBCSqlTime;

/**
 * Default implementation of the {@link Time} value based types.
 * 
 * @author Attila Mate
 */
public class JDBCSqlTimeImpl implements JDBCSqlTime {

  @Override
  public Time app2ext(Time appValue) {
    return appValue;
  }

  @Override
  public Time ext2app(Time extValue) {
    return extValue;
  }

}
