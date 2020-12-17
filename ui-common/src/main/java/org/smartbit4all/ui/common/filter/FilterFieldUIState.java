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
import org.smartbit4all.api.filter.bean.FilterField;
import org.smartbit4all.api.filter.bean.FilterOperation;
import org.smartbit4all.api.value.bean.Value;

public class FilterFieldUIState extends AbstractUIState {

  private String labelCode;

  private FilterField filter;
  private FilterGroupUIState group;
  private boolean isCloseable;
  private FilterLabelPosition position;
  private List<FilterOperation> operations;
  private List<Value> possibleValues;


  public FilterFieldUIState(FilterField filter,
      FilterGroupUIState group,
      FilterLabelPosition position, boolean isCloseable,
      List<FilterOperation> operations) {
    super();
    this.filter = filter;
    this.group = group;
    this.labelCode = filter.getMetaName();
    this.position = position;
    this.isCloseable = isCloseable;
    this.operations = operations;
  }

  public String getLabelCode() {
    return labelCode;
  }

  public Object getGroupId() {
    return group.getId();
  }

  public boolean isCloseable() {
    return isCloseable;
  }

  public FilterGroupUIState getGroup() {
    return group;
  }

  public FilterField getFilter() {
    return filter;
  }

  public FilterLabelPosition getPosition() {
    return position;
  }

  public List<FilterOperation> getOperations() {
    return operations;
  }

  public FilterOperation getSelectedOperation() {
    return filter.getOperation();
  }

  public void setPossibleValues(List<Value> possibleValues) {
    this.possibleValues = possibleValues;
  }

  public List<Value> getPossibleValues() {
    return possibleValues;
  }

  public void setGroup(FilterGroupUIState group) {
    this.group = group;
  }

}
