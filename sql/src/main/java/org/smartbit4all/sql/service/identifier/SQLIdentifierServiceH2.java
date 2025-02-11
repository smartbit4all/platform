package org.smartbit4all.sql.service.identifier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.smartbit4all.domain.service.identifier.CurrentIdentifier;
import org.smartbit4all.domain.service.identifier.NextIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;

public class SQLIdentifierServiceH2 extends SQLIdentifierService {

  @Autowired
  @Lazy
  SQLIdentifierServiceH2 self;

  public SQLIdentifierServiceH2(JdbcTemplate jdbcTemplate) {
    super(jdbcTemplate);
  }

  /**
   * The current values could be saved into.
   */
  protected Map<String, AtomicLong> currentValues = new ConcurrentHashMap<>();

  void incrementSequence(String name) {
    AtomicLong atomicLong = currentValues.computeIfAbsent(name, n -> new AtomicLong(0));
    atomicLong.incrementAndGet();
  }

  long getSequence(String name) {
    AtomicLong atomicLong = currentValues.computeIfAbsent(name, n -> new AtomicLong(0));
    return atomicLong.get();
  }

  @Override
  public NextIdentifier next() {
    return new SQLNextIdentifierH2(jdbcTemplate, self);
  }

  @Override
  public CurrentIdentifier current() {
    return new SQLCurrentIdentifierH2(self);
  }

}
