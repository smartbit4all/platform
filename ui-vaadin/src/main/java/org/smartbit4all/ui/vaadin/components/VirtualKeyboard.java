package org.smartbit4all.ui.vaadin.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

@CssImport("./styles/components/virtual-keyboard.css")
public class VirtualKeyboard extends Composite<Dialog> {

  private static final String CSSCLASS = "virtual-keyboard";

  private FlexLayout wrapper;

  private TextField workingTextField;
  private TextField transliterationTextField;
  private TextField selectedTextField;
  private ComboBox<TextField> cbxTextFieldsOnLayout;

  private Map<String, String>[] characters;

  private Dialog savingDialog;

  public VirtualKeyboard(Map<String, String>... characters) {
    this.characters = characters;
    init();
  }

  protected void init() {
    wrapper = new FlexLayout();
    addClassNameToComponent(wrapper, "virtual-keyboard-wrapper");
    
    workingTextField = new TextField();
    addClassNameToComponent(workingTextField, "virtual-keyboard-working-textfield");
    workingTextField.setReadOnly(true);
    
    transliterationTextField = new TextField();
    addClassNameToComponent(transliterationTextField, "virtual-keyboard-working-textfield");
    transliterationTextField.setValueChangeMode(ValueChangeMode.EAGER);
    transliterationTextField.addValueChangeListener(value -> transliterationValueChanged(value.getOldValue(), value.getValue(), value.isFromClient()));
    
    cbxTextFieldsOnLayout = new ComboBox<>();
    savingDialog = initSavingDialog();

    Component header = createHeader();
    FlexLayout contentWrapper = createContentWrapper();
    FlexLayout buttonWrapper = createButtonWrapper();

    wrapper.add(header, contentWrapper, buttonWrapper);
    getContent().add(wrapper);
    getContent().addDialogCloseActionListener(closeAction -> close());
  }

  private void transliterationValueChanged(String oldValue, String newValue, boolean isFromClient) {
    if (isFromClient && newValue.length() < oldValue.length()) {
      String oldWorkingTextFieldValue = workingTextField.getValue();
      String stringToBeDeleted = oldWorkingTextFieldValue.substring(oldWorkingTextFieldValue.length() - 1);
      workingTextField.setValue(oldWorkingTextFieldValue.substring(0, oldWorkingTextFieldValue.length() - 1));
      
      for (Map<String, String> characterMap : characters) {
        for (Map.Entry<String, String> mapElement : characterMap.entrySet()) {
          if (mapElement.getKey().equals(stringToBeDeleted)) {
            String valueToBeRemoved = mapElement.getValue();
            String remainingValue = oldValue.substring(0, oldValue.length() - valueToBeRemoved.length());
            transliterationTextField.setValue(remainingValue);
          }
        }
      }    
    }
    
    if (isFromClient && newValue.length() > oldValue.length()) {
      String addedString = newValue.substring(oldValue.length(), newValue.length());
      workingTextField.setValue(workingTextField.getValue() + addedString);
    }
  }

  private Dialog initSavingDialog() {
    Dialog dialog = new Dialog();
    Label selectLabel =
        new Label("Kérem válassza ki a kívánt mezőt, amibe kerüljön a megadott szöveg!");
    Button saveButton = new Button("Mentés");
    Button cancelButton = new Button("Mégse");

    FlexLayout savingDialogLayout = new FlexLayout();
    addClassNameToComponent(savingDialogLayout, "virtual-keyboard-savingdialog-layout");
    HorizontalLayout buttonLayout = new HorizontalLayout();
    buttonLayout.add(saveButton, cancelButton);
    savingDialogLayout.add(selectLabel, cbxTextFieldsOnLayout, buttonLayout);
    dialog.add(savingDialogLayout);

    saveButton.addClickListener(save -> {
      onSaveMethods.forEach(m -> m.accept(transliterationTextField.getValue()));
      cbxTextFieldsOnLayout.setValue(null);
      dialog.close();
    });
    cancelButton.addClickListener(cancel -> {
      cbxTextFieldsOnLayout.setValue(null);
      dialog.close();
    });
    return dialog;
  }

  protected Component createHeader() {
    FlexLayout header = new FlexLayout();
    addClassNameToComponent(header, "virtual-keyboard-header");
    VerticalLayout headerLayout = new VerticalLayout(workingTextField, transliterationTextField);
    header.add(headerLayout);
    return header;
  }

  protected FlexLayout createContentWrapper() {
    FlexLayout contentWrapper = new FlexLayout();

    for (Map<String, String> characterMap : characters) {
      FlexLayout characterListLayout = new FlexLayout();
      addClassNameToComponent(characterListLayout, "character-list-layout");
      for (Map.Entry<String, String> mapElement : characterMap.entrySet()) {
        Button characterButton = new Button(mapElement.getKey());
        characterButton.addClickListener(click -> {
          workingTextField.setValue(workingTextField.getValue() + mapElement.getKey());
          transliterationTextField.setValue(transliterationTextField.getValue() + mapElement.getValue());
        });
        characterListLayout.add(characterButton);
      }
      contentWrapper.add(characterListLayout);
    }
    return contentWrapper;
  }

  protected FlexLayout createButtonWrapper() {
    FlexLayout buttonWrapper = new FlexLayout();

    Button saveButton = new Button("Mentés");
    saveButton.addClickListener(click -> save());
    Button cancelButton = new Button("Mégse");
    cancelButton.addClickListener(click -> close());

    HorizontalLayout buttonLayout = new HorizontalLayout();
    addClassNameToComponent(buttonLayout, "virtual-keyboard-buttonlayout");
    buttonLayout.add(saveButton, cancelButton);
    buttonWrapper.add(buttonLayout);
    return buttonWrapper;
  }

  public void open() {
    getContent().open();
  }

  public void close() {
    workingTextField.setValue("");
    transliterationTextField.setValue("");
    getContent().close();
  }

  List<Consumer<String>> onSaveMethods = new ArrayList<>();

  public void addOnSaveMethod(Consumer<String> onSaveConsumer) {
    onSaveMethods.add(onSaveConsumer);
  }

  private void save() {
    if (selectedTextField == null) {
      savingDialog.open();
    } else {
      onSaveMethods.forEach(m -> m.accept(transliterationTextField.getValue()));
      close();
    }
  }

  private void addClassNameToComponent(HasStyle hasStyle, String className) {
    hasStyle.addClassName(CSSCLASS);
    hasStyle.addClassName(className);
  }

  public void setTextFieldsOnLayout(List<TextField> textFieldsOnLayout) {
    cbxTextFieldsOnLayout.setItems(textFieldsOnLayout);
    cbxTextFieldsOnLayout.setItemLabelGenerator(TextField::getLabel);
    cbxTextFieldsOnLayout.addValueChangeListener(select -> setSelectedTextField(select.getValue()));
  }

  public TextField getSelectedTextField() {
    return selectedTextField;
  }

  public void setSelectedTextField(TextField selectedTextField) {
    this.selectedTextField = selectedTextField;
  }
}
