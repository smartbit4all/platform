package org.smartbit4all.ui.vaadin.components.form2.dialog;

import org.smartbit4all.ui.api.form.model.WidgetDescriptor;
import org.smartbit4all.ui.api.form.model.WidgetInstance;
import org.smartbit4all.ui.common.form2.impl.PredictiveFormController;
import org.smartbit4all.ui.vaadin.components.form2.PredictiveFormInstanceViewUI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.data.binder.Binder;

public class TableDialog extends Dialog {

  public TableDialog(WidgetInstance instance, PredictiveFormInstanceViewUI ui, WidgetDescriptor descriptor, PredictiveFormController controller) {
    
    FlexLayout dialogLayout = new FlexLayout();
    dialogLayout.setClassName("table-dialog-layout");
    add(dialogLayout);
    
    Label titleLabel = new Label(descriptor.getLabel());
    
    Binder<WidgetInstance> binder = new Binder<>(WidgetInstance.class);
    binder.setBean(instance);
    
    FlexLayout buttonLayout = WidgetDialogUtil.getButtonLayout(instance, ui, binder, this, controller);
    buttonLayout.setClassName("table-dialog-button-layout");
    
    dialogLayout.add(titleLabel, buttonLayout);
  }

}
