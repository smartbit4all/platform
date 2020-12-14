/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.domain.meta.jdbc;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.smartbit4all.domain.meta.JDBCDataConverter;

/**
 * The interface of the {@link LocalDate} value based types.
 */
public interface JDBCLocalDateTimeSqlDate extends JDBCDataConverter<LocalDateTime, Timestamp> {

  @Override
  default int SQLType() {
    return Types.TIMESTAMP;
  }

  @Override
  default JDBCType bindType() {
    return JDBCType.TIMESTAMP;
  }

  @Override
  default Class<LocalDateTime> appType() {
    return LocalDateTime.class;
  }

  @Override
  default Class<Timestamp> extType() {
    return Timestamp.class;
  }

}
