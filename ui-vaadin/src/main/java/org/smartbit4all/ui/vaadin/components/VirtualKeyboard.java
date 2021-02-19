package org.smartbit4all.ui.vaadin.components;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextField;

@CssImport("./styles/components/virtual-keyboard.css")
public class VirtualKeyboard extends Composite<Dialog> {

  private static final String CSSCLASS = "virtual-keyboard";
  
  private FlexLayout wrapper;
  
  private TextField workingTextField;
  
  public VirtualKeyboard(List<String>... characters) {
    init();
  }
  
  protected void init() {
    // TODO Auto-generated method stub
    wrapper = new FlexLayout();
    addClassNameToComponent(wrapper, "virtual-keyboard-wrapper");
    Component header = createHeader();
    FlexLayout contentWrapper = new FlexLayout();
    FlexLayout buttonWrapper = new FlexLayout();

    
    wrapper.add(header, contentWrapper, buttonWrapper);
    getContent().add(wrapper);
  }
  
  protected Component createHeader() {
    return new FlexLayout();
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
