package org.smartbit4all.domain.meta.jdbc.impl;

import java.sql.Timestamp;
import org.smartbit4all.domain.meta.jdbc.JDBCSqlTimestamp;

/**
 * Default implementation of the {@link Timestamp} value based types.
 * 
 * @author Attila Mate
 */
public class JDBCSqlTimestampImpl implements JDBCSqlTimestamp {

  @Override
  public Timestamp app2ext(Timestamp appValue) {
    return appValue;
  }

  @Override
  public Timestamp ext2app(Timestamp extValue) {
    return extValue;
  }

}
