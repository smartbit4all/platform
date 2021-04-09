package org.smartbit4all.ui.vaadin.components.binder;

import java.util.function.Consumer;
import org.smartbit4all.api.object.ObjectEditing;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

public class VaadinBinders {

  public static <S extends ObjectEditing> VaadinButtonBinder bind(Button button, S editing,
      Consumer<S> function) {
    return new VaadinButtonBinder(button, editing, function);
  }

  public static VaadinTextFieldBinder bind(TextField textField, ObjectEditing editing,
      String path) {
    return new VaadinTextFieldBinder(textField, editing, path);
  }

  public static VaadinNumberFieldBinder bind(NumberField numberField, ObjectEditing editing,
      String path) {
    return new VaadinNumberFieldBinder(numberField, editing, path);
  }

  public static VaadinIntegerFieldBinder bind(IntegerField integerField, ObjectEditing editing,
      String path) {
    return new VaadinIntegerFieldBinder(integerField, editing, path);
  }

  public static void bindLabel(HasText label, ObjectEditing editing,
      String path) {
    new VaadinHasTextBinder(label, editing, path);
  }

  public static <T> VaadinComboBoxBinder<T> bind(ComboBox<T> comboBox,
      ObjectEditing editing,
      String path) {
    return new VaadinComboBoxBinder<>(comboBox, editing, path);
  }
}
