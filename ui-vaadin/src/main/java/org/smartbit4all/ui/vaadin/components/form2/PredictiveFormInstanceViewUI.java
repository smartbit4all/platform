package org.smartbit4all.ui.vaadin.components.form2;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import org.smartbit4all.ui.api.form.model.EntityFormInstance;
import org.smartbit4all.ui.api.form.model.PredictiveInputGraphNode;
import org.smartbit4all.ui.api.form.model.WidgetDescriptor;
import org.smartbit4all.ui.api.form.model.WidgetInstance;
import org.smartbit4all.ui.api.form.model.WidgetType;
import org.smartbit4all.ui.common.form2.impl.PredictiveFormController;
import org.smartbit4all.ui.common.form2.impl.PredictiveFormInstanceView;
import org.smartbit4all.ui.vaadin.components.form2.dialog.ComboBoxDialog;
import org.smartbit4all.ui.vaadin.components.form2.dialog.DateDialog;
import org.smartbit4all.ui.vaadin.components.form2.dialog.DateIntervalDialog;
import org.smartbit4all.ui.vaadin.components.form2.dialog.DoubleDialog;
import org.smartbit4all.ui.vaadin.components.form2.dialog.IntegerDialog;
import org.smartbit4all.ui.vaadin.components.form2.dialog.SurveyComboDialog;
import org.smartbit4all.ui.vaadin.components.form2.dialog.TextDialog;
import org.smartbit4all.ui.vaadin.components.form2.dialog.TextIntervalDialog;
import org.smartbit4all.ui.vaadin.components.navigation.Navigation;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

@CssImport("./styles/components/predictiveform/predictive-form.css")
public class PredictiveFormInstanceViewUI extends FlexLayout implements PredictiveFormInstanceView {

  // private static final Logger log = LoggerFactory.getLogger(PredictiveFormInstanceViewUI.class);
  private PredictiveFormController controller;
  private FlexLayout availableWidgetsHolder;
  private FlexLayout visibleWidgetsHolder;
  private FlexLayout lowerLayout;

  public PredictiveFormInstanceViewUI(PredictiveFormController controller) {
    this.controller = controller;
    controller.setUI(this);
    init();
    // renderWidgets();
  }

  private void init() {
    addClassName("predictive-form-view");
    initVisibleWidgetsHolder();
    initLowerLayout();
  }

  private void initVisibleWidgetsHolder() {
    visibleWidgetsHolder = new FlexLayout();
    visibleWidgetsHolder.addClassName("visible-widgets-layout");
    add(visibleWidgetsHolder);
  }

  private void initLowerLayout() {
    lowerLayout = new FlexLayout();
    lowerLayout.addClassName("lower-layout");

    availableWidgetsHolder = initAvailableWidgetsHolder();
    lowerLayout.add(availableWidgetsHolder);

    FlexLayout buttonGroupLayout = initButtonGroupLayout();
    lowerLayout.add(buttonGroupLayout);

    add(lowerLayout);
  }

  private void renderAvailableWidgets() {
    availableWidgetsHolder.removeAll();
    List<PredictiveInputGraphNode> availableNodes = controller.getAvailableNodes();
    if (availableNodes != null) {
      for (PredictiveInputGraphNode node : availableNodes) {
        Button availableWidgetView = createAvailableWidgetView(node);
        availableWidgetsHolder.add(availableWidgetView);
      }
    }
  }

  private void renderVisibleWidgets() {
    visibleWidgetsHolder.removeAll();
    List<WidgetInstance> visibleWidgets = controller.getVisibleWidgets();
    if (visibleWidgets != null) {
      for (WidgetInstance instance : visibleWidgets) {
        FlexLayout instanceView = renderVisibleWidgetView(instance);
        visibleWidgetsHolder.add(instanceView);
      }
    }
  }

  // TODO this doesn't work for every widget type
  private FlexLayout renderVisibleWidgetView(WidgetInstance instance) {
    WidgetDescriptor descriptor = controller.getWidgetDescriptor(instance.getDescriptorUri());
    boolean isWidgetSelected = controller.isWidgetSelected(instance);

    if (descriptor.getWidgetType() == WidgetType.CONTAINER) {
      return createVisibleWidgetContainerView(instance, descriptor, isWidgetSelected);
    } else {
      return createVisibleWidgetView(descriptor, instance, isWidgetSelected);
    }
  }

  private FlexLayout createVisibleWidgetContainerView(WidgetInstance instance,
      WidgetDescriptor descriptor, boolean isWidgetSelected) {
    FlexLayout containerLayout = new FlexLayout();
    containerLayout.setClassName("visible-widget-container-view");
    containerLayout.add(createVisibleWidgetView(descriptor, instance, isWidgetSelected));
    if (instance.getWidgets() != null) {
      for (WidgetInstance wi : instance.getWidgets()) {
        containerLayout.add(renderVisibleWidgetView(wi));
      }
    }

    containerLayout.addClickListener(e -> {
       controller.selectWidget(instance);
    });
    
    return containerLayout;
  }

  private FlexLayout createVisibleWidgetView(WidgetDescriptor descriptor, WidgetInstance instance, boolean isWidgetSelected) {
    FlexLayout layout = new FlexLayout();
    layout.setClassName("visible-widget-view");

    Icon icon = new Icon(descriptor.getIcon());
    icon.setClassName("visible-widget-view-icon");

    Label label = new Label(descriptor.getLabel());
    label.setClassName("visible-widget-view-label");

    String value = getValueFromWidgetInstance(instance, descriptor.getWidgetType());
    Label valueLabel = new Label(value);
    valueLabel.setClassName("visible-widget-view-value-label");
    
    if (isWidgetSelected) {
      layout.addClassName("selected-widget-view");
    }
    
    Button editButton = new Button();
    editButton.setIcon(new Icon(VaadinIcon.ELLIPSIS_DOTS_H));
    editButton.addClickListener(e -> {
      openValueDialog(descriptor.getWidgetType(), instance, descriptor);
    });

    layout.add(icon, label, valueLabel, editButton);
    return layout;
  }

