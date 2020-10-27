package org.smartbit4all.ui.vaadin.components.detailsdrawer;

import java.util.function.Supplier;
import org.smartbit4all.ui.vaadin.components.FlexBoxLayout;
import org.smartbit4all.ui.vaadin.layout.size.Horizontal;
import org.smartbit4all.ui.vaadin.layout.size.Right;
import org.smartbit4all.ui.vaadin.layout.size.Vertical;
import org.smartbit4all.ui.vaadin.util.LumoStyles;
import org.smartbit4all.ui.vaadin.util.UIUtils;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.shared.Registration;

public class DetailsDrawerFooter extends FlexBoxLayout {

  private Button view;
  private Button delete;
  private Button save;
  private Button cancel;

  public DetailsDrawerFooter() {
    setBackgroundColor(LumoStyles.Color.Contrast._5);
    setPadding(Horizontal.RESPONSIVE_L, Vertical.S);
    setSpacing(Right.S);
    setJustifyContentMode(JustifyContentMode.EVENLY);
    setWidthFull();
  }

  public Registration addViewListener(
      ComponentEventListener<ClickEvent<Button>> listener) {
    view = addButton(view, () -> UIUtils.createPrimaryButton(getTranslation("title.view")));
    return view.addClickListener(listener);
  }

  public Registration addDeleteListener(
      ComponentEventListener<ClickEvent<Button>> listener) {
    delete = addButton(delete, () -> UIUtils.createTertiaryButton(getTranslation("title.delete")));
    return delete.addClickListener(listener);
  }

  public Registration addSaveListener(
      ComponentEventListener<ClickEvent<Button>> listener) {
    save = addButton(save, () -> UIUtils.createPrimaryButton(getTranslation("title.save")));
    return save.addClickListener(listener);
  }

  public Registration addCancelListener(
      ComponentEventListener<ClickEvent<Button>> listener) {

    cancel = addButton(cancel, () -> UIUtils.createTertiaryButton(getTranslation("title.cancel")));
    return cancel.addClickListener(listener);
  }

  private Button addButton(Button buttonField, Supplier<Button> buttonFactory) {
    if(buttonField != null) {
      remove(buttonField);
    }
    buttonField = buttonFactory.get();
    add(buttonField);
    return buttonField;
  }
  
}
