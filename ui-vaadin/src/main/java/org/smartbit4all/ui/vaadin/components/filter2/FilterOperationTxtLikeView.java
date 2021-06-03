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
package org.smartbit4all.ui.vaadin.components.filter2;

import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.utility.PathUtility;
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;
import org.smartbit4all.ui.vaadin.components.binder.VaadinHasValueBinder;
import com.vaadin.flow.component.textfield.TextField;

public class FilterOperationTxtLikeView extends FilterOperationView {

  private TextField textField;
  private VaadinHasValueBinder<String, String> binder;

  public FilterOperationTxtLikeView(ObservableObject filterField, String path) {
    super(filterField, path);
    addClassName("filter-onefield");
    textField = new TextField();
    add(textField);

    binder = VaadinBinders.bind(textField, filterField, PathUtility.concatPath(path, "value1"));
  }

  @Override
  public void unbind() {
    if (binder != null) {
      binder.unbind();
      binder = null;
    }
  }

  @Override
  public void setPlaceholder(String placeHolderText) {
    textField.setPlaceholder(placeHolderText);
  }

}
