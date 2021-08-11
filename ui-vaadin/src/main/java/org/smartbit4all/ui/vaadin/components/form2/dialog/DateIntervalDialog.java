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

public class DateIntervalDialog extends Dialog{

  public DateIntervalDialog(WidgetInstance instance, PredictiveFormInstanceViewUI ui, WidgetDescriptor descriptor, PredictiveFormController controller) {
    FlexLayout dialogLayout = new FlexLayout();
    dialogLayout.setClassName("date-interval-dialog-layout");
    add(dialogLayout);
    
    Label titleLabel = new Label(descriptor.getLabel());
    
    DatePicker dtValue1 = new DatePicker();
    DatePicker dtValue2 = new DatePicker();
    FlexLayout valueLayout = new FlexLayout(dtValue1, dtValue2);
    
    Binder<WidgetInstance> binder = new Binder<>(WidgetInstance.class);
    binder.setBean(instance);
    binder.forField(dtValue1).bind(w -> {
      if (instance.getDateValues().size() > 0) {
        return instance.getDateValues().get(0).toLocalDate();
      } else {
        return null;
      }
    }, (w, v) -> {
      if (w.getDateValues().size() > 0) {
        w.getDateValues().set(0, v.atStartOfDay());
      } else {
        w.addDateValuesItem(v.atStartOfDay());
      }
    });
    binder.forField(dtValue2).bind(w -> {
      if (instance.getDateValues().size() > 1) {
        return instance.getDateValues().get(1).toLocalDate();
      } else {
        return null;
      }
    }, (w, v) -> {
      if (w.getDateValues().size() > 1) {
        w.getDateValues().set(1, v.atStartOfDay());
      } else {
        if (w.getDateValues().size() > 0) {
          w.getDateValues().add(v.atStartOfDay());
        } else {
          w.getDateValues().add(null);
          w.getDateValues().add(v.atStartOfDay());
        }
      }
    });
    
    FlexLayout buttonLayout = WidgetDialogUtil.getButtonLayout(instance, ui, binder, this, controller);
    buttonLayout.setClassName("date-interval-dialog-button-layout");
    
    dialogLayout.add(titleLabel, valueLayout, buttonLayout);
  }
  
}
