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
package org.smartbit4all.sql.application;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.smartbit4all.core.utility.concurrent.FutureValue;
import org.smartbit4all.sql.config.SQLDBParameter;
import org.smartbit4all.sql.config.SQLDBParameterBase;
import org.smartbit4all.sql.config.SQLConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * TODO Add {@link FutureValue} to access the time after the first successful time query!
 * 
 * @author Peter Boros
 */
public class TimeManagementServiceImpl
    extends org.smartbit4all.domain.application.TimeManagementServiceImpl {

  @Autowired
  JdbcTemplate jdbcTemplate;

  @Autowired
  private SQLConfig sqlConfig;

  @Override
  @Scheduled(initialDelay = 5000, fixedRate = 60000)
  public void synchnronizeClock() {
    // TODO Define the proper SQLDBParameter! The default is a good option, but think it over again!
    SQLDBParameter db = sqlConfig.db(SQLDBParameterBase.DEFAULT);
    Timestamp tsSysdate = jdbcTemplate.queryForObject(db.getDatetimeSQL(), Timestamp.class);
    LocalDateTime sysdate =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(tsSysdate.getTime()), ZoneId.systemDefault());;
    LocalDateTime now = LocalDateTime.now();
    // If there is a difference between the two clock then we initiate a new clock.
    Duration between = Duration.between(now, sysdate);
    Clock newClock = Clock.offset(Clock.systemDefaultZone(), between);
    synchronizedClock = newClock;
  }

}
