package org.smartbit4all.ui.vaadin.components.binder;

import java.util.function.BiConsumer;
import org.smartbit4all.ui.common.filter.AbstractUIState;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

public class VaadinBinders {

  public static void bind(Button button, AbstractUIState uiState, String opertation) {
    new VaadinButtonBinder(button, uiState, opertation);
  }

  public static <S extends AbstractUIState> VaadinTextFieldBinder<S> bind(TextField textField,
      S uiState, BiConsumer<S, String> setter, String propertyName) {
    return new VaadinTextFieldBinder(textField, uiState, setter, propertyName);
  }

  public static <S extends AbstractUIState> VaadinNumberFieldBinder<S> bind(
      NumberField numberField, S uiState, BiConsumer<S, Long> setter, String propertyName) {
    return new VaadinNumberFieldBinder(numberField, uiState, setter, propertyName);
  }

  public static <S extends AbstractUIState> VaadinIntegerFieldBinder<S> bind(
      IntegerField integerField, S uiState, BiConsumer<S, Integer> setter, String propertyName) {
    return new VaadinIntegerFieldBinder(integerField, uiState, setter, propertyName);
  }

  public static void bind(Label label, AbstractUIState uiState, String opertation) {
    new VaadinLabelBinder(label, uiState, opertation);
  }

  public static <S extends AbstractUIState, T> VaadinComboBoxBinder<S, T> bind(
      ComboBox<T> comboBox, S uiState, BiConsumer<S, T> setter, String propertyName) {
    return new VaadinComboBoxBinder<S, T>(comboBox, uiState, setter, propertyName);
  }
}
