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
import com.vaadin.flow.component.textfield.TextField;

public class FilterOperationOneFieldUI extends FilterOperationUI {

  private TextField textField;

  public FilterOperationOneFieldUI() {
    addClassName("filter-onefield");
    textField = new TextField();
    add(textField);
  }

  @Override
  public void setPlaceholder(String placeHolderText) {
    textField.setPlaceholder(placeHolderText);
  }

  @Override
  public void setValues(FilterOperandValue value1, FilterOperandValue value2,
      FilterOperandValue value3) {
    if (value1 == null || value1.getValue() == null) {
      textField.setValue(null);
      return;
    }
    textField.setValue(value1.getValue());
  }

  @Override
  public void setSelection(List<URI> list) {
    // TODO Auto-generated method stub

  }
}
