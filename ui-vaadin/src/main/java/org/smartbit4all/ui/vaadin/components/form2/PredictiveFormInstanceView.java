package org.smartbit4all.ui.vaadin.components.form2;

import java.util.List;
import org.smartbit4all.ui.api.form.model.WidgetDescriptor;
import org.smartbit4all.ui.common.form2.impl.PredictiveFormController;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

//@CssImport("./smartbit4all/styles/views/predictive-template-view.css")
//@CssImport(value = "./smartbit4all/styles/views/predictive-template-view-combobox.css",
//    themeFor = "vaadin-combo-box")
public class PredictiveFormInstanceView extends FlexLayout {

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
   * The lower layout, that holds the available widgets holder and the button group that controls the
   * navigation in the tree.
   */
  private FlexLayout lowerLayout;

  public PredictiveFormInstanceView(PredictiveFormController controller) {
    this.controller = controller;
    init();
  }

  private void init() {
    addClassName("predictive-form-view");
    // TODO in css !!!!!
    setFlexDirection(FlexDirection.COLUMN);
    setHeight("100%");
    initVisibleWidgetsHolder();
    initLowerLayout();
  }

  private void initVisibleWidgetsHolder() {
    visibleWidgetsHolder = new FlexLayout();
    visibleWidgetsHolder.addClassName("visible-widgets-layout");
    // TODO ALL this should be in CSS !!!
    visibleWidgetsHolder.setFlexDirection(FlexDirection.COLUMN);
    visibleWidgetsHolder.getStyle().set("overflow", "auto");
    visibleWidgetsHolder.getStyle().set("border", "1px solid");
    visibleWidgetsHolder.setWidth("100%");
    visibleWidgetsHolder.setHeight("80%");
    add(visibleWidgetsHolder);
  }

  private void initLowerLayout() {
    lowerLayout = new FlexLayout();
    lowerLayout.addClassName("lower-layout");
    // TODO this should be in CSS!!!!
    lowerLayout.getStyle().set("felx-shrink", "0");
    lowerLayout.getStyle().set("overflow", "auto");
    lowerLayout.getStyle().set("border", "1px solid");
    lowerLayout.setWidth("100%");
    lowerLayout.setHeight("35%");
    
    add(lowerLayout);
    
    availableWidgetsHolder = initAvailableWidgetsHolder();
    lowerLayout.add(availableWidgetsHolder);
    
    FlexLayout buttonGroupLayout = initButtonGroupLayout();
    lowerLayout.add(buttonGroupLayout);
    
    controller.loadAvailableWidgets();
  }
  
  /**
   * Removes every widget from the available widget holder, and renders the new ones.
   * 
   * @param widgetDescriptors the list of the available widget descriptors.
   */
  public void renderAvailableWidgets(List<WidgetDescriptor> widgetDescriptors) {
    availableWidgetsHolder.removeAll();
    for (WidgetDescriptor descriptor : widgetDescriptors) {
      Button availableWidgetView = createAvailableWidgetView(descriptor);
      availableWidgetsHolder.add(availableWidgetView);
    }
  }

  private FlexLayout initAvailableWidgetsHolder() {
    FlexLayout layout = new FlexLayout();
    layout.addClassName("available-widgets-holder");
    // TODO ALL this should be in CSS !!!
    layout.setWidth("100%");
    layout.setFlexWrap(FlexWrap.WRAP);
    return layout;
  }

  private FlexLayout initButtonGroupLayout() {
    FlexLayout buttonGroupLayout = new FlexLayout();
    buttonGroupLayout.addClassName("selection-layout-button-group");
    buttonGroupLayout.setWidthFull();
    buttonGroupLayout.setJustifyContentMode(JustifyContentMode.END);
    buttonGroupLayout.getStyle().set("margin-top", "auto");

    Icon backIcon = new Icon(VaadinIcon.ARROW_BACKWARD);
    Button backButton = new Button("Vissza", backIcon, e -> controller.stepBack());
    backButton.setClassName("selection-button-group");
    buttonGroupLayout.add(backButton);

    Icon homeIcon = new Icon(VaadinIcon.HOME);
    Button homeButton = new Button(homeIcon, e -> controller.loadRoot());
    homeButton.setClassName("selection-button-group");
    buttonGroupLayout.add(homeButton);

    Button finalize = new Button("Véglegesít", e -> confirmDialog());
    finalize.setClassName("selection-button-group");
    buttonGroupLayout.add(finalize);
    return buttonGroupLayout;
  }
  
  private Button createAvailableWidgetView(WidgetDescriptor descriptor) {
    Button button = new Button(descriptor.getLabel());
    button.setClassName("selection-button");
    Icon icon = new Icon(descriptor.getIcon());
    button.setIcon(icon);
    button.getStyle().set("margin", "5px"); // TODO should be in css
    button.addClickListener(e -> controller.selectWidget(descriptor));
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

}
