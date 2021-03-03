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

public enum TimeFilterOption {

  LAST_WEEK("filter.time.last_week"),
  THIS_MONTH("filter.time.this_month"),
  LAST_MONTH("filter.time.last_month"),
  YESTERDAY("filter.time.yesterday"),
  TODAY("filter.time.today"),
  LAST_FIVE_YEARS("filter.time.lastfiveyears"),
  OTHER("filter.time.other");

  private String label;

  private TimeFilterOption(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
