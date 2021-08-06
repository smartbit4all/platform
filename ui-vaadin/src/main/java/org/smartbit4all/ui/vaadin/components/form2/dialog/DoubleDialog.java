package org.smartbit4all.ui.vaadin.components.form2.dialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.ui.api.form.model.WidgetDescriptor;
import org.smartbit4all.ui.api.form.model.WidgetInstance;
import org.smartbit4all.ui.vaadin.components.form2.PredictiveFormInstanceViewUI;
import org.smartbit4all.ui.vaadin.util.Notifications;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

public class DoubleDialog extends Dialog {

private static final Logger log = LoggerFactory.getLogger(DoubleDialog.class);
  
  public DoubleDialog(WidgetInstance instance, PredictiveFormInstanceViewUI ui, WidgetDescriptor descriptor) {
    FlexLayout dialogLayout = new FlexLayout();
    dialogLayout.setClassName("double-dialog-layout");
    add(dialogLayout);
    
    Label titleLabel = new Label(descriptor.getLabel());
    NumberField nfValue = new NumberField();
    nfValue.setHasControls(true);
    nfValue.setStep(0.1);
    
    
    Binder<WidgetInstance> binder = new Binder<>(WidgetInstance.class);
    binder.setBean(instance);
    binder.forField(nfValue).bind(w -> {
      if (instance.getDoubleValues().size() > 0) {
        return instance.getDoubleValues().get(0);
      } else {
        return null;
      }
    }, (w, v) -> {
      w.getDoubleValues().clear();
      w.addDoubleValuesItem(v);
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
    buttonLayout.setClassName("double-dialog-button-layout");
    
    dialogLayout.add(titleLabel, nfValue, buttonLayout);
  }
  
}
