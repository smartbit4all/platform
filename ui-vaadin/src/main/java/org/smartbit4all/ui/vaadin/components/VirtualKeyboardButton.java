package org.smartbit4all.ui.vaadin.components;

import java.util.HashMap;
import java.util.Map;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasOrderedComponents;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;

public class VirtualKeyboardButton extends Composite<Button> {

  private static final String CSSCLASS = "virtual-keyboard-button";
  private HasOrderedComponents<Component> layout;
  private VirtualKeyboard virtualKeyboard;
  private Map<String, TextField> textFieldsOnLayout;
  private TextField textField;
  private Label actualLabel = new Label("proba");

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
      virtualKeyboard.setSelectedTextField(null);
      textFieldsOnLayout = getTextFieldsOnlayout(layout);
      if (textFieldsOnLayout.size() == 0) {
        Notification.show("Nincs hova beilleszteni a speciális karaktereket!");
      } else if (textFieldsOnLayout.size() == 1) {
        virtualKeyboard.setSelectedTextField(textFieldsOnLayout.get(textFieldsOnLayout.keySet().toArray()[0]));
        virtualKeyboard.open();
      } else {
        virtualKeyboard.setTextFieldsOnLayout(textFieldsOnLayout);
        virtualKeyboard.open();
      }
    } else {
      virtualKeyboard.setSelectedTextField(textField);
      virtualKeyboard.open();
    }
  }
  
  private void onVKeyboardSave(String value) {
    TextField selectedTextField = virtualKeyboard.getSelectedTextField();
    if (selectedTextField == null) {
      Notification.show("A mentés nem sikerült, kérem válasszon ki egy mezőt!");
    } else {
      virtualKeyboard.getSelectedTextField().setValue(value);
    }
  }
  
  protected Map<String, TextField> getTextFieldsOnlayout(HasOrderedComponents<Component> layout) {
    Map<String, TextField> textFieldsOnLayout = new HashMap<>();
    for (int i = 0; i < layout.getComponentCount(); i++) {
      Component component = layout.getComponentAt(i);
      if (component instanceof TextField) {
        TextField textField = (TextField)component;
        textFieldsOnLayout.put(actualLabel.getText(), textField);
      } else if (component instanceof HasOrderedComponents) {
        textFieldsOnLayout.putAll(getTextFieldsOnlayout((HasOrderedComponents<Component>)component));
      } else if (component instanceof Div) {
        textFieldsOnLayout.putAll(getTextFieldsOnDiv((Div)component));
      } else if (component instanceof Label) {
        Label label = (Label)component;
        if (label.getClassName().equals("filter-name")) {
          actualLabel.setText(label.getText());
        } 
      }
    }
    return textFieldsOnLayout;
  }
  
  private Map<String, TextField> getTextFieldsOnDiv(Div div) {
    Map<String, TextField> textFieldsOnDiv = new HashMap<>();
    div.getChildren().forEach(child -> {
      if (child instanceof TextField) {
        TextField textField = (TextField)child;
        textField.setLabel(actualLabel.getText());
        textFieldsOnDiv.put(actualLabel.getText(), textField);
      } else if (child instanceof HasOrderedComponents) {
        textFieldsOnDiv.putAll(getTextFieldsOnlayout((HasOrderedComponents<Component>)child));
      } else if (child instanceof Div) {
        textFieldsOnDiv.putAll(getTextFieldsOnDiv((Div)child));
      } else if (child instanceof Label) {
        Label label = (Label)child;
        if (label.getClassName().equals("filter-name")) {
          actualLabel.setText(label.getText());
        } 
      }
    });
    return textFieldsOnDiv;
  }

}
