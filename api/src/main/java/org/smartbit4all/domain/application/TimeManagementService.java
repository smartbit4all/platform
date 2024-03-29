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
package org.smartbit4all.domain.application;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * The time management is responsible for managing the timing of the application and can access the
 * database time with the {@link #getSystemTime()} function.
 * 
 * @author Peter Boros
 */
public interface TimeManagementService {

  /**
   * Returns the system time synchronized with the database.
   * 
   * @return
   */
  LocalDateTime getSystemTime();

  /**
   * @return The synchronized Clock in the application.
   */
  Clock getSynchronizedClock();

  /**
   * Do the synchronization of the system clock.
   */
  void synchnronizeClock(Clock synchronizedClock);

}
