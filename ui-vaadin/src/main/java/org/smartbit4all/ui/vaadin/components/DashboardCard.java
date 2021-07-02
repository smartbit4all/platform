package org.smartbit4all.ui.vaadin.components;

import org.smartbit4all.ui.vaadin.components.navigation.Navigation;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class DashboardCard extends FlexLayout {

  private VaadinIcon topIcon;
  private VaadinIcon bottomIcon;

  public DashboardCard(String titleText, String viewName) {
    this(titleText, viewName, VaadinIcon.PLUS, VaadinIcon.ARROW_RIGHT);
  }

  public DashboardCard(String titleText, String viewName, VaadinIcon topIcon,
      VaadinIcon bottomIcon) {
    super();
    this.topIcon = topIcon;
    this.bottomIcon = bottomIcon;

    addClassName("sb4-page1-card");
    createTopButton(viewName);
    createTitle(titleText);
    createBottomButton(viewName);
  }
  
  public DashboardCard(String titleText, String viewName, VaadinIcon topIcon) {
    this(titleText, viewName, topIcon, VaadinIcon.ARROW_RIGHT);
  }

  public DashboardCard(String titleText,
      ComponentEventListener<ClickEvent<Button>> clickListener) {
    this(titleText, clickListener, VaadinIcon.PLUS, VaadinIcon.ARROW_RIGHT);
  }

  public DashboardCard(String titleText,
      ComponentEventListener<ClickEvent<Button>> clickListener, VaadinIcon topIcon,
      VaadinIcon bottomIcon) {
    super();
    this.topIcon = topIcon;
    this.bottomIcon = bottomIcon;

    addClassName("sb4-page1-card");
    createTopButton(clickListener);
    createTitle(titleText);
    createBottomButton(clickListener);
  }

  protected void createBottomButton(String viewName) {
    ComponentEventListener<ClickEvent<Button>> clickListener =
        event -> getUI().ifPresent(ui -> Navigation
            .to(viewName)
            .navigate(ui));
    createBottomButton(clickListener);
  }

  protected void createBottomButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
    Button bottomButton = new Button("Tovább", bottomIcon.create(),
        clickListener);
    bottomButton.addClassName("sb4-page1-card-bottom");
    add(bottomButton);
  }

  protected void createTitle(String titleText) {
    Label title = new Label(titleText);
    title.addClassName("sb4-page1-card-title");
    add(title);
  }

  protected void createTopButton(String viewName) {
    ComponentEventListener<ClickEvent<Button>> listener =
        event -> getUI().ifPresent(ui -> Navigation
            .to(viewName)
            .navigate(ui));
    createTopButton(listener);
  }

  protected void createTopButton(ComponentEventListener<ClickEvent<Button>> listener) {
    Button topButton = new Button();
    topButton.setIcon(topIcon.create());
    topButton.addClassName("sb4-page1-card-top");
    topButton.addClickListener(listener);
    add(topButton);
  }
}
