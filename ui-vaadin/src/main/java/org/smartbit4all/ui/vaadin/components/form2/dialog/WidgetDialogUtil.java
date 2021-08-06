package org.smartbit4all.ui.vaadin.components.form2.dialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.ui.api.form.model.WidgetInstance;
import org.smartbit4all.ui.common.form2.impl.PredictiveFormController;
import org.smartbit4all.ui.vaadin.components.form2.PredictiveFormInstanceViewUI;
import org.smartbit4all.ui.vaadin.util.Notifications;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

public class WidgetDialogUtil {
  
  private static final Logger log = LoggerFactory.getLogger(WidgetDialogUtil.class);

  public static FlexLayout getButtonLayout(WidgetInstance instance, PredictiveFormInstanceViewUI ui,
      Binder<WidgetInstance> binder, Dialog dialog, PredictiveFormController controller) {
    Button btnCancel = new Button("Mégsem", e -> {
      if (dialog.isOpened()) {
        dialog.close();
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
      dialog.close();
    });
    
    Button btnDelete = new Button(new Icon(VaadinIcon.TRASH));
    btnDelete.addClickListener(e -> {
      controller.deleteWidgetInstance(instance);
      dialog.close();
    });
    
    FlexLayout buttonLayout = new FlexLayout(btnSave, btnCancel, btnDelete);
    return buttonLayout;
  }
  
}
