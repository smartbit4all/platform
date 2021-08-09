package org.smartbit4all.ui.vaadin.components.form2.dialog;

import org.smartbit4all.ui.api.form.model.WidgetDescriptor;
import org.smartbit4all.ui.api.form.model.WidgetInstance;
import org.smartbit4all.ui.common.form2.impl.PredictiveFormController;
import org.smartbit4all.ui.vaadin.components.form2.PredictiveFormInstanceViewUI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class ContainerDialog extends Dialog{
  
  public ContainerDialog(WidgetInstance instance, PredictiveFormInstanceViewUI ui, WidgetDescriptor descriptor, PredictiveFormController controller) {
    FlexLayout dialogLayout = new FlexLayout();
    dialogLayout.setClassName("container-dialog-layout");
    add(dialogLayout);
    
    Label titleLabel = new Label(descriptor.getLabel());
    
    FlexLayout buttonLayout = new FlexLayout();
    Button btnCancel = new Button("Mégsem", e -> {
      if (isOpened()) {
        close();
      }
    });
    
    Button btnDelete = new Button(new Icon(VaadinIcon.TRASH));
    btnDelete.addClickListener(e -> {
      controller.deleteWidgetInstance(instance);
      close();
    });
    buttonLayout.add(btnCancel, btnDelete);
    
    buttonLayout.setClassName("container-dialog-button-layout");
    
    dialogLayout.add(titleLabel, buttonLayout);
  }

}
