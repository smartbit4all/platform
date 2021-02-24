package org.smartbit4all.ui.vaadin.components;

import java.util.ArrayList;
import java.util.List;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasOrderedComponents;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.TextField;

public class VirtualKeyboardButton extends Composite<Button> {

  private static final String CSSCLASS = "virtual-keyboard-button";
  private HasOrderedComponents<Component> layout;
  private VirtualKeyboard virtualKeyboard;
  private List<TextField> textFieldsOnLayout;
  private TextField textField;

  public VirtualKeyboardButton(VirtualKeyboard virtualKeyboard, HasOrderedComponents<Component> layout) {
    this.virtualKeyboard = virtualKeyboard;
    this.layout = layout;
    init();
  }
  
  public VirtualKeyboardButton(VirtualKeyboard virtualKeyboard, TextField textField) {
    this.virtualKeyboard = virtualKeyboard;
    this.textField = textField;
    init();
  }

  protected void init() {
    addButtonName("Virtual");
    getContent().addClickListener(click -> clickListener());
    virtualKeyboard.addOnSaveMethod(value -> onVKeyboardSave(value));
  }

  protected void addButtonName(String name) {
    getContent().setText(name);
  }

  public void clickListener() {
    if (textField == null) {
      textFieldsOnLayout = getTextFieldsOnlayout(layout);
      virtualKeyboard.setTextFieldsOnLayout(textFieldsOnLayout);
    } else {
      virtualKeyboard.setSelectedTextField(textField);
    }
    virtualKeyboard.open();
  }
  
  private void onVKeyboardSave(String value) {
    virtualKeyboard.getSelectedTextField().setValue(value);
  }
  
  protected List<TextField> getTextFieldsOnlayout(HasOrderedComponents<Component> layout) {
    ArrayList<TextField> textFieldsOnLayout = new ArrayList<>();
    for (int i = 0; i < layout.getComponentCount(); i++) {
      Component component = layout.getComponentAt(i);
      if (component instanceof TextField) {
        textFieldsOnLayout.add((TextField) component);
      } else if (component instanceof HasOrderedComponents) {
        textFieldsOnLayout.addAll(getTextFieldsOnlayout((HasOrderedComponents<Component>)component));
      }
    }
    return textFieldsOnLayout;
  }

}
