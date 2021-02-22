package org.smartbit4all.ui.vaadin.components;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.TextField;

public class VirtualKeyboardButton extends Composite<Button> {
  
  private static final String CSSCLASS = "virtual-keyboard-button";
  private TextField workingTextField;
  private VirtualKeyboard virtualKeyboard;
  
  public VirtualKeyboardButton(VirtualKeyboard virtualKeyboard, TextField workingTextField) {
    this.virtualKeyboard = virtualKeyboard;
    this.workingTextField = workingTextField;
    init();
  }
  
  protected void init() {
    addButtonName("Virtual");
    getContent().addClickListener(click -> clickListener());
  }

  protected void addButtonName(String name) {
    getContent().setText(name);
  }

  public void clickListener() {
    virtualKeyboard.setTextValue(workingTextField.getValue());
    virtualKeyboard.addOnSaveMethod(value -> workingTextField.setValue(value));
    virtualKeyboard.open();
  }

}
