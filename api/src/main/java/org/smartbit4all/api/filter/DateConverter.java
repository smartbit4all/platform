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
package org.smartbit4all.api.filter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Converts the String given by LocalDateTime's or LocalDate's toString() method to a LocalDateTime
 * or LocalDate.
 * 
 * TODO use generic data converter based on exact formatting
 * 
 * @author Zsombor Nyilas
 *
 */
public class DateConverter {

  public static final String PREFIX_STRING = "java.time.String;";
  public static final String PREFIX_DATE = "java.time.LocalDate;";
  public static final String PREFIX_DATETIME = "java.time.LocalDateTime;";

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

  public static LocalDate getDateWithType(String dateString) {
    if (dateString.startsWith(PREFIX_DATE)) {
      return getDate(dateString.substring(PREFIX_DATE.length()));
    } else if (dateString.startsWith(PREFIX_DATETIME)) {
      LocalDateTime dateTime = getDateTime(dateString.substring(PREFIX_DATETIME.length()));
      return LocalDate.from(dateTime);
    }
    return null;
  }

  public static LocalDateTime getDateTimeWithType(String dateString) {
    if (dateString.startsWith(PREFIX_DATETIME)) {
      return getDateTime(dateString.substring(PREFIX_DATETIME.length()));
    } else if (dateString.startsWith(PREFIX_DATE)) {
      LocalDate date = getDate(dateString.substring(PREFIX_DATE.length()));
      return LocalDateTime.of(date, LocalTime.of(12, 0));
    }
    return null;
  }

}
