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

  private FilterField filter;
  private FilterGroupUIState group;
  private FilterSelectorUIState selector;
  private boolean isCloseable;
  private FilterLabelPosition position;
  private FilterOperation selectedOperation;
  private List<Value> possibleValues;
  private boolean isDraggable;

  public FilterFieldUIState(FilterField filter,
      FilterSelectorUIState selector, FilterGroupUIState group,
      FilterLabelPosition position, boolean isCloseable) {
    super();
    this.isDraggable = false;
    this.filter = filter;
    this.group = group;
    this.selector = selector;
    this.position = position;
    this.isCloseable = isCloseable;
  }

  public String getLabelCode() {
    return selector.getLabelCode();
  }

  public Object getSelectorId() {
    return selector.getId();
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
    return selector.getOperations();
  }

  public FilterOperation getSelectedOperation() {
    return selectedOperation;
  }

  public void setSelectedOperation(FilterOperation selectedOperation) {
    this.selectedOperation = selectedOperation;
    filter.setOperationCode(selectedOperation.getOperationCode());
    filter.setPropertyUri1(selectedOperation.getPropertyUri1());
    filter.setPropertyUri2(selectedOperation.getPropertyUri2());
    // TODO default values? maybe value caching for different operations?
    filter.setValue1(null);
    filter.setValue2(null);
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

  public boolean isDraggable() {
    return isDraggable;
  }

  public void setDraggable(boolean isDraggable) {
    this.isDraggable = isDraggable;
  }

}
