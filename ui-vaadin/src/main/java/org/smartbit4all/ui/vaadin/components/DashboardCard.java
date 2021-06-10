package org.smartbit4all.ui.vaadin.components;

import org.smartbit4all.ui.vaadin.components.navigation.Navigation;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class DashboardCard extends FlexLayout {

  public DashboardCard(String titleText, String viewName) {
    super();

    addClassName("sb4-page1-card");
    createPlusButton(viewName);
    createTitle(titleText);
    createEnterButton(viewName);
  }

  public DashboardCard(String titleText,
      ComponentEventListener<ClickEvent<Button>> clickListener) {
    super();

    addClassName("sb4-page1-card");
    createPlusButton(clickListener);
    createTitle(titleText);
    createEnterButton(clickListener);
  }

  protected void createEnterButton(String viewName) {
    ComponentEventListener<ClickEvent<Button>> clickListener =
        event -> getUI().ifPresent(ui -> Navigation
            .to(viewName)
            .navigate(ui));
    createEnterButton(clickListener);
  }

  protected void createEnterButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
    Button enterButton = new Button("Tovább", VaadinIcon.ARROW_RIGHT.create(),
        clickListener);
    enterButton.addClassName("sb4-page1-card-enter");
    add(enterButton);
  }

  protected void createTitle(String titleText) {
    Label title = new Label(titleText);
    title.addClassName("sb4-page1-card-title");
    add(title);
  }

  protected void createPlusButton(String viewName) {
    ComponentEventListener<ClickEvent<Button>> listener =
        event -> getUI().ifPresent(ui -> Navigation
            .to(viewName)
            .navigate(ui));
    createPlusButton(listener);
  }

  protected void createPlusButton(ComponentEventListener<ClickEvent<Button>> listener) {
    Button plusButton = new Button();
    plusButton.setIcon(VaadinIcon.PLUS.create());
    plusButton.addClassName("sb4-page1-card-plus");
    plusButton.addClickListener(listener);
    add(plusButton);
  }
}
