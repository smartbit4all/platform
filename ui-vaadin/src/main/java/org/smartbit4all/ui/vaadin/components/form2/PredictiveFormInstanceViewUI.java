package org.smartbit4all.ui.vaadin.components.form2;

import java.net.URI;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.ui.api.form.model.PredictiveInputGraphNode;
import org.smartbit4all.ui.api.form.model.WidgetDescriptor;
import org.smartbit4all.ui.api.form.model.WidgetInstance;
import org.smartbit4all.ui.api.form.model.WidgetType;
import org.smartbit4all.ui.common.form2.impl.PredictiveFormController;
import org.smartbit4all.ui.common.form2.impl.PredictiveFormInstanceView;
import org.smartbit4all.ui.vaadin.components.form2.dialog.TextDialog;
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

  private static final Logger log = LoggerFactory.getLogger(PredictiveFormInstanceViewUI.class);

  /**
   * The controller of the form.
   */
  private PredictiveFormController controller;

  /**
   * The holder of the selectable widgets, on the lower part of the screen.
   */
  private FlexLayout availableWidgetsHolder;

  /**
   * The holder of the already selected widgets, on the upper part of the screen.
   */
  private FlexLayout visibleWidgetsHolder;

  /**
   * The lower layout, that holds the available widgets holder and the button group that controls
   * the navigation in the tree.
   */
  private FlexLayout lowerLayout;

  /**
   * The layout that's content is being edited right now.
   */
  private FlexLayout currentLayout;

  public PredictiveFormInstanceViewUI(PredictiveFormController controller) {
    this.controller = controller;
    init();
    controller.setUI(this);
    renderWidgets();
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
    List<WidgetDescriptor> availableWidgets = controller.getAvailableWidgets();
//    List<PredictiveInputGraphNode> availableNodes = controller.getAvailableNodes();
    if (availableWidgets != null) {
      for (WidgetDescriptor descriptor : availableWidgets) {
        Button availableWidgetView = createAvailableWidgetView(descriptor.getLabel(),
            descriptor.getIcon(), descriptor.getUri());
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
    
    if (descriptor.getWidgetType() == WidgetType.CONTAINER) {
      FlexLayout containerLayout = new FlexLayout();
      containerLayout.setClassName("visible-widget-container-view");
      containerLayout.add(createVisibleWidgetView(descriptor, instance));
      if (instance.getWidgets() != null) {
        for (WidgetInstance wi : instance.getWidgets()) {
          containerLayout.add(renderVisibleWidgetView(wi));
        }
      }
      return containerLayout;
    } else {
      return createVisibleWidgetView(descriptor, instance);
    }
  }
  
  private FlexLayout createVisibleWidgetView(WidgetDescriptor descriptor, WidgetInstance instance) {
    FlexLayout layout = new FlexLayout();
    layout.setClassName("visible-widget-view");
    
    Icon icon = new Icon(descriptor.getIcon());
    icon.setClassName("visible-widget-view-icon");
    
    Label label = new Label(descriptor.getLabel());
    label.setClassName("visible-widget-view-label");
    
    String value = getValueFromWidgetInstance(instance, descriptor.getWidgetType());
    Label valueLabel = new Label(value);
    valueLabel.setClassName("visible-widget-view-value-label");
    
    layout.add(icon, label, valueLabel);
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
    Button homeButton = new Button(homeIcon, e -> controller.jumpToStart());
    homeButton.setClassName("selection-button-group");
    buttonGroupLayout.add(homeButton);

    Button finalize = new Button("Véglegesít", e -> confirmDialog());
    finalize.setClassName("selection-button-group");
    buttonGroupLayout.add(finalize);
    return buttonGroupLayout;
  }

  private Button createAvailableWidgetView(String label, String iconPath, URI descriptorUri) {
    Button button = new Button(label);
    button.setClassName("selection-button");
    Icon icon = new Icon(iconPath);
    button.setIcon(icon);
    button.addClickListener(e -> controller.selectWidget(descriptorUri));
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
  public void openValueDialog(WidgetType widgetType, WidgetInstance instance) {
    Dialog dialog;
    switch (widgetType) {
      case TEXT:
        dialog = new TextDialog(instance, this);
        break;

      default:
        return;
    }
    dialog.open();
  }

  private String getValueFromWidgetInstance(WidgetInstance instance, WidgetType widgetType) {
    switch (widgetType) {
      case TEXT:
        if (instance.getStringValues() != null && instance.getStringValues().size() > 0) {
          return instance.getStringValues().get(0);
        }
      default:
        return null;
    }
  }
}
