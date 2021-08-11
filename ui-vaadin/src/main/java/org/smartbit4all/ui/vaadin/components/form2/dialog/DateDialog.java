package org.smartbit4all.ui.vaadin.components.form2.dialog;

import org.smartbit4all.ui.api.form.model.WidgetDescriptor;
import org.smartbit4all.ui.api.form.model.WidgetInstance;
import org.smartbit4all.ui.common.form2.impl.PredictiveFormController;
import org.smartbit4all.ui.vaadin.components.form2.PredictiveFormInstanceViewUI;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.data.binder.Binder;

public class DateDialog extends Dialog {
  
  public DateDialog(WidgetInstance instance, PredictiveFormInstanceViewUI ui, WidgetDescriptor descriptor, PredictiveFormController controller) {
    FlexLayout dialogLayout = new FlexLayout();
    dialogLayout.setClassName("date-dialog-layout");
    add(dialogLayout);
    
    Label titleLabel = new Label(descriptor.getLabel());
    
    DatePicker dtValue = new DatePicker();
    
    Binder<WidgetInstance> binder = new Binder<>(WidgetInstance.class);
    binder.setBean(instance);
    binder.forField(dtValue).bind(w -> {
      if (instance.getDateValues().size() > 0) {
        return instance.getDateValues().get(0).toLocalDate();
      } else {
        return null;
      }
    }, (w, v) -> {
      w.getDateValues().clear();
      w.addDateValuesItem(v.atStartOfDay());
    });
    
    FlexLayout buttonLayout = WidgetDialogUtil.getButtonLayout(instance, ui, binder, this, controller);
    buttonLayout.setClassName("date-dialog-button-layout");
    
    dialogLayout.add(titleLabel, dtValue, buttonLayout);
  }

}
