package org.smartbit4all.ui.vaadin.components.form2.dialog;

import org.smartbit4all.ui.api.form.model.WidgetDescriptor;
import org.smartbit4all.ui.api.form.model.WidgetInstance;
import org.smartbit4all.ui.common.form2.impl.PredictiveFormController;
import org.smartbit4all.ui.vaadin.components.form2.PredictiveFormInstanceViewUI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;

public class DoubleDialog extends Dialog {

  public DoubleDialog(WidgetInstance instance, PredictiveFormInstanceViewUI ui, WidgetDescriptor descriptor, PredictiveFormController controller) {
    FlexLayout dialogLayout = initDialogLayout(instance, ui, descriptor, controller);
    add(dialogLayout);
  }

  private FlexLayout initDialogLayout(WidgetInstance instance, PredictiveFormInstanceViewUI ui,
      WidgetDescriptor descriptor, PredictiveFormController controller) {
    FlexLayout dialogLayout = new FlexLayout();
    dialogLayout.setClassName("double-dialog-layout");
    setMinWidth("20%");
        
    Label titleLabel = new Label(descriptor.getLabel());
    NumberField nfValue = new NumberField();
    nfValue.setHasControls(true);
    nfValue.setStep(0.5);
    nfValue.setWidthFull();
    
    Binder<WidgetInstance> binder = new Binder<>(WidgetInstance.class);
    binder.setBean(instance);
    binder.forField(nfValue).asRequired("A mező kitöltése kötelező").bind(w -> {
      if (instance.getDoubleValues().size() > 0) {
        return instance.getDoubleValues().get(0);
      } else {
        return null;
      }
    }, (w, v) -> {
      w.getDoubleValues().clear();
      w.addDoubleValuesItem(v);
    });
    
    FlexLayout buttonLayout = WidgetDialogUtil.getButtonLayout(instance, ui, binder, this, controller);
    buttonLayout.setClassName("double-dialog-button-layout");
    
    dialogLayout.add(titleLabel, nfValue, buttonLayout);
    return dialogLayout;
  }
  
  public static FlexLayout getValueLayout(WidgetInstance instance, WidgetDescriptor descriptor) {
    FlexLayout dialogLayout = new FlexLayout();
    dialogLayout.setClassName("double-dialog-layout");
        
    NumberField nfValue = new NumberField();
    nfValue.setHasControls(true);
    nfValue.setStep(0.5);
    nfValue.setWidthFull();
    
    Binder<WidgetInstance> binder = new Binder<>(WidgetInstance.class);
    binder.setBean(instance);
    binder.forField(nfValue).asRequired("A mező kitöltése kötelező").bind(w -> {
      if (instance.getDoubleValues().size() > 0) {
        return instance.getDoubleValues().get(0);
      } else {
        return null;
      }
    }, (w, v) -> {
      w.getDoubleValues().clear();
      w.addDoubleValuesItem(v);
    });
    
    dialogLayout.add(nfValue);
    return dialogLayout;
  }
  
}
