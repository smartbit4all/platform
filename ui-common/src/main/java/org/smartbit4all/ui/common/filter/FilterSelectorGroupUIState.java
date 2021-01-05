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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.smartbit4all.api.filter.bean.FilterGroupMeta;
import org.smartbit4all.api.filter.bean.FilterGroupType;

public class FilterSelectorGroupUIState extends AbstractUIState {

  private String labelCode;
  private String iconCode;
  private List<FilterSelectorUIState> filterSelectors = new ArrayList<>();
  private boolean isCloseable;

  FilterGroupUIState currentGroupUIState;

  public FilterSelectorGroupUIState(FilterGroupMeta filterGroupMeta) {
    super();
    this.labelCode = filterGroupMeta.getLabelCode();
    this.iconCode = filterGroupMeta.getIconCode();
    // TODO handle isCloseable
  }

  void addFilterSelector(FilterSelectorUIState filterSelector) {
    filterSelectors.add(filterSelector);
  }

  public String getLabelCode() {
    return labelCode;
  }

  public String getIconCode() {
    return iconCode;
  }

  public boolean isCloseable() {
    return isCloseable;
  }

  public FilterGroupType getType() {
    // TODO should we handle anything else on selector level?
    return FilterGroupType.AND;
  }

  public List<FilterSelectorUIState> filterSelectors() {
    return Collections.unmodifiableList(filterSelectors);
  }
}
