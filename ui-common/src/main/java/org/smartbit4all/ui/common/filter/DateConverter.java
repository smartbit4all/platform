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
package org.smartbit4all.ui.common.filter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Converts the String given by LocalDateTime's or LocalDate's toString() method to a LocalDateTime
 * or LocalDate.
 * 
 * @author Zsombor Nyilas
 *
 */
public class DateConverter {

  public static LocalDateTime getDateTime(String dateTimeString) {
    String[] dateTimeParts = dateTimeString.split("T");
    return LocalDateTime.of(getDate(dateTimeParts[0]), getTime(dateTimeParts[1]));
  }

  public static LocalDate getDate(String dateString) {
    String[] dateParts = dateString.split("-");
    LocalDate localDate =
        LocalDate.of(Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]),
            Integer.parseInt(dateParts[2].substring(0, 2)));
    return localDate;
  }

  private static LocalTime getTime(String timeString) {
    String[] timeParts = timeString.split(":");
    return LocalTime.of(Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]));
  }

}
