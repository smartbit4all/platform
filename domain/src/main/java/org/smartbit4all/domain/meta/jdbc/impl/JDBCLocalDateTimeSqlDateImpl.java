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
package org.smartbit4all.domain.meta.jdbc.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.smartbit4all.domain.meta.jdbc.JDBCLocalDateTimeSqlDate;

/**
 * Default implementation of the {@link Date} value based types.
 * 
 */
public class JDBCLocalDateTimeSqlDateImpl implements JDBCLocalDateTimeSqlDate {

  @Override
  public Timestamp app2ext(LocalDateTime appValue) {
    return appValue == null ? null : Timestamp.valueOf(appValue);
  }

  @Override
  public LocalDateTime ext2app(Timestamp extValue) {
    return extValue == null ? null : extValue.toLocalDateTime();
  }

}
