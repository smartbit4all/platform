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

public class TableDialog extends Dialog {

  public TableDialog(WidgetInstance instance, PredictiveFormInstanceViewUI ui, WidgetDescriptor descriptor, PredictiveFormController controller) {
    
    FlexLayout dialogLayout = new FlexLayout();
    dialogLayout.setClassName("table-dialog-layout");
    add(dialogLayout);
    setMinWidth("20%");
    
    Label titleLabel = new Label(descriptor.getLabel());
    
    Binder<WidgetInstance> instanceBinder = new Binder<>(WidgetInstance.class);
    instanceBinder.setBean(instance);
    
    // TODO there must be a simpler and more futureproof way than this
    FlexLayout inputLayout = new FlexLayout();
    inputLayout.addClassName("table-dialog-inputs-layout");
    for (WidgetInstance wi : instance.getWidgets()) {
      WidgetDescriptor widgetDescriptor = controller.getWidgetDescriptor(wi.getDescriptorUri());
      NumberField nfValue = new NumberField();
      nfValue.setLabel(widgetDescriptor.getLabel());
      nfValue.setWidthFull();
      instanceBinder.forField(nfValue).asRequired("A mező kitöltése kötelező").bind(w -> {
        if (!wi.getDoubleValues().isEmpty()) {
          return wi.getDoubleValues().get(0);
        } else {
          return null;
        }
      }, (w, v) -> {
        if (wi.getDoubleValues().isEmpty()) {
          wi.addDoubleValuesItem(v);
        } else {
          wi.getDoubleValues().set(0, v);
        }
      });
      inputLayout.add(nfValue);
    }
    
    
    FlexLayout buttonLayout = WidgetDialogUtil.getButtonLayout(instance, ui, instanceBinder, this, controller);
    buttonLayout.setClassName("table-dialog-button-layout");
    
    dialogLayout.add(titleLabel, inputLayout, buttonLayout);
  }

}
