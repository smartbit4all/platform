package org.smartbit4all.ui.vaadin.components.filter2;

import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.utility.PathUtility;
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;
import com.vaadin.flow.component.textfield.TextField;

public class FilterOperationTxtLikeMinView extends FilterOperationView {


  private TextField textField;

  public FilterOperationTxtLikeMinView(ObservableObject filterField, String path) {
    addClassName("filter-onefield");
    addClassName("filter-txt-like-min");
    textField = new TextField();
    textField.setClearButtonVisible(true);
    textField.addClassName("filter-txt-like-min-txt");
    VaadinBinders.bind(textField, filterField, PathUtility.concatPath(path, "value1"),
        new UpperCaseConverter());
    // TODO min 3 char lenght

    add(textField);
  }

  @Override
  public void setPlaceholder(String placeHolderText) {
    textField.setPlaceholder(placeHolderText);
  }



}
