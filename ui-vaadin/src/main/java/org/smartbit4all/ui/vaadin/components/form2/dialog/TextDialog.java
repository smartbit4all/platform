package org.smartbit4all.ui.vaadin.components.form2.dialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.ui.api.form.model.WidgetInstance;
import org.smartbit4all.ui.vaadin.components.form2.PredictiveFormInstanceViewUI;
import org.smartbit4all.ui.vaadin.util.Notifications;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

public class TextDialog extends Dialog {
  
  private static final Logger log = LoggerFactory.getLogger(TextDialog.class);
  
  public TextDialog(WidgetInstance instance, PredictiveFormInstanceViewUI ui) {
    FlexLayout dialogLayout = new FlexLayout();
    add(dialogLayout);

    TextField tfValue = new TextField();
    
    Binder<WidgetInstance> binder = new Binder<>(WidgetInstance.class);
    binder.forField(tfValue).bind(w -> {
      if (instance.getStringValues() != null && instance.getStringValues().size() > 0) {
        return instance.getStringValues().get(0);
      } else {
        return null;
      }
    }, (w, v) -> {
      if (w.getStringValues() != null) {
        w.getStringValues().set(0, v);
      } else {
        w.addStringValuesItem(v);
      }
    });
    
    Button btnCancel = new Button("Mégsem", e -> {
      if (isOpened()) {
        close();
      }
    });
    Button btnSave = new Button("Mentés", e -> {
      try {
        binder.writeBean(instance);
      } catch (ValidationException e1) {
        String msg = "Bean validation failed!";
        log.error(msg);
        Notifications.showErrorNotification(msg);
      }
      ui.renderWidgets();
      close();
    });
    FlexLayout buttonLayout = new FlexLayout(btnSave, btnCancel);
    
    dialogLayout.add(tfValue, buttonLayout);
  }
}
