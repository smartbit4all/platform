package org.smartbit4all.ui.vaadin.components;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

@CssImport("./styles/components/virtual-keyboard.css")
public class VirtualKeyboard extends Composite<Dialog> {

  private static final String CSSCLASS = "virtual-keyboard";
  
  private FlexLayout wrapper;
  
  private TextField workingTextField;
  
  private List<String>[] characters;
  
  public VirtualKeyboard(List<String>... characters) {
    this.characters = characters;
    init();
  }
  
  protected void init() {
    wrapper = new FlexLayout();
    addClassNameToComponent(wrapper, "virtual-keyboard-wrapper");
    workingTextField = new TextField();
    addClassNameToComponent(workingTextField, "virtual-keyboard-working-textfield");
    Component header = createHeader();
    FlexLayout contentWrapper = createContentWrapper();
    FlexLayout buttonWrapper = createButtonWrapper();
    
    wrapper.add(header, contentWrapper, buttonWrapper);
    getContent().add(wrapper);
  }

  protected Component createHeader() {
    FlexLayout header = new FlexLayout();
    header.add(workingTextField);
    return header;
  }
  
  protected FlexLayout createContentWrapper() {
    FlexLayout contentWrapper = new FlexLayout();
    for (List<String> characterList : characters) {
      VerticalLayout characterListLayout = new VerticalLayout();
      for (String character : characterList) {
        Button characterButton = new Button(character);
        characterButton.addClickListener(click -> workingTextField.setValue(workingTextField.getValue() + character));
        characterListLayout.add(characterButton);
      }
      contentWrapper.add(characterListLayout);
    }
    return contentWrapper;
  }
  
  protected FlexLayout createButtonWrapper() {
    FlexLayout buttonWrapper = new FlexLayout();
    
    Button okButton = new Button("OK");
    okButton.addClickListener(click -> save());
    Button cancelButton = new Button("Mégse");
    cancelButton.addClickListener(click -> close());
    
    HorizontalLayout buttonLayout = new HorizontalLayout();
    addClassNameToComponent(buttonLayout, "virtual-keyboard-buttonlayout");
    buttonLayout.add(okButton, cancelButton);
    buttonWrapper.add(buttonLayout);
    return buttonWrapper;
  }

  public void open() {
    getContent().open();
  }
  
  public void close() {
    getContent().close();
  }
  
  public void setTextValue(String textValue) {
    workingTextField.setValue(textValue);
  }
  
  List<Consumer<String>> onSaveMethods = new ArrayList<>();
  
  public void addOnSaveMethod(Consumer<String> onSaveConsumer) {
    onSaveMethods.add(onSaveConsumer);
  }
  
  private void save() {
    onSaveMethods.forEach(m -> m.accept(workingTextField.getValue()));
    this.close();
  }
  
  private void addClassNameToComponent(HasStyle hasStyle, String className) {
    hasStyle.addClassName(CSSCLASS);
    hasStyle.addClassName(className);
  }
  
}
