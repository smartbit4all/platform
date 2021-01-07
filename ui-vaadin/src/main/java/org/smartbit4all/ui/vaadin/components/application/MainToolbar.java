/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.ui.vaadin.components.application;

import org.smartbit4all.ui.vaadin.util.Buttons;
import org.smartbit4all.ui.vaadin.util.Css;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

@CssImport("./styles/components/application/main-toolbar.css")
public class MainToolbar extends FlexLayout {

  private String CLASS_NAME = "main-toolbar";

  private FlexLayout container;

  private Button menuIcon;

  private H4 title;
  private FlexLayout actionItems;
  private Icon avatar;
  private Button themeButton;

  private Class<? extends Component> homeView;

  public MainToolbar(Class<? extends Component> homeView, String title) {
    setClassName(CLASS_NAME);

    this.homeView = homeView;

    initMenuIcon();
    initTitle(title);
    initThemeButton();
    initAvatar();
    initActionItems();
    initContainer();
  }

  private void initThemeButton() {
    themeButton = new Button(new Icon(VaadinIcon.ADJUST));
    themeButton.addClassName(CLASS_NAME + "__theme-button");

  }

  private void initMenuIcon() {
    menuIcon = Buttons.createTertiaryInlineButton(VaadinIcon.MENU);
    menuIcon.addClassName(CLASS_NAME + "__menu-icon");
    menuIcon.addClickListener(e -> MainScreen.get().getMenuHolder().toggle());
    Css.setAriaLabel("Menu", menuIcon);
    Css.setLineHeight("1", menuIcon);
  }

  private void initTitle(String title) {
    this.title = new H4(title);
    this.title.setClassName(CLASS_NAME + "__title");
  }

  private void initAvatar() {
    avatar = VaadinIcon.USER_CARD.create();
    avatar.setClassName(CLASS_NAME + "__avatar");

    ContextMenu contextMenu = new ContextMenu(avatar);
    contextMenu.setOpenOnClick(true);
    contextMenu.addItem(getTranslation(
        "application.logout"),
        e -> UI.getCurrent().getPage().executeJs("window.open(\"/logout\", \"_self\");"));
  }

  private void initActionItems() {
    actionItems = new FlexLayout();
    actionItems.addClassName(CLASS_NAME + "__action-items");
    actionItems.setVisible(false);
  }

  private void initContainer() {
    container = new FlexLayout(menuIcon, this.title, actionItems, themeButton, avatar);
    container.addClassName(CLASS_NAME + "__container");
    container.setAlignItems(FlexComponent.Alignment.CENTER);
    add(container);
  }

  /* === MENU ICON === */

  public Button getMenuIcon() {
    return menuIcon;
  }

  /* === TITLE === */

  public String getTitle() {
    return this.title.getText();
  }

  public void setTitle(String title) {
    this.title.setText(title);
  }

  /* === ACTION ITEMS === */

  public Component addActionItem(Component component) {
    actionItems.add(component);
    updateActionItemsVisibility();
    return component;
  }

  public Button addActionItem(VaadinIcon icon) {
    Button button =
        Buttons.createButton(icon, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
    addActionItem(button);
    return button;
  }

  public void removeAllActionItems() {
    actionItems.removeAll();
    updateActionItemsVisibility();
  }

  /* === AVATAR == */

  public Icon getAvatar() {
    return avatar;
  }

  /* === THEME BUTTON == */

  public Button getThemeButton() {
    return themeButton;
  }

  /* === RESET === */

  public void reset() {
    title.setText("");
    removeAllActionItems();
  }

  /* === UPDATE VISIBILITY === */

  private void updateActionItemsVisibility() {
    actionItems.setVisible(actionItems.getComponentCount() > 0);
  }

}
