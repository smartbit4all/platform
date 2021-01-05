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
package org.smartbit4all.ui.vaadin.components.filter;

import java.net.URI;
import java.util.List;
import org.smartbit4all.api.filter.bean.FilterOperandValue;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public abstract class FilterOperationUI extends FlexLayout {

  FilterFieldUI filterFieldUi;

  public abstract void setPlaceholder(String placeHolderText);

  public abstract void setValues(FilterOperandValue value1, FilterOperandValue value2,
      FilterOperandValue value3);

  public abstract void setSelection(List<URI> list);

  public void setFilterFieldUI(FilterFieldUI filterFieldUI) {
    this.filterFieldUi = filterFieldUI;
  }

  public void valueChanged(String filterId, FilterOperandValue value1, FilterOperandValue value2,
      FilterOperandValue value3) {
    filterFieldUi.getController().filterValueChanged(filterId, value1, value2, value3);
  }

  public void selectionChanged(String filterId, List<URI> values) {
    filterFieldUi.getController().filterSelectionChanged(filterId, values);
  }

  public String getFilterId() {
    return filterFieldUi.getFilterId();
  }


}
