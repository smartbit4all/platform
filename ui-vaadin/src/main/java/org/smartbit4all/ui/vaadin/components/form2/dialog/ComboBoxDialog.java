package org.smartbit4all.ui.vaadin.components.form2.dialog;

import java.util.Arrays;
import org.smartbit4all.ui.api.form.model.WidgetDescriptor;
import org.smartbit4all.ui.api.form.model.WidgetInstance;
import org.smartbit4all.ui.common.form2.impl.PredictiveFormController;
import org.smartbit4all.ui.vaadin.components.form2.PredictiveFormInstanceViewUI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.data.binder.Binder;

public class ComboBoxDialog extends Dialog {
  
  public ComboBoxDialog(WidgetInstance instance, PredictiveFormInstanceViewUI ui, WidgetDescriptor descriptor, PredictiveFormController controller) {
    FlexLayout dialogLayout = new FlexLayout();
    dialogLayout.setClassName("combobox-dialog-layout");
    add(dialogLayout);
    
    Label titleLabel = new Label(descriptor.getLabel());
    
    ComboBox<String> cbValue = new ComboBox<>();
    cbValue.setItems(Arrays.asList("mock1", "mock2", "mock3"));
    
    Binder<WidgetInstance> binder = new Binder<>(WidgetInstance.class);
    binder.setBean(instance);
    binder.forField(cbValue).asRequired("A mező kitöltése kötelező").bind(w -> {
      if (instance.getStringValues().size() > 0) {
        return instance.getStringValues().get(0);
      } else {
        return null;
      }
    }, (w, v) -> {
      w.getStringValues().clear();
      w.addStringValuesItem(v);
    });
    
    FlexLayout buttonLayout = WidgetDialogUtil.getButtonLayout(instance, ui, binder, this, controller);
    buttonLayout.setClassName("combobox-dialog-button-layout");
    
    dialogLayout.add(titleLabel, cbValue, buttonLayout);
  }

}
