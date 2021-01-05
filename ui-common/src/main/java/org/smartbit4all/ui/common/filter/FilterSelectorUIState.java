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

import java.util.List;
import org.smartbit4all.api.filter.bean.FilterFieldMeta;
import org.smartbit4all.api.filter.bean.FilterOperation;

public class FilterSelectorUIState extends AbstractUIState {

  private String labelCode;

  private List<FilterOperation> operations;

  private FilterSelectorGroupUIState group;
  private boolean enabled;

  public FilterSelectorUIState(FilterSelectorGroupUIState group, FilterFieldMeta filterMeta) {
    super();
    this.labelCode = filterMeta.getLabelCode();
    this.operations = filterMeta.getOperations();
    this.group = group;
    this.enabled = true;
  }

  public String getLabelCode() {
    return labelCode;
  }

  public FilterSelectorGroupUIState getGroup() {
    return group;
  }

  public List<FilterOperation> getOperations() {
    return operations;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

}
