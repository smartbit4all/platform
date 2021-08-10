package org.smartbit4all.ui.vaadin.components.form2.dialog;

import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.ui.api.form.model.WidgetDescriptor;
import org.smartbit4all.ui.api.form.model.WidgetInstance;
import org.smartbit4all.ui.api.form.model.WidgetType;
import org.smartbit4all.ui.common.form2.impl.PredictiveFormController;
import org.smartbit4all.ui.vaadin.components.form2.PredictiveFormInstanceViewUI;
import org.smartbit4all.ui.vaadin.util.Notifications;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
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
  
  public static void openValueDialog(WidgetType widgetType, WidgetInstance instance,
      WidgetDescriptor descriptor, PredictiveFormInstanceViewUI ui, PredictiveFormController controller) {
    Dialog dialog;
    switch (widgetType) {
      case TEXT:
        dialog = new TextDialog(instance, ui, descriptor, controller);
        break;
      case TEXT_INTERVAL:
        dialog = new TextIntervalDialog(instance, ui, descriptor, controller);
        break;
      case SURVEY_COMBO:
        // TODO this dialog is not fully implemented yet
        dialog = new SurveyComboDialog(instance, ui, descriptor, controller);
        break;
      case NUMBER:
        dialog = new DoubleDialog(instance, ui, descriptor, controller);
        break;
      case INTEGER:
        dialog = new IntegerDialog(instance, ui, descriptor, controller);
        break;
      case COMBOBOX:
        dialog = new ComboBoxDialog(instance, ui, descriptor, controller);
        break;
      case DATE:
        dialog = new DateDialog(instance, ui, descriptor, controller);
        break;
      case DATE_INTERVAL:
        dialog = new DateIntervalDialog(instance, ui, descriptor, controller);
        break;
      case CONTAINER:
        dialog = new ContainerDialog(instance, ui, descriptor, controller);
        break;
        
      default:
        return;
    }
    dialog.open();
  }

  public static FlexLayout getValueLayoutFromWidgetInstance(WidgetInstance instance,
      WidgetType widgetType) {
    FlexLayout layout = new FlexLayout();
    
    List<String> stringValues = instance.getStringValues();
    List<Double> doubleValues = instance.getDoubleValues();
    List<Integer> intValues = instance.getIntValues();
    List<LocalDateTime> dateValues = instance.getDateValues();

    switch (widgetType) {
      case TEXT:
        if (stringValues != null && stringValues.size() > 0) {
          layout.add(new Label(stringValues.get(0)));
        }
        break;
      case TEXT_INTERVAL:
        if (stringValues.size() > 1) {
          layout.add(new Label(stringValues.get(0) + " : " + stringValues.get(1)));
        } else if (stringValues.size() > 0){
          layout.add(new Label(stringValues.get(0)));
        }
        break;
      case NUMBER:
        if (doubleValues.size() > 0) {
          layout.add(new Label(doubleValues.get(0).toString()));
        }
        break;
      case INTEGER:
        if (intValues.size() > 0) {
          layout.add(new Label(intValues.get(0).toString()));
        }
        break;
      case COMBOBOX:
        if (stringValues != null && stringValues.size() > 0) {
          layout.add(new Label(stringValues.get(0)));
        }
        break;
      case DATE:
        if (dateValues != null && dateValues.size() > 0) {
          layout.add(new Label(dateValues.get(0).toString()));
        }
        break;
      case DATE_INTERVAL:
        if (dateValues.size() > 1) {
          layout.add(new Label(dateValues.get(0) + " : " + dateValues.get(1)));
        } else if (dateValues.size() > 0){
          layout.add(new Label(dateValues.get(0).toString()));
        }
        break;
      default:
        break;
    }
    return layout;
  }
  
}
