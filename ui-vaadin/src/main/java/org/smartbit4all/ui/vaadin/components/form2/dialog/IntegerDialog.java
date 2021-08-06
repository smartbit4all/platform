package org.smartbit4all.ui.vaadin.components.form2.dialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.ui.api.form.model.WidgetDescriptor;
import org.smartbit4all.ui.api.form.model.WidgetInstance;
import org.smartbit4all.ui.common.form2.impl.PredictiveFormController;
import org.smartbit4all.ui.vaadin.components.form2.PredictiveFormInstanceViewUI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.Binder;

public class IntegerDialog extends Dialog {

  private static final Logger log = LoggerFactory.getLogger(IntegerDialog.class);
  
  public IntegerDialog(WidgetInstance instance, PredictiveFormInstanceViewUI ui, WidgetDescriptor descriptor, PredictiveFormController controller) {
    FlexLayout dialogLayout = new FlexLayout();
    dialogLayout.setClassName("integer-dialog-layout");
    add(dialogLayout);
    
    Label titleLabel = new Label(descriptor.getLabel());
    IntegerField ifValue = new IntegerField();
    
    Binder<WidgetInstance> binder = new Binder<>(WidgetInstance.class);
    binder.setBean(instance);
    binder.forField(ifValue).bind(w -> {
      if (instance.getIntValues().size() > 0) {
        return instance.getIntValues().get(0);
      } else {
        return null;
      }
    }, (w, v) -> {
      w.getIntValues().clear();
      w.addIntValuesItem(v);
    });
    
    FlexLayout buttonLayout = WidgetDialogUtil.getButtonLayout(instance, ui, binder, this, controller);
    buttonLayout.setClassName("integer-dialog-button-layout");
    
    dialogLayout.add(titleLabel, ifValue, buttonLayout);
  }

}
