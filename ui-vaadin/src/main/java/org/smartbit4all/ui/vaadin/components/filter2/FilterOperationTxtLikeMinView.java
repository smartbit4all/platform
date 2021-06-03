package org.smartbit4all.ui.vaadin.components.filter2;

import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.utility.PathUtility;
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;
import org.smartbit4all.ui.vaadin.components.binder.VaadinHasValueBinder;
import com.vaadin.flow.component.textfield.TextField;

public class FilterOperationTxtLikeMinView extends FilterOperationView {


  private TextField textField;
  private VaadinHasValueBinder<String, String> binder;

  public FilterOperationTxtLikeMinView(ObservableObject filterField, String path) {
    super(filterField, path);
    addClassName("filter-onefield");
    addClassName("filter-txt-like-min");
    textField = new TextField();
    textField.setClearButtonVisible(true);
    textField.addClassName("filter-txt-like-min-txt");
    add(textField);

    binder = VaadinBinders.bind(textField, filterField, PathUtility.concatPath(path, "value1"),
        new UpperCaseConverter());
    // TODO min 3 char length
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