  private FlexLayout initAvailableWidgetsHolder() {
    FlexLayout layout = new FlexLayout();
    layout.addClassName("available-widgets-holder");
    return layout;
  }

  private FlexLayout initButtonGroupLayout() {
    FlexLayout buttonGroupLayout = new FlexLayout();
    buttonGroupLayout.addClassName("selection-layout-button-group");

    Icon backIcon = new Icon(VaadinIcon.ARROW_BACKWARD);
    Button backButton = new Button("Vissza", backIcon, e -> controller.stepBack());
    backButton.setClassName("selection-button-group");
    buttonGroupLayout.add(backButton);

    Icon homeIcon = new Icon(VaadinIcon.HOME);
    Button homeButton = new Button(homeIcon, e -> controller.goToRoot());
    homeButton.setClassName("selection-button-group");
    buttonGroupLayout.add(homeButton);

    Button finalize = new Button("Véglegesít", e -> confirmDialog());
    finalize.setClassName("selection-button-group");
    buttonGroupLayout.add(finalize);
    return buttonGroupLayout;
  }

  private Button createAvailableWidgetView(PredictiveInputGraphNode node) {
    WidgetDescriptor descriptor = controller.getWidgetDescriptor(node.getDescriptorUri());
    Button button = new Button(descriptor.getLabel());
    button.setClassName("selection-button");
    Icon icon = new Icon(descriptor.getIcon());
    button.setIcon(icon);
    button.addClickListener(e -> controller.addWidget(node));
    return button;
  }

  private void confirmDialog() {
    Dialog dialog = new Dialog();

    Label titleLabel = new Label("Szeretné véglegesíteni a dokumentumot?");
    titleLabel.setClassName("dialog-title");
    dialog.add(titleLabel);
    HorizontalLayout layout = new HorizontalLayout();
    dialog.add(layout);
    dialog.setCloseOnEsc(false);
    dialog.setCloseOnOutsideClick(false);

    Button confirmButton = new Button("Igen", event -> {
      controller.save();
      dialog.close();
    });
    confirmButton.setClassName("selection-button-group");
    Button cancelButton = new Button("Mégse", event -> {
      dialog.close();
    });
    cancelButton.setClassName("selection-button-group");

    Label emptyLabel = new Label();
    emptyLabel.getElement().getStyle().set("flex-grow", "1");

    layout.add(confirmButton, emptyLabel, cancelButton);
    dialog.setWidth("60%");
    dialog.open();
  }

  @Override
  public void renderWidgets() {
    renderAvailableWidgets();
    renderVisibleWidgets();
  }

  @Override
  public void openValueDialog(WidgetType widgetType, WidgetInstance instance,
      WidgetDescriptor descriptor) {
    Dialog dialog;
    switch (widgetType) {
      case TEXT:
        dialog = new TextDialog(instance, this, descriptor, controller);
        break;
      case TEXT_INTERVAL:
        dialog = new TextIntervalDialog(instance, this, descriptor, controller);
        break;
      case SURVEY_COMBO:
        dialog = new SurveyComboDialog(instance, this, descriptor, controller);
        break;
      case NUMBER:
        dialog = new DoubleDialog(instance, this, descriptor, controller);
        break;
      case INTEGER:
        dialog = new IntegerDialog(instance, this, descriptor, controller);
        break;
      case COMBOBOX:
        dialog = new ComboBoxDialog(instance, this, descriptor, controller);
        break;
      case DATE:
        dialog = new DateDialog(instance, this, descriptor, controller);
        break;
      case DATE_INTERVAL:
        dialog = new DateIntervalDialog(instance, this, descriptor, controller);
        break;
        
      default:
        return;
    }
    dialog.open();
  }

  // TODO these could go into the specific dialog classes
  private String getValueFromWidgetInstance(WidgetInstance instance, WidgetType widgetType) {
    List<String> stringValues = instance.getStringValues();
    List<Double> doubleValues = instance.getDoubleValues();
    List<Integer> intValues = instance.getIntValues();
    List<LocalDateTime> dateValues = instance.getDateValues();

    switch (widgetType) {
      case TEXT:
        if (stringValues != null && stringValues.size() > 0) {
          return stringValues.get(0);
        }
      case TEXT_INTERVAL:
        if (stringValues.size() > 1) {
          return stringValues.get(0) + " : " + stringValues.get(1);
        } else if (stringValues.size() > 0){
          return stringValues.get(0);
        }
      case NUMBER:
        if (doubleValues.size() > 0) {
          return doubleValues.get(0).toString();
        }
      case INTEGER:
        if (intValues.size() > 0) {
          return intValues.get(0).toString();
        }
      case COMBOBOX:
        if (stringValues != null && stringValues.size() > 0) {
          return stringValues.get(0);
        }
      case DATE:
        if (dateValues != null && dateValues.size() > 0) {
          return dateValues.get(0).toString();
        }
      case DATE_INTERVAL:
        if (dateValues.size() > 1) {
          return dateValues.get(0) + " : " + dateValues.get(1);
        } else if (dateValues.size() > 0){
          return dateValues.get(0).toString();
        }
      default:
        return null;
    }
  }

  @Override
  public void navigateTo(EntityFormInstance instance) {
    getUI().ifPresent(ui -> Navigation
        .to("predictiveform")
        .param("instance", instance)
        .navigate(ui));

  }

  public void setInstanceUri(URI uri) {
    controller.loadTemplate(uri);
  }
}
