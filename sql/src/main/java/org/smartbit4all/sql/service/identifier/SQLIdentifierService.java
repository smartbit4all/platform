/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.sql.service.identifier;

import java.util.Objects;
import java.util.function.Supplier;
import org.smartbit4all.domain.service.identifier.IdentifierService;
import org.smartbit4all.domain.service.identifier.NextIdentifier;
import org.springframework.jdbc.core.JdbcTemplate;

public class SQLIdentifierService implements IdentifierService {

  protected JdbcTemplate jdbcTemplate;
  protected Supplier<NextIdentifier> nextIdentifier;

  /**
   * Creates a new instance backed by the Oracle based {@link SQLNextIdentifierOracle}.
   * 
   * @param jdbcTemplate the {@link JdbcTemplate} used access the database, not null
   */
  public SQLIdentifierService(JdbcTemplate jdbcTemplate) {
    this(jdbcTemplate, () -> new SQLNextIdentifierOracle(jdbcTemplate));
  }

  /**
   * Creates a new instance backed by a custom {@link NextIdentifier}.
   * 
   * @param jdbcTemplate the {@link JdbcTemplate} used access the database, not null
   * @param nextIdentifier a {@code Supplier} to be used to acquire fresh {@link NextIdentifier}
   *        {@code SB4Function}s
   */
  public SQLIdentifierService(JdbcTemplate jdbcTemplate, Supplier<NextIdentifier> nextIdentifier) {
    super();
    this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate, "jdbcTemplate cannot be null!");
    this.nextIdentifier = Objects.requireNonNull(nextIdentifier, "nextIdentifier cannot be null!");
  }

  @Override
  public NextIdentifier next() {
    return nextIdentifier.get();
  }

}
