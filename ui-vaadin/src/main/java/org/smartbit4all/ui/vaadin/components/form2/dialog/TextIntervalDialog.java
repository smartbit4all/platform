package org.smartbit4all.ui.vaadin.components.form2.dialog;

import org.smartbit4all.ui.api.form.model.WidgetDescriptor;
import org.smartbit4all.ui.api.form.model.WidgetInstance;
import org.smartbit4all.ui.common.form2.impl.PredictiveFormController;
import org.smartbit4all.ui.vaadin.components.form2.PredictiveFormInstanceViewUI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

public class TextIntervalDialog extends Dialog {
  
  public TextIntervalDialog(WidgetInstance instance, PredictiveFormInstanceViewUI ui, WidgetDescriptor descriptor, PredictiveFormController controller) {
    FlexLayout dialogLayout = new FlexLayout();
    dialogLayout.setClassName("text-dialog-layout");
    add(dialogLayout);
    
    Label titleLabel = new Label(descriptor.getLabel());
    
    TextField tfValue1 = new TextField();
    TextField tfValue2 = new TextField();
    FlexLayout valueLayout = new FlexLayout(tfValue1, tfValue2);
    
    Binder<WidgetInstance> binder = new Binder<>(WidgetInstance.class);
    binder.setBean(instance);
    binder.forField(tfValue1).asRequired("A mező kitöltése kötelező").bind(w -> {
      if (instance.getStringValues().size() > 0) {
        return instance.getStringValues().get(0);
      } else {
        return null;
      }
    }, (w, v) -> {
      if (w.getStringValues().size() > 0) {
        w.getStringValues().set(0, v);
      } else {
        w.addStringValuesItem(v);
      }
    });
    binder.forField(tfValue2).asRequired("A mező kitöltése kötelező").bind(w -> {
      if (instance.getStringValues().size() > 1) {
        return instance.getStringValues().get(1);
      } else {
        return null;
      }
    }, (w, v) -> {
      if (w.getStringValues().size() > 1) {
        w.getStringValues().set(1, v);
      } else {
        if (w.getStringValues().size() > 0) {
          w.getStringValues().add(v);
        } else {
          w.getStringValues().add(null);
          w.getStringValues().add(v);
        }
      }
    });
    
    FlexLayout buttonLayout = WidgetDialogUtil.getButtonLayout(instance, ui, binder, this, controller);
    buttonLayout.setClassName("text-dialog-button-layout");
    
    dialogLayout.add(titleLabel, valueLayout, buttonLayout);
  }

}
