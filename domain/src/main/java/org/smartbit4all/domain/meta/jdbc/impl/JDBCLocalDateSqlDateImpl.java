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
import java.time.LocalDate;
import org.smartbit4all.domain.meta.jdbc.JDBCLocalDateSqlDate;

/**
 * Default implementation of the {@link Date} value based types.
 * 
 * @author Attila Mate
 */
public class JDBCLocalDateSqlDateImpl implements JDBCLocalDateSqlDate {

  @Override
  public Date app2ext(LocalDate appValue) {
    return appValue == null ? null : Date.valueOf(appValue);
  }

  @Override
  public LocalDate ext2app(Date extValue) {
    return extValue == null ? null : extValue.toLocalDate();
  }

}
